package express.atc.backend.facade.impl;

import express.atc.backend.enums.TrackingStatus;
import express.atc.backend.facade.MessageFacade;
import express.atc.backend.service.EmailService;
import express.atc.backend.service.MessageService;
import express.atc.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static express.atc.backend.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageFacadeImpl implements MessageFacade {

    private final UserService userService;
    private final MessageService smsMessageService;
    private final EmailService emailMessageService;

    @Value("${project.url}")
    private String ourUrl;

    @Override
    @Async
    public void sendTrackingInfo(String userPhone, TrackingStatus status, String orderNumber, String marketplace) {
        Map<String, Object> emailInfo = new HashMap<>();
        emailInfo.put("trackNumber", orderNumber);
        emailInfo.put("marketplace", marketplace);
        emailInfo.put("ourUrl", ourUrl);
        try {
            switch (status) {
                case NEED_DOCUMENT -> {
                    log.info("Sending NEED_DOCUMENT to {}", userPhone);
                    var user = userService.findOrCreateByPhone(userPhone);
                    smsMessageService.send(user.getPhone(), String.format(SMS_NEED_DOCUMENT, orderNumber, marketplace, ourUrl));
                    if (user.getEmail() != null) {
                        emailMessageService.sendMessageUsingTemplate(
                                user.getEmail(),
                                String.format(EMAIL_TITLE_NEED_DOCUMENT, orderNumber, marketplace),
                                emailInfo,
                                "need_document.html"
                        );
                    }
                }
                case NEED_PAYMENT -> {
                    log.info("Sending NEED_PAYMENT to {}", userPhone);
                    var user = userService.findOrCreateByPhone(userPhone);
                    smsMessageService.send(user.getPhone(), String.format(SMS_NEED_PAYMENT, orderNumber, marketplace, ourUrl));
                    if (user.getEmail() != null) {
                        emailMessageService.sendMessageUsingTemplate(
                                user.getEmail(),
                                String.format(EMAIL_TITLE_NEED_PAYMENT, orderNumber, marketplace),
                                emailInfo,
                                "need_payment.html"
                        );
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
