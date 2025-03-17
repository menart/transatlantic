package express.atc.backend.service.Impl;

import express.atc.backend.calculate.CalcCustomsFee;
import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.db.entity.TrackingRouteEntity;
import express.atc.backend.db.repository.TrackingRepository;
import express.atc.backend.db.repository.TrackingRouteRepository;
import express.atc.backend.dto.*;
import express.atc.backend.enums.TrackingStatus;
import express.atc.backend.exception.ApiException;
import express.atc.backend.exception.TrackNotFoundException;
import express.atc.backend.integration.cargoflow.service.CargoflowService;
import express.atc.backend.integration.robokassa.service.RobokassaService;
import express.atc.backend.mapper.TrackingMapper;
import express.atc.backend.mapper.TrackingRouteMapper;
import express.atc.backend.service.MessageService;
import express.atc.backend.service.StatusService;
import express.atc.backend.service.TrackingService;
import express.atc.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static express.atc.backend.Constants.*;
import static express.atc.backend.enums.TrackingStatus.ACTIVE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final CargoflowService cargoflowService;
    private final TrackingRepository trackingRepository;
    private final TrackingRouteRepository trackingRouteRepository;
    private final TrackingMapper trackingMapper;
    private final TrackingRouteMapper trackingRouterMapper;
    private final CalcCustomsFee calcCustomsFee;
    private final UserService userService;
    private final RobokassaService robokassaService;
    private final StatusService statusService;
    private final MessageService messageService;

    @Override
    public TrackingDto find(String trackNumber, String userPhone) throws TrackNotFoundException {
        UserDto user = userService.findUserByPhone(userPhone);
        var dto = findTrack(trackNumber);
        if (user == null || !dto.getPhone().equals(user.getPhone())) {
            dto.setGoods(null);
        }
        return dto;
    }

    @Override
    public CalculateDto calc(String trackNumber, String userPhone) throws TrackNotFoundException {
        UserDto user = userService.findUserByPhone(userPhone);
        var dto = findTrack(trackNumber);
        return calcTrack(dto.getGoods(), dto.getOrderId(), user);
    }

    @Override
    public TrackingPageDto list(Integer page, int count, String userPhone, TrackingStatus filter) {
        updateListTracking(userPhone);
        Pageable pageable = PageRequest.of(page, count);
        return new TrackingPageDto(
                findAndFilterList(userPhone, pageable, filter)
                        .stream()
                        .map(this::updateRoute)
                        .map(trackingMapper::toDto)
                        .toList(),
                page,
                (int) Math.ceil(trackingRepository.countByUserPhone(userPhone) / (double) count),
                count,
                need(userPhone)
        );
    }

    public boolean uploadFile(MultipartFile file, String trackNumber) {
        var logisticsOrderCode = trackingRepository.findByTrackNumber(trackNumber)
                .orElseThrow(() -> new ApiException(ORDER_NOT_FOUND, HttpStatus.BAD_REQUEST))
                .getLogisticsOrderCode();
        cargoflowService.uploadFile(file, logisticsOrderCode);
        return true;
    }

    public void updateListTracking(String userPhone) {
        var list = cargoflowService.getSetInfoByPhone(userPhone).stream();
        Long maxOrderId = trackingRepository.getMaxOrderIdByUserPhone(userPhone);
        if (maxOrderId != null) {
            list = list.filter(t -> t.getOrderId() > maxOrderId);
        }
        trackingRepository.saveAll(list
                .map(trackingMapper::toEntity)
                .toList()
        );
        findAndFilterList(userPhone, null, null).forEach(this::updateRoute);
    }

    @Override
    public boolean paymentConfirmation(Long orderId, String userPhone) {
        var entity = trackingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiException(ORDER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        if (userPhone.equals(entity.getUserPhone()) && TrackingStatus.NEED_PAYMENT.equals(entity.getStatus())) {
            entity.setStatus(TrackingStatus.PAYMENT_CONFIRMATION);
            return true;
        }
        return false;
    }

    @Override
    public Set<TrackingDto> getAllTrackByPhone(String userPhone) {
        updateListTracking(userPhone);
        return cargoflowService.getSetInfoByPhone(userPhone);
    }

    @Override
    public TrackingNeedingDto need(String userPhone) {
        List<String> needPay = trackingRepository.findTrackNumberByNeed(userPhone, TrackingStatus.NEED_PAYMENT);
        List<String> needDocument = trackingRepository.findTrackNumberByNeed(userPhone, TrackingStatus.NEED_DOCUMENT);
        return new TrackingNeedingDto(needPay, needDocument);
    }

    @Override
    @Transactional
    public void updateByLogisticCode(String logisticsOrderCode) {
        var entity = trackingRepository.findByLogisticsOrderCode(logisticsOrderCode)
                .orElseGet(() -> {
                    var dto = cargoflowService.getInfoByLogisticsOrderCode(logisticsOrderCode).getFirst();
                    if (dto == null) {
                        log.error("{} logisticsOrderCode: {}", ORDER_NOT_FOUND, logisticsOrderCode);
                        throw new ApiException(ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);
                    }
                    return trackingRepository.save(trackingMapper.toEntity(dto).setUserPhone(dto.getPhone()));
                });
        updateRoute(entity);
    }

    private TrackingDto findTrack(String trackNumber) throws TrackNotFoundException {
        Optional<TrackingEntity> entity = trackingRepository.findByTrackNumber(trackNumber);
        return trackingMapper.toDto(
                entity
                        .map(this::updateRoute)
                        .orElseGet(() -> findByCargoFlow(trackNumber))
        );
    }

    private CalculateDto calcTrack(OrdersDto goods, long OrderId, UserDto user) {
        var calculate = calcCustomsFee.calculate(goods);
        if (calculate != null) {
            calculate.setUrl(makePaymentUrl(OrderId, calculate, user));
        }
        return calculate;
    }


    private TrackingEntity updateRoute(TrackingEntity entity) {
        var maxRouteId = entity.getRoutes() != null ?
                entity.getRoutes().stream()
                .map(TrackingRouteEntity::getRouteId)
                .max(Long::compareTo)
                        .orElse(null)
                : null;
        var trackingRouters =
                cargoflowService.updateRoute(entity.getOrderId(), maxRouteId)
                        .stream()
                        .filter(Objects::nonNull)
                        .map(trackingRouterMapper::toEntity)
                        .map(trackingRoute -> trackingRoute.setTracking(entity))
                        .map(trackingRouteRepository::save)
                        .collect(Collectors.toSet());
        if (entity.getRoutes() != null) {
            entity.getRoutes().addAll(trackingRouters);
        } else {
            entity.setRoutes(trackingRouters);
        }
        var lastRoute = new TreeSet<>(entity.getRoutes()).last();
        if (!Objects.equals(maxRouteId, lastRoute.getRouteId())) {
            entity.setStatus(statusService.getStatus(lastRoute.getStatus()).mapStatus());
            if (entity.getStatus().equals(TrackingStatus.NEED_PAYMENT)) {
                messageService.send(entity.getUserPhone(), String.format(SMS_NEED_PAYMENT, entity.getTrackNumber()));
            }
            if (entity.getStatus().equals(TrackingStatus.NEED_DOCUMENT)) {
                messageService.send(entity.getUserPhone(), String.format(SMS_NEED_DOCUMENT, entity.getTrackNumber()));
            }
            trackingRepository.save(entity);
        }
        return entity;
    }

    private TrackingEntity findByCargoFlow(String trackNumber) throws TrackNotFoundException {
        var dto = getInfoByTrackNumber(trackNumber).orElseThrow(TrackNotFoundException::new);
        var trackingEntity = trackingRepository.save(trackingMapper.toEntity(dto).setRoutes(null));
        updateRoute(trackingEntity);
        return trackingEntity;
    }

    private Optional<TrackingDto> getInfoByTrackNumber(String trackNumber) {
        List<TrackingDto> trackingDtoList = cargoflowService.getInfoByTrackNumber(trackNumber);
        if (CollectionUtils.isNotEmpty(trackingDtoList)) {
            return Optional.of(trackingDtoList.getFirst());
        } else {
            return Optional.empty();
        }
    }

    private String makePaymentUrl(Long orderId, CalculateDto calculate, UserDto user) {
        List<PaymentItemDto> items = new ArrayList<>();
        items.add(PaymentItemDto.builder()
                .name(STRING_FEE)
                .quantity(1L)
                .amount(calculate.getFee())
                .build());
        items.add(PaymentItemDto.builder()
                .name(STRING_TAX)
                .quantity(1L)
                .amount(calculate.getTax())
                .build());
        items.add(PaymentItemDto.builder()
                .name(STRING_COMPENSATION)
                .quantity(1L)
                .amount(calculate.getCompensation())
                .build());
        var payment = PaymentDto.builder()
                .orderId(orderId)
                .items(items)
                .email(user.getEmail())
                .amount(items.stream()
                        .map(PaymentItemDto::getAmount)
                        .reduce(0L, Long::sum)
                )
                .build();
        return robokassaService.makePaymentUrl(payment);
    }

    private List<TrackingEntity> findAndFilterList(String userPhone, Pageable pageable, TrackingStatus filter) {
        if (filter == null || ACTIVE.equals(filter)) {
            return trackingRepository.findAllByUserPhoneAndStatusNot(userPhone, TrackingStatus.ARCHIVE, pageable);
        }
        return trackingRepository.findAllByUserPhoneAndStatus(userPhone, filter, pageable);
    }
}
