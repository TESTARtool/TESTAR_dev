package org.testar.reporting;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public abstract class BaseReportUtil
{
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

    public void appendToFileName(String appendToName)
    {
        try
        {
            Path oldFile = Paths.get(file.getAbsolutePath()); // get full name
            Path directory = Paths.get(file.getParent()); //get directory
            String newName = enforceFileSuffix(file.getName().replace(".html", appendToName));
            Files.move(oldFile, oldFile.resolveSibling(newName));
            file = new File(directory + File.separator + newName); //update the file path
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void renameFile(String newName)
    {
        try
        {
            Path path = Paths.get(file.getAbsolutePath()); // get full name
            Files.move(path, path.resolveSibling(enforceFileSuffix(newName)));
            file = new File(path.getParent() + File.separator + newName); //update the file path
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void moveFile(String newDirectory)
    {
        try
        {
            Path newPath = Paths.get(newDirectory);
            Path oldPath = Paths.get(file.getAbsolutePath()); //full name
            String fileName = file.getName();
            Files.move(oldPath, newPath.resolveSibling(fileName));
            file = new File(newPath + File.separator + file); //update the file path
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
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));

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
