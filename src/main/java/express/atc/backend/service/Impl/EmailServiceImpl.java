package express.atc.backend.service.Impl;

import express.atc.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

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
}
