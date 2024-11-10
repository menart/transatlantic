package express.atc.backend.integration.cargoflow.service;

import express.atc.backend.dto.TrackingDto;
import express.atc.backend.dto.TrackingRouteDto;

import java.util.List;
import java.util.Set;

public interface CargoflowService {

    List<TrackingDto> getInfoByTrackNumber(String trackNumber);

    Set<TrackingRouteDto> updateRoute(Long orderId, Long historyId);
}
