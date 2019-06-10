package nl.ou.testar.temporal.scratch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {


        System.out.println("Hello World!");
        ObjectMapper objectMapper = new ObjectMapper();
        Car car = new Car("yellow", "renault");

        File output = new File("car.json");
        try {

            String result = objectMapper.writeValueAsString(car);
            // let's write the resulting json to a file
            if (output.exists() || output.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(output.getAbsolutePath()));
                writer.write(result);
                writer.close();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

