package express.atc.backend.integration.cargoflow.client;

import express.atc.backend.integration.cargoflow.config.FeignConfig;
import express.atc.backend.integration.cargoflow.dto.CargoflowOrder;
import express.atc.backend.integration.metrics.annotation.IntegrationMetrics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "cargoflowListOrderClient",
        url = "${cargoflow.entity.orders}",
        configuration = FeignConfig.class
)
public interface CargoflowListOrderClient {

    @IntegrationMetrics(
            integrationName = "Cargoflow",
            operationName = "getOrdersByPhone",
            logRequest = true,
            logResponse = false
    )
    @GetMapping
    List<CargoflowOrder> getOrdersByPhone(@RequestParam("phoneNumber") String phoneNumber);
}