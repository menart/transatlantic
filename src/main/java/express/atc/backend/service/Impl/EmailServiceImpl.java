package express.atc.backend.service.Impl;

import express.atc.backend.exception.ApiException;
import express.atc.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static express.atc.backend.Constants.EMAIL_SEND_EXCEPTION;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;

    @Value("${spring.mail.username}")
    private String senderUser;

    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderUser);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            emailSender.send(message);
        } catch (MailSendException | SMTPSendFailedException exception) {
            log.error(exception.getMessage(), exception);
            throw new ApiException(
                    String.format(EMAIL_SEND_EXCEPTION, to),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    @Override
    public void sendMessageUsingTemplate(String to, String subject, Map<String, Object> templateModel,
                                         String templateName) throws MessagingException {

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        thymeleafContext.setVariable("senderName", senderUser);
        String htmlBody = thymeleafTemplateEngine.process(templateName, thymeleafContext);

        sendHtmlMessage(to, subject, htmlBody);
    }
}
