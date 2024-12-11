package express.atc.backend.integration.cargoflow.dto.Order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderParticipant(
        String phone,
        String name,
        OrderAddress address,
        String companyName
) {
}
