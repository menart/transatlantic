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
public class OrderGood {

    private String url;
    private String name;
    private Long price;
    private Long weight;
    private Long quantity;
    private Long itemPrice;
    private String priceUnit;
    private String productId;
    private String weightUnit;
    private String categoryName;
    private String priceCurrency;
    private Long categoryFeature;
}
