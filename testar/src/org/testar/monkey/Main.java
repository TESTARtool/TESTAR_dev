// GIPHOUSE, file changed by team
/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2026 Open Universiteit - www.ou.nl
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

package org.testar.monkey;

import org.testar.CodingManager;
import org.testar.StateManagementTags;
import org.testar.environment.Environment;
import org.testar.environment.UnknownEnvironment;
import org.testar.managers.NativeHookManager;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.windows.Windows10;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.settings.backend.LogSerialiser;
import org.testar.serialisation.ScreenshotSerialiser;
import org.testar.serialisation.TestSerialiser;
import org.testar.settings.Settings;
import org.testar.settings.dialog.SettingsDialog;
import org.testar.settings.dialog.tagsvisualization.ConcreteTagFilter;
import org.testar.settings.dialog.tagsvisualization.TagFilter;
import org.testar.settings.backend.BackendMain;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static org.testar.monkey.ConfigTags.*;
import static org.testar.monkey.Util.compileProtocol;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;

public class Main {

    public static final String TESTAR_VERSION = "v2.7.20 (3-Feb-2026)";

    // public static final String TESTAR_DIR_PROPERTY = "DIRNAME";
    // Use the OS environment to obtain TESTAR directory
    public static final String SETTINGS_FILE = "test.settings";
    public static final String SUT_SETTINGS_EXT = ".sse";
    public static String SSE_ACTIVATED = null;

    // Default paths
    public static String testarDir = "." + File.separator;
    public static String settingsDir = testarDir + "settings" + File.separator;
    public static String outputDir = testarDir + "output" + File.separator;
    public static String tempDir = outputDir + "temp" + File.separator;

    /**
     * This method scans the settings directory of TESTAR for a file that end with
     * extension SUT_SETTINGS_EXT
     *
     * @return A list of file names that have extension SUT_SETTINGS_EXT
     */
    public static String[] getSSE() {
        return new File(settingsDir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(SUT_SETTINGS_EXT);
            }
        });
    }

    /**
     * According to the TESTAR directory and SSE file (settings and protocol to run)
     * return the path of the selected settings
     *
     * @return test.settings path
     */
    public static String getTestSettingsFile() {
        return settingsDir + SSE_ACTIVATED + File.separator + SETTINGS_FILE;
    }

    /**
     * Main method to run TESTAR
     *
     * @param args
     * @throws IOException
     */

    public static void main(String[] args) throws IOException {

        isValidJavaEnvironment();

        verifyTestarInitialDirectory();

        initTagVisualization();

        initTestarSSE(args);

        Settings settings = null;

        System.out.println("TESTAR version is <" + TESTAR_VERSION + ">");
        if (SSE_ACTIVATED != null) {
            String testSettingsFileName = getTestSettingsFile();
            System.out.println("Test settings is <" + testSettingsFileName + ">");
            settings = Settings.loadSettings(args, testSettingsFileName);

            startBackend(settings, testSettingsFileName);
        } else {
            startBackend();
        }

        // Block main thread
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignored) {
        }
    }

    public static String findBrowser() {
        List<DesktopWindow> windows = WindowUtils.getAllWindows(true);

        for (DesktopWindow window : windows) {
            if (window.getTitle() != null && window.getTitle().toUpperCase().contains("TESTAR")) {
                System.out.println("Browser window found at: " + window.getTitle());
                return window.getTitle();
            }
        }
        System.err.println("Failed to find Web Interface window.");
        return "Failed to find the browser";
    }

    public static void minimizeBrowserOnWindows(String windowName) {
        WinDef.HWND hWnd = User32.INSTANCE.FindWindow(null, windowName);
        User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_MINIMIZE);
    }

    public static void runTestar(Settings settings) {
        setTestarDirectory(settings);
        initCodingManager(settings);
        initOperatingSystem();

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            String browserName = findBrowser();
            minimizeBrowserOnWindows(browserName);
        }

        startTestar(settings);
    }

    private static boolean isValidJavaEnvironment() {
        try {
            if (!System.getenv("JAVA_HOME").contains("jdk"))
                System.out.println("JAVA HOME is not properly aiming to the Java Development Kit");

            System.out.println("Detected Java version is : " + System.getenv("JAVA_HOME"));
        } catch (Exception e) { // TODO check what kind of exceptions are possible
            System.out.println("Exception: Something is wrong with your JAVA_HOME \n"
                    + "Check if JAVA_HOME system variable is correctly defined \n \n"
                    + "GO TO: https://testar.org/faq/ to obtain more details \n \n");
        }

        return true;
    }

    /**
     * Verify the initial directory of TESTAR
     * If this directory didn't contain testar.bat file inform the user
     */
    private static void verifyTestarInitialDirectory() {
        // Obtain Files name of current testarDir
        Set<String> filesName = new HashSet<>();
        File[] filesList = new File(testarDir).listFiles();
        for (File file : filesList) {
            filesName.add(file.getName());
        }

        // Verify if we are in the correct executable testar directory (contains
        // testar.bat)
        if (!filesName.contains("testar.bat")) {
            System.out.println("WARNING: We cannot find testar.bat executable file.");
            System.out.println(
                    "WARNING: Please change to /testar/bin/ folder (contains testar.bat) and try to execute testar again.");
            System.out.println(String.format("WARNING: Current directory %s with existing files:",
                    new File(testarDir).getAbsolutePath()));
            filesName.forEach(System.out::println);
            System.exit(-1);
        }
    }

    /**
     * Set the current directory of TESTAR, settings and output folders
     */
    private static void setTestarDirectory(Settings settings) {
        outputDir = settings.get(ConfigTags.OutputDir);
        tempDir = settings.get(ConfigTags.TempDir);
    }

    /**
     * Find or create the .sse file, to known with what settings and protocol start
     * TESTAR
     *
     * @param args
     */
    private static void initTestarSSE(String[] args) {

        Locale.setDefault(Locale.ENGLISH);

        // TODO: put the code below into separate method/class
        // Get the files with SUT_SETTINGS_EXT extension and check whether it is not
        // empty     * TESTAR
        // and that there is exactly one.

        // Allow users to use command line to choose a protocol modifying sse file
        for (String sett : args) {
            if (sett.toString().contains("sse="))
                try {
                    protocolFromCmd(sett);
                } catch (IOException e) {
                    System.out.println("Error trying to modify sse from command line");
                }
        }

        String[] files = getSSE();

        // If there is more than 1 sse file, then delete them all
        if (files != null && files.length > 1) {
            System.out.println("Too many *.sse files - exactly one expected!");
            for (String f : files) {
                System.out.println("Delete file <" + f + "> = " + new File(settingsDir + f).delete());
            }
            files = null;
        }

        // If the protocol of selected sse file does not exist, delete it
        // Example: unknown.sse file exists but the settings/unknown folder does not
        if (files != null && files.length == 1 && !existsSSE(files[0].replace(SUT_SETTINGS_EXT, ""))) {
            System.out.println("Protocol of indicated .sse file does not exist");
            System.out.println("Delete file <" + files[0] + "> = " + new File(settingsDir + files[0]).delete());
            files = null;
        }

        // If there is none, set settings to null and handle selection in the backend
        if (files == null || files.length == 0) {
            System.out.println("No SSE file found.");
            System.out.println("Settings is set to null. Please select a protocol in the web interface to start TESTAR.");
        } else {
            // Use the only file that was found
            SSE_ACTIVATED = extractSSEName(files[0]);
        }
    }

    /**
     * Extracts the protocol name from a given .sse filename.
     * If the filename ends with the .sse extension, removes it.
     * Otherwise returns the original string unchanged.
     *
     * @param fileName Name of the .sse file
     * @return Protocol name without the .sse extension
     */
    private static String extractSSEName(String fileName) {
        if (fileName != null && fileName.endsWith(SUT_SETTINGS_EXT)) {
            return fileName.substring(0, fileName.length() - SUT_SETTINGS_EXT.length());
        }
        return fileName;
    }

    /**
     * Open TESTAR GUI to allow the users modify the settings and the protocol with
     * which the want run TESTAR
     *
     * @param settings
     * @param testSettingsFileName
     * @return true if users starts TESTAR, or false is users close TESTAR
     */
    public static boolean startTestarDialog(Settings settings, String testSettingsFileName) {
        // Start up the TESTAR Dialog
        try {
            if ((settings = new SettingsDialog().run(settings, testSettingsFileName)) == null) {
                return false;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Start TESTAR protocol with the selected settings
     * <p>
     * This method get the specific protocol class of the selected settings to run
     * TESTAR
     *
     * @param settings
     */
    private static void startTestar(Settings settings) {
        URLClassLoader loader = null;

        String pc = settings.get(ProtocolClass);
        String protocolClass = pc.substring(pc.lastIndexOf('/') + 1, pc.length());

        try {
            // Compile the Java protocols if AlwaysCompile setting is true
            if (settings.get(ConfigTags.AlwaysCompile)) {
                compileProtocol(Main.settingsDir, pc, settings.get(ConfigTags.ProtocolCompileDirectory));
            }

            List<String> cp = new ArrayList<>(settings.get(MyClassPath));
            cp.add(settings.get(ConfigTags.ProtocolCompileDirectory));
            URL[] classPath = new URL[cp.size()];
            for (int i = 0; i < cp.size(); i++) {
                classPath[i] = new File(cp.get(i)).toURI().toURL();
            }

            loader = new URLClassLoader(classPath);

            LogSerialiser.log("Trying to load TESTAR protocol in class '" + protocolClass +
                    "' with class path '" + Util.toString(cp) + "'\n", LogSerialiser.LogLevel.Debug);

            @SuppressWarnings("unchecked")
            UnProc<Settings> protocol = (UnProc<Settings>) loader.loadClass(protocolClass).getConstructor()
                    .newInstance();
            LogSerialiser.log("TESTAR protocol loaded!\n", LogSerialiser.LogLevel.Debug);

            LogSerialiser.log("Starting TESTAR protocol ...\n", LogSerialiser.LogLevel.Debug);

            // Run TESTAR protocol with the selected settings
            protocol.run(settings);

        } catch (InstantiationException e) {
            e.printStackTrace();
            e.printStackTrace(LogSerialiser.getLogStream());
            LogSerialiser.log("An unexpected error occurred: " + e + "\n", LogSerialiser.LogLevel.Critical);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            e.printStackTrace(LogSerialiser.getLogStream());
            LogSerialiser.log("An unexpected error occurred: " + e + "\n", LogSerialiser.LogLevel.Critical);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            e.printStackTrace(LogSerialiser.getLogStream());
            LogSerialiser.log("An unexpected error occurred: " + e + "\n", LogSerialiser.LogLevel.Critical);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            e.printStackTrace(LogSerialiser.getLogStream());
            LogSerialiser.log("An unexpected error occurred: " + e + "\n", LogSerialiser.LogLevel.Critical);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            e.printStackTrace(LogSerialiser.getLogStream());
            LogSerialiser.log("An unexpected error occurred: " + e + "\n", LogSerialiser.LogLevel.Critical);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            LogSerialiser.log("An unexpected error occurred: " + e + "\n", LogSerialiser.LogLevel.Critical);
        } catch (ClassNotFoundException e) {
            // Invalid TESTAR protocol class configuration
            String protocolMsg = String.format(
                    "The protocol '%s' from the selected ProtocolClass setting '%s' does not exist"
                            + System.getProperty("line.separator")
                            + "Please, edit the test.settings file and update the ProtocolClass setting adequately",
                    protocolClass, pc);

            System.err.println(protocolMsg);
            LogSerialiser.log(protocolMsg + "\n", LogSerialiser.LogLevel.Critical);

            e.printStackTrace();
            e.printStackTrace(LogSerialiser.getLogStream());
            LogSerialiser.log("An unexpected error occurred: " + e + "\n", LogSerialiser.LogLevel.Critical);
        } finally {
            // Closing TESTAR
            if (loader != null) {
                try {
                    loader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            TestSerialiser.exit();
            ScreenshotSerialiser.exit();
            LogSerialiser.exit();
        }
    }

    /**
     * Close the Serialiser classes and stop the TESTAR process.
     */
    public static void stopTestar() {
        // Unregister the JNativeHook library
        NativeHookManager.unregisterNativeHook();

        TestSerialiser.exit();
        ScreenshotSerialiser.exit();
        LogSerialiser.exit();

        System.exit(0);
    }

    /**
     * This method creates a sse file to change TESTAR protocol if sett param
     * matches an existing protocol
     *
     * @param sett
     * @throws IOException
     */
    public static void protocolFromCmd(String sett) throws IOException {
        String sseName = sett.substring(sett.indexOf("=") + 1);

        // Command line protocol doesn't exist
        if (!existsSSE(sseName)) {
            System.out.println("CMD Protocol: " + sseName + " doesn't exist");
        } else {
            // Obtain previous sse file and delete it (if exist)
            String[] files = getSSE();
            if (files != null) {
                for (String f : files)
                    new File(settingsDir + f).delete();

            }

            // Create the new sse file
            String sseDir = settingsDir + sseName + SUT_SETTINGS_EXT;
            File f = new File(sseDir);
            if (!f.exists())
                f.createNewFile();

            System.out.println("Protocol changed from command line to: " + sseName);

        }
    }

    // Check if sse protocol exist
    private static boolean existsSSE(String sseName) {
        for (File f : new File(settingsDir).listFiles()) {
            if (new File(settingsDir + sseName + File.separator + SETTINGS_FILE).exists()) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method initializes the coding manager with custom tags to use for
     * constructing
     * concrete and abstract state ids, if provided of course.
     *
     * @param settings
     */
    private static void initCodingManager(Settings settings) {
        // we look if there are user-provided custom state tags in the settings
        // if so, we provide these to the coding manager

        Set<Tag<?>> stateManagementTags = StateManagementTags.getAllTags();
        // for the concrete state tags we use all the state management tags that are
        // available
        if (!stateManagementTags.isEmpty()) {
            CodingManager.setCustomTagsForConcreteId(stateManagementTags.toArray(new Tag<?>[0]));
        }

        // then the attributes for the abstract state id
        if (!settings.get(ConfigTags.AbstractStateAttributes).isEmpty()) {
            Tag<?>[] abstractTags = settings.get(AbstractStateAttributes).stream()
                    .map(StateManagementTags::getTagFromSettingsString).filter(Objects::nonNull).toArray(Tag<?>[]::new);
            CodingManager.setCustomTagsForAbstractId(abstractTags);
        }
    }

    /**
     * Set the concrete implementation of IEnvironment based on the Operating system
     * on which the application is running.
     */
    private static void initOperatingSystem() {
        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WINDOWS_10)) {
            Environment.setInstance(new Windows10());
        } else {
            System.out.printf(
                    "WARNING: Current OS %s has no concrete environment implementation, using default environment and default getDisplayScale value\n",
                    NativeLinker.getPLATFORM_OS());
            Environment.setInstance(new UnknownEnvironment());
        }
    }

    private static void initTagVisualization() {
        TagFilter.setInstance(new ConcreteTagFilter());
    }

    private static void startBackend() {
        boolean serveStaticFiles = true;
        BackendMain backend = new BackendMain(serveStaticFiles);
        Runtime.getRuntime().addShutdownHook(new Thread(backend::stop));
        BackendMain.openWebInterfaceInBrowser();
    }

    private static void startBackend(Settings settings, String testSettingsFileName) {
        boolean serveStaticFiles = true;
        BackendMain backend = new BackendMain(settings, testSettingsFileName, serveStaticFiles);
        Runtime.getRuntime().addShutdownHook(new Thread(backend::stop));
        BackendMain.openWebInterfaceInBrowser();
    }
}
