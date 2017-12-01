package org.afsirs.web.util;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class JsonUtil {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonFactory FACTORY = new JsonFactory();
    public static final String EMPTY_ARRAY = "[]";
    public static final String EMPTY_DOC = "{}";
    
    public static String toJsonStr(Object data){
        try {
        return new String(toJsonByteArray(data));
        } catch (JsonProcessingException ex) {
            LOG.warn(ex.getMessage());
            return null;
        }
    }
    
    public static byte[] toJsonByteArray(Object data) throws JsonProcessingException {
        return MAPPER.writeValueAsBytes(data);
    }

//    public static byte[] toJsonByteArray(Object data) throws JsonProcessingException, IOException {
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                JsonGenerator generator = FACTORY.createGenerator(baos);) {
//
//            if (data instanceof Map) {
//                toJsonByteArray((Map) data, generator);
//            } else if (data instanceof Object[]) {
//                toJsonByteArray(Arrays.asList((Object[]) data), generator);
//            } else if (data instanceof List) {
//                toJsonByteArray((List) data, generator);
//            } else if (data instanceof Document) {
//                generator.writeRawValue(((Document) data).toJson());
//            } else {
//                generator.writeObject(data);
//            }
//            generator.flush();
//            return baos.toByteArray();
//        }
//    }
//
//    private static void toJsonByteArray(Map<String, Object> data, JsonGenerator generator) throws JsonProcessingException, IOException {
//        generator.writeStartObject();
//        for (String key : data.keySet()) {
//            generator.writeFieldName(key);
//            Object value = data.get(key);
//            if (value instanceof Map) {
//                toJsonByteArray((Map) value, generator);
//            } else if (value instanceof Object[]) {
//                toJsonByteArray(Arrays.asList((Object[]) value), generator);
//            } else if (value instanceof List) {
//                toJsonByteArray((List) value, generator);
//            } else if (value instanceof ObjectId) {
//                toJsonByteArray((ObjectId) value, generator);
//            } else if (value instanceof Document) {
//                generator.writeRawValue(((Document) value).toJson());
//            } else {
//                generator.writeRawValue(MAPPER.writeValueAsString(value));
//            }
//        }
//        generator.writeEndObject();
//    }
//
//    private static void toJsonByteArray(List<Object> data, JsonGenerator generator) throws JsonProcessingException, IOException {
//        generator.writeStartArray();
//        for (Object value : data) {
//            if (value instanceof Map) {
//                toJsonByteArray((Map) value, generator);
//            } else if (value instanceof Object[]) {
//                toJsonByteArray(Arrays.asList((Object[]) value), generator);
//            } else if (value instanceof List) {
//                toJsonByteArray((List) value, generator);
//            } else if (value instanceof ObjectId) {
//                toJsonByteArray((ObjectId) value, generator);
//            } else if (value instanceof Document) {
//                generator.writeRaw(((Document) value).toJson());
//            } else {
//                generator.writeRaw(MAPPER.writeValueAsString(value));
//            }
//        }
//        generator.writeEndArray();
//    }
//
//    private static void toJsonByteArray(ObjectId data, JsonGenerator generator) throws JsonProcessingException, IOException {
//        generator.writeStartObject();
//        generator.writeStringField("$oid", data.toString());
//        generator.writeEndObject();
//    }

    public static <T> T toObject(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (IOException ex) {
            LOG.warn(ex.getMessage());
            return null;
        }
    }

    public static LinkedHashMap toOrderedMap(String json) {
        return toObject(json, LinkedHashMap.class);
    }

    public static HashMap toMap(String json) {
        return toObject(json, HashMap.class);
    }
    
    public static ArrayList toList(String json) {
        return toObject(json, ArrayList.class);
    }
    
    public static <T> T toObject(String json, TypeReference<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (IOException ex) {
            LOG.warn(ex.getMessage());
            return null;
        }
    }
    
//    public static ArrayList<Document> toDocList(String json) {
//        try {
//            ArrayList<Document> jsons = MAPPER.readValue(json, new TypeReference<ArrayList<Document>>() {});
//            return jsons;
//        } catch (IOException ex) {
//            LOG.warn(ex.getMessage());
//            return null;
//        }
//    }
}
