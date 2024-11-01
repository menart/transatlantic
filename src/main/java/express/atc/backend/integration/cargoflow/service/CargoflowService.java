package express.atc.backend.integration.cargoflow.service;

import express.atc.backend.dto.TrackingDto;

import java.util.List;

public interface CargoflowService {

    List<TrackingDto> getInfoByTrackNumber(String trackNumber);
}
