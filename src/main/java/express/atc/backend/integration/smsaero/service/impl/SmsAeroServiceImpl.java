package express.atc.backend.integration.smsaero.service.impl;

import express.atc.backend.integration.smsaero.client.SmsAeroClient;
import express.atc.backend.integration.smsaero.dto.SmsAeroResponse;
import express.atc.backend.integration.smsaero.service.SmsAeroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsAeroServiceImpl implements SmsAeroService {

    private final SmsAeroClient smsAeroClient;

    @Value("${sms-aero.sign}")
    private String smsAeroSign;

    @Value("${sms-aero.enable}")
    private boolean enable;

    @Override
    public void send(String phone, String message) {
        log.info("send sms to {} : {}", phone, message);

        if (enable) {
            try {
                SmsAeroResponse response = smsAeroClient.sendSms(phone, smsAeroSign, message);
                log.info("response sms for {}: {}", phone, response != null ? response.getMessage() : "error");
            } catch (Exception e) {
                log.error("Failed to send SMS to {}: {}", phone, e.getMessage(), e);
            }
        } else {
            log.info("SMS sending is disabled. Would send to {}: {}", phone, message);
        }
    }
}