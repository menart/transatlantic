package express.atc.backend.integration.cargoflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import express.atc.backend.serializer.DateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private OffsetDateTime opTime;
    private TimeZone opTimezone;
    private String opLocation;

    private Double weight;
    private String hsCodeTime;
    private String idInfo;
    private Integer failureCount;
    private String status;
    private Integer code;
    private String bizType;
    private String trackingDescription;
    private String logisticsOrderCode;
    private String idValidationStatus;
    private Long tariff;
    private String properties;

    private String lastmileInfo;
    private String lastmileStatus;

    @JsonDeserialize(using = DateTimeDeserializer.class)
    private OffsetDateTime createdAt;
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private OffsetDateTime updatedAt;
    private String hsCodeStatus;
}
