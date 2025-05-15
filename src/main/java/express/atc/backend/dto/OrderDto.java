package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import express.atc.backend.helper.MoneySymbol;
import express.atc.backend.model.MoneyModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {
    @Schema(description = "index")
    private int index;
    @Schema(description = "Наименование товара")
    private String name;
    @Schema(description = "Ссылка на товар")
    private String url;
    @Schema(description = "Количество товара")
    private String quantity;
    @Schema(description = "Стоимость товара")
    private MoneyModel priceModel;
    @Schema(description = "Вес товара")
    private String weight;

    @Schema(description = "Стоимость товара")
    public String getPrice() {
        return MoneySymbol.getStringMoney(priceModel);
    }
}
