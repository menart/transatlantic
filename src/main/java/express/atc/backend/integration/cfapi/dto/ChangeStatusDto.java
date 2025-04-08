package express.atc.backend.integration.cfapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import express.atc.backend.integration.cfapi.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.TimeZone;

public record ChangeStatusDto(
        String trackingNumber,
        OrderStatus status,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime opTime,
        String timeZone,
        String opLocation
) {}
