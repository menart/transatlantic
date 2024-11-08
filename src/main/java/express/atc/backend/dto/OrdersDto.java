package express.atc.backend.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import express.atc.backend.serializer.MoneyDeserializer;
import express.atc.backend.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDto {
    @Schema(description = "Стоимость списка товаров")
    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private long price;
    @Schema(description = "Валюта заказа")
    private String currency;
    @Schema(description = "Вес товаров")
    private long weight;
    @Schema(description = "Список товаров")
    List<OrderDto> items;
}
