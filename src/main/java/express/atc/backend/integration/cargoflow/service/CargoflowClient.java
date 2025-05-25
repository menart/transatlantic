package express.atc.backend.integration.cargoflow.service;

import express.atc.backend.integration.cargoflow.dto.CargoflowOrder;
import express.atc.backend.integration.cargoflow.dto.ConditionDto;
import express.atc.backend.integration.cargoflow.dto.FileAttachDto;
import express.atc.backend.integration.cargoflow.dto.FileDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CargoflowClient {

    <T> List<T> getFromCargoflowEntity(List<ConditionDto> condition, String entity, Class<T> response);

    FileDto uploadFileToCargoflow(MultipartFile file);

    List<CargoflowOrder> getFromCargoflowListOrders(String phoneNumber);

    void attachFileToCargoflow(FileAttachDto attach);
}
