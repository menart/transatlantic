package express.atc.backend.integration.cargoflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import express.atc.backend.integration.cargoflow.dto.Order.OrderPropertyDto;
import express.atc.backend.integration.cargoflow.serializer.CargoflowPropertyDeserializer;

import java.time.LocalDateTime;
import java.util.TimeZone;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CargoflowOrder(
        Long id,
        String reference,
        String trackingNumber,
        String orderCollected,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime opTime,
        TimeZone opTimezone,
        String opLocation,
        Double weight,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime hsCodeTime,
        String idInfo,
        Integer failureCount,
        String status,
        Integer code,
        String bizType,
        String trackingDescription,
        String logisticsOrderCode,
        String idValidationStatus,
        Long tariff,
    @JsonDeserialize(using = CargoflowPropertyDeserializer.class)
        OrderPropertyDto properties,
        String lastmileInfo,
        String lastmileStatus,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime updatedAt,
        String hsCodeStatus
) {
}
