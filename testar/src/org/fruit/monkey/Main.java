/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 Open Universiteit - www.ou.nl
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

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.monkey;

import static org.fruit.monkey.ConfigTags.*;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import nl.ou.testar.tgherkin.model.Document;
import org.fruit.alayer.Tag;
import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.UnProc;
import org.fruit.Util;

public class Main {
  private static final double MINIMUM_PERCENTAGE_DEFAULT = 95.0;
  private static final double DISCOUNT_DEFAULT = .95;
  private static final double MAXREWARD_DEFAULT = 9999999.0;
  private static final double STARTUP_TIME_DEFAULT = 8.0;
  private static final double MAXTIME_DEFAULT = 31536000.0;
  private static final double REPLAY_RETRY_TIME_DEFAULT = 30.0;
  public static final String SETTINGS_DIR_PROPERTY = "SettingsDir";
  public static String SETTINGS_DIR_DEFAULT = "./resources/settings/";

  private static String settingsDir = null;

  @SuppressWarnings ("unchecked")
  private static void overrideClass(Settings settings, Tag<?> configtag, String mnemonic) {
    String configtagname;
    String systemConfigtagName;
    configtagname = configtag.name();
    systemConfigtagName = System.getProperty(configtagname, null);
    if (systemConfigtagName == null) {
      systemConfigtagName = System.getProperty(mnemonic, null); // mnemonic
    }
    if (systemConfigtagName != null) {
      if (configtag.type() == Boolean.class) {
         settings.set((Tag<Boolean>)configtag, !(new Boolean(systemConfigtagName).booleanValue()));
         LogSerialiser.log("Property <" + configtagname + "> overridden to <" + systemConfigtagName + ">", LogSerialiser.LogLevel.Critical);
      }
      if (configtag.type() == String.class) {
         settings.set((Tag<String>)configtag, systemConfigtagName);
         LogSerialiser.log("Property <" + configtagname + "> overridden to <" + systemConfigtagName + ">", LogSerialiser.LogLevel.Critical);
      }
      if (configtag.type() == Integer.class || configtag.type() == Float.class) {
        try {
          if (configtag.type() == Integer.class) {
            Integer sl = new Integer(systemConfigtagName);
            settings.set((Tag<Integer>)configtag, sl);
          }
          if (configtag.type() == Float.class) {
            Float fl = new Float(systemConfigtagName);
            settings.set((Tag<Float>)configtag, fl);
          }
          LogSerialiser.log("Property <" + configtagname + "> overridden to <" + systemConfigtagName + ">", LogSerialiser.LogLevel.Critical);
        } catch (NumberFormatException e) {
          LogSerialiser.log("Property <" + configtagname + "> could not be set! (using default)", LogSerialiser.LogLevel.Critical);
        }

      }
    }
  }

  // TODO: Understand what this exactly does?
  /**
   * Override something. Not sure what
   * @param settings The settings to load
   */
  private static void overrideWithUserProperties(Settings settings) {
    // headless mode
    overrideClass(settings, ShowVisualSettingsDialogOnStartup, "headless");

    // TestGenerator
    overrideClass(settings, TestGenerator, "TG");

    // SequenceLength
    overrideClass(settings, SequenceLength, "SL");

    // GraphResumingActivated
    overrideClass(settings, GraphResuming, "GRA");

    // ForceToSequenceLength
    overrideClass(settings, ForceToSequenceLength, "F2SL");

    // TypingTextsForExecutedAction
    overrideClass(settings, TypingTextsForExecutedAction, "TT");

    // StateScreenshotSimilarityThreshold
    overrideClass(settings, StateScreenshotSimilarityThreshold, "SST");

    // UnattendedTests
    overrideClass(settings, UnattendedTests, "UT");
  }

  public static String getSettingsDir() {
    if (settingsDir == null) {
      settingsDir = System.getenv(SETTINGS_DIR_PROPERTY);
      if (settingsDir == null) {
        settingsDir = SETTINGS_DIR_DEFAULT;
      }
      LogSerialiser.log("Property <" + SETTINGS_DIR_PROPERTY + "> set to <" + settingsDir + ">", LogSerialiser.LogLevel.Info);
    }
    return  settingsDir;
  }

  public static final String SETTINGS_FILE = "test.settings";
  public static final String SUT_SETTINGS_EXT = ".sse";
  private static String sseActivated = null;

  public static void setSseActivated(String sseActivated) {
    Main.sseActivated = sseActivated;
  }

  /**
   *  This method creates the drop down menu to select a protocol when TESTAR starts WITHOUT a .sse file
   */
  //FIXME: This method throws a NullPointerException when you do not start TESTAR explicitly
  //       from the bin directory because it cannot find the settings files
  private static void settingsSelection() {
    Set<String> sutSettings = new HashSet<String>();
    for (File f: new File(getSettingsDir()).listFiles()) {
      if (new File(f.getPath() + "/" + SETTINGS_FILE).exists()) {
        sutSettings.add(f.getName());
      }
    }
    if (sutSettings.isEmpty()) {
      System.out.println("No SUT settings found!");
    }
    else {
      Object[] options = sutSettings.toArray();
      Arrays.sort(options);
      JFrame settingsSelectorDialog = new JFrame();
      settingsSelectorDialog.setAlwaysOnTop(true);
      String s = (String) JOptionPane.showInputDialog(settingsSelectorDialog,
          "SUT setting:", "Test setting selection",
          JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
      if (s == null) {
        sseActivated = null;
        return;
      }
      final String sse = s + SUT_SETTINGS_EXT;
      try {
        File f = new File(getSettingsDir() + File.separator + sse);
        if (f.createNewFile()) {
          //System.out.println("Using <" + s + "> test settings");
          sseActivated = s;
          return;
        }
      } catch (IOException e) {
        System.out.println("Exception creating <" + sse + "> file");
      }
    }
    sseActivated = null;
  }

  /**
   * This method scans the settings directory of TESTAR for a file that end with extension SUT_SETTINGS_EXT
   * @return A list of file names that have extension SUT_SETTINGS_EXT
   */
  public static String[] getSSE() {
    return new File(getSettingsDir()).list(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(SUT_SETTINGS_EXT);
      }
    });
  }

  public static String getSettingsFile() {
    return getSettingsDir() + sseActivated + File.separator + SETTINGS_FILE;
  }

  private static void startLogs(Settings settings) {
    // Starting the logs
    try {
      // TODO: The date format is not consistent everywhere (see DATE-FORMAT comments)
      String logFileName = Util.dateString("yyyy_MM_dd__HH_mm_ss") + ".log";
      File logFile = new File(settings.get(OutputDir) + File.separator + logFileName);
      if (logFile.exists()) {
        logFile = Util.generateUniqueFile(settings.get(OutputDir), logFileName);
      }
      LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile))), settings.get(LogLevel)); // by urueda
    } catch (Throwable t) {
      System.out.println("Cannot initialize log file!");
      t.printStackTrace(System.out);
      System.exit(-1);
    }
    //TODO: DATE-FORMAT not consistent
    LogSerialiser.log(Util.dateString("dd.MMMMM.yyyy HH:mm:ss") + " TESTAR " + SettingsDialog.TESTAR_VERSION + " is running" + /*Util.lineSep() + Util.lineSep() +*/ " with the next settings:\n", LogSerialiser.LogLevel.Critical);
    LogSerialiser.log("\n-- settings start ... --\n\n", LogSerialiser.LogLevel.Critical);
    LogSerialiser.log(settings.toString() + "\n", LogSerialiser.LogLevel.Critical);
    LogSerialiser.log("-- ... settings end --\n\n", LogSerialiser.LogLevel.Critical);
  }

  public static boolean startTestarDialog(Settings settings, String testSettingsFileName) {
    // Start up the TESTAR Dialog
    if (settings.get(ConfigTags.ShowVisualSettingsDialogOnStartup)) {
      try {
        Settings testsettings = new SettingsDialog().run(settings, testSettingsFileName);
        if (testsettings == null) {
          return false;
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return true;
  }

  public static void startTestar(Settings settings, String testSettings) {

    URLClassLoader loader = null;
    try {
      List<String> cp = settings.get(MyClassPath);
      URL[] classPath = new URL[cp.size()];
      for (int i = 0; i < cp.size(); i++) {

        classPath[i] = new File(cp.get(i)).toURI().toURL();

      }
      loader = new URLClassLoader(classPath);

      String protocolClass = settings.get(ProtocolClass).replace("/",".");
      LogSerialiser.log("Trying to load TESTAR protocol in class '" +
          protocolClass +
          "' with class path '" + Util.toString(cp) + "'\n", LogSerialiser.LogLevel.Debug);
      @SuppressWarnings("unchecked")
      UnProc<Settings> protocol = (UnProc<Settings>) loader.loadClass(protocolClass).getConstructor().newInstance();
      LogSerialiser.log("TESTAR protocol loaded!\n", LogSerialiser.LogLevel.Debug);

      LogSerialiser.log("Starting TESTAR protocol ...\n", LogSerialiser.LogLevel.Debug);

      protocol.run(settings);

    } catch (Throwable t) {
      LogSerialiser.log("An unexpected error occurred: " + t + "\n", LogSerialiser.LogLevel.Critical);
      System.out.println("Main: Exception caught");
      t.printStackTrace();
      t.printStackTrace(LogSerialiser.getLogStream());
    } finally {
      TestSerialiser.exit();
      ScreenshotSerialiser.exit();
      LogSerialiser.exit();
      if (loader != null) {
        try {
          loader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      System.exit(0);
    }

  }

  private static Settings initTestarSettings(String[] args) {

    Settings settings = null;

    Locale.setDefault(Locale.ENGLISH);

    // TODO: put the code below into separate method/class
    // Get the files with SUT_SETTINGS_EXT extension and check whether it is not empty
    // and that there is exactly one.

    //Allow users to use command line to choose a protocol modifying sse file
    for (String sett: args) {
      if (sett.toString().contains("sse=")) {
        try {
          protocolFromCmd(sett);
         } catch(Exception e) {
          System.out.println("Error trying to modify sse from command line");
        }
      }
    }

    String[] files = getSSE();
    // If there is more than 1, then delete them all
    if (files != null && files.length > 1) {
      System.out.println("Too many *.sse files - exactly one expected!");
      for (String f: files) {
        System.out.println("Delete file <" + f + "> = " + new File(f).delete());
      }
      files = null;
    }
    //If there is none, then start up a selection menu
    if (files == null || files.length == 0) {
      settingsSelection();
      if (sseActivated == null) {
        System.exit(-1);
      }
    }
    else {
      //Use the only file that was found
      sseActivated = files[0].split(SUT_SETTINGS_EXT)[0];
    }

    return settings;
  }

  private static Settings loadSettings(Settings settings, String[] args, String testSettingsFileName) {
    try {
      settings = loadSettings(args, testSettingsFileName);
    } catch (ConfigException ce) {
      LogSerialiser.log("There is an issue with the configuration file: " + ce.getMessage() + "\n", LogSerialiser.LogLevel.Critical);
    }
    overrideWithUserProperties(settings);
    Float SST = settings.get(ConfigTags.StateScreenshotSimilarityThreshold, null);

    if (SST != null) {
      System.setProperty("SCRSHOT_SIMILARITY_THRESHOLD", SST.toString());
    }
    return settings;
  }

  public static void main(String[] args) throws IOException {

    Settings settings = initTestarSettings(args);

    String testSettingsFileName = getSettingsFile();
    System.out.println("Test settings is <" + testSettingsFileName + ">");

    settings = loadSettings(settings, args, testSettingsFileName);

    if (startTestarDialog(settings, testSettingsFileName)) {

      startLogs(settings);

      testSettingsFileName = getSettingsFile();
      settings = loadSettings(settings, args, testSettingsFileName);
      startTestar(settings, testSettingsFileName);
    }

    TestSerialiser.exit();
    ScreenshotSerialiser.exit();
    LogSerialiser.exit();

    System.exit(0);

  }

  // TODO: This methods should be part of the Settings class. It contains all the default values of the settings.
  /**
   * Load the default settings for all the configurable settings and add/overwrite with those from the file
   * This is needed because the user might not have set all the possible settings in the test.settings file.
   * @param argv input arguments
   * @param file file to load setting from
   * @return An instance of Settings
   * @throws ConfigException if configuration fails
   */
  public static Settings loadSettings(String[] argv, String file) throws ConfigException {
    Assert.notNull(file);
    try {
      List<Pair<?, ?>> defaults = new ArrayList<Pair<?, ?>>();

      defaults.add(Pair.from(ProcessesToKillDuringTest, "(?!x)x"));
      defaults.add(Pair.from(ShowVisualSettingsDialogOnStartup, true));
      defaults.add(Pair.from(FaultThreshold, 0.1));
      defaults.add(Pair.from(LogLevel, 1));
      defaults.add(Pair.from(Mode, RuntimeControlsProtocol.Modes.Spy));
      defaults.add(Pair.from(OutputDir, "."));
      defaults.add(Pair.from(TempDir, "."));
      defaults.add(Pair.from(OnlySaveFaultySequences, false));
      defaults.add(Pair.from(PathToReplaySequence, "./output/temp"));
      defaults.add(Pair.from(ActionDuration, 0.1));
      defaults.add(Pair.from(TimeToWaitAfterAction, 0.1));
      defaults.add(Pair.from(ExecuteActions, true));
      defaults.add(Pair.from(DrawWidgetUnderCursor, false));
      defaults.add(Pair.from(DrawWidgetInfo, true));
      defaults.add(Pair.from(VisualizeActions, false));
      defaults.add(Pair.from(VisualizeSelectedAction, false));
      defaults.add(Pair.from(SequenceLength, 10));
      defaults.add(Pair.from(ReplayRetryTime, REPLAY_RETRY_TIME_DEFAULT));
      defaults.add(Pair.from(Sequences, 1));
      defaults.add(Pair.from(MaxTime, MAXTIME_DEFAULT));
      defaults.add(Pair.from(StartupTime, STARTUP_TIME_DEFAULT));
      defaults.add(Pair.from(SUTConnectorValue, ""));
      defaults.add(Pair.from(Delete, new ArrayList<String>()));
      defaults.add(Pair.from(CopyFromTo, new ArrayList<Pair<String, String>>()));
      defaults.add(Pair.from(SuspiciousTitles, "(?!x)x"));
      defaults.add(Pair.from(ClickFilter, "(?!x)x"));
      defaults.add(Pair.from(MyClassPath, Arrays.asList(".")));
      defaults.add(Pair.from(ProtocolClass, "org.fruit.monkey.DefaultProtocol"));
      defaults.add(Pair.from(ForceForeground, true));
      defaults.add(Pair.from(UseRecordedActionDurationAndWaitTimeDuringReplay, true));
      defaults.add(Pair.from(StopGenerationOnFault, true));
      defaults.add(Pair.from(TimeToFreeze, 10.0));
      defaults.add(Pair.from(ShowSettingsAfterTest, true));
      defaults.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_CMDLINE));
      defaults.add(Pair.from(TestGenerator, "random"));
      defaults.add(Pair.from(MaxReward, MAXREWARD_DEFAULT));
      defaults.add(Pair.from(Discount, DISCOUNT_DEFAULT));
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

      // Tgherkin and subroutine tags
      defaults.add(Pair.from(TgherkinDocument, ""));
      defaults.add(Pair.from(TgherkinFileData, ""));
      defaults.add(Pair.from(CompoundActionData, ""));
      defaults.add(Pair.from(ApplyDefaultOnMismatch, true));
      defaults.add(Pair.from(ContinueToApplyDefault, true));
      defaults.add(Pair.from(RepeatTgherkinScenarios, true));
      defaults.add(Pair.from(GenerateTgherkinReport, false));
      defaults.add(Pair.from(StoreTgherkinReport, false));
      defaults.add(Pair.from(ReportDerivedGestures, false));
      defaults.add(Pair.from(ReportState, false));
      defaults.add(Pair.from(ConfidenceThreshold, 0.7));
      defaults.add(Pair.from(TgherkinReportIncludeOCR, false));
      defaults.add(Pair.from(TgherkinReportIncludeImageRecognition, false));
      defaults.add(Pair.from(TgherkinNrOfNOPRetries, 0));
      defaults.add(Pair.from(TgherkinExecutionMode, Document.getRegisteredExecutionModes()[0]));
      defaults.add(Pair.from(MinimumPercentageForImageRecognition, MINIMUM_PERCENTAGE_DEFAULT));

      //Overwrite the default settings with those from the file
      Settings settings = Settings.fromFile(defaults, file);

      //If user use command line to input properties, mix file settings with cmd properties
      if (argv.length>0) {
        try {
          settings = Settings.fromFileCmd(defaults, file, argv);
        } catch(Exception e) {
          System.out.println("Error with command line properties. Examples:");
          System.out.println("testar SUTConnectorValue=\"C:\\\\Windows\\\\System32\\\\notepad.exe\" Sequences=11 SequenceLength=12 SuspiciousTitle=.*aaa.*");
          System.out.println("SUTConnectorValue=\" \"\"C:\\\\Program Files\\\\Internet Explorer\\\\iexplore.exe\"\" \"\"https://www.google.es\"\" \"");
        }
      }

      //Make sure that Prolog is ALWAYS false, even if someone puts it to true in their test.settings file
      //Need this during re-factoring process of getting Prolog code out. Re-factoring will assume that
      //PrologActivated is ALWAYS false.
      //Evidently it will now be IMPOSSIBLE for it to be true hahahahahahaha
      settings.set(ConfigTags.PrologActivated, false);
      return settings;
    } catch (IOException ioe) {
      throw new ConfigException("Unable to load configuration file!", ioe);
    }
  }

  /**
   * This method creates a sse file to change TESTAR protocol if sett param matches an existing protocol
   * @param sett protocol name
   * @throws IOException input/output exception
   */
  @SuppressWarnings ("unused")
  public static void protocolFromCmd(String sett) throws IOException {
    String sseName = sett.substring(sett.indexOf("=")+1);
    boolean existSSE = false;

    //Check if choose protocol exist
    for (File f: new File(getSettingsDir()).listFiles()) {
      if (new File(getSettingsDir()+sseName + "/" + SETTINGS_FILE).exists()) {
        existSSE = true;
        break;
      }
    }

    //Command line protocol doesn't exist
    if (!existSSE) {
      System.out.println("Protocol: "+sseName+" doesn't exist");
    }

    else {
      //Obtain previous sse file and delete it (if exist)
      String[] files = getSSE();
      if (files != null) {
        for (String f: files) {
          //System.out.println("delete file: "+getSettingsDir()+f.toString());
          new File(getSettingsDir()+f).delete();
        }
      }

      //Create the new sse file
      String sseDir = getSettingsDir()+sseName+".sse";
      File f = new File(sseDir);
      if (!f.exists()) {
        f.createNewFile();
      }
      System.out.println("Protocol changed from command line to: "+sseName);
    }
  }
}
