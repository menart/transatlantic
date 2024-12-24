package express.atc.backend.integration.cargoflow.service.impl;

import express.atc.backend.dto.TrackingDto;
import express.atc.backend.dto.TrackingRouteDto;
import express.atc.backend.exception.ApiException;
import express.atc.backend.integration.cargoflow.dto.CargoflowOrder;
import express.atc.backend.integration.cargoflow.dto.ConditionDto;
import express.atc.backend.integration.cargoflow.dto.FileAttachDto;
import express.atc.backend.integration.cargoflow.dto.OrderHistory;
import express.atc.backend.integration.cargoflow.mapper.CargoflowMapper;
import express.atc.backend.integration.cargoflow.service.CargoflowClient;
import express.atc.backend.integration.cargoflow.service.CargoflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static express.atc.backend.integration.cargoflow.CargoflowConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CargoflowServiceImpl implements CargoflowService {

    @Value("${cargoflow.entity.order}")
    private String orderEntity;
    @Value("${cargoflow.entity.order_history}")
    private String orderHistoryEntity;
    private final CargoflowMapper cargoflowMapper;
    private final CargoflowClient cargoflowClient;

    @Override
    public List<TrackingDto> getInfoByTrackNumber(String trackNumber) {
        return getTrackingInfoFromCargoflow(trackNumber);
    }

    @Override
    public TreeSet<TrackingDto> getSetInfoByPhone(String userPhone) {
        return cargoflowClient.getFromCargoflowListOrders(userPhone).stream()
                .map(cargoflowMapper::toTracking)
                .map(trackingDto -> trackingDto.setRoutes(new TreeSet<>(getRoute(trackingDto.getOrderId()))))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private List<TrackingDto> getTrackingInfoFromCargoflow(String trackNumber) {
        var condition = new ConditionDto(CONDITION_ORDER_PROPERTY, CONDITION_OPERATOR_EQ, trackNumber);
        var cargoflowOrders = cargoflowClient.getFromCargoflowEntity(List.of(condition), orderEntity, CargoflowOrder.class);
        log.info("{}", cargoflowOrders);
        return cargoflowOrders.stream()
                .map(cargoflowMapper::toTracking)
                .map(trackingDto ->
                        trackingDto.setRoutes(new TreeSet<>(getRoute(trackingDto.getOrderId()))))
                .toList();
    }

    public Set<TrackingRouteDto> updateRoute(Long orderId, Long orderHistoryId) {
        List<OrderHistory> routes = getTrackingRouteInfoFromCargoflow(orderId, orderHistoryId);
        if (CollectionUtils.isNotEmpty(routes)) {
            return routes.stream()
                    .map(cargoflowMapper::toRoutes)
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    @Override
    public void uploadFile(MultipartFile file, String logisticsOrderCode) {
        try {
            var fileDto = cargoflowClient.uploadFileToCargoflow(file.getName(), file.getResource());
            cargoflowClient.attachFileToCargoflow(new FileAttachDto(logisticsOrderCode, fileDto.id()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiException(UPLOAD_ERROR, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private Set<TrackingRouteDto> getRoute(Long orderId) {
        return updateRoute(orderId, null);
    }

    private List<OrderHistory> getTrackingRouteInfoFromCargoflow(Long orderId, Long orderHistoryId) {
        var condition = new ArrayList<ConditionDto>();
        condition.add(new ConditionDto(CONDITION_ROUTE_PROPERTY, CONDITION_OPERATOR_EQ, orderId.toString()));
        if (orderHistoryId != null) {
            condition.add(new ConditionDto(CONDITION_ROUTE_ID_PROPERTY, CONDITION_OPERATOR_GT, orderHistoryId.toString()));
        }
        var listRoutes = cargoflowClient.getFromCargoflowEntity(condition, orderHistoryEntity, OrderHistory.class);
        log.info("{}", listRoutes);
        return listRoutes;
    }
}
