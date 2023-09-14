package writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringJoiner;

public class CSVFileWriter
{
    FileWriter              writer;
    private File            file;
    private String          delimiter;
    
    public CSVFileWriter(String filePath, String fileName)
    { this(filePath, fileName, ";"); }
    
    public CSVFileWriter(String filePath, String fileName, String delimiter)
    {
        setNewFile(filePath, fileName);
        this.delimiter = delimiter;
    }
    
    public void addRow(String... values) throws IOException
    { addRow(true, values); } //default is append
    
    public void addRow(boolean append, String... values) throws IOException
    {
        StringJoiner    joiner;
        try
        {
            writer = new FileWriter(this.file, append); //recreate as needed
            joiner = new StringJoiner(this.delimiter);
    
            for(String v : values)
                joiner.add(v);
            joiner.add(System.getProperty("line.separator"));
            writer.write(joiner.toString());
            writer.close();
        }
        catch (IOException e)
        {
            System.out.println("The CSV file could not be found, created, or written in.");
            e.printStackTrace();
        }
    }
    
    public void setDelimiter(String delimiter)
    { this.delimiter = delimiter; }
    
    public void setNewFile(String filePath, String fileName)
    { this.file = new File(filePath + File.separator + fileName + ".csv"); }
    
    public boolean fileIsEmpty()
    { return (file.length() == 0); }
}
