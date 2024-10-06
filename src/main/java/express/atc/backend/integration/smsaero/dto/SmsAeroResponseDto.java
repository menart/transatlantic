package express.atc.backend.integration.smsaero.dto;

import express.atc.backend.integration.smsaero.enums.SmsAeroStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmsAeroResponseDto {

    private int id;
    private String from;
    private String number;
    private String text;
    private SmsAeroStatus status;
    private String extendStatus;
    private String channel;
    private Double cost;
    private LocalDateTime dateCreate;
    private LocalDateTime dateSend;
}
