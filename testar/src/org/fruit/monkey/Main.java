/**
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 Open Universiteit - www.ou.nl
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
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
 *
 * @author Sebastian Bauersfeld
 */


/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.monkey;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;
import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.UnProc;
import org.fruit.Util;
import org.fruit.alayer.Tag;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.fruit.monkey.ConfigTags.AbstractStateAttributes;
import static org.fruit.monkey.ConfigTags.AccessBridgeEnabled;
import static org.fruit.monkey.ConfigTags.ActionDuration;
import static org.fruit.monkey.ConfigTags.AlgorithmFormsFilling;
import static org.fruit.monkey.ConfigTags.AlwaysCompile;
import static org.fruit.monkey.ConfigTags.ClickFilter;
import static org.fruit.monkey.ConfigTags.ConcreteStateAttributes;
import static org.fruit.monkey.ConfigTags.CopyFromTo;
import static org.fruit.monkey.ConfigTags.Delete;
import static org.fruit.monkey.ConfigTags.Discount;
import static org.fruit.monkey.ConfigTags.DrawWidgetInfo;
import static org.fruit.monkey.ConfigTags.DrawWidgetTree;
import static org.fruit.monkey.ConfigTags.DrawWidgetUnderCursor;
import static org.fruit.monkey.ConfigTags.ExecuteActions;
import static org.fruit.monkey.ConfigTags.ExplorationSampleInterval;
import static org.fruit.monkey.ConfigTags.FaultThreshold;
import static org.fruit.monkey.ConfigTags.ForceForeground;
import static org.fruit.monkey.ConfigTags.ForceToSequenceLength;
import static org.fruit.monkey.ConfigTags.GraphDBEnabled;
import static org.fruit.monkey.ConfigTags.GraphDBPassword;
import static org.fruit.monkey.ConfigTags.GraphDBUrl;
import static org.fruit.monkey.ConfigTags.GraphDBUser;
import static org.fruit.monkey.ConfigTags.GraphResuming;
import static org.fruit.monkey.ConfigTags.GraphsActivated;
import static org.fruit.monkey.ConfigTags.LogLevel;
import static org.fruit.monkey.ConfigTags.MaxReward;
import static org.fruit.monkey.ConfigTags.MaxTime;
import static org.fruit.monkey.ConfigTags.Mode;
import static org.fruit.monkey.ConfigTags.MyClassPath;
import static org.fruit.monkey.ConfigTags.NonReactingUIThreshold;
import static org.fruit.monkey.ConfigTags.OfflineGraphConversion;
import static org.fruit.monkey.ConfigTags.OnlySaveFaultySequences;
import static org.fruit.monkey.ConfigTags.OutputDir;
import static org.fruit.monkey.ConfigTags.PathToReplaySequence;
import static org.fruit.monkey.ConfigTags.ProcessListenerEnabled;
import static org.fruit.monkey.ConfigTags.ProcessLogs;
import static org.fruit.monkey.ConfigTags.ProcessesToKillDuringTest;
import static org.fruit.monkey.ConfigTags.PrologActivated;
import static org.fruit.monkey.ConfigTags.ProtocolClass;
import static org.fruit.monkey.ConfigTags.ReplayRetryTime;
import static org.fruit.monkey.ConfigTags.SUTConnector;
import static org.fruit.monkey.ConfigTags.SUTConnectorValue;
import static org.fruit.monkey.ConfigTags.SUTProcesses;
import static org.fruit.monkey.ConfigTags.SequenceLength;
import static org.fruit.monkey.ConfigTags.Sequences;
import static org.fruit.monkey.ConfigTags.ShowSettingsAfterTest;
import static org.fruit.monkey.ConfigTags.ShowVisualSettingsDialogOnStartup;
import static org.fruit.monkey.ConfigTags.StartupTime;
import static org.fruit.monkey.ConfigTags.StateScreenshotSimilarityThreshold;
import static org.fruit.monkey.ConfigTags.StopGenerationOnFault;
import static org.fruit.monkey.ConfigTags.SuspiciousProcessOutput;
import static org.fruit.monkey.ConfigTags.SuspiciousTitles;
import static org.fruit.monkey.ConfigTags.TempDir;
import static org.fruit.monkey.ConfigTags.TestGenerator;
import static org.fruit.monkey.ConfigTags.TimeToFreeze;
import static org.fruit.monkey.ConfigTags.TimeToWaitAfterAction;
import static org.fruit.monkey.ConfigTags.TypingTextsForExecutedAction;
import static org.fruit.monkey.ConfigTags.UnattendedTests;
import static org.fruit.monkey.ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay;
import static org.fruit.monkey.ConfigTags.VisualizeActions;
import static org.fruit.monkey.ConfigTags.VisualizeSelectedAction;

public class Main {

    //public static final String TESTAR_DIR_PROPERTY = "DIRNAME"; //Use the OS environment to obtain TESTAR directory
    static final String SETTINGS_FILE = "test.settings";
    static final String SUT_SETTINGS_EXT = ".sse";
    static String SSE_ACTIVATED = null;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //Default paths
    private static String testarDir = "." + File.separator;
    public static String settingsDir = testarDir + "settings" + File.separator;
    private static String outputDir = testarDir + "output" + File.separator;
    private static String tempDir = outputDir + "temp" + File.separator;


    /**
     * This method scans the settings directory of TESTAR for a file that end with extension SUT_SETTINGS_EXT
     * @return A list of file names that have extension SUT_SETTINGS_EXT
     */
    static String[] getSSE() {
        return new File(settingsDir).list((dir, name) -> name.endsWith(SUT_SETTINGS_EXT));
    }

    /**
     * According to the TESTAR directory and SSE file (settings and protocol to run)
     * return the path of the selected settings
     *
     * @return test.settings path
     */
    private static String getTestSettingsFile() {
        return settingsDir + SSE_ACTIVATED + File.separator + SETTINGS_FILE;
    }

    /**
     * Main method to run TESTAR
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {

        initTestarSSE(args);

        String testSettingsFileName = getTestSettingsFile();
        System.out.println("Test settings is <" + testSettingsFileName + ">");

        final Optional<Settings> settings = loadTestarSettings(args, testSettingsFileName);

        // Continuous Integration: If GUI is disabled TESTAR was executed from command line.
        // We only want to execute TESTAR one time with the selected settings.
        settings.ifPresent(setting -> {
            if (setting.get(ConfigTags.ShowVisualSettingsDialogOnStartup)) {
                while (startTestarDialog(setting, getTestSettingsFile())) {
                    Optional<Settings> newSettings = loadTestarSettings(args, getTestSettingsFile());
                    newSettings.ifPresent(Main::setSettings);
                }
            } else {
                Main.setSettings(setting);
            }
        });


        TestSerialiser.exit();
        ScreenshotSerialiser.exit();
        LogSerialiser.exit();

        System.exit(0);

    }

    private static void setSettings(final Settings settings) {
        settingsLogs(settings);
        setTestarDirectory(settings);
        initCodingManager(settings);
        startTestar(settings);
    }


    /**
     * Set the current directory of TESTAR, settings and output folders
     */
    private static void setTestarDirectory(final Settings settings) {
        outputDir = settings.get(ConfigTags.OutputDir);
        tempDir = settings.get(ConfigTags.TempDir);
    }

    /**
     * Find or create the .sse file, to known with what settings and protocol start TESTAR
     *
     * @param args
     */
    private static void initTestarSSE(String[] args) {

        Locale.setDefault(Locale.ENGLISH);

        // TODO: put the code below into separate method/class
        // Get the files with SUT_SETTINGS_EXT extension and check whether it is not empty
        // and that there is exactly one.

        readProtocolFromCmd(args);

        String[] files = getSSE();

        if (files == null || files.length == 0) {
            showSettingsSelectionMenu();
        } else if (files.length > 1) {
            tooManyFiles(files);
        } else {
            //Use the only file that was found
            SSE_ACTIVATED = files[0].split(SUT_SETTINGS_EXT)[0];
        }
    }

    private static void showSettingsSelectionMenu() {
        settingsSelection();
        if (SSE_ACTIVATED == null) {
            System.exit(-1);
        }
    }

    private static void tooManyFiles(final String[] files) {
        System.out.println("Too many *.sse files - exactly one expected!");
        Arrays.stream(files)
                .forEach(file -> System.out.println("Delete file <" + file + "> = " + new File(file).delete()));
    }

    private static void readProtocolFromCmd(final String[] args) {
        Arrays.stream(args)
                .filter(setting -> setting.contains("sse="))
                .forEach(setting -> {
                    try {
                        protocolFromCmd(setting);
                    } catch (IOException e) {
                        System.out.println("Error trying to modify sse from command line");
                    }
                });
    }

    private static List<File> addSettingsToDropdown() {
        return Arrays.stream(Objects.requireNonNull(new File(settingsDir).listFiles()))
                .filter(file -> new File(file.getPath() + File.separator + SETTINGS_FILE).exists())
                .collect(Collectors.toList());
    }

    /**
     *  This method creates the dropdown menu to select a protocol when TESTAR starts WITHOUT a .sse file
     */
    private static void settingsSelection() {
        final Set<String> sutSettings = new HashSet<>();

        addSettingsToDropdown()
                .forEach(file -> sutSettings.add(file.getName()));

        if (sutSettings.isEmpty()) {
            System.out.println("No SUT settings found!");
            SSE_ACTIVATED = null;
        } else {
            final Object[] options = sutSettings.toArray();
            Arrays.sort(options);
            final JFrame settingsSelectorDialog = new JFrame();
            settingsSelectorDialog.setAlwaysOnTop(true);
            final String sseSelected = (String) JOptionPane.showInputDialog(settingsSelectorDialog,
                    "SUT setting:", "Test setting selection", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (sseSelected == null) {
                SSE_ACTIVATED = null;
            } else {
                final String sseFile = sseSelected + SUT_SETTINGS_EXT;

                selectSse(sseSelected, sseFile);
            }
        }
    }

    private static void selectSse(final String sseSelected, final String sseFile) {
        final File f = new File(settingsDir + File.separator + sseFile);
        try {
            if (f.createNewFile()) {
                SSE_ACTIVATED = sseSelected;
            }
        } catch (IOException e) {
            System.out.println("Exception creating <" + sseFile + "> file");
        }
    }

    //TODO: After know what overrideWithUserProperties does, unify this method with loadSettings

    /**
     * Load the settings of the selected test.settings file
     *
     * @param args
     * @param testSettingsFileName
     * @return settings
     */
    private static Optional<Settings> loadTestarSettings(final String[] args, final String testSettingsFileName) {
        Optional<Settings> settings = Optional.empty();
        try {
            settings = Optional.of(loadSettings(args, testSettingsFileName));
        } catch (ConfigException ce) {
            LogSerialiser.log("There is an issue with the configuration file: " + ce.getMessage() + "\n", LogSerialiser.LogLevel.Critical);
        }

        //TODO: Understand what this exactly does?
        settings.ifPresent(Main::overrideWithUserProperties);

        settings.ifPresent(setting -> {
            final Float SST = setting.get(ConfigTags.StateScreenshotSimilarityThreshold, null);
            if (SST != null) {
                System.setProperty("SCRSHOT_SIMILARITY_THRESHOLD", SST.toString());
            }
        });

        return settings;
    }

    /**
     * Open TESTAR GUI to allow the users modify the settings and the protocol with which the want run TESTAR
     *
     * @param settings
     * @param testSettingsFileName
     * @return true if users starts TESTAR, or false is users close TESTAR
     */
    private static boolean startTestarDialog(Settings settings, final String testSettingsFileName) {
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
     * If logs file doesn't exist create it
     * Create a log into the output/logs directory to save the information of selected settings
     *
     * @param settings
     */
    private static void settingsLogs(final Settings settings) {
        //Check if logs dir exist, if not create it
        File logsDir = new File(outputDir + File.separator + "logs");
        if (!logsDir.exists())
            logsDir.mkdirs();

        System.out.println("Existe " + outputDir + File.separator + "logs" + " resultado: " + logsDir.exists());

        // Starting the logs
        try {
            String logFileName = Util.dateString("yyyy_MM_dd__HH_mm_ss") + ".log";
            File logFile = new File(outputDir + File.separator + "logs" + File.separator + logFileName);
            if (logFile.exists()) {
                logFile = Util.generateUniqueFile(outputDir, logFileName);
            }
            LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile))), settings.get(LogLevel));
        } catch (Throwable t) {
            System.out.println("Cannot initialize log file!");
            t.printStackTrace(System.out);
            System.exit(-1);
        }
        LogSerialiser.log(Util.dateString(DATE_FORMAT) + " TESTAR " + SettingsDialog.TESTAR_VERSION + " is running with the next settings:\n", LogSerialiser.LogLevel.Critical);
        LogSerialiser.log("\n-- settings start ... --\n\n", LogSerialiser.LogLevel.Critical);
        LogSerialiser.log(settings.toString() + "\n", LogSerialiser.LogLevel.Critical);
        LogSerialiser.log("-- ... settings end --\n\n", LogSerialiser.LogLevel.Critical);
    }

    /**
     * This method initializes the coding manager with custom tags to use for constructing
     * concrete and abstract state id's, if provided of course.
     * @param settings
     */
    private static void initCodingManager(Settings settings) {
        // we look if there are user-provided custom state tags in the settings
        // if so, we provide these to the coding manager
        int i;

        // first the attributes for the concrete state id
        if (!settings.get(ConcreteStateAttributes).isEmpty()) {
            i = 0;

            Tag<?>[] concreteTags = new Tag<?>[settings.get(ConcreteStateAttributes).size()];
            for (String concreteStateAttribute : settings.get(ConcreteStateAttributes)) {
                concreteTags[i++] = CodingManager.allowedStateTags.get(concreteStateAttribute);
            }

            CodingManager.setCustomTagsForConcreteId(concreteTags);
        }

        // then the attributes for the abstract state id
        if (!settings.get(AbstractStateAttributes).isEmpty()) {
            i = 0;

            Tag<?>[] abstractTags = new Tag<?>[settings.get(AbstractStateAttributes).size()];
            for (String abstractStateAttribute : settings.get(AbstractStateAttributes)) {
                abstractTags[i++] = CodingManager.allowedStateTags.get(abstractStateAttribute);
            }

            CodingManager.setCustomTagsForAbstractId(abstractTags);
        }

    }

    /**
     * Start TESTAR protocol with the selected settings
     *
     * This method get the specific protocol class of the selected settings to run TESTAR
     *
     * @param settings
     */
    private static void startTestar(final Settings settings) {

        URLClassLoader loader = null;

        try {
            List<String> cp = settings.get(MyClassPath);
            URL[] classPath = new URL[cp.size()];
            for (int i = 0; i < cp.size(); i++) {

                classPath[i] = new File(cp.get(i)).toURI().toURL();
            }

            loader = new URLClassLoader(classPath);

            String pc = settings.get(ProtocolClass);
            String protocolClass = pc.substring(pc.lastIndexOf('/') + 1, pc.length());

            LogSerialiser.log("Trying to load TESTAR protocol in class '" + protocolClass +
                    "' with class path '" + Util.toString(cp) + "'\n", LogSerialiser.LogLevel.Debug);

            @SuppressWarnings("unchecked")
            UnProc<Settings> protocol = (UnProc<Settings>) loader.loadClass(protocolClass).getConstructor().newInstance();
            LogSerialiser.log("TESTAR protocol loaded!\n", LogSerialiser.LogLevel.Debug);

            LogSerialiser.log("Starting TESTAR protocol ...\n", LogSerialiser.LogLevel.Debug);

            //Run TESTAR protocol with the selected settings
            protocol.run(settings);

        } catch (Throwable t) {
            LogSerialiser.log("An unexpected error occurred: " + t + "\n", LogSerialiser.LogLevel.Critical);
            System.out.println("Main: Exception caught");
            t.printStackTrace();
            t.printStackTrace(LogSerialiser.getLogStream());
        } finally {
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

    // TODO: This methods should be part of the Settings class. It contains all the default values of the settings.

    /**
     * Load the default settings for all the configurable settings and add/overwrite with those from the file
     * This is needed because the user might not have set all the possible settings in the test.settings file.
     * @param argv
     * @param file
     * @return An instance of Settings
     * @throws ConfigException
     */
    static Settings loadSettings(final String[] argv, final String file) throws ConfigException {
        Assert.notNull(file);
        try {
            List<Pair<?, ?>> defaults = new ArrayList<Pair<?, ?>>();
            defaults.add(Pair.from(ProcessesToKillDuringTest, "(?!x)x"));
            defaults.add(Pair.from(ShowVisualSettingsDialogOnStartup, true));
            defaults.add(Pair.from(FaultThreshold, 0.1));
            defaults.add(Pair.from(LogLevel, 1));
            defaults.add(Pair.from(Mode, RuntimeControlsProtocol.Modes.Spy));
            defaults.add(Pair.from(OutputDir, outputDir));
            defaults.add(Pair.from(TempDir, tempDir));
            defaults.add(Pair.from(OnlySaveFaultySequences, false));
            defaults.add(Pair.from(PathToReplaySequence, tempDir));
            defaults.add(Pair.from(ActionDuration, 0.1));
            defaults.add(Pair.from(TimeToWaitAfterAction, 0.1));
            defaults.add(Pair.from(ExecuteActions, true));
            defaults.add(Pair.from(DrawWidgetUnderCursor, false));
            defaults.add(Pair.from(DrawWidgetInfo, true));
            defaults.add(Pair.from(VisualizeActions, false));
            defaults.add(Pair.from(VisualizeSelectedAction, false));
            defaults.add(Pair.from(SequenceLength, 10));
            defaults.add(Pair.from(ReplayRetryTime, 30.0));
            defaults.add(Pair.from(Sequences, 1));
            defaults.add(Pair.from(MaxTime, 31536000.0));
            defaults.add(Pair.from(StartupTime, 8.0));
            defaults.add(Pair.from(SUTConnectorValue, ""));
            defaults.add(Pair.from(Delete, new ArrayList<String>()));
            defaults.add(Pair.from(CopyFromTo, new ArrayList<Pair<String, String>>()));
            defaults.add(Pair.from(SuspiciousTitles, "(?!x)x"));
            defaults.add(Pair.from(ClickFilter, "(?!x)x"));
            defaults.add(Pair.from(MyClassPath, Arrays.asList(settingsDir)));
            defaults.add(Pair.from(ProtocolClass, "org.fruit.monkey.DefaultProtocol"));
            defaults.add(Pair.from(ForceForeground, true));
            defaults.add(Pair.from(UseRecordedActionDurationAndWaitTimeDuringReplay, true));
            defaults.add(Pair.from(StopGenerationOnFault, true));
            defaults.add(Pair.from(TimeToFreeze, 10.0));
            defaults.add(Pair.from(ShowSettingsAfterTest, true));
            defaults.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_CMDLINE));
            defaults.add(Pair.from(TestGenerator, "random"));
            defaults.add(Pair.from(MaxReward, 9999999.0));
            defaults.add(Pair.from(Discount, .95));
            defaults.add(Pair.from(AlgorithmFormsFilling, false));
            defaults.add(Pair.from(TypingTextsForExecutedAction, 10));
            defaults.add(Pair.from(DrawWidgetTree, false));
            defaults.add(Pair.from(ExplorationSampleInterval, 1));
            defaults.add(Pair.from(GraphsActivated, true));
            defaults.add(Pair.from(PrologActivated, false));
            defaults.add(Pair.from(GraphResuming, true));
            defaults.add(Pair.from(ForceToSequenceLength, true));
            defaults.add(Pair.from(NonReactingUIThreshold, 100)); // number of executed actions
            defaults.add(Pair.from(OfflineGraphConversion, true));
            defaults.add(Pair.from(StateScreenshotSimilarityThreshold, Float.MIN_VALUE)); // disabled
            defaults.add(Pair.from(UnattendedTests, false)); // disabled
            defaults.add(Pair.from(AccessBridgeEnabled, false)); // disabled
            defaults.add(Pair.from(SUTProcesses, ""));
            defaults.add(Pair.from(GraphDBEnabled, false));
            defaults.add(Pair.from(GraphDBUrl, ""));
            defaults.add(Pair.from(GraphDBUser, ""));
            defaults.add(Pair.from(GraphDBPassword, ""));
            defaults.add(Pair.from(AlwaysCompile, true));
            defaults.add(Pair.from(ProcessListenerEnabled, false));
            defaults.add(Pair.from(SuspiciousProcessOutput, "(?!x)x"));
            defaults.add(Pair.from(ProcessLogs, ".*.*"));

            defaults.add(Pair.from(ConcreteStateAttributes, new ArrayList<>(CodingManager.allowedStateTags.keySet())));
            defaults.add(Pair.from(AbstractStateAttributes, new ArrayList<String>() {
                {
                    add("Role");
                }
            }));

            //Overwrite the default settings with those from the file
            Settings settings = Settings.fromFile(defaults, file);
            //Make sure that Prolog is ALWAYS false, even if someone puts it to true in their test.settings file
            //Need this during refactoring process of getting Prolog code out. Refactoring will assume that
            //PrologActivated is ALWAYS false.
            //Evidently it will now be IMPOSSIBLE for it to be true hahahahahahaha
            settings.set(ConfigTags.PrologActivated, false);

            // check that the abstract state properties and the abstract action properties have at least 1 value
            if ((settings.get(ConcreteStateAttributes)).isEmpty()) {
                throw new ConfigException("Please provide at least 1 valid concrete state attribute or leave the key out of the settings file");
            }

            // check that the abstract state properties and the abstract action properties have at least 1 value
            if ((settings.get(AbstractStateAttributes)).isEmpty()) {
                throw new ConfigException("Please provide at least 1 valid abstract state attribute or leave the key out of the settings file");
            }

            return settings;
        } catch (IOException ioe) {
            throw new ConfigException("Unable to load configuration file!", ioe);
        }
    }

    /**
     * This method creates a sse file to change TESTAR protocol if sett param matches an existing protocol
     * @param sett
     * @throws IOException
     */
    private static void protocolFromCmd(final String sett) throws IOException {
        final String sseName = sett.substring(sett.indexOf("=") + 1);
        boolean existSSE = false;

        //Check if choose protocol exist
        for (File f : new File(settingsDir).listFiles()) {
            if (new File(settingsDir + sseName + File.separator + SETTINGS_FILE).exists()) {
                existSSE = true;
                break;
            }
        }

        //Command line protocol doesn't exist
        if (!existSSE) {
            System.out.println("Protocol: " + sseName + " doesn't exist");
        } else {
            //Obtain previous sse file and delete it (if exist)
            String[] files = getSSE();
            if (files != null) {
                for (String f : files)
                    new File(settingsDir + f).delete();

            }

            //Create the new sse file
            String sseDir = settingsDir + sseName + SUT_SETTINGS_EXT;
            File f = new File(sseDir);
            if (!f.exists())
                f.createNewFile();

            System.out.println("Protocol changed from command line to: " + sseName);

        }
    }

    //TODO: Understand what this exactly does?

    /**
     * Override something. Not sure what
     * @param settings
     */
    private static void overrideWithUserProperties(final Settings settings) {
        String pS, p;
        // headless mode
        pS = ConfigTags.ShowVisualSettingsDialogOnStartup.name();
        p = System.getProperty(pS, null);
        if (p == null) {
            p = System.getProperty("headless", null); // mnemonic
        }
        if (p != null) {
            settings.set(ConfigTags.ShowVisualSettingsDialogOnStartup, !(new Boolean(p).booleanValue()));
            LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">", LogSerialiser.LogLevel.Critical);
        }
        // TestGenerator
        pS = ConfigTags.TestGenerator.name();
        p = System.getProperty(pS, null);
        if (p == null) {
            p = System.getProperty("TG", null); // mnemonic
        }
        if (p != null) {
            settings.set(ConfigTags.TestGenerator, p);
            LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">", LogSerialiser.LogLevel.Critical);
        }
        // SequenceLength
        pS = ConfigTags.SequenceLength.name();
        p = System.getProperty(pS, null);
        if (p == null) {
            p = System.getProperty("SL", null); // mnemonic
        }
        if (p != null) {
            try {
                Integer sl = new Integer(p);
                settings.set(ConfigTags.SequenceLength, sl);
                LogSerialiser.log("Property <" + pS + "> overridden to <" + sl.toString() + ">", LogSerialiser.LogLevel.Critical);
            } catch (NumberFormatException e) {
                LogSerialiser.log("Property <" + pS + "> could not be set! (using default)", LogSerialiser.LogLevel.Critical);
            }
        }
        // GraphResumingActivated
        pS = ConfigTags.GraphResuming.name();
        p = System.getProperty(pS, null);
        if (p == null) {
            p = System.getProperty("GRA", null); // mnemonic
        }
        if (p != null) {
            settings.set(ConfigTags.GraphResuming, new Boolean(p).booleanValue());
            LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">", LogSerialiser.LogLevel.Critical);
        }
        // ForceToSequenceLength
        pS = ConfigTags.ForceToSequenceLength.name();
        p = System.getProperty(pS, null);
        if (p == null) {
            p = System.getProperty("F2SL", null); // mnemonic
        }
        if (p != null) {
            settings.set(ConfigTags.ForceToSequenceLength, new Boolean(p).booleanValue());
            LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">", LogSerialiser.LogLevel.Critical);
        }
        // TypingTextsForExecutedAction
        pS = ConfigTags.TypingTextsForExecutedAction.name();
        p = System.getProperty(pS, null);
        if (p == null) {
            p = System.getProperty("TT", null); // mnemonic
        }
        if (p != null) {
            try {
                Integer tt = new Integer(p);
                settings.set(ConfigTags.TypingTextsForExecutedAction, tt);
                LogSerialiser.log("Property <" + pS + "> overridden to <" + tt.toString() + ">", LogSerialiser.LogLevel.Critical);
            } catch (NumberFormatException e) {
                LogSerialiser.log("Property <" + pS + "> could not be set! (using default)", LogSerialiser.LogLevel.Critical);
            }
        }
        // StateScreenshotSimilarityThreshold
        pS = ConfigTags.StateScreenshotSimilarityThreshold.name();
        p = System.getProperty(pS, null);
        if (p == null) {
            p = System.getProperty("SST", null); // mnemonic
        }
        if (p != null) {
            try {
                Float sst = new Float(p);
                settings.set(ConfigTags.StateScreenshotSimilarityThreshold, sst);
                LogSerialiser.log("Property <" + pS + "> overridden to <" + sst.toString() + ">", LogSerialiser.LogLevel.Critical);
            } catch (NumberFormatException e) {
                LogSerialiser.log("Property <" + pS + "> could not be set! (using default)", LogSerialiser.LogLevel.Critical);
            }
        }
        // UnattendedTests
        pS = ConfigTags.UnattendedTests.name();
        p = System.getProperty(pS, null);
        if (p == null) {
            p = System.getProperty("UT", null); // mnemonic
        }
        if (p != null) {
            settings.set(ConfigTags.UnattendedTests, new Boolean(p).booleanValue());
            LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">", LogSerialiser.LogLevel.Critical);
        }
    }

}
