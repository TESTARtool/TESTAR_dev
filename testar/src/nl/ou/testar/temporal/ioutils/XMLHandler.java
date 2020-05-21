package nl.ou.testar.temporal.ioutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class XMLHandler {
    /**
     * @param fromFile source file containing the XML
     * @param cls JAVA Class  to put the XML into
     * @return Object of type Class containing the XML values
     */
    @SuppressWarnings("unused")
    private static <T> T load(String fromFile, Class<T> cls) { // future use
        try {
            File input = new File(fromFile);
            if (input.exists()) {
                ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
                return objectMapper.readValue(input, cls);
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void save(Object content, String toFile, boolean failOnEmptyBean,boolean zip) {
        try {
            File output = new File(toFile);

                XmlMapper xmlMapper = new XmlMapper();
                xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, failOnEmptyBean);
                //xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                //xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
                xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
                String result = xmlMapper.writeValueAsString(content);
                if (!zip) {
                    if (output.exists() || output.createNewFile()) {
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output.getAbsolutePath())));//, StandardCharsets.UTF_8));
                        writer.append(result);
                        writer.close();
                    }
                }else {
                    FileOutputStream fos = new FileOutputStream(toFile+".zip");
                    ZipOutputStream zipOut = new ZipOutputStream(fos);
                    InputStream sis = new ByteArrayInputStream(result.getBytes()); // default charset!

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

                }

        } catch (
                IOException e) {
            e.printStackTrace();
        }
/*        catch (XMLStreamException e) {
            e.printStackTrace();
        }*/
    }
    public  static void save(Object content, String toFile,boolean zip ) {
          save( content,  toFile, true,zip);

    }


}


