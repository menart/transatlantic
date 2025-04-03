package express.atc.backend.integration.cfapi.dto;

import express.atc.backend.integration.cfapi.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.TimeZone;

public record ChangeStatusDto(
        String trackingNumber,
        OrderStatus status,
        String description,
        LocalDateTime opTime,
        TimeZone timeZone,
        String opLocation
) {}
