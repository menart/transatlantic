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
    private final MessageService messageService;
    private final EmailService emailService;

    @Value("${project.url}")
    private String ourUrl;

    @Override
    @Async
    public void sendTrackingInfo(String userPhone, TrackingStatus status, String trackNumber, String marketplace) {
        Map<String, Object> emailInfo = new HashMap<>();
        emailInfo.put("trackNumber", trackNumber);
        emailInfo.put("marketplace", marketplace);
        emailInfo.put("ourUrl", ourUrl);
        try {
            switch (status) {
                case NEED_DOCUMENT -> {
                    var user = userService.findOrCreateByPhone(userPhone);
                    messageService.send(user.getPhone(), String.format(SMS_NEED_DOCUMENT, trackNumber, marketplace, ourUrl));
                    if (user.getEmail() != null) {
                        emailService.sendMessageUsingTemplate(
                                user.getEmail(),
                                String.format(EMAIL_TITLE_NEED_DOCUMENT, trackNumber, marketplace),
                                emailInfo,
                                "need_document.html"
                        );
                    }
                }
                case NEED_PAYMENT -> {
                    var user = userService.findOrCreateByPhone(userPhone);
                    messageService.send(user.getPhone(), String.format(SMS_NEED_PAYMENT, trackNumber, marketplace, ourUrl));
                    if (user.getEmail() != null) {
                        emailService.sendMessageUsingTemplate(
                                user.getEmail(),
                                String.format(EMAIL_TITLE_NEED_PAYMENT, trackNumber, marketplace),
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
