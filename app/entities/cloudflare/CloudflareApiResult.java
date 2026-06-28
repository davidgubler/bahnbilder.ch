package entities.cloudflare;

import java.util.List;

public class CloudflareApiResult<T> {
    private Boolean success;
    private List<String> errors;
    private List<String> messages;
    private CloudflareResultInfo result_info;
    private T result;

    public CloudflareApiResult() {
        // empty constructor for deserializer
    }

    public T getResult() {
        return result;
    }
}