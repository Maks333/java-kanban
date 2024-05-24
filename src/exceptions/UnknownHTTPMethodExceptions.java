package exceptions;

public class UnknownHTTPMethodExceptions extends RuntimeException {
    public UnknownHTTPMethodExceptions(String message) {
        super(message);
    }
}
