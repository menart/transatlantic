package express.atc.backend.integration.dto;

import express.atc.backend.integration.enums.SmsAeroStatus;

import java.time.LocalDateTime;

public class SmsAeroResponseDto {

    private int id;
    private String from;
    private String number;
    private String text;
    private SmsAeroStatus status;
    private String extendStatus;
    private String channel;
    private LocalDateTime dateCreate;
    private LocalDateTime dateSend;
}
