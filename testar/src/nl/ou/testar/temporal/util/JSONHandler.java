package nl.ou.testar.temporal.util;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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

}


