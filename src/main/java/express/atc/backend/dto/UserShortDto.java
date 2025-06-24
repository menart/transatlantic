package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record UserShortDto(
        @Schema(description = "Имя")
        String firstName,
        @Schema(description = "Отчество (может отсутствовать)")
        String lastName,
        @Schema(description = "Фамилия")
        String surname,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonSerialize(using = LocalDateSerializer.class)
        @Schema(description = "Дата рождения в формате гггг-мм-дд")
        LocalDate birthday,
        @Schema(description = "Адрес электронной почты")
        String email,
        boolean full
) {
}
