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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static express.atc.backend.Constants.*;
import static express.atc.backend.enums.TrackingStatus.*;
import static express.atc.backend.integration.cfapi.enums.OrderStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    @Value("#{'${service.tracking.work-provider-ids:}'.split(',')}")
    private List<String> workProviderIds = Collections.emptyList();

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
        return calcTrack(dto.getGoods(), dto.getOrderId(), dto.getOrderNumber(), user);
    }

    @Override
    public TrackingPageDto list(Integer page, int count, TrackingStatus filter) {
        String userPhone = requestInfo.getUser().getPhone();
        log.info("get list for user {}: page: {}, count: {}, status: {}", userPhone, page, count, filter);
        Pageable pageable = PageRequest.of(page, count);
        return new TrackingPageDto(
                findAndFilterList(userPhone, pageable, filter)
                        .stream()
                        .map(this::updateRoute)
                        .map(trackingMapper::toDto)
                        .toList(),
                page,
                (int) Math.ceil(countFilterList(userPhone, filter) / (double) count),
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

    @Async
    public void updateListTracking(String userPhone) {
        var list = cargoflowService.getSetInfoByPhone(userPhone).stream();
        Long maxOrderId = trackingRepository.getMaxOrderIdByUserPhone(userPhone);
        if (maxOrderId != null) {
            list = list.filter(t -> t.getOrderId() > maxOrderId);
        }
        var listEntity = list
                .map(this::mapToEntity)
                .toList();
        trackingRepository.saveAll(listEntity);
        findAndFilterList(userPhone, null, ACTIVE).forEach(this::updateRoute);
    }

    private TrackingEntity mapToEntity(TrackingDto dto) {
        return trackingRepository.findByOrderId(dto.getOrderId()).orElse(trackingMapper.toEntity(dto));
    }

    private TrackingRouteEntity mapRouteToEntity(TrackingRouteDto dto) {
        return trackingRouteRepository.findByRouteId(dto.getRouteId())
                .orElse(trackingRouterMapper.toEntity(dto));
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
    public String paymentControl(String outSum, Long orderId, String orderNumber, String checkSum) {
        outSum = outSum.replace(",", ".");
        var entity = trackingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiException(ORDER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        var calc = calcTrack(entity.getGoods(), entity.getOrderId(), orderNumber, null);
        log.info("payment dto,: {}", calc);
        log.info("payment parameters,: {}, {}, {}, {}", Double.parseDouble(outSum) * 100L, orderId, orderNumber, checkSum);
        try {
            if (!calc.getPaid().equals(Math.round(Double.parseDouble(outSum) * 100L))) {
                throw new ApiException(PAYMENT_NOT_EQUALS, HttpStatus.BAD_REQUEST);
            }
        } catch (NumberFormatException exception) {
            log.error("{}", exception.getMessage(), exception);
            throw new ApiException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        String request = robokassaService.paymentResult(outSum, orderId, orderNumber, checkSum);
        if (cfApiService.changeStatusToCargoflow(entity.getTrackNumber(), CUSTOMS_FEE_PAID)) {
            updateRoute(entity);
        }
        return request;
    }

    @Override
    public Boolean setToArchive() {
        trackingRepository.findAll().stream().parallel()
                .forEach(entity ->
                        cfApiService.changeStatusToCargoflow(entity.getTrackNumber(), LASTMILE_DELIVERED)
                );
        return true;
    }

    @Override
    public Boolean getAllTrackByPhone() {
        try {
            updateListTracking(requestInfo.getUser().getPhone());
        } catch (Error e) {
            log.error("get list for user {}: error: ", requestInfo.getUser().getPhone(), e);
        }
        return true;
    }

    @Override
    public TrackingNeedingDto need() {
        String userPhone = requestInfo.getUser().getPhone();
        List<String> needPay = trackingRepository.findOrderNumberByNeed(userPhone, NEED_PAYMENT);
        List<String> needDocument = trackingRepository.findOrderNumberByNeed(userPhone, FIRST_NEED_DOCUMENT);
        needDocument.addAll(trackingRepository.findOrderNumberByNeed(userPhone, NEED_DOCUMENT));
        return new TrackingNeedingDto(needPay, needDocument);
    }

    @Override
    @Transactional
    public void updateByOrderCode(String orderCode) {
        var entity = trackingRepository.findByTrack(orderCode)
                .orElseGet(() -> trackingRepository.save(findByCargoFlow(orderCode)));
        updateRoute(entity);
    }

    public void setStatusFirstNeedDocument(String logisticsOrderCode) {
        var entity = trackingRepository.findByTrack(logisticsOrderCode)
                .orElse(trackingRepository.save(findByCargoFlow(logisticsOrderCode)));
        updateRoute(entity);
        entity.setStatus(FIRST_NEED_DOCUMENT);
        entity.setFlagNeedDocument(true);
        messageFacade.sendTrackingInfo(
                entity.getUserPhone(),
                entity.getStatus(),
                entity.getOrderNumber(),
                entity.getMarketplace());
        trackingRepository.save(entity);
    }

    private TrackingDto findTrack(String number) throws TrackNotFoundException {
        Optional<TrackingEntity> entity = trackingRepository.findByTrack(number);
        return trackingMapper.toDto(
                entity
                        .map(this::updateRoute)
                        .orElseGet(() -> findByCargoFlow(number))
        );
    }

    private CalculateDto calcTrack(OrdersDto goods, long OrderId, String orderNumber, UserDto user) {
        var calculate = calcCustomsFee.calculate(goods);
        if (calculate != null && user != null) {
            calculate.setUrl(makePaymentUrl(OrderId, calculate, orderNumber, user));
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
                        .map(this::mapRouteToEntity)
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
            var status = statusService.getStatus(lastRoute.getStatus()).mapStatus();
            switch (status) {
                case NEED_PAYMENT -> {
                    if (workProviderIds.contains(entity.getProviderId())) {
                        entity.setStatus(NEED_PAYMENT);
                    }
                }
                case NEED_DOCUMENT -> {
                    if (entity.getFlagNeedDocument()) {
                        entity.setStatus(NEED_DOCUMENT);
                    }
                }
                default -> entity.setStatus(status);
            }
            trackingRepository.save(entity);
            messageFacade.sendTrackingInfo(
                    entity.getUserPhone(),
                    entity.getStatus(),
                    entity.getOrderNumber(),
                    entity.getMarketplace());
        }
        return entity;
    }

    private TrackingEntity findByCargoFlow(String number) throws TrackNotFoundException {
        var dto = getInfoByTrackNumberOrOrderNumber(number).orElseThrow(TrackNotFoundException::new);
        var trackingEntity = trackingRepository.save(mapToEntity(dto));
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

    private String makePaymentUrl(Long orderId, CalculateDto calculate, String orderNumber, UserDto user) {
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
                .orderNumber(orderNumber)
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
        if (NEED_DOCUMENT.equals(filter) || FIRST_NEED_DOCUMENT.equals(filter)) {
            var list = trackingRepository.findAllByUserPhoneAndStatus(userPhone, FIRST_NEED_DOCUMENT, pageable);
            list.addAll(trackingRepository.findAllByUserPhoneAndStatus(userPhone, NEED_DOCUMENT, pageable));
            return list;
        }
        return trackingRepository.findAllByUserPhoneAndStatus(userPhone, filter, pageable);
    }

    private int countFilterList(String userPhone, TrackingStatus filter) {
        if (filter == null || ACTIVE.equals(filter)) {
            return trackingRepository.getCountByUserPhoneAndStatusNot(userPhone, TrackingStatus.ARCHIVE);
        }
        return trackingRepository.getCountByUserPhoneAndStatus(userPhone, filter);
    }
}
