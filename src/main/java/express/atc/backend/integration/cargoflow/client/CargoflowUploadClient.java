package express.atc.backend.integration.cargoflow.client;

import express.atc.backend.integration.cargoflow.config.FeignConfig;
import express.atc.backend.integration.cargoflow.config.MultipartSupportConfig;
import express.atc.backend.integration.cargoflow.dto.FileDto;
import express.atc.backend.integration.metrics.annotation.IntegrationMetrics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "cargoflowUploadClient",
        url = "${cargoflow.upload.endpoint}",
        configuration = {FeignConfig.class, MultipartSupportConfig.class}
)
public interface CargoflowUploadClient {

    @IntegrationMetrics(
            integrationName = "Cargoflow",
            operationName = "uploadFile",
            logRequest = true,
            logResponse = false
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    FileDto uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "name", required = false) String fileName
    );
}