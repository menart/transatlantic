package express.atc.backend.mapper;

import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.dto.OrderDto;
import express.atc.backend.dto.TrackingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring")
public interface TrackingMapper extends EntityMapper<TrackingDto, TrackingEntity> {

    @Mapping(target = "userPhone", source = "phone")
    TrackingEntity toEntity(TrackingDto dto);
}
