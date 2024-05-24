package exceptions;

public class UnknownHTTPMethodException extends RuntimeException {
    public UnknownHTTPMethodException(String message) {
        super(message);
    }
}
