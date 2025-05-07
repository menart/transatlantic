package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangePasswordDto(
        @Schema(description = "Пароль пользователя")
        String password,
        @Schema(description = "Подтверждение пароля")
        String confirmed
) {
}
