package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TrackingPageDto(
        @Schema(description = "Список")
        List<TrackingDto> list,
        @Schema(description = "Номер страницы")
        int pageNumber,
        @Schema(description = "Количество страниц")
        int numberOfPage,
        @Schema(description = "Количество объектов на странице")
        int quantityPerPage
        , TrackingNeedingDto needs
) {
}
