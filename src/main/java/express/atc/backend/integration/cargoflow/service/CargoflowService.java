package express.atc.backend.integration.cargoflow.service;

import express.atc.backend.dto.TrackingDto;

public interface CargoflowService {

    TrackingDto getInfoByTrackNumber(String trackNumber);
}
