package express.atc.backend.service.Impl;

import express.atc.backend.calculate.CalcCustomsFee;
import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.db.repository.TrackingRepository;
import express.atc.backend.dto.TrackingDto;
import express.atc.backend.exception.BadRequestException;
import express.atc.backend.exception.TrackNotFoundException;
import express.atc.backend.integration.cargoflow.service.CargoflowService;
import express.atc.backend.mapper.TrackingMapper;
import express.atc.backend.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final CargoflowService cargoflowService;
    private final TrackingRepository trackingRepository;
    private final TrackingMapper trackingMapper;
    private final CalcCustomsFee calcCustomsFee;

    @Override
    public TrackingDto find(String trackNumber, String userPhone) throws TrackNotFoundException {
        Optional<TrackingEntity> entity = trackingRepository.findByTrackNumber(trackNumber);
        TrackingDto dto = trackingMapper.toDto(entity.isEmpty() ? findByCargoFlow(trackNumber) : entity.get());
        if (!dto.getPhone().equals(userPhone)) {
            dto.setGoods(null);
        } else {
            try {
                dto.setCalculate(calcCustomsFee.calculate(dto.getGoods()));
            } catch (BadRequestException exception) {
                log.error(exception.getMessage());
            }
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
            return Optional.of(trackingDtoList.getFirst());
        } else {
            return Optional.empty();
        }
    }
}
