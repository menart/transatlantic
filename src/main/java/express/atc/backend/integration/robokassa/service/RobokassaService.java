package express.atc.backend.integration.robokassa.service;

import express.atc.backend.dto.PaymentDto;

public interface RobokassaService {

    String makePaymentUrl(PaymentDto payment);

    String paymentResult(String outSum, Long orderId, String trackingNumber, String checkSum);
}
