package express.atc.backend.mapper;

import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.dto.TrackingDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TrackingRouteMapper.class})
public interface TrackingMapper extends EntityMapper<TrackingDto, TrackingEntity> {

    @Mapping(target = "userPhone", source = "phone")
    TrackingEntity toEntity(TrackingDto dto);

    @Mapping(target = "phone", source = "userPhone")
    TrackingDto toDto(TrackingEntity entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "orderNumber", source = "orderNumber")
    @Mapping(target = "trackNumber", source = "trackNumber")
    @Mapping(target = "logisticsOrderCode", source = "logisticsOrderCode")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "marketplace", source = "marketplace")
    @Mapping(target = "orderDatetime", source = "orderDatetime")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "goods", source = "goods")
    @Mapping(target = "provider", source = "provider")
    void updateEntityFromDto(TrackingDto dto, @MappingTarget TrackingEntity entity);
}
