package express.atc.backend.mapper;

import express.atc.backend.db.entity.TrackingRouteEntity;
import express.atc.backend.dto.TrackingRouteDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@DecoratedWith(TrackingRouteMapperDecorator.class)
public interface TrackingRouteMapper extends EntityMapper<TrackingRouteDto, TrackingRouteEntity> {

    TrackingRouteDto toDto(TrackingRouteEntity entity);
}
