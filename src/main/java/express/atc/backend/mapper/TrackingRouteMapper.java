package express.atc.backend.mapper;

import express.atc.backend.db.entity.TrackingRouteEntity;
import express.atc.backend.dto.TrackingRouteDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrackingRouteMapper extends EntityMapper<TrackingRouteDto, TrackingRouteEntity> {

}
