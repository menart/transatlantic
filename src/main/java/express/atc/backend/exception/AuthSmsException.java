package express.atc.backend.exception;

import org.springframework.http.HttpStatus;

public class AuthSmsException extends ApiException{

    public AuthSmsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
