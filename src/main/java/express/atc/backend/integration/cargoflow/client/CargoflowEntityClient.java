package express.atc.backend.integration.cargoflow.client;

import express.atc.backend.integration.cargoflow.config.FeignConfig;
import express.atc.backend.integration.cargoflow.dto.RequestDto;
import express.atc.backend.integration.metrics.annotation.IntegrationMetrics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "cargoflowEntityClient",
        url = "${cargoflow.entity.endpoint}",
        configuration = FeignConfig.class
)
public interface CargoflowEntityClient {

    @IntegrationMetrics(
            integrationName = "Cargoflow",
            operationName = "getEntity",
            logRequest = true,
            logResponse = false
    )
    @PostMapping(value = "/entities/{entity}/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<?>> getEntity(
            @PathVariable("entity") String entity,
            @RequestBody RequestDto request,
            @RequestParam(value = "view", defaultValue = "_local") String view
    );
}