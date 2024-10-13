package express.atc.backend.integration.cargoflow.dto;

import java.util.List;


public record FilterDto(
        List<ConditionDto> conditions
) {
}
