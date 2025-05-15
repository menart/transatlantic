package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import express.atc.backend.helper.MoneySymbol;
import express.atc.backend.model.MoneyModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrdersDto {
    @Schema(description = "Стоимость списка товаров")
    private MoneyModel priceModel;
    @Schema(description = "Валюта заказа")
    private String currency;
    @Schema(description = "Вес товаров")
    private long weight;
    @Schema(description = "Список товаров")
    List<OrderDto> items;

    public List<OrderDto> getItems() {
        AtomicInteger index = new AtomicInteger();
        return items.stream()
                .peek(item -> item.setIndex(index.getAndIncrement()))
                .toList();
    }

    @Schema(description = "Стоимость списка товаров")
    public String getPrice() {
        return MoneySymbol.getStringMoney(priceModel);
    }
}
