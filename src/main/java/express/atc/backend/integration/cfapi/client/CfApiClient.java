package express.atc.backend.integration.cfapi.client;

import express.atc.backend.integration.cfapi.config.CfApiClientConfig;
import express.atc.backend.integration.cfapi.dto.CfApiRequestDto;
import express.atc.backend.integration.cfapi.enums.MsgType;
import express.atc.backend.metrics.annotation.IntegrationMetrics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "cfApiClient",
        url = "${cfapi.url}",
        configuration = CfApiClientConfig.class
)
public interface CfApiClient {

    @PostMapping("/messages")
    @IntegrationMetrics(
            integrationName = "CfApi",
            operationName = "sendMessage",
            logRequest = true,
            logResponse = false
    )
    CfApiRequestDto sendMessage(
            @RequestHeader("msgId") String msgId,
            @RequestHeader("checksum") String checksum,
            @RequestHeader("msgType") MsgType msgType,
            @RequestHeader("platformId") String platformId,
            @RequestBody String body
    );

    @PostMapping("/events")
    @IntegrationMetrics(
            integrationName = "CfApi",
            operationName = "sendEvent",
            logRequest = true,
            logResponse = false
    )
    CfApiRequestDto sendEvent(
            @RequestHeader("msgId") String msgId,
            @RequestHeader("checksum") String checksum,
            @RequestHeader("msgType") MsgType msgType,
            @RequestHeader("platformId") String platformId,
            @RequestBody String body
    );
}