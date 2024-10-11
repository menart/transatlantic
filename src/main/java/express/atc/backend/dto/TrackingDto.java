package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackingDto {

    @Schema(description = "Номер заказа")
    private String orderNumber;
    @Schema(description = "Номер трека отслеживания")
    private String trackNumber;
    @Schema(description = "Адрес получателя")
    private String address;
    @Schema(description = "Магазин")
    private String marketplace;
    @Schema(description = "Состав заказа, может отсутствовать, если не совпадает номер телефона")
    private OrderDto items;
}
