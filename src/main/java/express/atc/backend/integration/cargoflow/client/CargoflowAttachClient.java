package express.atc.backend.integration.cargoflow.client;

import express.atc.backend.integration.cargoflow.config.FeignConfig;
import express.atc.backend.integration.cargoflow.dto.FileAttachDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "cargoflowAttachClient",
        url = "${cargoflow.upload.attach}",
        configuration = FeignConfig.class
)
public interface CargoflowAttachClient {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    void attachFile(@RequestBody FileAttachDto attach);
}