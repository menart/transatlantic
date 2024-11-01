package express.atc.backend.integration.cargoflow.dto.Order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
public class OrderParcel {

    private Long price;
    private String priceUnit;
    private String priceCurrency;
    private Long suggestedWeight;
    private String suggestedWeightUnit;
    private List<OrderGood> goodsList;
}
