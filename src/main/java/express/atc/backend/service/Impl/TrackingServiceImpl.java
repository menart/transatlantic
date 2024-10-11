package express.atc.backend.service.Impl;

import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.db.repository.TrackingRepository;
import express.atc.backend.dto.TrackingDto;
import express.atc.backend.integration.cargoflow.service.CargoflowService;
import express.atc.backend.mapper.TrackingMapper;
import express.atc.backend.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final CargoflowService cargoflowService;
    private final TrackingRepository trackingRepository;
    private final TrackingMapper trackingMapper;

    @Override
    public TrackingDto find(String trackNumber, String userPhone) {
        TrackingEntity entity = trackingRepository.findByTrackNumber(trackNumber)
                .orElse(findByCargoFlow(trackNumber));
        return trackingMapper.toDto(entity);
    }

    private TrackingEntity findByCargoFlow(String trackNumber) {
        return new TrackingEntity();
    }
}
