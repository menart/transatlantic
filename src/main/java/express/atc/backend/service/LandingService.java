package express.atc.backend.service;

import express.atc.backend.dto.DeliveryDto;
import jakarta.mail.MessagingException;

public interface LandingService {
    boolean deliveryRequest(DeliveryDto delivery) throws MessagingException;
}
