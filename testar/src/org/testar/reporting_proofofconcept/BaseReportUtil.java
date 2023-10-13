package org.testar.reporting_proofofconcept;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public abstract class BaseReportUtil
{
    protected static final  String            CHARSET = "UTF-8";
    private                 File              file;
    protected               ArrayList<String> content = new ArrayList<>();
    private          final  String            FILE_SUFFIX; // lower case, includes period
    
    protected BaseReportUtil(String fileString, String fileSuffix)
    {
        FILE_SUFFIX = (fileSuffix.toLowerCase().startsWith(".")) ? fileSuffix : "." + fileSuffix; //add period if not included
        this.file = new File(enforceFileSuffix(fileString));
        createFile();
    }
    
    private String enforceFileSuffix(String fileName)
    {
        return fileName.toLowerCase().endsWith(FILE_SUFFIX) ? fileName : fileName + FILE_SUFFIX;
    }
    
    protected String[] splitStringAtNewline(String longString)
    {
        return longString.split("\\r?\\n|\\r");
    }
    
    private void createFile()
    {
        if (!file.exists())
        {
            File parentDirectory = file.getParentFile();
            if (parentDirectory != null && !parentDirectory.exists() && (!parentDirectory.mkdirs())) //try to create the needed directories
                System.out.println("Failed to create the directory structure.");
            try
            {
                file.createNewFile();
//                if (file.createNewFile())
//                    System.out.println("File created: " + file.getAbsolutePath());
            }
            catch (Exception e)
            {
                System.out.println("Error creating the file: " + e.getMessage());
            }
        }
    }
    
    public boolean appendToFileName(String appendToName)
    {
        return file.renameTo(new File(enforceFileSuffix(file.getName().replace(".html", appendToName))));
    }
    
    public boolean renameFile(String newName)
    {
        return file.renameTo(new File(enforceFileSuffix(newName)));
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
    
    public void duplicateFile(String destinationPath)
    {
        try
        {
            Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(destinationPath),  StandardCopyOption.REPLACE_EXISTING);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error copying the file: " + e.getMessage());
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void writeToFile()
    {
        if(!content.isEmpty())
        {
            try
            {
                PrintWriter writer = new PrintWriter(file, CHARSET);
        
                for(String str : content)
                    writer.println(str);
        
                writer.close();
                content.clear(); //empty the queue
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
