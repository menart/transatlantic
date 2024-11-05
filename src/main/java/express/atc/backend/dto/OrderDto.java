package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @Schema(description = "Наименование товара")
    private String name;
    @Schema(description = "Ссылка на товар")
    private String url;
    @Schema(description = "Количество товара")
    private String quantity;
    @Schema(description = "Стоимость товара")
    private String price;
    @Schema(description = "Вес товара")
    private String weight;
}
