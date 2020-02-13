package nl.ou.testar.temporal.util;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSONHandler {
    public static Object load(String fromFile, Class cls) { // CLASS method
        try {
            File input = new File(fromFile);
            if (input.exists()) {
                ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
                Object result = objectMapper.readValue(input, cls);
                return cls.cast(result);
            }
        } catch (
                JsonProcessingException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void save(Object content, String toFile, boolean failOnEmptyBean) {
        try {
            File output = new File(toFile);
            ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, failOnEmptyBean);
            objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true); //
            String result = objectMapper.writeValueAsString(content);
            // let's write the resulting json to a file
            if (output.exists() || output.createNewFile()) {
                BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output.getAbsolutePath()), StandardCharsets.UTF_8));
                writer.append(result);
                writer.close();
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
    public static void save(Object content, String toFile ) {
        save( content,  toFile, true);

    }
    public static ArrayList<String> peek(String target, String inFile) {
        ArrayList<String>  tmp = new ArrayList<String>();
        try (JsonParser jParser = new JsonFactory()
                .createParser(new File(inFile))) {

            // loop until token equal to "}"
            while (jParser.nextToken() != JsonToken.END_OBJECT) {

                String fieldname = jParser.getCurrentName();

                if (target.equals(fieldname)) {
                    // current token is the target,
                    // move to next, which is field value
                    JsonToken token = jParser.nextToken();
                    if (token == JsonToken.START_ARRAY) {
                        while (jParser.nextToken() != JsonToken.END_ARRAY) {
                            tmp.add(jParser.getText());
                        }
                    }
                    else
                        if (token == JsonToken.START_OBJECT){
                            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                                tmp.add(jParser.getText());
                            }
                        }
                        else{
                        tmp.add(jParser.getText());
                        }
                    break;  //stop when found
                }
            }

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
return tmp;//either empty of filed was found
    }

    //=============not used ??
    public Map<String, Object> peekNode (String inFile) {
        return peekNode(inFile, "", true, "");
    }
    public Map<String, Object> peekNode (String inFile, String nodeStartsWith) {
        return peekNode(inFile, nodeStartsWith, true, "");
    }
    public Map<String, Object> peekNode (String inFile, Boolean primitvesOnly) {
        return peekNode(inFile, "", primitvesOnly, "");
    }
    public Map<String, Object> peekNode (String inFile, String nodeStartsWith, Boolean primitvesOnly) {
        return peekNode(inFile, nodeStartsWith, primitvesOnly, "");
    }

    private Map<String, Object> peekNode (String inFile, String nodeStartsWith,
                                          Boolean primitvesOnly, String subnode){
        // actually need proper error  handling... throws Exception {
        // peeks only one level deep from the root

        File jsonFile = new File(inFile).getAbsoluteFile();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(jsonFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (subnode != "") root = root.get(subnode);
        return getFields(root, nodeStartsWith, primitvesOnly);
    }
    private static Map<String, Object> getFields (JsonNode data, String nodeStartsWith, Boolean primitivesonly){
        Map<String, Object> attributes = new HashMap<>();
        for (Iterator<Map.Entry<String, JsonNode>> it = data.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> field = it.next();
            String key = field.getKey();
            if (key.startsWith(nodeStartsWith)) {
                JsonNode value = field.getValue();
                if (value.isBoolean()) {
                    attributes.put(key, value.asBoolean());
                } else if (value.isLong()) {
                    attributes.put(key, value.asLong());
                } else if (value.isDouble()) {
                    attributes.put(key, value.asDouble());
                } else if (!primitivesonly && value.isArray()) {
                    attributes.put(key, value.toString());
                } else if (!primitivesonly && value.isObject()) {
                    attributes.put(key, value.toString());

                } else
                    attributes.put(key, value.asText());
            }
        }
        return attributes;
    }
    //=============not used??
}



