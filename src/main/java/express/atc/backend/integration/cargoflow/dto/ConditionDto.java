package express.atc.backend.integration.cargoflow.dto;

public record ConditionDto(
        String property,
        String operator,
        String value
) {
}
