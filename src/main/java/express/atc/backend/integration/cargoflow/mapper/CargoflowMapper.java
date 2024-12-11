package express.atc.backend.integration.cargoflow.mapper;

import express.atc.backend.dto.OrderDto;
import express.atc.backend.dto.OrdersDto;
import express.atc.backend.dto.TrackingDto;
import express.atc.backend.dto.TrackingRouteDto;
import express.atc.backend.integration.cargoflow.dto.CargoflowOrder;
import express.atc.backend.integration.cargoflow.dto.Order.OrderGood;
import express.atc.backend.integration.cargoflow.dto.Order.OrderParcel;
import express.atc.backend.integration.cargoflow.dto.OrderHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CargoflowMapper {

    @Mapping(target = "phone", source = "properties.buyer.phone")
    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "trackNumber", source = "trackingNumber")
    @Mapping(target = "orderDatetime", source = "createdAt")
    @Mapping(target = "orderNumber", source = "properties.epOrderId")
    @Mapping(target = "address", source = "properties.buyer.address.detailAddress")
    @Mapping(target = "marketplace", source = "properties.sender.companyName")
    @Mapping(target = "goods",
            expression = "java(toOrders(order.properties().parcel()))")
    TrackingDto toTracking(CargoflowOrder order);

    @Mapping(target = "price", source = "price")
    @Mapping(target = "weight", source = "suggestedWeight")
    @Mapping(target = "currency", source = "priceCurrency")
    @Mapping(target = "items",
            expression = "java(toListItems(parcel.goodsList()))")
    OrdersDto toOrders(OrderParcel parcel);

    List<OrderDto> toListItems(List<OrderGood> goods);

    @Mapping(target = "routeId", source = "id")
    @Mapping(target = "location", source = "opLocation")
    @Mapping(target = "routeTime", expression = "java(toLocalDateTime(history))")
    TrackingRouteDto toRoutes(OrderHistory history);

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(OrderHistory history) {
        return LocalDateTime.ofInstant(history.opTime(), history.opTimezone().toZoneId());
    }
}
