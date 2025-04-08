package express.atc.backend.integration.cargoflow.dto.Order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderParcel(
        Long price,
        String priceUnit,
        String priceCurrency,
        Long suggestedWeight,
        String suggestedWeightUnit,
        List<OrderGood> goodsList
) {
}
