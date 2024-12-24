package express.atc.backend.model;

import express.atc.backend.enums.TrackingStatus;

public record TrackingStatusModel(
        String status,
        String descriptionRus,
        String descriptionEng,
        TrackingStatus mapStatus
) {
}
