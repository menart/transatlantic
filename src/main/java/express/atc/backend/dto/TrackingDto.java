package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import express.atc.backend.enums.TrackingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.TreeSet;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackingDto implements Comparable<TrackingDto> {
    @JsonIgnore
    private String phone;
    @Schema(description = "Id заказа")
    private Long orderId;
    @Schema(description = "Номер заказа")
    private String orderNumber;
    @Schema(description = "Номер трека отслеживания")
    private String trackNumber;
    @Schema(description = "Логистический код заказа")
    private String logisticsOrderCode;
    @Schema(description = "Адрес получателя")
    private String address;
    @Schema(description = "Магазин")
    private String marketplace;
    @Schema(description = "Состав заказа, может отсутствовать, если не совпадает номер телефона")
    private OrdersDto goods;
    @Schema(description = "Дата создания заказа yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime orderDatetime;
    @Schema(description = "Статус заказа")
    private TrackingStatus status;
    @Schema(description = "Список маршрутов")
    private TreeSet<TrackingRouteDto> routes;
    @JsonIgnore
    @Schema(description = "Customs operator Id")
    private String providerId;

    @Override
    public int compareTo(TrackingDto o) {
        return orderId.compareTo(o.orderId);
    }
}
