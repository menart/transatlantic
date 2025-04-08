package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;

import static express.atc.backend.Constants.DISAGREE;
import static express.atc.backend.Constants.PHONE_NOT_VALID;

public record AuthSmsDto(

    @Pattern(regexp = "([78])[0-9]{10}", message = PHONE_NOT_VALID)
    @Schema(description = "Номер телефона в формате 7/8 + десять цифр")
    String phone,
    @Schema(description =
            "Согласие  с пользовательским соглашением и соглашением обработки персональных данных, " +
                    "а также договором-офертой")
    @AssertTrue(message = DISAGREE)
    boolean agree) {
}
