package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TrackingNeedingDto(
        @Schema(description = "Список трек-номеров требующие оплаты")
        List<String> needPay,
        @Schema(description = "Список трек-номеров требующие загрузки документов")
        List<String> needDocument
) {
}
