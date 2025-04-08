package express.atc.backend.mapper;

import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.dto.TrackingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TrackingRouteMapper.class})
public interface TrackingMapper extends EntityMapper<TrackingDto, TrackingEntity> {

    @Mapping(target = "userPhone", source = "phone")
    TrackingEntity toEntity(TrackingDto dto);

    @Mapping(target = "phone", source = "userPhone")
    TrackingDto toDto(TrackingEntity entity);
}
