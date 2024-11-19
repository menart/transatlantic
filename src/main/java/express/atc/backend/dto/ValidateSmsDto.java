package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static express.atc.backend.Constants.PHONE_NOT_VALID;
import static express.atc.backend.Constants.VALIDATE_CODE;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ValidateSmsDto {

    @NotNull
    @Pattern(regexp = "([78])[0-9]{10}", message = PHONE_NOT_VALID)
    @Schema(description = "Номер телефона в формате 7/8 + десять цифр")
    private String phone;

    @Schema(description = "Код полученный в SMS")
    @NotNull
    @Pattern(regexp = "[0-9]{1,10}", message = VALIDATE_CODE)
    private String code;
}
