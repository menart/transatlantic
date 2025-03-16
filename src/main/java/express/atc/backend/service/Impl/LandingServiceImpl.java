package express.atc.backend.service.Impl;

import express.atc.backend.dto.DeliveryDto;
import express.atc.backend.service.EmailService;
import express.atc.backend.service.LandingService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LandingServiceImpl implements LandingService {

    @Value("${landing.delivery.email}")
    private String deliveryEmail;
    @Value("${landing.delivery.template}")
    private String deliveryTemplate;

    private final EmailService emailService;

    @Override
    public boolean deliveryRequest(DeliveryDto delivery) throws MessagingException {
        emailService.sendMessageUsingTemplate(
                delivery.email(),
                delivery.fullName() + " Спасибо за ваш заказ ",
                Map.of("delivery", delivery),
                deliveryTemplate
        );
        emailService.sendMessageUsingTemplate(
                deliveryEmail,
                "Получен новый заказ от " + delivery.fullName(),
                Map.of("delivery", delivery),
                deliveryTemplate
        );
        return true;
    }
}
