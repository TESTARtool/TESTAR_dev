package nl.ou.testar.temporal.util;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVHandler {
    public static List load(String fromFile, Class cls) { // CLASS method
        try {
            File input = new File(fromFile);
            if (input.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(input.getAbsoluteFile()));


                FileInputStream fis = new FileInputStream(fromFile);
                     InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                List result  = new CsvToBeanBuilder(isr).withType(cls).build().parse();
               // List result  = new CsvToBeanBuilder(reader).withType(cls).build().parse();
                return result;
            }
        }  catch (
                IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void save(List content, String toFile) {
        try {
            //File output = new File(toFile);
            //if (output.exists() || output.createNewFile()) {

                FileOutputStream fos = new FileOutputStream(toFile);
                OutputStreamWriter osw = new OutputStreamWriter(fos,StandardCharsets.UTF_8);

                //BufferedWriter writer = new BufferedWriter(new FileWriter(output.getAbsolutePath()));
                //StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
                StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(osw).build();
                beanToCsv.write(content);
                //writer.close();
                osw.close();
           // }
        } catch (
                IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }



}


