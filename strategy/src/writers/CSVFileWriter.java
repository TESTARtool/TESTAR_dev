package writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class CSVFileWriter
{
    FileWriter                      writer;
    private File                    file;
    private String                  delimiter;
    private Map<Integer, Field>      fields; //the fields and their data
    private Map<String, Integer>    fieldsIndex; //the lookup that insures a fixed order
    
    public CSVFileWriter(String filePath, String fileName)
    { this(filePath, fileName, ";"); }
    
    public CSVFileWriter(String filePath, String fileName, String delimiter)
    {
        setNewFile(filePath, fileName);
        this.delimiter = delimiter;
        fields = new HashMap<>();
        fieldsIndex = new HashMap<>();
    }

    public boolean createFile()
    {
        try
        {
            return file.createNewFile();
        } catch (IOException e)
        {
            System.out.println("The CSV file could not be created.");
            throw new RuntimeException(e);
        }
    }

    public void addField(String shortName, String name, String value)
    {
        Field newField = new Field(shortName, name, value);
        int index = fieldsIndex.size();
        fields.put(index, newField);
        fieldsIndex.put(shortName, index);
    }

    public void addField(String shortName, String name)
    {
        Field newField = new Field(shortName, name, null);
        int index = fieldsIndex.size();
        fields.put(index, newField);
        fieldsIndex.put(shortName, index);
    }

    public void setFieldValue(String shortName, String newValue)
    {
        Integer index = fieldsIndex.get(shortName);
        if(index != null) //no warning if field doesn't exist
        {
            Field field = fields.get(index);
            field.setValue(newValue);
        }
    }

    public void resetValue(String shortName)
    {
        Integer index = fieldsIndex.get(shortName);
        if(index != null) //no warning if field doesn't exist
        {
            Field field = fields.get(index);
            field.resetValue();
        }
    }

    public void resetValues()
    {
        for(Field field : fields.values())
            field.resetValue();
    }

    public void writeTitleRow()
    {
       writeRow(false);
    }

    public void writeCurrentRow()
    {
        writeRow(true);
    }

    private void writeRow(boolean normalRow)
    {
        StringJoiner joiner;
        try
        {
            writer = new FileWriter(this.file, false); //recreate writer as needed, always overwrite file
            joiner = new StringJoiner(this.delimiter);

            for(int i = 0; i < fields.size(); i++)
            {
                if(normalRow)
                    joiner.add(fields.get(i).getValue());
                else
                    joiner.add(fields.get(i).getName());
            }
            joiner.add(System.getProperty("line.separator"));
            writer.write(joiner.toString());
            writer.close();
        }
        catch (IOException e)
        {
            System.out.println("The CSV file could not be written in.");
            e.printStackTrace();
        }
    }
    
    public void setDelimiter(String delimiter)
    { this.delimiter = delimiter; }
    
    public void setNewFile(String filePath, String fileName)
    { this.file = new File(filePath + File.separator + fileName + ".csv"); }
    
    public boolean fileIsEmpty()
    { return (file.length() == 0); }

    private class Field
    {
        private String shortName;
        private String name;
        private String value;

        public Field(String shortName, String name, String value)
        {
            this.shortName = shortName;
            this.name = name;
            this.value = value;
        }

        public String getShortName() {return shortName;}
        public String getName() {return name;}
        public String getValue() {return value;}
        public void setValue(String newValue) {value = newValue;}
        public void resetValue() {value = null;}
    }
}
