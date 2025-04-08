package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import express.atc.backend.enums.Language;
import express.atc.backend.enums.UserRole;
import express.atc.backend.serializer.LocalDateValidDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

import static express.atc.backend.Constants.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@Accessors(chain = true)
public class UserDto {
    @JsonIgnore
    private Long id;
    @JsonIgnore
    private String phone;
    @JsonIgnore
    private UserRole role;
    @NotNull(message = "Поле Имя не может быть пустой")
    @Size(min = 1, max = 255, message = "Имя: " + NOT_VALID_SIZE)
    @Schema(description = "Имя (максимальная длина поля 255)")
    private String firstName;
    @Schema(description = "Отчество (может отсутствовать) (максимальная длина поля 255)")
    @Size(max = 255, message = "Отчество: " + NOT_VALID_SIZE)
    private String lastName;
    @NotNull(message = "Поле Фамилия не может быть пустой")
    @Schema(description = "Фамилия (максимальная длина поля 255)")
    @Size(min = 1, max = 255, message = "Фамилия: " + NOT_VALID_SIZE)
    private String surname;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateValidDeserializer.class)
    @Schema(description = "Дата рождения в формате гггг-мм-дд")
    @NotNull(message = "Дата рождения: " + DATE_NOT_VALID)
    private LocalDate birthday;
    @Schema(description = "Документ удостверяющий личность")
    @Valid
    private DocumentDto document;
    @Schema(description = "ИНН, для физлиц 12 цифр")
    @Pattern(regexp = "[0-9]{12}", message = INN_NOT_VALID)
    private String inn;
    @Schema(description = "Адрес электронной почты (максимальная длина поля 150)")
    @Size(min = 5, max = 150, message = "Адрес электронной почты: " + NOT_VALID_SIZE)
    @Email(message = EMAIL_NOT_VALID)
    private String email;
    @Schema(description = "Язык приложения для пользователя")
    private Language language;
    @Schema(description =
            "Согласие \"Публичной офертой о заключении договора на предоставление " +
                    "услуг таможенного представительства для физических лиц\"")
    @AssertTrue(message = DISAGREE)
    @Builder.Default
    private boolean agree = true;
    @Builder.Default
    private boolean full = true;
}
