package org.testar.reporting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testar.reporting_proofofconcept.HTMLreportUtil;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateReport
{
    private static final String TEST_DIRECTORY = "D:\\Projecten\\TESTAR_dev\\testar\\test\\org\\testar\\reporting";
    private static final String TEST_FILE = TEST_DIRECTORY + "\\test-file";
    private static final String TEST_MOVED_DIRECTORY = TEST_DIRECTORY + "\\newDirectory";
    private static final String TEST_RENAME_FILE = "renamed-file";
    private static final String TEST_COPY_FILE = "testCopy";
    
    private HTMLreportUtil htmlReporter;
    
    @BeforeEach
    public void setUp()
    {
        htmlReporter = new HTMLreportUtil(TEST_FILE);
    }
    
    @Test
    public void testFileCreationAndWrite()
    {
        htmlReporter.addContent("Line 1");
        htmlReporter.addContent("Line 2");
        htmlReporter.writeToFile();
        
        assertTrue(new File(TEST_FILE).exists());
        
        // Check if the content is written correctly
        try
        {
            Scanner scanner = new Scanner(new File(TEST_FILE));
            assertEquals("Line 1", scanner.nextLine());
            assertEquals("Line 2", scanner.nextLine());
            scanner.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testRenameFile()
    {
        htmlReporter.renameFile(TEST_RENAME_FILE);
        
        assertTrue(new File(TEST_RENAME_FILE).exists());
        assertTrue(!new File(TEST_FILE).exists());
    }
    
    @Test
    public void testMoveFile()
    {
        htmlReporter.moveFile(TEST_MOVED_DIRECTORY);
        
        assertTrue(new File(TEST_MOVED_DIRECTORY, TEST_FILE).exists());
        assertTrue(!new File(TEST_FILE).exists());
    }
    
    @Test
    public void testCopyFile()
    {
        htmlReporter.duplicateFile(TEST_COPY_FILE);
        
        assertTrue(new File(TEST_COPY_FILE).exists());
        
        // Ensure that the copy is not tracked
        htmlReporter.addContent("New Content");
        htmlReporter.writeToFile();
        try
        {
            Scanner scanner = new Scanner(new File(TEST_COPY_FILE));
            assertEquals("Line 1", scanner.nextLine());
            assertEquals("Line 2", scanner.nextLine());
            scanner.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
    @AfterEach
    public void tearDown()
    {
        // Clean up test files and directory
        new File(TEST_FILE).delete();
        new File(TEST_RENAME_FILE).delete();
        new File(TEST_MOVED_DIRECTORY+TEST_FILE).delete();
        new File(TEST_COPY_FILE).delete();
        new File(TEST_DIRECTORY).delete();
    }
}
