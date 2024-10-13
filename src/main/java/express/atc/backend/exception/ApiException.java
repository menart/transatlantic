package express.atc.backend.exception;

public class ApiException extends Exception{
    public ApiException(String message) {
        super(message);
    }
}
