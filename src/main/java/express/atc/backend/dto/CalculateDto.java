package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import express.atc.backend.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculateDto {

    @Schema(description = "Тип расчета платежа")
    private String type;
    @Schema(description = "Таможенная пошлина")
    @JsonSerialize(using = MoneySerializer.class)
    private Long fee;
    @Schema(description = "Сбор за таможенное оформление")
    @JsonSerialize(using = MoneySerializer.class)
    private Long tax;
    @Schema(description = "Компенсация эквайринга")
    @JsonSerialize(using = MoneySerializer.class)
    private Long compensation;
    @Schema(description = "Итоговая сумма")
    @JsonSerialize(using = MoneySerializer.class)
    private Long paid;
    @Schema(description = "Список курсов")
    private List<RateDto> rates;
}
