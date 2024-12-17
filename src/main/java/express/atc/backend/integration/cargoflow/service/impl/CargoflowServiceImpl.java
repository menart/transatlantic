package express.atc.backend.integration.cargoflow.service.impl;

import express.atc.backend.dto.TrackingDto;
import express.atc.backend.dto.TrackingRouteDto;
import express.atc.backend.exception.ApiException;
import express.atc.backend.integration.cargoflow.dto.*;
import express.atc.backend.integration.cargoflow.mapper.CargoflowMapper;
import express.atc.backend.integration.cargoflow.service.CargoflowAuthService;
import express.atc.backend.integration.cargoflow.service.CargoflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

import static express.atc.backend.integration.cargoflow.CargoflowConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CargoflowServiceImpl implements CargoflowService {

    private final WebClient cargoflowEntityWebClient;
    private final WebClient cargoflowUploadWebClient;
    private final WebClient cargoflowAttachWebClient;
    @Value("${cargoflow.entity.order}")
    private String orderEntity;
    @Value("${cargoflow.entity.order_history}")
    private String orderHistoryEntity;
    private final CargoflowAuthService cargoflowAuthService;
    private final CargoflowMapper cargoflowMapper;

    @Override
    public List<TrackingDto> getInfoByTrackNumber(String trackNumber) {
        return getTrackingInfoFromCargoflow(trackNumber);
    }

    private List<TrackingDto> getTrackingInfoFromCargoflow(String trackNumber) {
        var condition = new ConditionDto(CONDITION_ORDER_PROPERTY, CONDITION_OPERATOR_EQ, trackNumber);
        var cargoflowOrders = getFromCargoflowEntity(List.of(condition), orderEntity, CargoflowOrder.class);
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
            var fileDto = uploadFileToCargoflow(file.getName(), file.getResource());
            attachFileToCargoflow(new FileAttachDto(logisticsOrderCode, fileDto.id()));
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
        var listRoutes = getFromCargoflowEntity(condition, orderHistoryEntity, OrderHistory.class);
        log.info("{}", listRoutes);
        return listRoutes;
    }

    private <T> List<T> getFromCargoflowEntity(List<ConditionDto> condition, String entity, Class<T> response) {
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

    private FileDto uploadFileToCargoflow(String fileName, Resource file) {
        String token = cargoflowAuthService.getToken();
        var response = cargoflowUploadWebClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder.queryParam("name", fileName).build())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .body(BodyInserters.fromResource(file))
                .exchangeToMono(
                        clientResponse -> {
                            if (clientResponse.statusCode().is2xxSuccessful()) {
                                return clientResponse.bodyToMono(FileDto.class);
                            } else {
                                throw new ApiException(UPLOAD_ERROR, HttpStatus.SERVICE_UNAVAILABLE);
                            }
                        }
                )
                .block();
        log.info("{}", response);
        return response;
    }

    private void attachFileToCargoflow(FileAttachDto attach) {
        String token = cargoflowAuthService.getToken();
        var response = cargoflowAttachWebClient
                .post()
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .bodyValue(attach)
                .exchangeToMono(
                        clientResponse -> {
                            if (clientResponse.statusCode().is2xxSuccessful()) {
                                return clientResponse.bodyToMono(String.class);
                            } else {
                                throw new ApiException(UPLOAD_ERROR, HttpStatus.SERVICE_UNAVAILABLE);
                            }
                        }
                )
                .block();
        log.info("{}", response);
    }

}
