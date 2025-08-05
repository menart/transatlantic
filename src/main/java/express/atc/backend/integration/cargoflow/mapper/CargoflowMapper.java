package express.atc.backend.integration.cargoflow.mapper;

import express.atc.backend.dto.OrderDto;
import express.atc.backend.dto.OrdersDto;
import express.atc.backend.dto.TrackingDto;
import express.atc.backend.dto.TrackingRouteDto;
import express.atc.backend.enums.TrackingStatus;
import express.atc.backend.integration.cargoflow.dto.CargoflowOrder;
import express.atc.backend.integration.cargoflow.dto.Order.OrderGood;
import express.atc.backend.integration.cargoflow.dto.Order.OrderParcel;
import express.atc.backend.integration.cargoflow.dto.Order.OrderPropertyDto;
import express.atc.backend.integration.cargoflow.dto.OrderHistory;
import express.atc.backend.model.MoneyModel;
import org.apache.logging.log4j.util.Strings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CargoflowMapper {

    @Mapping(target = "phone", expression = "java(getPhone(order.properties()))")
    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "trackNumber", source = "trackingNumber")
    @Mapping(target = "orderDatetime", source = "createdAt")
    @Mapping(target = "orderNumber", source = "reference")
    @Mapping(target = "address", expression = "java(getAddress(order.properties()))")
    @Mapping(target = "marketplace", source = "properties.sender.companyName")
    @Mapping(target = "status", expression = "java(setActiveStatus())")
    @Mapping(target = "goods",
            expression = "java(toOrders(order.properties().parcel()))")
    @Mapping(target = "providerId", source = "route.customsOperator.providerId")
    TrackingDto toTracking(CargoflowOrder order);

    @Mapping(target = "priceModel", expression = "java(toMoneyModel(parcel.priceCurrency(), parcel.price()))")
    @Mapping(target = "weight", source = "suggestedWeight")
    @Mapping(target = "currency", source = "priceCurrency")
    @Mapping(target = "items",
            expression = "java(toListItems(parcel.goodsList()))")
    OrdersDto toOrders(OrderParcel parcel);

    @Mapping(target = "priceModel", expression = "java(toMoneyModel(order.priceCurrency(), order.price()))")
    OrderDto toOrder(OrderGood order);

    List<OrderDto> toListItems(List<OrderGood> goods);

    @Mapping(target = "routeId", source = "id")
    @Mapping(target = "location", source = "opLocation")
    @Mapping(target = "routeTime", expression = "java(toLocalDateTime(history))")
    TrackingRouteDto toRoutes(OrderHistory history);

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(OrderHistory history) {
        return LocalDateTime.ofInstant(history.opTime(), history.opTimezone().toZoneId());
    }

    default TrackingStatus setActiveStatus() {
        return TrackingStatus.ACTIVE;
    }

    default String getPhone(OrderPropertyDto property) {
        String phone = property.buyer() != null ? property.buyer().phone() : property.receiver().phone();
        if (!Strings.isBlank(phone)) {
            phone = phone.replaceAll("[^0-9]", "");
            if (phone.charAt(0) == '8') {
                phone = "7" + phone.substring(1);
            }
            if (phone.length() < 11 && phone.charAt(0) == '9') {
                phone = "7" + phone;
            }
        }
        return phone;
    }

    default String getAddress(OrderPropertyDto property) {
        return property.buyer() != null
                ? property.buyer().address().detailAddress()
                : property.receiver().address().detailAddress();
    }

    default MoneyModel toMoneyModel(String currency, Long price) {
        return new MoneyModel(price, currency);
    }
}
