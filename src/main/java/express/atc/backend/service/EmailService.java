package express.atc.backend.service;

public interface EmailService {

    void sendMessage(String to, String senderSubject, String senderBody);
}
