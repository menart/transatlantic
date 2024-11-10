package express.atc.backend.integration.cargoflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import express.atc.backend.serializer.InstantDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.TimeZone;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistory {

    private Long id;
    private String opLocation;
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant opTime;
    private TimeZone opTimezone;
    private String status;
}
