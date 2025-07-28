package express.atc.backend.integration.cfapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import express.atc.backend.serializer.LocalDateValidDeserializer;

import java.time.LocalDate;

import static express.atc.backend.Constants.BLANK_STRING;

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
        public PersonInfoDto {
                name = normalizeString(name);
                lastName = normalizeString(lastName);
                patronymic = normalizeString(patronymic);
                docTypeCode = normalizeString(docTypeCode);
                docSeries = normalizeString(docSeries);
                docNumber = normalizeString(docNumber);
                docOrganization = normalizeString(docOrganization);
                docOrganizationCode = normalizeString(docOrganizationCode);
                taxNumber = normalizeString(taxNumber);
        }

        // Вспомогательный метод для замены пустых строк
        private static String normalizeString(String value) {
                return (value == null || value.trim().isEmpty()) ? BLANK_STRING : value.trim();
        }
}
