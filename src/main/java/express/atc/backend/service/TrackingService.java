package express.atc.backend.service;

import express.atc.backend.dto.TrackingDto;
import express.atc.backend.exception.TrackNotFoundException;

public interface TrackingService {

    TrackingDto find(String trackNumber, String userPhone) throws TrackNotFoundException;
}
