package org.testar.reporting_proofofconcept;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public abstract class BaseReportUtil
{
    protected static final  String            CHARSET = "UTF-8";
    private                 File              file;
    protected               ArrayList<String> content = new ArrayList<>();
    private          final  String            FILE_SUFFIX; // lower case, includes period
    
    protected BaseReportUtil(String fileString, String fileSuffix)
    {
        FILE_SUFFIX = (fileSuffix.toLowerCase().startsWith(".")) ? fileSuffix : "." + fileSuffix;
        this.file = new File(ensureCorrectFileSuffix(fileString));
    }
    
    private String ensureCorrectFileSuffix(String fileName)
    {
        return fileName.toLowerCase().endsWith(FILE_SUFFIX) ? fileName : fileName + FILE_SUFFIX;
    }
    
    public boolean renameFile(String newName)
    {
        return file.renameTo(new File(ensureCorrectFileSuffix(newName)));
    }
    
    public void moveFile(String newDirectory)
    {
        try
        {
            Path newPath = Paths.get(newDirectory);
            Path oldPath = Paths.get(file.getAbsolutePath());
            String fileName = file.getName();
            Files.move(oldPath, newPath.resolveSibling(fileName));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public String getFileName()
    {
        return file.getName();
    }
    
    public String getFullFilePath()
    {
        return file.getPath();
    }
    
    public void writeReport()
    {
        try
        {
            file.getParentFile().mkdirs();

            if (!file.exists())
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
