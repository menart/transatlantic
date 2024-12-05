package express.atc.backend.integration.robokassa.service.impl;

import express.atc.backend.dto.PaymentDto;
import express.atc.backend.dto.PaymentItemDto;
import express.atc.backend.exception.ApiException;
import express.atc.backend.integration.robokassa.dto.InvoiceDto;
import express.atc.backend.integration.robokassa.dto.ReceiptDto;
import express.atc.backend.integration.robokassa.dto.ReceiptItemDto;
import express.atc.backend.integration.robokassa.dto.RobokassaDto;
import express.atc.backend.integration.robokassa.service.RobokassaService;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.yaml.snakeyaml.util.UriEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static express.atc.backend.integration.robokassa.config.RobokassaConfig.ROBOKASSA_ERROR_RESPONSE;

@Service
@RequiredArgsConstructor
@Slf4j
public class RobokassaServiceImpl implements RobokassaService {

    private final WebClient robokassaWebClient;

    @Value("${robokassa.merchant_login}")
    private String merchantLogin;
    @Value("${robokassa.description}")
    private String description;
    @Value("${robokassa.tax}")
    private String tax;
    @Value("${robokassa.algorithm}")
    private String algorithm;
    @Value("${robokassa.password_1}")
    private String password;
    @Value("${robokassa.test}")
    private boolean test;
    @Value("${robokassa.payment-url}")
    private String paymentUrl;
    @Value("${robokassa.success-url}")
    private String successUrl;
    @Value("${robokassa.success-method}")
    private String successMethod;
    @Value("${robokassa.fail-url}")
    private String failUrl;
    @Value("${robokassa.fail-method}")
    private String failMethod;


    @Override
    public String makePaymentUrl(PaymentDto payment) {
        var request = mappingFromPayment(payment);
        log.info("request: {}", request);
        try {
            var response = robokassaWebClient
                    .get()
                    .uri(uriBuilder ->
                            request.getUribuilder(uriBuilder)
                                    .queryParam("SuccessUrl2", successUrl)
                                    .queryParam("SuccessUrl2Method", successMethod)
                                    .queryParam("FailUrl2", failUrl)
                                    .queryParam("FailUrl2Method", failMethod)
                                    .build())
                    .retrieve()
                    .bodyToMono(InvoiceDto.class)
                    .block();
            log.info("response: {}", response);
            if (response != null && response.getErrorCode() == 0) {
                return paymentUrl + response.getInvoiceID();
            } else {
                throw new ApiException(ROBOKASSA_ERROR_RESPONSE, HttpStatus.SERVICE_UNAVAILABLE);
            }
        } catch (Exception e) {
            throw new ApiException(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private RobokassaDto mappingFromPayment(PaymentDto payment) {
        var dto = RobokassaDto.builder()
                .merchantLogin(merchantLogin)
                .outSum(payment.getAmount() / 100.00)
                .description(description)
                .invId(payment.getOrderId())
                .email(payment.getEmail())
                .receipt(ReceiptDto.builder()
                        .items(payment.getItems().stream()
                                .map(this::mappingItem)
                                .toList())
                        .build())
                .isTest(test ? 1 : null)
                .build();
        dto.setSignatureValue(calcSignature(dto));
        return dto;
    }

    private ReceiptItemDto mappingItem(PaymentItemDto item) {
        return ReceiptItemDto.builder()
                .name(item.getName())
                .quantity(item.getQuantity().doubleValue())
                .sum(item.getAmount() / 100.00)
                .tax(tax)
                .paymentMethod("full_payment")
                .build();
    }

    private String calcSignature(RobokassaDto dto) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            String encodingStr = dto.getMerchantLogin() + ":" + dto.getOutSum() + ":" + dto.getInvId()
                    + ":" + UriEncoder.encode(dto.getReceipt().toString())
                    + ":" + successUrl + ":" + successMethod
                    + ":" + failUrl + ":" + failMethod
                    + ":" + password;
            log.info(encodingStr);
            md.update(encodingStr.getBytes());
            return DatatypeConverter
                    .printHexBinary(md.digest()).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
