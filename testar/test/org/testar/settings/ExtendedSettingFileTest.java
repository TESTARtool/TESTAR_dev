package org.testar.settings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.junit.Assert.*;

public class ExtendedSettingFileTest {
    final private String _rootDir = System.getProperty("user.dir") + File.separator;
    final private String _relativePath = "extended_settings_test" + File.separatorChar;
    final private String _workingDir = _rootDir + _relativePath;

    ExtendedSettingFile sut;
    ReentrantReadWriteLock fileAccessLock;

    @After
    public void CleanUp() {
        removeAllXmlTestFiles();
    }

    @Before
    public void Setup() {
        File directory = new File(_workingDir);
        if (!directory.exists()) {
            assertTrue(directory.mkdir());
        }
        removeAllXmlTestFiles();

        fileAccessLock = new ReentrantReadWriteLock();
    }

    void removeAllXmlTestFiles() {
        // Search and delete all XML files
        for (File file : Objects.requireNonNull(new File(_workingDir).listFiles())) {
            if (file.getName().endsWith(".xml")) {
                assertTrue(file.delete());
            }
        }
    }

    Boolean fileContains(String absolutePath, String... expectedLines) {
        Boolean[] result = new Boolean[expectedLines.length];
        Arrays.fill(result, Boolean.FALSE);

        try {
            File file = new File(absolutePath);
            for (int i = 0; i < expectedLines.length; i++) {
                Scanner myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    if (data.replaceAll("\\s+", "")
                            .equalsIgnoreCase(expectedLines[i].replaceAll("\\s+", ""))) {
                        result[i] = true;
                        break;
                    }
                }
                myReader.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return Arrays.stream(result).allMatch(x -> x);
    }

    @Test
    public void settingFileCreationWhenNotExisting() {
        // GIVEN The file doesn't exist.
        final String unknownFile = "ghost.xml";
        File testFile = new File(_workingDir + unknownFile);
        assertFalse(testFile.exists());

        // WHEN Trying to load an extended setting without default values.
        sut = new ExtendedSettingFile(_relativePath + unknownFile, fileAccessLock);
        TestSetting element = sut.load(TestSetting.class);

        // THEN The file is created.
        assertTrue(testFile.exists());
        assertNull(element);
    }

    @Test
    public void settingFileCreationWithDefaultValuesWhenNotExisting() {
        // GIVEN The file doesn't exist.
        final String unknownFile = "default.xml";
        File testFile = new File(_workingDir + unknownFile);
        assertFalse(testFile.exists());

        // WHEN Trying to load an extended setting without default values.
        sut = new ExtendedSettingFile(_relativePath + unknownFile, fileAccessLock);
        TestSetting element = sut.load(TestSetting.class, TestSetting::CreateDefault);

        // THEN The file is created containing default values.
        assertTrue(testFile.exists());
        assertNotNull(element);
        assertEquals(TestSetting.DEFAULT_VALUE, element.value);
    }

    @Test()
    public void saveValueWhenFileNotExists() {
        // GIVEN The file doesn't exist.
        final String unknownFile = "save.xml";
        File testFile = new File(_workingDir + unknownFile);
        assertFalse(testFile.exists());

        // WHEN Trying to save an extended setting before loading.
        sut = new ExtendedSettingFile(_relativePath + unknownFile, fileAccessLock);
        TestSetting element = new TestSetting();
        sut.save(element);

        // THEN The file is created.
        assertTrue(testFile.exists());
    }

    @Test
    public void loadFromFileWithOnlyUnknownElements() {
        // GIVEN The file contains only unknown elements.
        final String unknownFile = "unknown.xml";
        XmlFile.CreateUnknownFile(_workingDir + unknownFile);

        // WHEN Trying to load an extended setting without default values.
        sut = new ExtendedSettingFile(_relativePath + unknownFile, fileAccessLock);
        TestSetting element = sut.load(TestSetting.class);

        // THEN The element remains empty.
        assertNull(element);
    }

    @Test
    public void saveToFileWithOnlyUnknownElements() {
        // GIVEN The file contains only unknown elements.
        final String unknownFile = "unknown_save.xml";
        XmlFile.CreateUnknownFile(_workingDir + unknownFile);

        // WHEN Trying to save an extended setting.
        sut = new ExtendedSettingFile(_relativePath + unknownFile, fileAccessLock);
        TestSetting data = TestSetting.CreateDefault();
        sut.save(data);

        // THEN The data is added to the file.
        assertTrue(fileContains(_workingDir + unknownFile, "<testSetting>",
                "<value>Default</value>",
                "</testSetting>"));
    }

    @Test
    public void updateKnownElement() {
        // GIVEN The file contains one known element.
        final String knownFile = "update_known.xml";
        XmlFile.CreateSingleTestSetting(_workingDir + knownFile);

        // WHEN Trying to update a known element.
        sut = new ExtendedSettingFile(_relativePath + knownFile, fileAccessLock);
        TestSetting data = sut.load(TestSetting.class);
        data.value = "updated";
        sut.save(data);

        // THEN The data is updated and saved to the file.
        assertTrue(fileContains(_workingDir + knownFile, "<testSetting>",
                "<value>updated</value>",
                "</testSetting>"));
    }

    @Test
    public void updateKnownElementWhenFileMultipleKnownElements() {
        // GIVEN The file contains multiple known elements.
        final String knownFile = "update_known_multiple.xml";
        XmlFile.CreateMultipleTestSetting(_workingDir + knownFile);

        // WHEN Trying to update a known element.
        sut = new ExtendedSettingFile(_relativePath + knownFile, fileAccessLock);
        TestSetting data = sut.load(TestSetting.class);
        data.value = "version3";
        sut.save(data);

        // THEN The first element is updated and saved to the file.
        assertTrue(fileContains(_workingDir + knownFile, "<value>version3</value>", "<value>version2</value>"));
        assertFalse(fileContains(_workingDir + knownFile, "<value>version1</value>"));
    }

    @Test
    public void updateMultipleElement() {
        // GIVEN The file contains multiple known elements.
        final String knownFile = "update_multiple_elements.xml";
        File testFile = new File(_workingDir + knownFile);
        assertFalse(testFile.exists());
        sut = new ExtendedSettingFile(_relativePath + knownFile, fileAccessLock);
        ExtendedSettingFile sutTwo = new ExtendedSettingFile(_relativePath + knownFile, fileAccessLock);
        TestSetting elementOne = sut.load(TestSetting.class, TestSetting::CreateDefault);
        OtherSetting elementTwo = sutTwo.load(OtherSetting.class, OtherSetting::CreateDefault);
        assertTrue(testFile.exists());
        assertNotNull(elementOne);

        // WHEN Trying to update the first element.
        elementOne.value = "updated";
        sut.save(elementOne);

        // THEN The update is stored.
        assertEquals("updated", elementOne.value);
        assertTrue(fileContains(_workingDir + knownFile, "<value>updated</value>", "<speed>5</speed>"));

        // WHEN Trying to update the second element.
        elementTwo.speed = 6;
        sutTwo.save(elementTwo);

        // THEN The update is stored.
        assertEquals(6, elementTwo.speed);
        assertTrue(fileContains(_workingDir + knownFile, "<value>updated</value>", "<speed>6</speed>"));
    }

    @Test(expected = ClassCastException.class)
    public void updateElementWithWrongBaseClass() {
        // GIVEN The file contains multiple known elements.
        final String knownFile = "update_wrong_element.xml";
        File testFile = new File(_workingDir + knownFile);
        assertFalse(testFile.exists());
        sut = new ExtendedSettingFile(_relativePath + knownFile, fileAccessLock);
        TestSetting elementOne = sut.load(TestSetting.class, TestSetting::CreateDefault);
        OtherSetting elementTwo = sut.load(OtherSetting.class, OtherSetting::CreateDefault);
        assertTrue(testFile.exists());
        assertNotNull(elementOne);

        // WHEN Trying to update the first element while the _loadedvalue has been assigned to OtherSetting.
        elementOne.value = "updated";
        sut.save(elementOne);

        // THEN An exception should been throw.
    }
}
