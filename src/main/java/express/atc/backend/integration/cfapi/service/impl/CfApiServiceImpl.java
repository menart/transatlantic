package express.atc.backend.integration.cfapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import express.atc.backend.dto.ProviderInfoDto;
import express.atc.backend.dto.UserDto;
import express.atc.backend.integration.cfapi.client.CfApiClient;
import express.atc.backend.integration.cfapi.dto.CfApiPersonalInfoDto;
import express.atc.backend.integration.cfapi.dto.CfApiRequestDto;
import express.atc.backend.integration.cfapi.dto.ChangeStatusDto;
import express.atc.backend.integration.cfapi.enums.MsgType;
import express.atc.backend.integration.cfapi.enums.OrderStatus;
import express.atc.backend.integration.cfapi.enums.RequestCfapiType;
import express.atc.backend.integration.cfapi.mapper.UserInfoMapper;
import express.atc.backend.integration.cfapi.service.CfApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static express.atc.backend.integration.cfapi.enums.MsgType.CF_ID_Update;
import static express.atc.backend.integration.cfapi.enums.MsgType.EXT_CF_TRACKING_EVENT;
import static express.atc.backend.integration.cfapi.enums.RequestCfapiType.EVENTS;
import static express.atc.backend.integration.cfapi.enums.RequestCfapiType.MESSAGES;

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
    @Value("${cfapi.location}")
    private String cfLocation;

    private final CfApiClient cfApiClient;
    private final ObjectMapper objectMapper;
    private final UserInfoMapper userInfoMapper;

    @Override
    public Boolean sendPersonalInfo(String trackingNumber, UserDto user, ProviderInfoDto provider) {
        var personalInfoForCfApi = new CfApiPersonalInfoDto(
                trackingNumber,
                userInfoMapper.toPersonalInfoForCfApi(user)
        );
        String msgString;
        try {
            msgString = objectMapper.writeValueAsString(personalInfoForCfApi);
            log.info("msg: {}", msgString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return sendCFApiRequest(msgString, MESSAGES, CF_ID_Update, provider.providerId(), provider.providerSecret());
    }

    @Override
    public Boolean changeStatusToCargoflow(String trackingNumber, OrderStatus status) {
        var date = LocalDateTime.now();
        ZoneId zone = ZoneId.of("Europe/Moscow");
        ZonedDateTime zonedDateTime = date.atZone(zone);
        var msg = new ChangeStatusDto(
                trackingNumber,
                status,
                "",
                date.atZone(zone).toLocalDateTime(),
                zonedDateTime.getOffset().toString(),
                cfLocation
        );
        String msgString;
        try {
            msgString = objectMapper.writeValueAsString(msg);
            log.info("msg: {}", msgString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return sendCFApiRequest(msgString, EVENTS, EXT_CF_TRACKING_EVENT, platformId, token);
    }

    private String calcChecksum(String requestUrl, String method, String msg, String platform, String secret) {
        log.info("url: {}", requestUrl);
        StringBuilder sb = new StringBuilder()
                .append(requestUrl)
                .append(method)
                .append(platform)
                .append(secret)
                .append(msg);
        log.info("Digest base string: {}", new String(sb.toString().getBytes(StandardCharsets.UTF_8)));
        String hexDigest = DigestUtils.sha256Hex(sb.toString().getBytes(StandardCharsets.UTF_8));
        log.info("Calculated checksum: {}", hexDigest);
        return hexDigest;
    }

    private boolean sendCFApiRequest(String msgString,
                                     RequestCfapiType type,
                                     MsgType msgType,
                                     String platform,
                                     String secret) {
        var checksum = calcChecksum(
                cfApiUrl + type.toString().toLowerCase(),
                "POST",
                msgString,
                platform,
                secret);
        var uuid = UUID.randomUUID().toString();
        log.info("checksum: {}, uuid: {}", checksum, uuid);

        try {
            CfApiRequestDto response;
            if (type == MESSAGES) {
                response = cfApiClient.sendMessage(uuid, checksum, msgType, platform, msgString);
            } else {
                response = cfApiClient.sendEvent(uuid, checksum, msgType, platform, msgString);
            }

            log.info("CF API response: {}", response);
            return true;

        } catch (Exception e) {
            log.error("Failed to send request to CF API", e);
            return false;
        }
    }
}