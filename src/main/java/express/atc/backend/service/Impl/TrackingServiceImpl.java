package express.atc.backend.service.Impl;

import express.atc.backend.calculate.CalcCustomsFee;
import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.db.entity.TrackingRouteEntity;
import express.atc.backend.db.repository.TrackingRepository;
import express.atc.backend.db.repository.TrackingRouteRepository;
import express.atc.backend.dto.TrackingDto;
import express.atc.backend.exception.BadRequestException;
import express.atc.backend.exception.TrackNotFoundException;
import express.atc.backend.integration.cargoflow.service.CargoflowService;
import express.atc.backend.mapper.TrackingMapper;
import express.atc.backend.mapper.TrackingRouteMapper;
import express.atc.backend.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public TrackingDto find(String trackNumber, String userPhone) throws TrackNotFoundException {
        Optional<TrackingEntity> entity = trackingRepository.findByTrackNumber(trackNumber);
        TrackingDto dto = trackingMapper.toDto(entity.isEmpty()
                ? findByCargoFlow(trackNumber)
                : updateRoute(entity.get()));
        if (!dto.getPhone().equals(userPhone)) {
            dto.setGoods(null);
        } else {
            try {
                dto.setCalculate(calcCustomsFee.calculate(dto.getGoods()));
            } catch (BadRequestException exception) {
                log.error(exception.getMessage());
            }
        }
        return dto;
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
}
