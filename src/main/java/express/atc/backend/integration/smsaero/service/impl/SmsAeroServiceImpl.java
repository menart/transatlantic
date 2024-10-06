package express.atc.backend.integration.smsaero.service.impl;

import express.atc.backend.integration.smsaero.dto.SmsAeroResponse;
import express.atc.backend.integration.smsaero.service.SmsAeroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsAeroServiceImpl implements SmsAeroService {

    private final WebClient smsAeroWebClient;

    @Value("${sms-aero.sign}")
    private String smsAeroSign;

    @Value("${sms-aero.enable}")
    private boolean enable;

    @Override
    public void send(String phone, String message) {
        log.info("send sms to {} : {}", phone, message);
        if(enable) {
            var response = smsAeroWebClient
                    .get()
                    .uri(
                            uriBuilder -> uriBuilder
                                    .path("/sms/send")
                                    .queryParam("number", phone)
                                    .queryParam("sign", smsAeroSign)
                                    .queryParam("text", message)
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(SmsAeroResponse.class)
                    .block();
            log.info("response sms for {}: {}", phone, response != null ? response.getMessage() : "error");
        }
    }


}
