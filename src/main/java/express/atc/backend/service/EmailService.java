package express.atc.backend.service;

import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailService {

    void sendMessageUsingTemplate(String to, String subject, Map<String, Object> templateModel, String templateName)
            throws MessagingException;
}
