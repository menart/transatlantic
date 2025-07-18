package express.atc.backend.integration.cargoflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CargoflowProviderDto(
        String id,
        String providerSecret,
        String providerId
) {
}
