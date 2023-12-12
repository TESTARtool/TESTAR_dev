/***************************************************************************************************
 *
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.reporting;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public abstract class BaseFormatUtil
{
    private                 File              file;
    protected               ArrayList<String> content = new ArrayList<>();
    private          final  String            FILE_SUFFIX; // lower case, includes period

    public File getFile() {
    	return file;
    }

    protected BaseFormatUtil(String fileString, String fileSuffix)
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
            file = new File(path.getParent() + File.separator + enforceFileSuffix(newName)); //update the file path
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
            //Files.move(oldPath, newPath.resolveSibling(fileName));
            Files.move(oldPath, newPath.resolve(fileName));
            file = new File(newPath + File.separator + file.getName()); //update the file path
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
            Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
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
