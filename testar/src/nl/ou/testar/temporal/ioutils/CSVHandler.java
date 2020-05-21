package nl.ou.testar.temporal.ioutils;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVHandler {
    public static <T> List<T> load(String fromFile, Class<T> cls) { // CLASS method
        try {
            File input = new File(fromFile);
            if (input.exists()) {
                FileInputStream fis = new FileInputStream(fromFile);
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                CsvToBeanBuilder<T> csvBuilder= new CsvToBeanBuilder<>(isr)  ;
                return csvBuilder.withType(cls).withSeparator(';').build().parse();
            }
        }  catch (
                IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static <T>void save(List<T> content, String toFile) {
        try {
            //File output = new File(toFile);
            //if (output.exists() || output.createNewFile()) {

                FileOutputStream fos = new FileOutputStream(toFile);
                OutputStreamWriter osw = new OutputStreamWriter(fos,StandardCharsets.UTF_8);

                //BufferedWriter writer = new BufferedWriter(new FileWriter(output.getAbsolutePath()));
                //StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
                StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(osw).withSeparator(';').build();

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


