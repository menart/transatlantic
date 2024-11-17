package express.atc.backend.exception;

import org.springframework.http.HttpStatus;

import static express.atc.backend.exception.ExceptionMessage.TRACK_NOT_FOUND;

public class TrackNotFoundException extends ApiException {

    public TrackNotFoundException() {
        super(TRACK_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
