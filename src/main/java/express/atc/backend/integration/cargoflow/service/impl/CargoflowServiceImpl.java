package express.atc.backend.integration.cargoflow.service.impl;

import express.atc.backend.dto.TrackingDto;
import express.atc.backend.dto.TrackingRouteDto;
import express.atc.backend.integration.cargoflow.dto.*;
import express.atc.backend.integration.cargoflow.mapper.CargoflowMapper;
import express.atc.backend.integration.cargoflow.service.CargoflowAuthService;
import express.atc.backend.integration.cargoflow.service.CargoflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CargoflowServiceImpl implements CargoflowService {

    private final static String CONDITION_ORDER_PROPERTY = "trackingNumber";
    private final static String CONDITION_ROUTE_PROPERTY = "order";
    private final static String CONDITION_ROUTE_ID_PROPERTY = "id";
    private final static String CONDITION_OPERATOR_EQ = "=";
    private final static String CONDITION_OPERATOR_GT = ">";
    private final WebClient cargoflowEntityWebClient;
    @Value("${cargoflow.entity.order}")
    private String orderEntity;

    private final CargoflowAuthService cargoflowAuthService;
    @Value("${cargoflow.entity.order_history}")
    private String orderHistoryEntity;
    private final CargoflowMapper cargoflowMapper;

    @Override
    public List<TrackingDto> getInfoByTrackNumber(String trackNumber) {
        return getTrackingInfoFromCargoflow(trackNumber);
    }

    private List<TrackingDto> getTrackingInfoFromCargoflow(String trackNumber) {
        var condition = new ConditionDto(CONDITION_ORDER_PROPERTY, CONDITION_OPERATOR_EQ, trackNumber);
        var cargoflowOrders = getFromCargoflow(List.of(condition), orderEntity, CargoflowOrder.class);
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

    private Set<TrackingRouteDto> getRoute(Long orderId) {
        return updateRoute(orderId, null);
    }

    private List<OrderHistory> getTrackingRouteInfoFromCargoflow(Long orderId, Long orderHistoryId) {
        var condition = new ArrayList<ConditionDto>();
        condition.add(new ConditionDto(CONDITION_ROUTE_PROPERTY, CONDITION_OPERATOR_EQ, orderId.toString()));
        if (orderHistoryId != null) {
            condition.add(new ConditionDto(CONDITION_ROUTE_ID_PROPERTY, CONDITION_OPERATOR_GT, orderHistoryId.toString()));
        }
        var listRoutes = getFromCargoflow(condition, orderHistoryEntity, OrderHistory.class);
        log.info("{}", listRoutes);
        return listRoutes;
    }

    private <T> List<T> getFromCargoflow(List<ConditionDto> condition, String entity, Class<T> response) {
        String token = cargoflowAuthService.getToken();
        var request = new RequestDto(new FilterDto(condition), "_local");
        var responseList = cargoflowEntityWebClient
                .post()
                .uri(uriBuilder -> uriBuilder.build(entity))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(response)
                .collectList()
                .block();
        log.info("{}", responseList);
        return responseList;
    }
}
