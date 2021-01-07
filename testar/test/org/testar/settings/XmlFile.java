package org.testar.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class XmlFile {

    static final String UNKNOWN_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<root>\n" +
            "\t<unkown withAttribute=\"2\">value</unkown>\n" +
            "\t<unkownTag>\n" +
            "\t\t<value>23</value>\n" +
            "\t</unkownTag>\n" +
            "\t<emptyTag/>\n" +
            "</root>";

    public static void CreateUnknownFile(final String absolutePath) {
        CreateFile(absolutePath, UNKNOWN_CONTENT);
    }

    static final String SINGLE_TEST_SETTING_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<root>\n" +
            "\t<unkown withAttribute=\"2\">value</unkown>\n" +
            "\t<unkownTag>\n" +
            "\t\t<value>23</value>\n" +
            "\t</unkownTag>\n" +
            "\t<testSetting>\n" +
            "\t\t<value>Default</value>\n" +
            "\t</testSetting>\n" +
            "\t<emptyTag/>\n" +
            "</root>";

    public static void CreateSingleTestSetting(final String absolutePath) {
        CreateFile(absolutePath, SINGLE_TEST_SETTING_CONTENT);
    }

    static final String MULTIPLE_TEST_SETTING_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<root>\n" +
            "\t<unkown withAttribute=\"2\">value</unkown>\n" +
            "\t<unkownTag>\n" +
            "\t\t<value>23</value>\n" +
            "\t</unkownTag>\n" +
            "\t<testSetting>\n" +
            "\t\t<value>version1</value>\n" +
            "\t</testSetting>\n" +
            "\t<testSetting>\n" +
            "\t\t<value>version2</value>\n" +
            "\t</testSetting>\n" +
            "\t<emptyTag/>\n" +
            "</root>";

    public static void CreateMultipleTestSetting(final String absolutePath) {
        CreateFile(absolutePath, MULTIPLE_TEST_SETTING_CONTENT);
    }

    private static void CreateFile(final String absolutePath, final String content) {
        try {
            FileWriter fileWriter = new FileWriter(absolutePath);
            fileWriter.write(content);
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File testFile = new File(absolutePath);
        assertTrue(testFile.exists());
    }
}