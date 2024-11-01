package express.atc.backend.integration.cargoflow.dto.Order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
public class OrderParticipant {
    private String phone;
    private String name;
    private OrderAddress address;
    private String companyName;
}
