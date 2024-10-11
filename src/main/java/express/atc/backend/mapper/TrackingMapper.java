package express.atc.backend.mapper;

import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.dto.TrackingDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrackingMapper extends EntityMapper<TrackingDto, TrackingEntity> {
}
