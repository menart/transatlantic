package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DeliveryItemDto(
        @Schema(description = "Ширина")
        @NotNull(message = "Ширина: поле не может быть пустым")
        @Min(value = 1L, message = "Ширина не может быть меньше 1")
        int width,
        @Schema(description = "Длина")
        @NotNull(message = "Длина: поле не может быть пустым")
        @Min(value = 1L, message = "Длина не может быть меньше 1")
        int length,
        @Schema(description = "Высота")
        @NotNull(message = "Высота: поле не может быть пустым")
        @Min(value = 1L, message = "Высота не может быть меньше 1")
        int height
) {
}
