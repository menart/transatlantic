package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RateDto {
    @Schema(description = "Наименование валюты")
    private String name;
    @Schema(description = "Код валюты")
    private String code;
    @Schema(description = "Курс за 1 рубль")
    private double rate;
}
