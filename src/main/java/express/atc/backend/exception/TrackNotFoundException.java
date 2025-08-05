package express.atc.backend.exception;

import org.springframework.http.HttpStatus;

import static express.atc.backend.Constants.TRACK_NOT_FOUND;

public class TrackNotFoundException extends ApiException {

    public TrackNotFoundException(String trackNumber) {
        super(String.format(TRACK_NOT_FOUND, trackNumber), HttpStatus.NOT_FOUND);
    }
}
