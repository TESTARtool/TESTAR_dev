package nl.ou.testar.temporal.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class XMLHandler {
    private  static Object loadNA(String fromFile, Class cls) { // CLASS method
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

            //ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            //objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, failOnEmptyBean);
            // let's write the resulting json to a file
            if (output.exists() || output.createNewFile()) {
                XmlMapper xmlMapper = new XmlMapper();
                xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, failOnEmptyBean);

                //xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                //xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
                xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);

               /* StringWriter stringWriter = new StringWriter();
                XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
                xmlOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
                // above line is not recommended https://github.com/FasterXML/jackson-dataformat-xml/issues/326#issue-391788245
                XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(stringWriter);


                //xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, failOnEmptyBean);
                sw.writeStartDocument("utf-8","1.0");
                xmlMapper.writeValue(sw,content);
                sw.writeComment("test CSS");
                sw.writeEndDocument();
                sw.flush();
                sw.close();
                */
                String result = xmlMapper.writeValueAsString(content);
                BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output.getAbsolutePath())));//, StandardCharsets.UTF_8));
                writer.append(result);
                writer.close();



            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
/*        catch (XMLStreamException e) {
            e.printStackTrace();
        }*/
    }
    public  static void save(Object content, String toFile ) {
          save( content,  toFile, true);

    }


}


