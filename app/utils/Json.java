package utils;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;

public class Json {
    public static final ObjectMapper MAPPER;
    static {
        MAPPER = new ObjectMapper();
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}