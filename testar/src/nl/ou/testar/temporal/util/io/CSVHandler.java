package nl.ou.testar.temporal.util.io;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.bean.CsvToBean;
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
                //inspired by https://sourceforge.net/p/opencsv/support-requests/50/ for backslash escape problem
                //backslash is the  escape character when reading files via default CSVReader !!
                RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().withSeparator(';').build();
                CSVReader csvReader = new CSVReaderBuilder(isr).withCSVParser(rfc4180Parser).build();
                CsvToBeanBuilder<T> csvBuilder= new CsvToBeanBuilder<>(isr) ;
                CsvToBean<T> csvToBean= csvBuilder.withType(cls).build();
                csvToBean.setCsvReader(csvReader);
                return csvToBean.parse();
            }
        }  catch (
                IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static <T>void save(List<T> content, String toFile) {
        try {
                FileOutputStream fos = new FileOutputStream(toFile);
                OutputStreamWriter osw = new OutputStreamWriter(fos,StandardCharsets.UTF_8);
                StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(osw).withSeparator(';').build();

                beanToCsv.write(content);
                osw.close();
        } catch (
                IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }

}


