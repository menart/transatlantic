package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record FeedbackFieldDto(
        @Schema(description = "Имя отправителя")
        String name,
        @Schema(description = "Тело сообщения")
        String body
) {
}
