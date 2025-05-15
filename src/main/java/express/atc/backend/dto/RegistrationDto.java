package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import static express.atc.backend.Constants.*;

public record RegistrationDto(
        @Pattern(regexp = "([78])[0-9]{10}", message = PHONE_NOT_VALID)
        @Schema(description = "Номер телефона в формате 7/8 + десять цифр")
        String phone,
        @Schema(description = "Адрес электронной почты (максимальная длина поля 150)")
        @Size(min = 5, max = 150, message = "Адрес электронной почты: " + NOT_VALID_SIZE)
        @Email(message = EMAIL_NOT_VALID)
        String email,
        String password,
        String conformation,
        @Schema(description =
                "Согласие \"Публичной офертой о заключении договора на предоставление " +
                        "услуг таможенного представительства для физических лиц\"")
        @AssertTrue(message = DISAGREE)
        Boolean agree,
        @Schema(description = "Код полученный в SMS")
        @NotNull
        @Pattern(regexp = "[0-9]{1,10}", message = VALIDATE_CODE)
        String code
) {
}
