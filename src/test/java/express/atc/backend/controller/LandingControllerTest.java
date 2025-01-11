package express.atc.backend.controller;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import express.atc.backend.AbstractControllerTest;
import express.atc.backend.dto.DeliveryDto;
import express.atc.backend.dto.ErrorResponseDto;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import java.util.List;

import static express.atc.backend.Constants.EMAIL_SEND_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LandingControllerTest extends AbstractControllerTest {

    private final String webPath = "/api/landing/delivery";

    @Value("${landing.delivery.email}")
    private String emailSender;
    @Value("${spring.mail.username}")
    private String user;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"))
            .withPerMethodLifecycle(false);

    @Test
    @SneakyThrows
    void deliverySendTest() {
        String emailClient = "email@test.com";
        DeliveryDto request = new DeliveryDto(
                "full name",
                emailClient,
                "title",
                1,
                "items",
                "description"
        );
        greenMail.start();
        mvc.perform(post(webPath)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
        assertEquals(2, greenMail.getReceivedMessages().length);
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals(emailSender, receivedMessage.getAllRecipients()[0].toString());
        assertEquals(user, receivedMessage.getFrom()[0].toString());
        assertEquals("Получен новый заказ от full name", receivedMessage.getSubject());

        receivedMessage = greenMail.getReceivedMessages()[1];

        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals(emailClient, receivedMessage.getAllRecipients()[0].toString());
        assertEquals(user, receivedMessage.getFrom()[0].toString());
        assertEquals("full name Спасибо за ваш заказ ", receivedMessage.getSubject());
    }

    @Test
    @SneakyThrows
    void deliveryFailTest() {
        String emailClient = "email@test.com";
        DeliveryDto request = new DeliveryDto(
                "full name",
                emailClient,
                "title",
                1,
                "items",
                "description"
        );
        var response = new ErrorResponseDto("SERVICE_UNAVAILABLE",
                List.of(String.format(EMAIL_SEND_EXCEPTION, emailSender)));
        greenMail.stop();
        mvc.perform(post(webPath)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}