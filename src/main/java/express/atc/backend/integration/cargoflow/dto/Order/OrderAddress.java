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
public class OrderAddress {

    private String city;
    private String country;
    private String province;
    private String detailAddress;
}
