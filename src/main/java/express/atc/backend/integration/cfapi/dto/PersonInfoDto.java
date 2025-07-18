package express.atc.backend.integration.cfapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import express.atc.backend.serializer.LocalDateValidDeserializer;

import java.time.LocalDate;

public record PersonInfoDto(
        String name,
        String lastName,
        String patronymic,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateValidDeserializer.class)
        LocalDate birthDate,
        String docTypeCode,
        String docSeries,
        String docNumber,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateValidDeserializer.class)
        LocalDate docDate,
        String docOrganization,
        String docOrganizationCode,
        String taxNumber
) {
}
