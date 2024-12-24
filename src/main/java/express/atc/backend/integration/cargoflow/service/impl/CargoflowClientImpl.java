package express.atc.backend.integration.cargoflow.service.impl;

import express.atc.backend.exception.ApiException;
import express.atc.backend.exception.CargoflowException;
import express.atc.backend.integration.cargoflow.dto.*;
import express.atc.backend.integration.cargoflow.service.CargoflowClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static express.atc.backend.integration.cargoflow.CargoflowConstants.UPLOAD_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class CargoflowClientImpl implements CargoflowClient {

    private final WebClient cargoflowEntityWebClient;
    private final WebClient cargoflowUploadWebClient;
    private final WebClient cargoflowAttachWebClient;
    private final WebClient cargoflowListOrderWebClient;
    private static final String CARGOFLOW_GRAND_TYPE = "password";
    private final WebClient cargoflowAuthWebClient;
    @Value("${cargoflow.password}")
    private String cargoflowPassword;
    @Value("${cargoflow.username}")
    private String cargoflowUsername;
    private CargoflowToken cargoflowToken;

    @Override
    public <T> List<T> getFromCargoflowEntity(List<ConditionDto> condition, String entity, Class<T> response) {
        var request = new RequestDto(new FilterDto(condition), "_local");
        var responseList = cargoflowEntityWebClient
                .post()
                .uri(uriBuilder -> uriBuilder.build(entity))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(getToken()))
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(response)
                .collectList()
                .block();
        log.info("{}", responseList);
        return responseList;
    }

    @Override
    public FileDto uploadFileToCargoflow(String fileName, Resource file) {
        var response = cargoflowUploadWebClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder.queryParam("name", fileName).build())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(getToken()))
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

    @Override
    public void attachFileToCargoflow(FileAttachDto attach) {
        var response = cargoflowAttachWebClient
                .post()
                .headers(httpHeaders -> httpHeaders.setBearerAuth(getToken()))
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

    @Override
    public List<CargoflowOrder> getFromCargoflowListOrders(String phoneNumber) {
        var responseList = cargoflowListOrderWebClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.queryParam("phoneNumber", phoneNumber)
                                .build())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(getToken()))
                .retrieve()
                .bodyToFlux(CargoflowOrder.class)
                .collectList()
                .block();
        log.info("{}", responseList);
        return responseList;
    }

    private CargoflowToken getNewToken() {
        try {
            var cargoflowToken = cargoflowAuthWebClient
                    .post()
                    .body(BodyInserters.fromFormData("grant_type", CARGOFLOW_GRAND_TYPE)
                            .with("username", cargoflowUsername)
                            .with("password", cargoflowPassword))
                    .retrieve()
                    .bodyToMono(CargoflowToken.class)
                    .block();
            if (Objects.isNull(cargoflowToken)) {
                throw new CargoflowException("Не удалось авторизоваться в Cargoflow");
            }
            cargoflowToken.setExpiresDateTime(LocalDateTime.now().plusSeconds(cargoflowToken.getExpiresIn()));
            return cargoflowToken;
        } catch (Exception exception) {
            throw new CargoflowException(exception.getMessage());
        }
    }

    private String getToken() {
        if (cargoflowToken == null
                || LocalDateTime.now().isAfter(cargoflowToken.getExpiresDateTime())) {
            cargoflowToken = getNewToken();
        }
        return cargoflowToken.getAccessToken();
    }
}
