/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;
import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.UnProc;
import org.fruit.Util;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static org.fruit.monkey.ConfigTags.*;

public class Main {

  // by urueda
  private static void overrideWithUserProperties(Settings settings) { // overrides test.settings
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

  // begin by urueda
  public static final String SETTINGS_FILE = "test.settings";
  public static final String SUT_SETTINGS_EXT = ".sse";
  public static String SSE_ACTIVATED = null;
  // end by urueda

  // by urueda
  private static void settingsSelection() {
    Set<String> sutSettings = new HashSet<String>();
    for (File f : new File("./settings").listFiles()) {
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
        SSE_ACTIVATED = null;
        return;
      }
      final String sse = s + SUT_SETTINGS_EXT;
      try {
        File f = new File("./settings/" + sse);
        if (f.createNewFile()) {
          //System.out.println("Using <" + s + "> test settings");
          SSE_ACTIVATED = s;
          return;
        }
      } catch (IOException e) {
        System.out.println("Exception creating <" + sse + "> file");
      }
    }
    SSE_ACTIVATED = null;
  }

  // by urueda
  public static String[] getSSE() {
    return new File("./settings/").list(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(SUT_SETTINGS_EXT);
      }
    });
  }

  public static void main(String[] args) throws IOException {
    Settings settings = null;
    //by fraalpe2
    Locale.setDefault(Locale.ENGLISH);

    // begin by urueda
    String[] files = getSSE();
    if (files != null && files.length > 1) {
      System.out.println("Too many *.sse files - exactly one expected!");
      for (String f : files) {
        System.out.println("Delete file <" + f + "> = " + new File(f).delete());
      }
      files = null;
    }
    if (files == null || files.length == 0) {
      settingsSelection();
      if (SSE_ACTIVATED == null) {
        System.exit(-1);
      }
    }
    else {
      SSE_ACTIVATED = files[0].split(SUT_SETTINGS_EXT)[0];
    }
    String testSettings = "./settings/" + SSE_ACTIVATED + "/" + SETTINGS_FILE;
    System.out.println("Test settings is <" + testSettings + ">");
    URLClassLoader loader = null;
    // end by urueda

    try {
      settings = loadSettings(args, testSettings);
      overrideWithUserProperties(settings); // by urueda
      Float SST = settings.get(ConfigTags.StateScreenshotSimilarityThreshold, null);
      if (SST != null) {
        System.setProperty("SCRSHOT_SIMILARITY_THRESHOLD", SST.toString());
      }

      if (settings.get(ConfigTags.ShowVisualSettingsDialogOnStartup)) {
        if ((settings = new SettingsDialog().run(settings, testSettings)) == null) {
          return;
        }
      }

      try {
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

      //logln(Util.dateString("dd.MMMMM.yyyy HH:mm:ss"));
      //logln("Hello, I'm the FRUIT Monkey!" + Util.lineSep() + Util.lineSep() + "These are my settings:", Main.LogLevel.Critical);
      // start by urueda
      LogSerialiser.log(Util.dateString("dd.MMMMM.yyyy HH:mm:ss") + " TESTAR " + SettingsDialog.TESTAR_VERSION + " is running" + /*Util.lineSep() + Util.lineSep() +*/ " with the next settings:\n", LogSerialiser.LogLevel.Critical);
      LogSerialiser.log("\n-- settings start ... --\n\n", LogSerialiser.LogLevel.Critical);
      // end by urueda
      LogSerialiser.log(settings.toString() + "\n", LogSerialiser.LogLevel.Critical);
      LogSerialiser.log("-- ... settings end --\n\n", LogSerialiser.LogLevel.Critical); // by urueda
      List<String> cp = settings.get(MyClassPath);
      URL[] classPath = new URL[cp.size()];
      for (int i = 0; i < cp.size(); i++) {
        classPath[i] = new File(cp.get(i)).toURI().toURL();
      }
      loader = new URLClassLoader(classPath);

      //logln("Trying to load monkey protocol in class '" + settings.get(ProtocolClass) + "' with class path '" + Util.toString(cp) + "'", Main.LogLevel.Debug);
      String protocolClass = settings.get(ProtocolClass).split("/")[1]; // by urueda
      LogSerialiser.log("Trying to load TESTAR protocol in class '" +
          protocolClass +
          "' with class path '" + Util.toString(cp) + "'\n", LogSerialiser.LogLevel.Debug); // by urueda
      @SuppressWarnings("unchecked")
      UnProc<Settings> protocol = (UnProc<Settings>) loader.loadClass(protocolClass).getConstructor().newInstance();
      //logln("Monkey protocol loaded!", Main.LogLevel.Debug);
      LogSerialiser.log("TESTAR protocol loaded!\n", LogSerialiser.LogLevel.Debug); // by urueda

      //logln("Starting monkey protocol ...", Main.LogLevel.Debug);
      LogSerialiser.log("Starting TESTAR protocol ...\n", LogSerialiser.LogLevel.Debug); // by urueda
      protocol.run(settings);
    } catch (ConfigException ce) {
      LogSerialiser.log("There is an issue with the configuration file: " + ce.getMessage() + "\n", LogSerialiser.LogLevel.Critical);
    } catch (Throwable t) {
      LogSerialiser.log("An unexpected error occurred: " + t + "\n", LogSerialiser.LogLevel.Critical);
      t.printStackTrace(System.out);
      t.printStackTrace(LogSerialiser.getLogStream());
    } finally {
      //logln("Monkey stopped execution.", Main.LogLevel.Critical);
      //logln(Util.dateString("dd.MMMMM.yyyy HH:mm:ss"));
      //LogSerialiser.log("TESTAR stopped execution at " + Util.dateString("dd.MMMMM.yyyy HH:mm:ss") + "\n", LogSerialiser.LogLevel.Critical); // by urueda
      //LogSerialiser.finish(); // by urueda
      //if(settings != null && settings.get(ShowSettingsAfterTest))
      //Runtime.getRuntime().exec("cmd /c start monkey.bat && exit");
      // begin by urueda
      TestSerialiser.exit();
      ScreenshotSerialiser.exit();
      LogSerialiser.exit();
      Grapher.exit();
      if (loader != null) {
        try {
          loader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      // end by urueda
      System.exit(0);
    }
  }

  public static Settings loadSettings(String[] argv, String file) throws ConfigException {
    Assert.notNull(file); // by urueda
    try {
      List<Pair<?, ?>> defaults = new ArrayList<Pair<?, ?>>();

      defaults.add(Pair.from(ProcessesToKillDuringTest, "(?!x)x"));
      defaults.add(Pair.from(ShowVisualSettingsDialogOnStartup, true));
      defaults.add(Pair.from(FaultThreshold, 0.1));
      defaults.add(Pair.from(LogLevel, 1));
      defaults.add(Pair.from(Mode, AbstractProtocol.Modes.Spy));
      defaults.add(Pair.from(OutputDir, "."));
      defaults.add(Pair.from(TempDir, "."));
      defaults.add(Pair.from(OnlySaveFaultySequences, false));
      defaults.add(Pair.from(PathToReplaySequence, "./output/temp")); // by urueda
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
      defaults.add(Pair.from(MyClassPath, Arrays.asList(".")));
      defaults.add(Pair.from(ProtocolClass, "org.fruit.monkey.DefaultProtocol"));
      defaults.add(Pair.from(ForceForeground, true));
      defaults.add(Pair.from(UseRecordedActionDurationAndWaitTimeDuringReplay, true));
      defaults.add(Pair.from(StopGenerationOnFault, true));
      defaults.add(Pair.from(TimeToFreeze, 10.0));
      defaults.add(Pair.from(ShowSettingsAfterTest, true));
      // begin by urueda
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
      // end by urueda
      defaults.add(Pair.from(GraphDBEnabled, false));
      defaults.add(Pair.from(GraphDBUrl, ""));
      defaults.add(Pair.from(GraphDBUser, ""));
      defaults.add(Pair.from(GraphDBPassword, ""));

      defaults.add(Pair.from(AlwaysCompile, true));

      return Settings.fromFile(defaults, file);
    } catch (IOException ioe) {
      throw new ConfigException("Unable to load configuration file!", ioe);
    }
  }

}
