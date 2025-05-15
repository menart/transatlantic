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
import express.atc.backend.facade.MessageFacade;
import express.atc.backend.integration.cargoflow.service.CargoflowService;
import express.atc.backend.integration.cfapi.service.CfApiService;
import express.atc.backend.integration.robokassa.service.RobokassaService;
import express.atc.backend.mapper.TrackingMapper;
import express.atc.backend.mapper.TrackingRouteMapper;
import express.atc.backend.service.StatusService;
import express.atc.backend.service.TrackingService;
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
import static express.atc.backend.enums.TrackingStatus.*;
import static express.atc.backend.integration.cfapi.enums.OrderStatus.CUSTOMS_FEE_PAID;
import static express.atc.backend.integration.cfapi.enums.OrderStatus.CUSTOMS_ID_COLLECTED;

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
    private final RequestInfo requestInfo;
    private final RobokassaService robokassaService;
    private final StatusService statusService;
    private final MessageFacade messageFacade;
    private final CfApiService cfApiService;

    @Override
    public TrackingDto find(String number) throws TrackNotFoundException {
        UserDto user = requestInfo.getUser();
        var dto = findTrack(number);
        if (user == null || !dto.getPhone().equals(user.getPhone())) {
            dto.setGoods(null);
        }
        return dto;
    }

    @Override
    public CalculateDto calc(String trackNumber) throws TrackNotFoundException {
        UserDto user = requestInfo.getUser();
        var dto = findTrack(trackNumber);
        return calcTrack(dto.getGoods(), dto.getOrderId(), dto.getTrackNumber(), user);
    }

    @Override
    public TrackingPageDto list(Integer page, int count, TrackingStatus filter) {
        String userPhone = requestInfo.getUser().getPhone();
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
                need()
        );
    }

    public boolean uploadOneFile(MultipartFile file, String trackNumber) {
        var entity = trackingRepository.findByTrack(trackNumber)
                .orElseThrow(() -> new ApiException(ORDER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        cargoflowService.uploadFile(file, entity.getLogisticsOrderCode());
        if (cfApiService.changeStatusToCargoflow(entity.getTrackNumber(), CUSTOMS_ID_COLLECTED)) {
            updateRoute(entity);
        }
        return true;
    }

    @Override
    public boolean uploadFiles(MultipartFile[] files, String trackNumber) {
        var entity = trackingRepository.findByTrack(trackNumber)
                .orElseThrow(() -> new ApiException(ORDER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        for (var file : files) {
            cargoflowService.uploadFile(file, entity.getLogisticsOrderCode());
        }
        if (cfApiService.changeStatusToCargoflow(entity.getTrackNumber(), CUSTOMS_ID_COLLECTED)) {
            updateRoute(entity);
        }
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
    public boolean paymentConfirmation(Long orderId) {
        String userPhone = requestInfo.getUser().getPhone();
        var entity = trackingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiException(ORDER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        if (userPhone.equals(entity.getUserPhone()) && NEED_PAYMENT.equals(entity.getStatus())) {
            entity.setStatus(TrackingStatus.PAYMENT_CONFIRMATION);
            return true;
        }
        return false;
    }

    @Override
    public String paymentControl(String outSum, Long orderId, String trackingNumber, String checkSum) {
        var entity = trackingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiException(ORDER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        var calc = calcTrack(entity.getGoods(), entity.getOrderId(), trackingNumber, null);
        try {
            if (!calc.getPaid().equals((long) (Double.parseDouble(outSum) * 100))) {
                throw new ApiException(PAYMENT_NOT_EQUALS, HttpStatus.BAD_REQUEST);
            }
        } catch (NumberFormatException exception) {
            log.error("{}", exception.fillInStackTrace());
            throw new ApiException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        String request = robokassaService.paymentResult(outSum, orderId, trackingNumber, checkSum);
        if (cfApiService.changeStatusToCargoflow(entity.getTrackNumber(), CUSTOMS_FEE_PAID)) {
            updateRoute(entity);
        }
        return request;
    }

    @Override
    public Set<TrackingDto> getAllTrackByPhone(String userPhone) {
        updateListTracking(userPhone);
        return cargoflowService.getSetInfoByPhone(userPhone);
    }

    @Override
    public TrackingNeedingDto need() {
        String userPhone = requestInfo.getUser().getPhone();
        List<String> needPay = trackingRepository.findTrackNumberByNeed(userPhone, NEED_PAYMENT);
        List<String> needDocument = trackingRepository.findTrackNumberByNeed(userPhone, NEED_DOCUMENT);
        return new TrackingNeedingDto(needPay, needDocument);
    }

    @Override
    @Transactional
    public void updateByOrderCode(String orderCode, String rawStatus) {
        var entity = trackingRepository.findByTrack(orderCode)
                .orElseGet(() -> trackingRepository.save(findByCargoFlow(orderCode)));
        updateRoute(entity);
        var statusModel = statusService.getStatus(rawStatus);
        messageFacade.sendTrackingInfo(
                entity.getUserPhone(),
                entity.setStatus(statusModel != null ? statusModel.mapStatus() : NEED_DOCUMENT).getStatus(),
                entity.getTrackNumber(),
                entity.getMarketplace()
        );
    }

    private TrackingDto findTrack(String number) throws TrackNotFoundException {
        Optional<TrackingEntity> entity = trackingRepository.findByTrack(number);
        return trackingMapper.toDto(
                entity
                        .map(this::updateRoute)
                        .orElseGet(() -> findByCargoFlow(number))
        );
    }

    private CalculateDto calcTrack(OrdersDto goods, long OrderId, String trackingNumber, UserDto user) {
        var calculate = calcCustomsFee.calculate(goods);
        if (calculate != null && user != null) {
            calculate.setUrl(makePaymentUrl(OrderId, calculate, trackingNumber, user));
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
            messageFacade.sendTrackingInfo(
                    entity.getUserPhone(),
                    entity.getStatus(),
                    entity.getTrackNumber(),
                    entity.getMarketplace());
            trackingRepository.save(entity);
        }
        return entity;
    }

    private TrackingEntity findByCargoFlow(String number) throws TrackNotFoundException {
        var dto = getInfoByTrackNumberOrOrderNumber(number).orElseThrow(TrackNotFoundException::new);
        var trackingEntity = trackingRepository.save(trackingMapper.toEntity(dto).setRoutes(null));
        updateRoute(trackingEntity);
        return trackingEntity;
    }

    private Optional<TrackingDto> getInfoByTrackNumberOrOrderNumber(String number) {
        List<TrackingDto> trackingDtoList = cargoflowService.getInfoByNumber(number);
        if (CollectionUtils.isNotEmpty(trackingDtoList)) {
            return Optional.of(trackingDtoList.getFirst());
        } else {
            return Optional.empty();
        }
    }

    private String makePaymentUrl(Long orderId, CalculateDto calculate, String trackingNumber, UserDto user) {
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
                .trackingNumber(trackingNumber)
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
