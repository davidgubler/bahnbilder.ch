package utils;

public class AlreadyInProgressException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String operation;

    public AlreadyInProgressException(String operation) {
        this.operation = operation;
    }

    @Override
    public String getMessage() {
        return "Operation already in progress: " + operation;
    }
}
