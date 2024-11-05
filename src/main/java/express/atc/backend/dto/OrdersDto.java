package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDto {
    @Schema(description = "Стоимость списка товаров")
    private long price;
    @Schema(description = "Валюта заказа")
    private String currency;
    @Schema(description = "Вес товаров")
    private long weight;
    @Schema(description = "Список товаров")
    List<OrderDto> items;
}
