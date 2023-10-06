package org.testar.reporting_proofofconcept;

import java.io.*;
import java.util.ArrayList;

public abstract class BaseReporter
{
    protected static final String            CHARSET = "UTF-8";
    private                File              file;
    protected              ArrayList<String> content = new ArrayList<>();
    
    protected BaseReporter(File file)
    {
        setFile(file);
    }
    
    public void setFile(File file)
    {
        if(file.exists())
        
        this.file = file;
    }
    
    public void writeReport(String... content)
    {
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
                PrintWriter writer = new PrintWriter(file, CHARSET);
                
                for(String str : content)
                    writer.println(str);
    
                writer.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    // Get the file extension from the file path
    protected String getFileExtension()
    {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex >= 0 && lastDotIndex < fileName.length() - 1)
            return fileName.substring(lastDotIndex + 1);
        return "";
    }
    
    protected String[] splitStringAtNewline(String longString)
    {
        return longString.split("\\r?\\n|\\r");
    }
}
