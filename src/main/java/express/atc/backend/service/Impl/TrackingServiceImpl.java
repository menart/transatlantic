package express.atc.backend.service.Impl;

import express.atc.backend.integration.cargoflow.service.CargoflowService;
import express.atc.backend.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final CargoflowService cargoflowService;

    @Override
    public String find(String trackNumber, String userPhone) {

        return userPhone;
    }
}
