package express.atc.backend.service.Impl;

import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.db.repository.TrackingRepository;
import express.atc.backend.dto.TrackingDto;
import express.atc.backend.exception.TrackNotFoundException;
import express.atc.backend.integration.cargoflow.service.CargoflowService;
import express.atc.backend.mapper.TrackingMapper;
import express.atc.backend.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final CargoflowService cargoflowService;
    private final TrackingRepository trackingRepository;
    private final TrackingMapper trackingMapper;

    @Override
    public TrackingDto find(String trackNumber, String userPhone) throws TrackNotFoundException {
        Optional<TrackingEntity> entity = trackingRepository.findByTrackNumber(trackNumber);
        TrackingDto dto = trackingMapper.toDto(entity.isEmpty() ? findByCargoFlow(trackNumber) : entity.get());
        if (userPhone == null || !dto.getPhone().equals(userPhone)) {
            dto.setGoods(null);
        }
        return dto;
    }

    private TrackingEntity findByCargoFlow(String trackNumber) throws TrackNotFoundException {
        var trackingEntity = trackingMapper.toEntity(getInfoByTrackNumber(trackNumber)
                .orElseThrow(TrackNotFoundException::new));
        return trackingRepository.save(trackingEntity);
    }

    private Optional<TrackingDto> getInfoByTrackNumber(String trackNumber) {
        List<TrackingDto> trackingDtoList = cargoflowService.getInfoByTrackNumber(trackNumber);
        if (CollectionUtils.isNotEmpty(trackingDtoList)) {
            return Optional.of(trackingDtoList.get(0));
        } else {
            return Optional.empty();
        }
    }
}
