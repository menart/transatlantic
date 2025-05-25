package express.atc.backend.integration.cargoflow.service.impl;

import express.atc.backend.exception.ApiException;
import express.atc.backend.integration.cargoflow.dto.*;
import express.atc.backend.integration.cargoflow.service.CargoflowClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

import static express.atc.backend.integration.cargoflow.CargoflowConstants.UPLOAD_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class CargoflowClientImpl implements CargoflowClient {

    private final WebClient cargoflowEntityWebClient;
    private final WebClient cargoflowUploadWebClient;
    private final WebClient cargoflowAttachWebClient;
    private final WebClient cargoflowListOrderWebClient;

    @Override
    public <T> List<T> getFromCargoflowEntity(List<ConditionDto> condition, String entity, Class<T> response) {
        var request = new RequestDto(new FilterDto(condition), "_local");
        var responseList = cargoflowEntityWebClient
                .post()
                .uri(uriBuilder -> uriBuilder.build(entity))
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(response)
                .collectList()
                .block();
        log.info("{}", responseList);
        return responseList;
    }

    @Override
    public FileDto uploadFileToCargoflow(MultipartFile file) {
        // Проверяем что файл не пустой
        if (file.isEmpty()) {
            throw new ApiException("File is empty", HttpStatus.BAD_REQUEST);
        }

        // Строим multipart запрос согласно спецификации CUBA
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource())
                .filename(file.getOriginalFilename())
                .contentType(MediaType.parseMediaType(file.getContentType()));

        // Дополнительные параметры если требуются спецификацией
        builder.part("name", file.getOriginalFilename()); // Если нужно явно передавать имя отдельным полем

        return cargoflowUploadWebClient.post()
                .uri(UriBuilder::build)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .onStatus(
                        // Исправлено для Spring 6+
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ApiException(
                                        "File upload failed: " + error,
                                        HttpStatus.valueOf(response.statusCode().value())
                                )))
                )
                .bodyToMono(FileDto.class)
                .block();
    }

    @Override
    public void attachFileToCargoflow(FileAttachDto attach) {
        var response = cargoflowAttachWebClient
                .post()
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
                .retrieve()
                .bodyToFlux(CargoflowOrder.class)
                .collectList()
                .block();
        log.info("{}", responseList);
        return responseList;
    }
}
