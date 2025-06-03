package express.atc.backend.integration.cargoflow.dto.route;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomsOperatorDto(
        String providerId
) {
}
