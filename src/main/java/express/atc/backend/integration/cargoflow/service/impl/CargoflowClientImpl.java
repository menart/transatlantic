package express.atc.backend.integration.cargoflow.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import express.atc.backend.integration.cargoflow.client.*;
import express.atc.backend.integration.cargoflow.dto.*;
import express.atc.backend.integration.cargoflow.enums.CargoflowView;
import express.atc.backend.integration.cargoflow.exception.CargoflowApiException;
import express.atc.backend.integration.cargoflow.service.CargoflowClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CargoflowClientImpl implements CargoflowClient {

    private final CargoflowEntityClient entityClient;
    private final CargoflowUploadClient uploadClient;
    private final CargoflowAttachClient attachClient;
    private final CargoflowListOrderClient listOrderClient;
    private final ObjectMapper objectMapper;

    @Override
    public <T> List<T> getFromCargoflowEntity(List<ConditionDto> condition, String entity, Class<T> response) {
        return getFromCargoflowEntity(condition, entity, CargoflowView.LOCAL, response);
    }

    @Override
    public <T> List<T> getFromCargoflowEntity(List<ConditionDto> condition, String entity,
                                              CargoflowView view, Class<T> responseType) {
        try {
            RequestDto request = new RequestDto(new FilterDto(condition), view.getView());
            log.debug("Sending request to Cargoflow entity: {}, view: {}", entity, view.getView());

            ResponseEntity<List<?>> response = entityClient.getEntity(entity, request, view.getView());
            if(CollectionUtils.isNotEmpty(response.getBody())) {
                List<T> result = objectMapper.convertValue(response.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, responseType));
                log.debug("Received {} items from Cargoflow entity: {}", result.size(), entity);
                return result;
            } else {
                return List.of();
            }
        } catch (Exception e) {
            log.error("Failed to get entity from Cargoflow: {}", e.getMessage(), e);
            throw new CargoflowApiException(
                    "Failed to fetch data from Cargoflow",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }
    }

    @Override
    public FileDto uploadFileToCargoflow(MultipartFile file) {
        try {
            log.debug("Uploading file to Cargoflow: {}", file.getOriginalFilename());
            FileDto result = uploadClient.uploadFile(file, file.getOriginalFilename());
            log.debug("File uploaded successfully: {}", result.id());
            return result;
        } catch (Exception e) {
            log.error("Failed to upload file to Cargoflow: {}", e.getMessage(), e);
            throw new CargoflowApiException(
                    "Failed to upload file to Cargoflow",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }
    }

    @Override
    public void attachFileToCargoflow(FileAttachDto attach) {
        try {
            log.debug("Attaching file to order: {}", attach.logisticsOrderCode());
            attachClient.attachFile(attach);
            log.debug("File attached successfully to order: {}", attach.logisticsOrderCode());
        } catch (Exception e) {
            log.error("Failed to attach file in Cargoflow: {}", e.getMessage(), e);
            throw new CargoflowApiException(
                    "Failed to attach file in Cargoflow",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }
    }

    @Override
    public List<CargoflowOrder> getFromCargoflowListOrders(String phoneNumber) {
        try {
            log.debug("Getting orders for phone: {}", phoneNumber);
            List<CargoflowOrder> result = listOrderClient.getOrdersByPhone(phoneNumber);
            log.debug("Received {} orders for phone: {}", result.size(), phoneNumber);
            return result;
        } catch (Exception e) {
            log.error("Failed to get orders from Cargoflow: {}", e.getMessage(), e);
            throw new CargoflowApiException(
                    "Failed to get orders from Cargoflow",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }
    }
}