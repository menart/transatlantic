package express.atc.backend.service.Impl;

import express.atc.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;

    @Value("${spring.mail.username}")
    private String senderUser;

    @Override
    public void sendMessage(String to, String senderSubject, String senderBody) {
        log.info("send email for {}", to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderUser);
        message.setTo(to);
        message.setSubject(senderSubject);
        message.setText(senderBody);
        emailSender.send(message);
    }

    @Override
    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(senderUser);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
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
