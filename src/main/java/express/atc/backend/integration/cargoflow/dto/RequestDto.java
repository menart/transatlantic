package express.atc.backend.integration.cargoflow.dto;

public record RequestDto(
        FilterDto filter,
        String view
) {
}
