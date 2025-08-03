package utils;

public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String object;

    public NotFoundException(String object) {
        this.object = object;
    }

    @Override
    public String getMessage() {
        return object + " not found";
    }
}
