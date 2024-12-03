package express.atc.backend.service;

import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailService {

    void sendMessage(String to, String senderSubject, String senderBody);

    void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException;

    void sendMessageUsingTemplate(String to, String subject, Map<String, Object> templateModel, String templateName)
            throws MessagingException;
}
