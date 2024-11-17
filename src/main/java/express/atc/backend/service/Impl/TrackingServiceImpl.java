package express.atc.backend.service.Impl;

import express.atc.backend.calculate.CalcCustomsFee;
import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.db.entity.TrackingRouteEntity;
import express.atc.backend.db.repository.TrackingRepository;
import express.atc.backend.db.repository.TrackingRouteRepository;
import express.atc.backend.dto.*;
import express.atc.backend.exception.TrackNotFoundException;
import express.atc.backend.integration.cargoflow.service.CargoflowService;
import express.atc.backend.integration.robokassa.service.RobokassaService;
import express.atc.backend.mapper.TrackingMapper;
import express.atc.backend.mapper.TrackingRouteMapper;
import express.atc.backend.service.TrackingService;
import express.atc.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static express.atc.backend.Constants.*;

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

    @Override
    public TrackingDto find(String trackNumber, String userPhone) throws TrackNotFoundException {
        UserDto user = userService.findUserByPhone(userPhone);
        var dto = findTrack(trackNumber);
        if (user == null || !dto.getPhone().equals(user.getPhone())) {
            dto.setGoods(null);
        } else {
            dto.setCalculate(calcTrack(dto.getGoods(), dto.getOrderId(), user));
        }
        return dto;
    }

    @Override
    public CalculateDto calc(String trackNumber, String userPhone) throws TrackNotFoundException {
        UserDto user = userService.findUserByPhone(userPhone);
        var dto = findTrack(trackNumber);
        return calcTrack(dto.getGoods(), dto.getOrderId(), user);
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
        var maxRouteId = entity.getRoutes().stream()
                .map(TrackingRouteEntity::getRouteId)
                .max(Long::compareTo)
                .orElse(null);
        var trackingRouters =
                cargoflowService.updateRoute(entity.getOrderId(), maxRouteId)
                        .stream()
                        .filter(Objects::nonNull)
                        .map(trackingRouterMapper::toEntity)
                        .map(trackingRoute -> trackingRoute.setTracking(entity))
                        .map(trackingRouteRepository::save)
                        .collect(Collectors.toSet());
        entity.getRoutes().addAll(trackingRouters);
        return entity;
    }

    private TrackingEntity findByCargoFlow(String trackNumber) throws TrackNotFoundException {
        var dto = getInfoByTrackNumber(trackNumber).orElseThrow(TrackNotFoundException::new);
        var trackingEntity = trackingRepository.save(trackingMapper.toEntity(dto).setRoutes(null));
        trackingEntity.setRoutes(dto.getRoutes().stream()
                .map(trackingRouterMapper::toEntity)
                .map(trackingRoute -> trackingRoute.setTracking(trackingEntity))
                .map(trackingRouteRepository::save)
                .collect(Collectors.toSet())
        );
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
}
