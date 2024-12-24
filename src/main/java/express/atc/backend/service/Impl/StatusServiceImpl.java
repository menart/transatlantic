package express.atc.backend.service.Impl;

import express.atc.backend.db.entity.StatusEntity;
import express.atc.backend.db.repository.StatusRepository;
import express.atc.backend.model.TrackingStatusModel;
import express.atc.backend.service.StatusService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return mapStatus.get(status);
    }

    private TrackingStatusModel getStatus(StatusEntity status) {
        return new TrackingStatusModel(
                status.getStatus(),
                status.getDescriptionRus(),
                status.getDescriptionEng(),
                status.getMapStatus()
        );
    }
}
