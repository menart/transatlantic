package express.atc.backend.facade;

import express.atc.backend.enums.TrackingStatus;

public interface MessageFacade {

    void sendTrackingInfo(String userPhone, TrackingStatus status, String trackNumber, String marketplace);
}
