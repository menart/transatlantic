package express.atc.backend.service;

import express.atc.backend.dto.TrackingDto;

public interface TrackingService {

    TrackingDto find(String trackNumber, String userPhone);
}
