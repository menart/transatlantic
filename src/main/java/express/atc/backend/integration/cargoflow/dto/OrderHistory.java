package express.atc.backend.integration.cargoflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import express.atc.backend.serializer.InstantDeserializer;

import java.time.Instant;
import java.util.TimeZone;


@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderHistory(
        Long id,
        String opLocation,
    @JsonDeserialize(using = InstantDeserializer.class)
        Instant opTime,
        TimeZone opTimezone,
        String status
) {
}
