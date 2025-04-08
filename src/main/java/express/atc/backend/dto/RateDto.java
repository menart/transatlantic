package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RateDto {
    @Schema(description = "Индекс")
    private int index;
    @Schema(description = "Наименование валюты")
    private String name;
    @Schema(description = "Код валюты")
    private String code;
    @Schema(description = "Курс за 1 рубль")
    private double rate;
}
