package academy.redoak.servlet.chatserver.util;

import academy.redoak.servlet.chatserver.http.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Utility class for mapping objects to Strings and Strings to objects.
 */
public final class Mapper {

    private Mapper() {
        // private Constructor due to utility class
    }

    private static ObjectMapper mapper = new ObjectMapper();

    /*
     * Configuring the Jackson mapper object.
     */
    static {
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Writes an object into a string in JSON format.
     *
     * @param object The object to be written into the string.
     * @return The written string in JSON format.
     * @throws JsonProcessingException Standard Exception from Jackson, which may be thrown on issues.
     */
    public static String getAsJson(Response object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    /**
     * Reads an object from a string in JSON format.
     *
     * @param str The string to read the object from.
     * @param clazz The class of the object being read,
     * @param <T> The type of clazz.
     * @return The read object.
     *
     * @throws JsonProcessingException Standard Exception from Jackson, which may be thrown on issues.
     */
    public static <T> T getAsObject(String str, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(str, clazz);
    }
}
