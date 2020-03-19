package academy.redoak.servlet.chatserver.util;

import academy.redoak.servlet.chatserver.http.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class Mapper {

    private Mapper() {
        // private Constructor due to utility class
    }

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String getAsJson(Response object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public static <T> T getAsObject(String str, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(str, clazz);
    }
}
