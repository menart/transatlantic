package express.atc.backend.integration.cargoflow.dto;

import java.util.UUID;

public record FileDto(
        UUID id,
        String name,
        Long size
) {
}