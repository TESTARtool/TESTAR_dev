/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.monkey;

import static org.fruit.monkey.ConfigTags.ActionDuration;
import static org.fruit.monkey.ConfigTags.AlgorithmFormsFilling;
import static org.fruit.monkey.ConfigTags.ClickFilter;
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
import static org.fruit.monkey.ConfigTags.ProcessesToKillDuringTest;
import static org.fruit.monkey.ConfigTags.PrologActivated;
import static org.fruit.monkey.ConfigTags.ProtocolClass;
import static org.fruit.monkey.ConfigTags.ReplayRetryTime;
import static org.fruit.monkey.ConfigTags.SUTConnector;
import static org.fruit.monkey.ConfigTags.SUTConnectorValue;
import static org.fruit.monkey.ConfigTags.SequenceLength;
import static org.fruit.monkey.ConfigTags.Sequences;
import static org.fruit.monkey.ConfigTags.ShowSettingsAfterTest;
import static org.fruit.monkey.ConfigTags.ShowVisualSettingsDialogOnStartup;
import static org.fruit.monkey.ConfigTags.StartupTime;
import static org.fruit.monkey.ConfigTags.StateScreenshotSimilarityThreshold;
import static org.fruit.monkey.ConfigTags.UnattendedTests;
import static org.fruit.monkey.ConfigTags.StopGenerationOnFault;
import static org.fruit.monkey.ConfigTags.SuspiciousTitles;
import static org.fruit.monkey.ConfigTags.TempDir;
import static org.fruit.monkey.ConfigTags.TestGenerator;
import static org.fruit.monkey.ConfigTags.TimeToFreeze;
import static org.fruit.monkey.ConfigTags.TimeToWaitAfterAction;
import static org.fruit.monkey.ConfigTags.TypingTextsForExecutedAction;
import static org.fruit.monkey.ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay;
import static org.fruit.monkey.ConfigTags.VisualizeActions;
import static org.fruit.monkey.ConfigTags.VisualizeSelectedAction;
import static org.fruit.monkey.ConfigTags.Strategy;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.UnProc;
import org.fruit.Util;

import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;

public class Main {

	// by urueda
	private static void overrideWithUserProperties(Settings settings){ // overrides test.settings
		String pS, p;
		// headless mode
		pS = ConfigTags.ShowVisualSettingsDialogOnStartup.name();
		p = System.getProperty(pS,null);
		if (p == null)
			p= System.getProperty("headless",null); // mnemonic
		if (p != null){
			settings.set(ConfigTags.ShowVisualSettingsDialogOnStartup, !(new Boolean(p).booleanValue()));
			LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">",LogSerialiser.LogLevel.Critical);				
		}		
		// TestGenerator
		pS = ConfigTags.TestGenerator.name();
		p = System.getProperty(pS,null);
		if (p == null)
			p = System.getProperty("TG",null); // mnemonic
		if (p != null){
			settings.set(ConfigTags.TestGenerator,p);
			LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">",LogSerialiser.LogLevel.Critical);				
		}
		// SequenceLength
		pS = ConfigTags.SequenceLength.name();
		p = System.getProperty(pS,null);
		if (p == null)
			p = System.getProperty("SL",null); // mnemonic
		if (p != null){
			try{
				Integer sl = new Integer(p);
				settings.set(ConfigTags.SequenceLength, sl);
				LogSerialiser.log("Property <" + pS + "> overridden to <" + sl.toString() + ">",LogSerialiser.LogLevel.Critical);				
			} catch (NumberFormatException e) {
				LogSerialiser.log("Property <" + pS + "> could not be set! (using default)",LogSerialiser.LogLevel.Critical);
			}
		}
		// GraphResumingActivated
		pS = ConfigTags.GraphResuming.name();
		p = System.getProperty(pS,null);
		if (p == null)
			p= System.getProperty("GRA",null); // mnemonic
		if (p != null){
			settings.set(ConfigTags.GraphResuming, new Boolean(p).booleanValue());
			LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">",LogSerialiser.LogLevel.Critical);				
		}				
		// ForceToSequenceLength
		pS = ConfigTags.ForceToSequenceLength.name();
		p = System.getProperty(pS,null);
		if (p == null)
			p= System.getProperty("F2SL",null); // mnemonic
		if (p != null){
			settings.set(ConfigTags.ForceToSequenceLength, new Boolean(p).booleanValue());
			LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">",LogSerialiser.LogLevel.Critical);				
		}
		// TypingTextsForExecutedAction
		pS = ConfigTags.TypingTextsForExecutedAction.name();
		p = System.getProperty(pS,null);
		if (p == null)
			p = System.getProperty("TT",null); // mnemonic
		if (p != null){
			try{
				Integer tt = new Integer(p);
				settings.set(ConfigTags.TypingTextsForExecutedAction, tt);
				LogSerialiser.log("Property <" + pS + "> overridden to <" + tt.toString() + ">",LogSerialiser.LogLevel.Critical);				
			} catch (NumberFormatException e) {
				LogSerialiser.log("Property <" + pS + "> could not be set! (using default)",LogSerialiser.LogLevel.Critical);
			}
		}
		// StateScreenshotSimilarityThreshold
		pS = ConfigTags.StateScreenshotSimilarityThreshold.name();
		p = System.getProperty(pS,null);
		if (p == null)
			p = System.getProperty("SST",null); // mnemonic
		if (p != null){
			try{
				Float sst = new Float(p);
				settings.set(ConfigTags.StateScreenshotSimilarityThreshold, sst);
				LogSerialiser.log("Property <" + pS + "> overridden to <" + sst.toString() + ">",LogSerialiser.LogLevel.Critical);				
			} catch (NumberFormatException e) {
				LogSerialiser.log("Property <" + pS + "> could not be set! (using default)",LogSerialiser.LogLevel.Critical);
			}
		}		
		// UnattendedTests
		pS = ConfigTags.UnattendedTests.name();
		p = System.getProperty(pS,null);
		if (p == null)
			p= System.getProperty("UT",null); // mnemonic
		if (p != null){
			settings.set(ConfigTags.UnattendedTests, new Boolean(p).booleanValue());
			LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">",LogSerialiser.LogLevel.Critical);				
		}	
		// Strategy
		pS = ConfigTags.Strategy.name();
		p = System.getProperty(pS,null);
		if (p == null)
			p= System.getProperty("Strategy",null); // mnemonic
		if (p != null){
			settings.set(ConfigTags.Strategy, p);
			LogSerialiser.log("Property <" + pS + "> overridden to <" + p + ">",LogSerialiser.LogLevel.Critical);				
		}	
	}
	
	// begin by urueda
	public static final String SETTINGS_FILE = "test.settings";
	public static final String SUT_SETTINGS_EXT = ".sse";
	public static String SSE_ACTIVATED = null;
	// end by urueda
	
	// by urueda
	private static void settingsSelection(){
		Set<String> sutSettings = new HashSet<String>();
		for (File f : new File("./settings").listFiles()){
			if (new File(f.getPath() + "/" + SETTINGS_FILE).exists())
				sutSettings.add(f.getName());
		}
		if (sutSettings.isEmpty())
			System.out.println("No SUT settings found!");
		else{
	        Object[] options = sutSettings.toArray();
	        JFrame settingsSelectorDialog = new JFrame();
	        settingsSelectorDialog.setAlwaysOnTop(true);
	    	String s = (String) JOptionPane.showInputDialog(settingsSelectorDialog, "SUT setting:", "Test setting selection",
	    													JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
	    	if (s == null){
	    		SSE_ACTIVATED = null;
	    		return;
	    	}
	    	final String sse = s + SUT_SETTINGS_EXT;
	    	try {
	    		File f = new File("./settings/" + sse); 
				if (f.createNewFile()){
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
	public static String[] getSSE(){
		return new File("./settings/").list(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(SUT_SETTINGS_EXT);
			}
		});		
	}
	
	public static void main(String[] args) throws IOException{
		Settings settings = null;
		//by fraalpe2
		Locale.setDefault(Locale.ENGLISH);
		
		// begin by urueda
		String[] files = getSSE();
		if (files != null && files.length > 1){
			System.out.println("Too many *.sse files - exactly one expected!");
			for (String f : files){
				System.out.println("Delete file <" + f + "> = " + new File(f).delete());
			}
			files = null;
		}
		if (files == null || files.length == 0){
			settingsSelection();
			if (SSE_ACTIVATED == null){
				System.exit(-1);
			}
		} else
			SSE_ACTIVATED = files[0].split(SUT_SETTINGS_EXT)[0];
		String testSettings = "./settings/" + SSE_ACTIVATED + "/" + SETTINGS_FILE;
		System.out.println("Test settings is <" + testSettings + ">");
		URLClassLoader loader = null;
		// end by urueda
			
		try{
			settings = loadSettings(args, testSettings);
			overrideWithUserProperties(settings); // by urueda
			Float SST = settings.get(ConfigTags.StateScreenshotSimilarityThreshold, null);
			if (SST != null)
				System.setProperty("SCRSHOT_SIMILARITY_THRESHOLD", SST.toString());

			if(settings.get(ConfigTags.ShowVisualSettingsDialogOnStartup)){
				if((settings = new SettingsDialog().run(settings, testSettings)) == null)
					return;
			}

			try{
				String logFileName = Util.dateString("yyyy_MM_dd__HH_mm_ss") + ".log";
				File logFile = new File(settings.get(OutputDir) + File.separator + logFileName);
				if(logFile.exists())
					logFile = Util.generateUniqueFile(settings.get(OutputDir), logFileName);
				LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile))), settings.get(LogLevel)); // by urueda
			}catch(Throwable t){
				System.out.println("Cannot initialize log file!");
				t.printStackTrace(System.out);
				System.exit(-1);
			}

			//logln(Util.dateString("dd.MMMMM.yyyy HH:mm:ss"));
			//logln("Hello, I'm the FRUIT Monkey!" + Util.lineSep() + Util.lineSep() + "These are my settings:", Main.LogLevel.Critical);
			// start by urueda
			LogSerialiser.log(Util.dateString("dd.MMMMM.yyyy HH:mm:ss") + " TESTAR " + SettingsDialog.TESTAR_VERSION + " is running" + /*Util.lineSep() + Util.lineSep() +*/ " with the next settings:\n",LogSerialiser.LogLevel.Critical);
			LogSerialiser.log("\n-- settings start ... --\n\n",LogSerialiser.LogLevel.Critical);
			// end by urueda
			LogSerialiser.log(settings.toString() + "\n", LogSerialiser.LogLevel.Critical);
			LogSerialiser.log("-- ... settings end --\n\n",LogSerialiser.LogLevel.Critical); // by urueda
			List<String> cp = settings.get(MyClassPath);
			URL[] classPath = new URL[cp.size()];
			for(int i = 0; i < cp.size(); i++)
				classPath[i] = new File(cp.get(i)).toURI().toURL();
			loader = new URLClassLoader(classPath);

			//logln("Trying to load monkey protocol in class '" + settings.get(ProtocolClass) + "' with class path '" + Util.toString(cp) + "'", Main.LogLevel.Debug);
			String protocolClass = settings.get(ProtocolClass).split("/")[1]; // by urueda
			LogSerialiser.log("Trying to load TESTAR protocol in class '" +
							  protocolClass +
							  "' with class path '" + Util.toString(cp) + "'\n", LogSerialiser.LogLevel.Debug); // by urueda
			@SuppressWarnings("unchecked")
			UnProc<Settings> protocol = (UnProc<Settings>)loader.loadClass(protocolClass).getConstructor().newInstance();
			//logln("Monkey protocol loaded!", Main.LogLevel.Debug);
			LogSerialiser.log("TESTAR protocol loaded!\n", LogSerialiser.LogLevel.Debug); // by urueda

			//logln("Starting monkey protocol ...", Main.LogLevel.Debug);
			LogSerialiser.log("Starting TESTAR protocol ...\n", LogSerialiser.LogLevel.Debug); // by urueda
			protocol.run(settings);
		} catch (ConfigException ce){
			LogSerialiser.log("There is an issue with the configuration file: " + ce.getMessage() + "\n", LogSerialiser.LogLevel.Critical);
		} catch (Throwable t) {
			LogSerialiser.log("An unexpected error occurred: " + t + "\n", LogSerialiser.LogLevel.Critical);
			t.printStackTrace(System.out);
			t.printStackTrace(LogSerialiser.getLogStream());
		} finally{
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
			if (loader != null)
				try { loader.close(); } catch (IOException e) { e.printStackTrace(); } 
			// end by urueda
			System.exit(0);
		}
	}
	
	public static Settings loadSettings(String[] argv, String file) throws ConfigException{
		Assert.notNull(file); // by urueda
		try{
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
			defaults.add(Pair.from(StopGenerationOnFault , true));
			defaults.add(Pair.from(TimeToFreeze, 10.0));
			defaults.add(Pair.from(ShowSettingsAfterTest, true));
			// begin by urueda
			defaults.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_CMDLINE));
			defaults.add(Pair.from(TestGenerator, "random"));
			defaults.add(Pair.from(MaxReward, 9999999.0));
			defaults.add(Pair.from(Discount, .95));
			defaults.add(Pair.from(AlgorithmFormsFilling, false));
			defaults.add(Pair.from(TypingTextsForExecutedAction, 10));
			defaults.add(Pair.from(DrawWidgetTree,false));
			defaults.add(Pair.from(ExplorationSampleInterval, 1));
			defaults.add(Pair.from(GraphsActivated, true));
			defaults.add(Pair.from(PrologActivated, false));
			defaults.add(Pair.from(GraphResuming, true));
			defaults.add(Pair.from(ForceToSequenceLength, true));
			defaults.add(Pair.from(NonReactingUIThreshold, 100)); // number of executed actions
			defaults.add(Pair.from(OfflineGraphConversion, true));
			defaults.add(Pair.from(StateScreenshotSimilarityThreshold, Float.MIN_VALUE)); // disabled
			defaults.add(Pair.from(UnattendedTests, false)); // disabled
			defaults.add(Pair.from(Strategy, "strategy.txt")); 
			
			// end by urueda
			
			return Settings.fromFile(defaults, file);
		}catch(IOException ioe){
			throw new ConfigException("Unable to load configuration file!", ioe);
		}
	}
	
}