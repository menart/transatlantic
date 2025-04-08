package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import express.atc.backend.serializer.MoneyDeserializer;
import express.atc.backend.serializer.MoneySerializer;
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
    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private Long price;
    @Schema(description = "Вес товара")
    private String weight;
}
