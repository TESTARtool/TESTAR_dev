package org.testar.reporting_proofofconcept;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class CSVreporter extends BaseReporter
{
    private String               delimiter;
    private Map<Integer, Field>  fields; //the fields and their data
    private Map<String, Integer> fieldsIndex; //the lookup that insures a fixed order
    private boolean              fieldsLocked = false;
    
    public CSVreporter(String filePath, String delimiter)
    {
        super(new File(filePath.endsWith(".csv") ? filePath : filePath + ".csv"));
        this.delimiter = delimiter;
        fields         = new HashMap<>();
        fieldsIndex    = new HashMap<>();
    }
    
    public void addNewField(String shortName, String name, String value)
    {
        if(!fieldsLocked)
        {
            Field newField = new Field(shortName, name, value);
            int   index    = fieldsIndex.size();
            fields.put(index, newField);
            fieldsIndex.put(shortName, index);
        }
    }
    
    public void addNewField(String shortName, String name)
    {
        if(!fieldsLocked)
        {
            Field newField = new Field(shortName, name, null);
            int   index    = fieldsIndex.size();
            fields.put(index, newField);
            fieldsIndex.put(shortName, index);
        }
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
    
    private void startNextRow()
    {
        if(!fieldsLocked)
        {
            for(int i = 0; i < fields.size(); i++)
                content.add(fields.get(i).getName());
            fieldsLocked = true;
        }
        
        StringJoiner joiner = new StringJoiner(this.delimiter);
        for(int i = 0; i < fields.size(); i++)
            joiner.add(fields.get(i).getValue());
        content.add(joiner.toString());
        resetValues();
    }

    private class Field
    {
        private final String shortName;
        private final String name;
        private       String value;
        
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