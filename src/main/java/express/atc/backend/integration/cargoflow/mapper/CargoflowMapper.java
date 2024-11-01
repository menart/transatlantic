package express.atc.backend.integration.cargoflow.mapper;

import express.atc.backend.dto.OrderDto;
import express.atc.backend.dto.TrackingDto;
import express.atc.backend.integration.cargoflow.dto.CargoflowOrder;
import express.atc.backend.integration.cargoflow.dto.Order.OrderGood;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CargoflowMapper {

    @Mapping(target = "phone", source = "properties.buyer.phone")
    @Mapping(target = "trackNumber", source = "trackingNumber")
    @Mapping(target = "orderDatetime", source = "createdAt")
    @Mapping(target = "orderNumber", source = "properties.epOrderId")
    @Mapping(target = "address", source = "properties.buyer.address.detailAddress")
    @Mapping(target = "marketplace", source = "properties.sender.companyName")
    @Mapping(target = "items",
            expression = "java(toListItems(order.getProperties().getParcel().getGoodsList()))")
    TrackingDto toTracking(CargoflowOrder order);

    @Named("toItems")
    List<OrderDto> toListItems(List<OrderGood> goods);
}
