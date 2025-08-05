package express.atc.backend.service.Impl;

import express.atc.backend.db.entity.StatusEntity;
import express.atc.backend.db.repository.StatusRepository;
import express.atc.backend.enums.TrackingStatus;
import express.atc.backend.model.TrackingStatusModel;
import express.atc.backend.service.StatusService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;
    private Map<String, TrackingStatusModel> mapStatus = new HashMap<>();

    @PostConstruct
    public void updateStatus() {
        mapStatus = statusRepository.findAll().stream()
                .map(this::getStatus)
                .collect(Collectors.toMap(
                        TrackingStatusModel::status, Function.identity()));
    }

    @Override
    public TrackingStatusModel getStatus(String status) {
        var savedStatus = mapStatus.get(status.toUpperCase());
        return Optional.ofNullable(savedStatus)
                .orElseGet(()->getUnknownStatus(status));
    }

    private TrackingStatusModel getStatus(StatusEntity status) {
        return new TrackingStatusModel(
                status.getStatus().toUpperCase(),
                status.getDescriptionRus(),
                status.getDescriptionEng(),
                status.getMapStatus()
        );
    }

    private TrackingStatusModel getUnknownStatus(String status) {
        log.error("Unknown status: {}", status);
        return new TrackingStatusModel(
                status.toUpperCase(),
                "-",
                "-",
                TrackingStatus.ACTIVE
        );
    }
}
