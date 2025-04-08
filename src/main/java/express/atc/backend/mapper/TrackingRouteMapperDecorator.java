package express.atc.backend.mapper;

import express.atc.backend.db.entity.TrackingRouteEntity;
import express.atc.backend.dto.MessageDto;
import express.atc.backend.dto.TrackingRouteDto;
import express.atc.backend.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
public abstract class TrackingRouteMapperDecorator implements TrackingRouteMapper {

    @Autowired
    private StatusService statusService;
    @Autowired
    @Qualifier("delegate")
    private TrackingRouteMapper delegate;

    @Override
    public TrackingRouteDto toDto(TrackingRouteEntity entity) {
        return addMessage(delegate.toDto(entity));
    }

    @Override
    public List<TrackingRouteDto> toDto(List<TrackingRouteEntity> entity) {
        return entity.stream()
                .map(delegate::toDto)
                .peek(this::addMessage)
                .toList();
    }

    private TrackingRouteDto addMessage(TrackingRouteDto dto) {
        var modelStatus = statusService.getStatus(dto.getStatus());
        return modelStatus != null
                ? dto.setMessage(new MessageDto(modelStatus.descriptionRus(), modelStatus.descriptionEng()))
                : dto;
    }
}
