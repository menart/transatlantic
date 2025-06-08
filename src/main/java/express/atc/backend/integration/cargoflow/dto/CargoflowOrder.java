package express.atc.backend.integration.cargoflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import express.atc.backend.integration.cargoflow.dto.Order.OrderPropertyDto;
import express.atc.backend.integration.cargoflow.dto.route.RouteDto;
import express.atc.backend.integration.cargoflow.serializer.CargoflowPropertyDeserializer;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CargoflowOrder(
        Long id,
        String reference,
        String trackingNumber,
        Double weight,
        RouteDto route,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime hsCodeTime,
        String status,
        String logisticsOrderCode,
        @JsonDeserialize(using = CargoflowPropertyDeserializer.class)
        OrderPropertyDto properties,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime updatedAt
) {
}
