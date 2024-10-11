package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

public record ErrorResponseDto(
        @Schema(description = "Тип ошибки")
        String status,
        @Schema(description = "Список сообщений об ошибках")
        List<String> messages) implements Serializable {

}
