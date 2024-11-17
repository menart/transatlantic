package express.atc.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PageDto<T> {

    @Schema(description = "Список")
    private List<T> list;
    @Schema(description = "Номер страницы")
    private int pageNumber;
    @Schema(description = "Количество страниц")
    private int numberOfPage;
    @Schema(description = "Количество объектов на странице")
    private int quantityPerPage;
}
