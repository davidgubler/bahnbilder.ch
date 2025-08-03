package utils;

public class NotAllowedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Not allowed";
    }
}
