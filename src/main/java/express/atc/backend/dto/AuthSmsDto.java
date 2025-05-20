package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import static express.atc.backend.Constants.PHONE_NOT_VALID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthSmsDto(

        @Pattern(regexp = "([78])[0-9]{10}", message = PHONE_NOT_VALID)
        @Schema(description = "Номер телефона в формате 7/8 + десять цифр")
        String phone) {
}