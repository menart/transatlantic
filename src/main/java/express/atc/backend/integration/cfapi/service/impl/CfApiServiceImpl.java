package express.atc.backend.integration.cfapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import express.atc.backend.integration.cfapi.dto.ChangeStatusDto;
import express.atc.backend.integration.cfapi.enums.OrderStatus;
import express.atc.backend.integration.cfapi.service.CfApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CfApiServiceImpl implements CfApiService {

    @Value("${cfapi.token}")
    private String token;
    @Value("${cfapi.platformId}")
    private String platformId;
    @Value("${cfapi.url}")
    private String cfApiUrl;

    private final WebClient cfApiWebClient;
    private final ObjectMapper objectMapper;

    public Boolean changeStatusToCargoflow(String trackingNumber){
//        var date = OffsetDateTime.now();
//        var msg = new ChangeStatusDto(
//                trackingNumber,
//                OrderStatus.CUSTOMS_FEE_PAID,
//                "",
//                LocalDateTime.now(),
//                ,
//                "MOW"
//        );
//        var checksum = calcChecksum(cfApiUrl, "POST", msg);
//        var response = cfApiWebClient
//                .post()
//                .headers(httpHeaders -> {
//                    httpHeaders.add("checksum", "Bearer " + token);
//                });
        return true;
    }

    private String calcChecksum(String requestUrl, String method, ChangeStatusDto dto) {
        String msg = null;
        try {
            msg = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder()
                .append(requestUrl)
                .append(method)
                .append(platformId)
                .append(token)
                .append(msg);
        log.debug("Digest base string: {}", new String(sb.toString().getBytes(StandardCharsets.UTF_8)));
        String hexDigest = DigestUtils.sha256Hex(sb.toString().getBytes(StandardCharsets.UTF_8));
        log.debug("Calculated checksum: {}", hexDigest);
        return hexDigest;
    }
}
