package express.atc.backend.integration.cbrf.client;

import express.atc.backend.integration.cbrf.config.CbrfFeignConfig;
import express.atc.backend.integration.cbrf.dto.ListCurrencyDto;
import express.atc.backend.metrics.annotation.IntegrationMetrics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "cbrfClient",
        url = "${cbrf.url}",
        configuration = CbrfFeignConfig.class
)
public interface CbrfClient {

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    @IntegrationMetrics(
            integrationName = "cbrf",
            operationName = "getCurrencyRates",
            logRequest = true
    )
    ListCurrencyDto getCurrencyRates();
}