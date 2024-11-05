package express.atc.backend.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import express.atc.backend.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculateDto {

    @Schema(description = "Тип расчета платежа")
    private String type;
    @Schema(description = "Сумма платежа")
    @JsonSerialize(using = MoneySerializer.class)
    private Long fee;
}
