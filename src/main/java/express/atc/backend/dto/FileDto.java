package express.atc.backend.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record FileDto(
        @Schema(description = "Уникальный номер файла")
        UUID uuid,
        @Schema(description = "Имя файла")
        String filename,
        @Schema(description = "Размер файла в байтах")
        long size,
        @Schema(description = "Тип файла")
        String contentType) {
}
