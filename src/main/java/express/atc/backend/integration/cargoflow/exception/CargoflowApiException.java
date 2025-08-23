package express.atc.backend.integration.cargoflow.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CargoflowApiException extends RuntimeException {
    private final HttpStatus status;
    private final String responseBody;

    public CargoflowApiException(String message, HttpStatus status, String responseBody) {
        super(message);
        this.status = status;
        this.responseBody = responseBody;
    }

    public CargoflowApiException(String message, HttpStatus status) {
        this(message, status, null);
    }
}