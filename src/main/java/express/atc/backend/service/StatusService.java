package express.atc.backend.service;

import express.atc.backend.model.TrackingStatusModel;

public interface StatusService {
    TrackingStatusModel getStatus(String status);
}
