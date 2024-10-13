package express.atc.backend.integration.cargoflow.dto;

public record RequestOrderDto(
        FilterDto filter,
        String view
) {
}
