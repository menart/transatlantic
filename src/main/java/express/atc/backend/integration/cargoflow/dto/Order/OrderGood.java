package express.atc.backend.integration.cargoflow.dto.Order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderGood(
        String url,
        String name,
        Long price,
        Long weight,
        Long quantity,
        Long itemPrice,
        String priceUnit,
        String productId,
        String weightUnit,
        String categoryName,
        String priceCurrency,
        Long categoryFeature
) {
}