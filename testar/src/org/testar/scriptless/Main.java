/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.testar.config.ConfigTags;
import org.testar.config.TestarDirectories;
import org.testar.config.TestarInfo;
import org.testar.config.settings.Settings;
import org.testar.core.CodingManager;
import org.testar.core.StateManagementTags;
import org.testar.core.environment.Environment;
import org.testar.core.environment.UnknownEnvironment;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.dialog.SettingsDialog;
import org.testar.dialog.tagsvisualization.ConcreteTagFilter;
import org.testar.dialog.tagsvisualization.TagFilter;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.windows.Windows10;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class Main {

    public static final String TESTAR_VERSION = TestarInfo.VERSION;
    public static final String SETTINGS_FILE = TestarDirectories.SETTINGS_FILE;
    public static final String SUT_SETTINGS_EXT = TestarDirectories.SUT_SETTINGS_EXT;

    private static String activatedSse;

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        verifyJavaEnvironment();
        verifyTestarInitialDirectory();
        initTagVisualization();
        initTestarSse(args);

        String testSettingsFile = getTestSettingsFile();
        System.out.println("TESTAR version is <" + TESTAR_VERSION + ">");
        System.out.println("Test settings is <" + testSettingsFile + ">");

        Settings settings = Settings.loadSettings(args, testSettingsFile);

        if (!settings.get(ConfigTags.ShowVisualSettingsDialogOnStartup)) {
            setTestarDirectory(settings);
            initCodingManager(settings);
            initOperatingSystem();
            startTestar(settings);
        } else {
            while (startTestarDialog(settings, testSettingsFile)) {
                testSettingsFile = getTestSettingsFile();
                settings = Settings.loadSettings(args, testSettingsFile);

                setTestarDirectory(settings);
                initCodingManager(settings);
                initOperatingSystem();
                startTestar(settings);
            }

            System.exit(0);
        }
    }

    public static String[] getSseFiles() {
        return TestarDirectories.getSseFiles();
    }

    public static String getTestSettingsFile() {
        return TestarDirectories.getTestSettingsFile();
    }

    private static void verifyJavaEnvironment() {
        try {
            String javaHome = System.getenv("JAVA_HOME");
            if (javaHome != null && !javaHome.contains("jdk")) {
                System.out.println("JAVA HOME is not properly aiming to the Java Development Kit");
            }
            System.out.println("Detected Java version is : " + javaHome);
        } catch (Exception exception) {
            System.out.println("Exception: Something is wrong with your JAVA_HOME \n"
                    + "Check if JAVA_HOME system variable is correctly defined \n \n"
                    + "GO TO: https://testar.org/faq/ to obtain more details \n \n");
        }
    }

    private static void verifyTestarInitialDirectory() {
        Set<String> fileNames = new HashSet<>();
        File[] filesList = new File(TestarDirectories.getTestarDir()).listFiles();
        if (filesList != null) {
            for (File file : filesList) {
                fileNames.add(file.getName());
            }
        }

        if (!fileNames.contains("testar.bat")) {
            System.out.println("WARNING: We cannot find testar.bat executable file.");
            System.out.println("WARNING: Please change to /testar/bin/ folder (contains testar.bat) and try to execute testar again.");
            System.out.println(String.format(
                    "WARNING: Current directory %s with existing files:",
                    new File(TestarDirectories.getTestarDir()).getAbsolutePath()
            ));
            fileNames.forEach(System.out::println);
            System.exit(-1);
        }
    }

    private static void setTestarDirectory(Settings settings) {
        TestarDirectories.setOutputDir(settings.get(ConfigTags.OutputDir));
        TestarDirectories.setTempDir(settings.get(ConfigTags.TempDir));
    }

    private static void initTestarSse(String[] args) {
        Locale.setDefault(Locale.ENGLISH);

        for (String setting : args) {
            if (setting.contains("sse=")) {
                try {
                    protocolFromCmd(setting);
                } catch (IOException exception) {
                    System.out.println("Error trying to modify sse from command line");
                }
            }
        }

        String[] files = getSseFiles();

        if (files != null && files.length > 1) {
            System.out.println("Too many *.sse files - exactly one expected!");
            for (String file : files) {
                System.out.println("Delete file <" + file + "> = "
                        + new File(TestarDirectories.getSettingsDir() + file).delete());
            }
            files = null;
        }

        if (files != null && files.length == 1 && !existsSse(removeSseExtension(files[0]))) {
            System.out.println("Protocol of indicated .sse file does not exist");
            System.out.println("Delete file <" + files[0] + "> = "
                    + new File(TestarDirectories.getSettingsDir() + files[0]).delete());
            files = null;
        }

        if (files == null || files.length == 0) {
            settingsSelection();
            if (activatedSse == null) {
                System.exit(-1);
            }
        } else {
            activatedSse = removeSseExtension(files[0]);
            TestarDirectories.setSelectedSse(activatedSse);
        }
    }

    private static String removeSseExtension(String fileName) {
        if (fileName != null && fileName.endsWith(SUT_SETTINGS_EXT)) {
            return fileName.substring(0, fileName.length() - SUT_SETTINGS_EXT.length());
        }
        return fileName;
    }

    private static void settingsSelection() {
        Set<String> sutSettings = new HashSet<>();
        File[] files = new File(TestarDirectories.getSettingsDir()).listFiles();
        if (files != null) {
            for (File file : files) {
                File settingsFile = new File(file.getPath() + File.separator + SETTINGS_FILE);
                if (settingsFile.exists()) {
                    sutSettings.add(file.getName());
                }
            }
        }

        if (sutSettings.isEmpty()) {
            System.out.println("No SUT settings found!");
            return;
        }

        Object[] options = sutSettings.toArray();
        Arrays.sort(options);

        JFrame settingsSelectorDialog = new JFrame();
        settingsSelectorDialog.setAlwaysOnTop(true);
        String sseSelected = (String) JOptionPane.showInputDialog(
                settingsSelectorDialog,
                "Select the desired setting:",
                "TESTAR settings",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (sseSelected == null) {
            activatedSse = null;
            TestarDirectories.setSelectedSse(null);
            return;
        }

        String sseFile = sseSelected + SUT_SETTINGS_EXT;
        try {
            File file = new File(TestarDirectories.getSettingsDir() + File.separator + sseFile);
            if (file.createNewFile()) {
                activatedSse = sseSelected;
                TestarDirectories.setSelectedSse(activatedSse);
                return;
            }
        } catch (IOException exception) {
            System.out.println("Exception creating <" + sseFile + "> file");
        }

        activatedSse = null;
        TestarDirectories.setSelectedSse(null);
    }

    public static boolean startTestarDialog(Settings settings, String testSettingsFile) {
        try {
            Settings updatedSettings = new SettingsDialog().run(settings, testSettingsFile);
            return updatedSettings != null;
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private static void startTestar(Settings settings) {
        LogSerialiser.log("Starting TESTAR scriptless protocol ...\n", LogSerialiser.LogLevel.Debug);
        new ComposedProtocol() { }.accept(settings);
        LogSerialiser.exit();
    }

    public static void protocolFromCmd(String setting) throws IOException {
        String sseName = setting.substring(setting.indexOf("=") + 1);

        if (!existsSse(sseName)) {
            System.out.println("CMD Protocol: " + sseName + " doesn't exist");
            return;
        }

        String[] files = getSseFiles();
        if (files != null) {
            for (String file : files) {
                new File(TestarDirectories.getSettingsDir() + file).delete();
            }
        }

        String ssePath = TestarDirectories.getSettingsDir() + sseName + SUT_SETTINGS_EXT;
        File sseFile = new File(ssePath);
        if (!sseFile.exists()) {
            sseFile.createNewFile();
        }

        System.out.println("Protocol changed from command line to: " + sseName);
    }

    private static boolean existsSse(String sseName) {
        File[] files = new File(TestarDirectories.getSettingsDir()).listFiles();
        if (files == null) {
            return false;
        }

        for (File ignored : files) {
            File settingsFile = new File(
                    TestarDirectories.getSettingsDir() + sseName + File.separator + SETTINGS_FILE
            );
            if (settingsFile.exists()) {
                return true;
            }
        }

        return false;
    }

    private static void initCodingManager(Settings settings) {
        Set<org.testar.core.tag.Tag<?>> stateManagementTags = StateManagementTags.getAllTags();
        if (!stateManagementTags.isEmpty()) {
            CodingManager.setCustomTagsForConcreteId(stateManagementTags.toArray(new org.testar.core.tag.Tag<?>[0]));
        }

        if (!settings.get(ConfigTags.AbstractStateAttributes).isEmpty()) {
            org.testar.core.tag.Tag<?>[] abstractTags = settings.get(ConfigTags.AbstractStateAttributes)
                    .stream()
                    .map(StateManagementTags::getTagFromSettingsString)
                    .filter(Objects::nonNull)
                    .toArray(org.testar.core.tag.Tag<?>[]::new);
            CodingManager.setCustomTagsForAbstractId(abstractTags);
        }
    }

    private static void initOperatingSystem() {
        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WINDOWS_10)) {
            Environment.setInstance(new Windows10());
        } else {
            System.out.printf(
                    "WARNING: Current OS %s has no concrete environment implementation, using default environment and default getDisplayScale value\n",
                    NativeLinker.getPLATFORM_OS()
            );
            Environment.setInstance(new UnknownEnvironment());
        }
    }

    private static void initTagVisualization() {
        TagFilter.setInstance(new ConcreteTagFilter());
    }
}
