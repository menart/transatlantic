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
import express.atc.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static express.atc.backend.Constants.*;
import static express.atc.backend.enums.TrackingStatus.*;
import static express.atc.backend.integration.cfapi.enums.OrderStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final UserService userService;
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

    @Override
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
    public TrackingAdminDto findByAdmin(String number) {
        var dto = findTrack(number);
        var user = userService.findUserByPhone(dto.getPhone());
        var userPhone = user.getPhone();
        return new TrackingAdminDto(
                dto,
                user,
                userPhone
        );
    }

    @Override
    @Async
    public void sendUserInfo(UserDto responseUser) {
        trackingRepository.findAllByUserPhoneAndStatus(responseUser.getPhone(), FIRST_NEED_DOCUMENT)
                .forEach(trackingEntity ->
                        {
                            try {
                                var isSending = cfApiService.sendPersonalInfo(
                                        trackingEntity.getLogisticsOrderCode(),
                                        responseUser,
                                        trackingEntity.getProvider());
                                if (isSending) {
                                    trackingEntity.setStatus(ACTIVE);
                                    trackingRepository.save(trackingEntity);
                                }
                            } catch (Exception exception) {
                                log.error("{}", exception.getMessage(), exception);
                            }
                        }
                );
    }

    @Override
    public void sendUserInfoBatch(Integer infoSendBatchSize) {
        List<TrackingEntity> list = trackingRepository
                .findByStatusAndOrderUpdateAtLimit(FIRST_NEED_DOCUMENT, infoSendBatchSize);
        list.forEach(trackingEntity -> {
            var user = userService.findUserByPhone(trackingEntity.getUserPhone());
            if (user != null && user.isFull()) {
                try {
                    trackingEntity = updateTrackingEntity(trackingEntity);
                    var isSendPersonalInfo = cfApiService.sendPersonalInfo(
                            trackingEntity.getLogisticsOrderCode(),
                            user,
                            trackingEntity.getProvider());
                    if (isSendPersonalInfo) {
                        trackingEntity.setStatus(ACTIVE);
                    }
                } catch (TrackNotFoundException exception) {
                    log.error("sendUserInfoBatch error {}", exception.getMessage());
                } catch (Exception exception) {
                    log.error("{}", exception.getMessage(), exception);
                }
            }
            trackingEntity.setUpdatedAt(LocalDateTime.now());
        });
        trackingRepository.saveAll(list);
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
        List<String> needDocument = trackingRepository.findOrderNumberByNeed(userPhone, NEED_DOCUMENT);
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
        var entityFind = trackingRepository.findByTrack(logisticsOrderCode);
        try {
            var entity = entityFind.map(this::updateTrackingEntity)
                    .orElseGet(() -> trackingRepository.save(findByCargoFlow(logisticsOrderCode)));
            entity.setFlagNeedDocument(true);
            messageFacade.sendTrackingInfo(
                    entity.getUserPhone(),
                    entity.getStatus(),
                    entity.getOrderNumber(),
                    entity.getMarketplace());
            var userDto = userService.findUserByPhone(entity.getUserPhone());
            entity.setFlagNeedDocument(true);
            if (userDto != null && userDto.isFull()) {
                try {
                    cfApiService.sendPersonalInfo(logisticsOrderCode, userDto, entity.getProvider());
                } catch (Error e) {
                    log.error("{}", e.getMessage(), e);
                }
            } else {
                entity.setStatus(FIRST_NEED_DOCUMENT);
                messageFacade.sendTrackingInfo(entity.getUserPhone(),
                        entity.getStatus(), entity.getOrderNumber(), entity.getMarketplace());
            }
            trackingRepository.save(entity);
        } catch (TrackNotFoundException exception) {
            log.error("setStatusFirstNeedDocument error {}", exception.getMessage());
        }
    }

    private TrackingDto findTrack(String number) throws TrackNotFoundException {
        TrackingEntity entity = trackingRepository.findByTrack(number).map(this::updateRoute)
                .map(this::updateRoute)
                .orElseGet(() -> findByCargoFlow(number));
        trackingRepository.save(entity);
        return trackingMapper.toDto(entity);
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
                        .sorted()
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
                    var dto = getInfoByTrackNumberOrOrderNumber(entity.getTrackNumber())
                            .orElseThrow(() -> new ApiException(ORDER_NOT_FOUND, HttpStatus.BAD_REQUEST));
                    entity.setProviderId(dto.getProviderId());
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
        var dto = getInfoByTrackNumberOrOrderNumber(number).orElseThrow(() -> new TrackNotFoundException(number));
        var trackingEntity = trackingRepository.save(mapToEntity(dto));
        updateRoute(trackingEntity);
        return trackingEntity;
    }

    private Optional<TrackingDto> getInfoByTrackNumberOrOrderNumber(String number) {
        return cargoflowService.getInfoByNumber(number).stream().findFirst();
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

    private TrackingEntity updateTrackingEntity(TrackingEntity entity) {
        var dto = getInfoByTrackNumberOrOrderNumber(entity.getTrackNumber())
                .orElseThrow(() -> new TrackNotFoundException(entity.getTrackNumber()));
        trackingMapper.updateEntityFromDto(dto, entity);
        return updateRoute(entity);
    }
}
