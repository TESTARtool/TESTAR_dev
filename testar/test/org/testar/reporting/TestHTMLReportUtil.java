package org.testar.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestHTMLReportUtil
{	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private static final String TEST_ORIGINAL_DIRECTORY = "originalDirectory"; // C:/Users/username/AppData/Local/Tmp/junit/originalDirectory
	private static final String TEST_FILE_NAME = "test-file"; // C:/Users/username/AppData/Local/Tmp/junit/originalDirectory/test-file.html

	private static final String TEST_RENAME_FILE = "renamed-file"; // C:/Users/username/AppData/Local/Tmp/junit/originalDirectory/renamed-file.html

	private static final String TEST_MOVED_DIRECTORY = "newDirectory"; // C:/Users/username/AppData/Local/Tmp/junit/newDirectory

	private static final String TEST_COPY_DIRECTORY = "copyDirectory"; // C:/Users/username/AppData/Local/Tmp/junit/copyDirectory
	private static final String TEST_COPY_FILE = "copyFile"; // C:/Users/username/AppData/Local/Tmp/junit/copyDirectory/copyFile.html

	private HTMLreportUtil htmlReporter;

	@Before
	public void setUp() throws IOException
	{
		tempFolder.newFolder(TEST_ORIGINAL_DIRECTORY);
		String testFile = tempFolder.getRoot() + File.separator + TEST_ORIGINAL_DIRECTORY + File.separator + TEST_FILE_NAME;
		htmlReporter = new HTMLreportUtil(testFile);
	}

	@Test
	public void testFileCreationAndWrite()
	{
		String testFile = tempFolder.getRoot() + File.separator + TEST_ORIGINAL_DIRECTORY + File.separator + TEST_FILE_NAME + ".html";
		assertTrue(new File(testFile).exists());

		htmlReporter.addContent("Line 1");
		htmlReporter.addContent("Line 2");
		htmlReporter.writeToFile();

		// Check if the content is written correctly
		try
		{
			Scanner scanner = new Scanner(new File(testFile));
			assertEquals("Line 1", scanner.nextLine());
			assertEquals("Line 2", scanner.nextLine());
			scanner.close();
		} catch (FileNotFoundException e)
		{
			 fail(e.getMessage());
		}
	}

	@Test
	public void testRenameFile()
	{
		assertTrue(htmlReporter.getFile().getPath().endsWith(TEST_FILE_NAME + ".html"));

		htmlReporter.renameFile(TEST_RENAME_FILE);

		assertTrue(htmlReporter.getFile().getPath().endsWith(TEST_RENAME_FILE + ".html"));

		// Check that the original file was renamed correctly
		String renamedFile = tempFolder.getRoot() + File.separator + TEST_ORIGINAL_DIRECTORY + File.separator + TEST_RENAME_FILE + ".html";
		assertTrue(new File(renamedFile).exists());
		String oldTestFile = tempFolder.getRoot() + File.separator + TEST_ORIGINAL_DIRECTORY + File.separator + TEST_FILE_NAME + ".html";
		assertTrue(!new File(oldTestFile).exists());
	}

	@Test
	public void testMoveFile() throws IOException
	{
		assertTrue(htmlReporter.getFile().getPath().endsWith(TEST_ORIGINAL_DIRECTORY + File.separator + TEST_FILE_NAME + ".html"));

		tempFolder.newFolder(TEST_MOVED_DIRECTORY);
		String movedFolder = tempFolder.getRoot() + File.separator + TEST_MOVED_DIRECTORY;
		htmlReporter.moveFile(movedFolder);

		assertTrue(htmlReporter.getFile().getPath().endsWith(TEST_MOVED_DIRECTORY + File.separator + TEST_FILE_NAME + ".html"));

		String movedFile = tempFolder.getRoot() + File.separator + TEST_MOVED_DIRECTORY + File.separator + TEST_FILE_NAME + ".html";
		assertTrue(new File(movedFile).exists());
		String oldTestFile = tempFolder.getRoot() + File.separator + TEST_ORIGINAL_DIRECTORY + File.separator + TEST_FILE_NAME + ".html";
		assertTrue(!new File(oldTestFile).exists());
	}

	@Test
	public void testCopyFile() throws IOException
	{
		assertTrue(htmlReporter.getFile().getPath().endsWith(TEST_ORIGINAL_DIRECTORY + File.separator + TEST_FILE_NAME + ".html"));

		// Write content to the original file
		htmlReporter.addContent("Line 1");
		htmlReporter.addContent("Line 2");
		htmlReporter.writeToFile();

		tempFolder.newFolder(TEST_COPY_DIRECTORY);
		String copyFile = tempFolder.getRoot() + File.separator + TEST_COPY_DIRECTORY + File.separator + TEST_COPY_FILE + ".html";
		htmlReporter.duplicateFile(copyFile);

		assertTrue(htmlReporter.getFile().getPath().endsWith(TEST_ORIGINAL_DIRECTORY + File.separator + TEST_FILE_NAME + ".html"));

		String copiedFile = tempFolder.getRoot() + File.separator + TEST_COPY_DIRECTORY + File.separator + TEST_COPY_FILE + ".html";
		assertTrue(new File(copiedFile).exists());

		// Ensure that the copy is not tracked
		htmlReporter.addContent("New Content");
		htmlReporter.writeToFile();

		try
		{
			Scanner scanner = new Scanner(new File(copiedFile));
			assertEquals("Line 1", scanner.nextLine());
			assertEquals("Line 2", scanner.nextLine());
			scanner.close();
		} catch (FileNotFoundException e)
		{
			 fail(e.getMessage());
		}
	}
}
