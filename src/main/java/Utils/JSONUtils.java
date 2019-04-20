package Utils;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class JSONUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象序列化为JSON字符串
     * @param object
     * @return JSON字符串
     */
    public static String serialize(Object object) {
        Writer write = new StringWriter();
        try {
            objectMapper.writeValue(write, object);
        } catch (JsonGenerationException e) {
        } catch (JsonMappingException e) {
        } catch (IOException e) {
        }
        return write.toString();
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @param object
     * @return JSON字符串
     */
    public static <T> T deserialize(String json, Class<T> clazz) {
        Object object = null;
        try {
            object = objectMapper.readValue(json, TypeFactory.rawClass(clazz));
        } catch (JsonParseException e) {
        } catch (JsonMappingException e) {
        } catch (IOException e) {
        }
        return (T) object;
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @param object
     * @return JSON字符串
     */
    public static <T> T deserialize(String json, TypeReference<T> typeRef) {
        try {
            return (T) objectMapper.readValue(json, typeRef);
        } catch (JsonParseException e) {
        } catch (JsonMappingException e) {
        } catch (IOException e) {
        }
        return null;
    }
}
