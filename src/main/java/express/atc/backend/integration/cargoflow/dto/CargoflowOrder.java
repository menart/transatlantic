package express.atc.backend.integration.cargoflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime hsCodeStatus;
}
