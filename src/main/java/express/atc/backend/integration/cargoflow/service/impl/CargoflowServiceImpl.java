package express.atc.backend.integration.cargoflow.service.impl;

import express.atc.backend.dto.TrackingDto;
import express.atc.backend.integration.cargoflow.dto.CargoflowOrder;
import express.atc.backend.integration.cargoflow.dto.ConditionDto;
import express.atc.backend.integration.cargoflow.dto.FilterDto;
import express.atc.backend.integration.cargoflow.dto.RequestOrderDto;
import express.atc.backend.integration.cargoflow.mapper.CargoflowMapper;
import express.atc.backend.integration.cargoflow.service.CargoflowAuthService;
import express.atc.backend.integration.cargoflow.service.CargoflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CargoflowServiceImpl implements CargoflowService {

    private final static String CONDITION_PROPERTY = "trackingNumber";
    private final static String CONDITION_OPERATOR = "=";

    private final CargoflowAuthService cargoflowAuthService;
    private final WebClient cargoflowOrderWebClient;
    private final CargoflowMapper cargoflowMapper;

    @Override
    public List<TrackingDto> getInfoByTrackNumber(String trackNumber) {
        return getTrackingInfoFromCargoflow(trackNumber);
    }

    private List<TrackingDto> getTrackingInfoFromCargoflow(String trackNumber) {
        String token = cargoflowAuthService.getToken();
        var condition = new ConditionDto(CONDITION_PROPERTY, CONDITION_OPERATOR, trackNumber);
        var request = new RequestOrderDto(new FilterDto(List.of(condition)), "_local");
        var cargoflowOrders = cargoflowOrderWebClient
                .post()
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(CargoflowOrder.class)
                .collectList()
                .block();
        log.info("{}", cargoflowOrders);
        return cargoflowOrders.stream()
                .map(cargoflowMapper::toTracking)
                .toList();
    }
}
