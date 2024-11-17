package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import express.atc.backend.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static express.atc.backend.Constants.*;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculateDto {

    @Schema(description = "Тип расчета платежа")
    private String type;
    @Schema(description = STRING_FEE)
    @JsonSerialize(using = MoneySerializer.class)
    private Long fee;
    @Schema(description = STRING_TAX)
    @JsonSerialize(using = MoneySerializer.class)
    private Long tax;
    @Schema(description = STRING_COMPENSATION)
    @JsonSerialize(using = MoneySerializer.class)
    private Long compensation;
    @Schema(description = "Итоговая сумма")
    @JsonSerialize(using = MoneySerializer.class)
    private Long paid;
    @Schema(description = "Список курсов")
    private List<RateDto> rates;
    @Schema(description = "Ссылка для оплаты")
    private String url;

    public List<RateDto> getRates() {
        AtomicInteger index = new AtomicInteger();
        return rates.stream()
                .peek(item -> item.setIndex(index.getAndIncrement()))
                .toList();
    }
}
