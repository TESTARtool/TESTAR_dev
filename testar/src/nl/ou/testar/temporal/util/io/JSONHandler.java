package nl.ou.testar.temporal.util.io;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JSONHandler {
    public static <T> Object load(String fromFile, Class<T> cls) { // CLASS method
        try {
            File input = new File(fromFile);
            if (input.exists()) {
                ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
                Object result = objectMapper.readValue(input, cls);
                return cls.cast(result);
            }
        } catch (
                IOException e) { // catches also JsonProcessingException
            e.printStackTrace();
        }
        return null;
    }
      public static void save(Object content, String toFile, boolean failOnEmptyBean,boolean zip) {
        try {
            File output = new File(toFile);
            ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, failOnEmptyBean);
            objectMapper.getFactory().configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);
            // let's write the resulting json to a file
            objectMapper.writeValue(output, content);

            if (zip) {
                FileOutputStream fos = new FileOutputStream(toFile+".zip");
                ZipOutputStream zipOut = new ZipOutputStream(fos);
                InputStream sis = new FileInputStream(toFile); // default charset!

                ZipEntry zipEntry = new ZipEntry(Paths.get(toFile).getFileName().toString());
                zipOut.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while((length = sis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                zipOut.close();
                sis.close();
                fos.close();
                //noinspection ResultOfMethodCallIgnored
                output.delete();
            }

        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
    public static void save(Object content, String toFile, boolean zip ) {
        save( content,  toFile, true,zip);

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

        } catch (IOException e) {  //also catches JsonGenerationException,JsonProcessingException
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
    public Map<String, Object> peekNode (String inFile, Boolean primitivesOnly) {
        return peekNode(inFile, "", primitivesOnly, "");
    }
    public Map<String, Object> peekNode (String inFile, String nodeStartsWith, Boolean primitivesOnly) {
        return peekNode(inFile, nodeStartsWith, primitivesOnly, "");
    }

    private Map<String, Object> peekNode (String inFile, String nodeStartsWith,
                                          Boolean primitivesOnly, String subnode){
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
        if (!subnode.equals("")) root = root.get(subnode);
        return getFields(root, nodeStartsWith, primitivesOnly);
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



