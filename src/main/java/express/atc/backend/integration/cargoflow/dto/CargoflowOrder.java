package express.atc.backend.integration.cargoflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.OffsetDateTimeKeyDeserializer;
import express.atc.backend.integration.cargoflow.dto.Order.OrderPropertyDto;
import express.atc.backend.integration.cargoflow.serializer.CargoflowPropertyDeserializer;
import express.atc.backend.serializer.DateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.TimeZone;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class CargoflowOrder {

    private String id;
    private String reference;
    private String trackingNumber;

    private String orderCollected;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime opTime;
    private TimeZone opTimezone;
    private String opLocation;

    private Double weight;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime hsCodeTime;
    private String idInfo;
    private Integer failureCount;
    private String status;
    private Integer code;
    private String bizType;
    private String trackingDescription;
    private String logisticsOrderCode;
    private String idValidationStatus;
    private Long tariff;
    @JsonDeserialize(using = CargoflowPropertyDeserializer.class)
    private OrderPropertyDto properties;

    private String lastmileInfo;
    private String lastmileStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
    private String hsCodeStatus;
}
