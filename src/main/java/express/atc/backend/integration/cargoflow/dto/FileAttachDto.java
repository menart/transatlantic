package express.atc.backend.integration.cargoflow.dto;

import java.util.UUID;

public record FileAttachDto(
        String logisticsOrderCode,
        UUID fileDescriptorId
) {
}
