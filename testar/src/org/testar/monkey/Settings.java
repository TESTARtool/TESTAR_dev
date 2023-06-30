/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2023 Open Universiteit - www.ou.nl
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

import org.testar.StateManagementTags;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TaggableBase;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;

import static java.util.stream.Collectors.toList;

public class Settings extends TaggableBase implements Serializable {

	private static final long serialVersionUID = -1579293663489327737L;

	public static final String SUT_CONNECTOR_WINDOW_TITLE = "SUT_WINDOW_TITLE";
	public static final String SUT_CONNECTOR_PROCESS_NAME = "SUT_PROCESS_NAME";
	public static final String SUT_CONNECTOR_CMDLINE = "COMMAND_LINE";
	public static final String SUT_CONNECTOR_WEBDRIVER = "WEB_DRIVER";

	private static String settingsPath;

	public static String getSettingsPath() {
		return settingsPath;
	}

	public static void setSettingsPath(String path) {
		settingsPath = path;
	}

	public static class ConfigParseException extends FruitException{
		private static final long serialVersionUID = -245853379631399673L;
		public ConfigParseException(String message) {
			super(message);
		}
	}

	public static <T> String print(Tag<T> tag, T value){
		if(tag.type().equals(List.class) && !tag.equals(ConfigTags.CopyFromTo)){
			StringBuilder sb = new StringBuilder();
			String stringSeparator = getStringSeparator(tag);
			List<?> l = (List<?>) value;

			int i = 0;
			for(Object o : l){
				if(i > 0)
					sb.append(stringSeparator);
				sb.append(Util.toString(o));
				i++;
			}
			return sb.toString();
		}else if(tag.type().equals(List.class) && tag.equals(ConfigTags.CopyFromTo)){
			StringBuilder sb = new StringBuilder();
			@SuppressWarnings("unchecked")
			List<Pair<String, String>> l = (List<Pair<String, String>>) value;

			int i = 0;
			for(Pair<String, String> p : l){
				if(i > 0)
					sb.append(';');
				sb.append(p.left()).append(';').append(p.right());
				i++;
			}
			return sb.toString();
		}
		return Util.toString(value);
	}

	@SuppressWarnings("unchecked")
	public static <T> T parse(String stringValue, Tag<T> tag) throws ConfigParseException{
		if(tag.type().equals(Double.class)){
			try{
				return (T)(Double)Double.parseDouble(stringValue);
			}catch(NumberFormatException nfe){
				throw new ConfigParseException("Unable to parse value for tag " + tag);
			}
		}else if(tag.type().equals(RuntimeControlsProtocol.Modes.class)){
			try{
				return (T)RuntimeControlsProtocol.Modes.valueOf(stringValue);
			}catch(IllegalArgumentException iae){
				throw new ConfigParseException("Unknown Mode!");
			}
		}else if(tag.type().equals(Integer.class)){
			try{
				return (T)(Integer)Integer.parseInt(stringValue);
			}catch(NumberFormatException nfe){
				throw new ConfigParseException("Unable to parse value for tag " + tag);
			}
		}else if(tag.type().equals(Float.class)){
			try{
				return (T)(Float)Float.parseFloat(stringValue);
			}catch(NumberFormatException nfe){
				throw new ConfigParseException("Unable to parse value for tag " + tag);
			}
		}else if(tag.type().equals(Boolean.class)){
			try{
				return (T)(Boolean)Boolean.parseBoolean(stringValue);
			}catch(NumberFormatException nfe){
				throw new ConfigParseException("Unable to parse value for tag " + tag);
			}
		}else if(tag.type().equals(String.class)){
			return (T)stringValue;
		}else if(tag.type().equals(List.class) && !tag.equals(ConfigTags.CopyFromTo)){
			if(stringValue.trim().length() == 0)
				return (T) new ArrayList<String>();
			return (T)(Arrays.asList(stringValue.split(getStringSeparator(tag))).stream().map(String::trim).collect(toList()));
		}else if(tag.type().equals(List.class) && tag.equals(ConfigTags.CopyFromTo)){
			if(stringValue.trim().length() == 0)
				return (T) new ArrayList<Pair<String, String>>();
			List<String> pathList = Arrays.asList(stringValue.split(";"));
			if(pathList.size() % 2 != 0)
				throw new ConfigParseException("The number of paths must be even!");
			List<Pair<String, String>> ret = new ArrayList<>();
			for(int i = 0; i < pathList.size(); i += 2)
				ret.add(Pair.from(pathList.get(i), pathList.get(i + 1)));
			return (T)ret;
		}
		throw new ConfigParseException("");
	}


	public static Settings FromFile(String path) throws IOException{
		return fromFile(new ArrayList<Pair<?, ?>>(), path);
	}

	public static Settings fromFile(List<Pair<?, ?>> defaults, String path) throws IOException{
		Assert.notNull(path);
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(path);
		InputStreamReader isw = new InputStreamReader(fis, "UTF-8");
		Reader in = new BufferedReader(isw);
		props.load(in);
		in.close();			
		if (isw != null) isw.close();
		if (fis != null) fis.close();

		return new Settings(defaults, new Properties(props));
	}

	public static Settings fromFileCmd(List<Pair<?, ?>> defaults, String path, String[] argv) throws IOException{
		Assert.notNull(path);
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(path);
		InputStreamReader isw = new InputStreamReader(fis, "UTF-8");
		Reader in = new BufferedReader(isw);
		props.load(in);

		for(String sett : argv) {
			//Ignore sse value
			if(sett.toString().contains("sse=")) continue;

			System.out.println(sett.toString());
			StringReader sr = new StringReader(sett);
			props.load(sr);
		}

		in.close();			
		if (isw != null) isw.close();
		if (fis != null) fis.close();

		return new Settings(defaults, new Properties(props));
	}

	public Settings(){ this(new Properties()); }

	public Settings(Properties props){ this(new ArrayList<Pair<?, ?>>(), props); }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Settings(List<Pair<?, ?>> defaults, Properties props){
		Assert.notNull(props, defaults);

		for(Pair<?, ?> pair : defaults){
			Assert.notNull(pair.left(), pair.right());
			Assert.isTrue(pair.left() instanceof Tag);
			Tag<Object> tag = (Tag<Object>)pair.left();
			Object value = pair.right();
			Assert.isTrue(tag.type().isAssignableFrom(value.getClass()), "Wrong value type for tag " + tag.name());
			set(tag, value);
		}

		for(String key : props.stringPropertyNames()){
			String value = props.getProperty(key);

			Tag<?> defaultTag = null;

			for(Pair<?, ?> pair : defaults){
				Tag<?> tag = (Tag<?>)pair.left();
				if(tag.name().equals(key)){
					defaultTag = tag;
					break;
				}
			}

			if(defaultTag == null){
				set(Tag.from(key, String.class), value);
			}else{
				set((Tag)defaultTag, parse(value, defaultTag));
			}
		}

		verifySettings();
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();

		for(Tag<?> t : tags()){
			sb.append(t.name()).append("<")
			.append(t.type().getSimpleName()).append("> : ")
			.append(get(t)).append(Util.lineSep());
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	/**
	 * Make the default file Structure for the test.settings file
	 */
	public String toFileString() throws IOException {
		// Create the default settings structure
		StringBuilder sb = new StringBuilder(testSettingsStructure());
		// Then add the settings values
		try {
			for(Tag<?> t : tags()){
				int ini = sb.indexOf(t.name()+" =");
				int end = sb.indexOf(System.lineSeparator(), ini);

				if (ini != -1) { // Overwrite default tags with the new value
					sb = sb.delete(ini, end);
					sb.insert(ini, t.name() + " = " + escapeBackslash(print((Tag<Object>) t, get(t))));
				} else { // This tag is new a variable
					sb.append(t.name()).append(" = ").append(escapeBackslash(print((Tag<Object>) t, get(t)))).append(Util.lineSep());
				}
			}

		} catch (NoSuchTagException e) {
			System.out.println("Error trying to save current settings " + e);
		}

		return sb.toString();
	}

	private String testSettingsStructure() {
		// First, create the main structure that contains specific format and descriptions
		String mainString = String.join(System.getProperty("line.separator")
				, "#################################################################"
				, "# TESTAR mode"
				, "#"
				, "# Set the mode you want TESTAR to start in: Spy, Generate, Replay"
				, "#################################################################"
				, ""
				, ConfigTags.Mode.name() + " = "
				, ""
				, "#################################################################"
				, "# Connect to the System Under Test (SUT)"
				, "#"
				, "# Indicate how you want to connect to the SUT:"
				, "#"
				, "# SUTCONNECTOR = COMMAND_LINE, SUTConnectorValue property must be a command line that"
				, "# starts the SUT."
				, "# It should work from a Command Prompt terminal window (e.g. java - jar SUTs/calc.jar )."
				, "# For web applications, follow the next format: web_browser_path SUT_URL."
				, "#"
				, "# SUTCONNECTOR = SUT_WINDOW_TITLE, then SUTConnectorValue property must be the title displayed"
				, "# in the SUT main window. The SUT must be manually started and closed."
				, "#"
				, "# SUTCONNECTOR = SUT_PROCESS_NAME: SUTConnectorValue property must be the process name of the SUT."
				, "# The SUT must be manually started and closed."
				, "#################################################################"
				, ""
				, ConfigTags.SUTConnector.name() + " = "
				, ConfigTags.SUTConnectorValue.name() + " = "
				, ""
				, "#################################################################"
				, "# Java Swing applications & Access Bridge Enabled"
				, "#"
				, "# Activate the Java Access Bridge in your Windows System:"
				, "#		(Control Panel / Ease of Access / Ease of Access Center / Make the computer easier to see)"
				, "#"
				, "# Enable the variable Access Bridge Enabled in TESTAR as true"
				, "#################################################################"
				, ""
				, ConfigTags.AccessBridgeEnabled.name() + " = "
				, ""
				, "#################################################################"
				, "# Sequences"
				, "#"
				, "# Number of sequences and the length of these sequences"
				, "#################################################################"
				, ""
				, ConfigTags.Sequences.name() + " = "
				, ConfigTags.SequenceLength.name() + " = "
				, ""
				, "#################################################################"
				, "# SUT Protocol"
				, "#"
				, "# ProtocolClass: " + ConfigTags.ProtocolClass.getDescription()
				, "# AlwaysCompile: " + ConfigTags.AlwaysCompile.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.ProtocolClass.name() + " = "
				, ConfigTags.AlwaysCompile.name() + " = "
				, ""
				, "#################################################################"
				, "# Actionfilter"
				, "#"
				, "# Regular expression and Tags to apply them."
				, "# More filters can be added in Spy mode, "
				, "# these will be added to the protocol_filter.xml file."
				, "#################################################################"
				, ""
				, ConfigTags.ClickFilter.name() + " = "
				, ConfigTags.TagsToFilter.name() + " = "
				, ""
				, "#################################################################"
				, "# Processfilter"
				, "#"
				, "# Regular expression. Kill the processes that your SUT can start up"
				, "# but that you do not want to test."
				, "#################################################################"
				, ""
				, ConfigTags.ProcessesToKillDuringTest.name() + " = "
				, ""
				, "#################################################################"
				, "# Oracles based on suspicious tag values"
				, "#"
				, "# Regular expression and Tags to apply them"
				, "#################################################################"
				, ""
				, ConfigTags.SuspiciousTags.name() + " = "
				, ConfigTags.TagsForSuspiciousOracle.name() + " = "
				, ""
				, "#################################################################"
				, "# Oracles based on Suspicious Outputs detected by Process Listeners"
				, "#"
				, "# Requires ProcessListenerEnabled"
				, "# (Only available for desktop applications through COMMAND_LINE)"
				, "#"
				, "# Regular expression SuspiciousProcessOutput contains the specification"
				, "# of what is considered to be suspicious output."
				, "#################################################################"
				, ""
				, ConfigTags.ProcessListenerEnabled.name() + " = "
				, ConfigTags.SuspiciousProcessOutput.name() + " = "
				, ""
				, "#################################################################"
				, "# Process Logs"
				, "#"
				, "# Required ProcessListenerEnabled"
				, "# (Only available for desktop applications through COMMAND_LINE)"
				, "#"
				, "# Allow TESTAR to store execution logs coming from the processes."
				, "# You can use the regular expression ProcessLogs below to filter"
				, "# the logs. Use .*.* if you want to store all the outputs of the "
				, "# process."
				, "#################################################################"
				, ""
				, ConfigTags.ProcessLogs.name() + " = "
				, ""
				, "#################################################################"
				, "# Time configurations"
				, "#"
				, "# ActionDuration: " + ConfigTags.ActionDuration.getDescription()
				, "# TimeToWaitAfterAction: " + ConfigTags.TimeToWaitAfterAction.getDescription()
				, "# StartupTime: " + ConfigTags.StartupTime.getDescription()
				, "# MaxTime: " + ConfigTags.MaxTime.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.ActionDuration.name() + " = "
				, ConfigTags.TimeToWaitAfterAction.name() + " = "
				, ConfigTags.StartupTime.name() + " = "
				, ConfigTags.MaxTime.name() + " = "
				, ""
				, "#################################################################"
				, "# State model inference settings"
				, "#"
				, "# StateModelEnabled: " + ConfigTags.StateModelEnabled.getDescription()
				, "# DataStore: " + ConfigTags.DataStore.getDescription()
				, "# DataStoreType: " + ConfigTags.DataStoreType.getDescription()
				, "# DataStoreServer: " + ConfigTags.DataStoreServer.getDescription()
				, "# DataStoreDirectory: " + ConfigTags.DataStoreDirectory.getDescription()
				, "# DataStoreDB: " + ConfigTags.DataStoreDB.getDescription()
				, "# DataStoreUser: " + ConfigTags.DataStoreUser.getDescription()
				, "# DataStorePassword: " + ConfigTags.DataStorePassword.getDescription()
				, "# DataStoreMode: " + ConfigTags.DataStoreMode.getDescription()
				, "# ApplicationName: " + ConfigTags.ApplicationName.getDescription()
				, "# ApplicationVersion: " + ConfigTags.ApplicationVersion.getDescription()
				, "# ActionSelectionAlgorithm: " + ConfigTags.ActionSelectionAlgorithm.getDescription()
				, "# StateModelStoreWidgets: " + ConfigTags.StateModelStoreWidgets.getDescription()
				, "# ResetDataStore: " + ConfigTags.ResetDataStore.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.StateModelEnabled.name() + " = "
				, ConfigTags.DataStore.name() + " = "
				, ConfigTags.DataStoreType.name() + " = "
				, ConfigTags.DataStoreServer.name() + " = "
				, ConfigTags.DataStoreDirectory.name() + " = "
				, ConfigTags.DataStoreDB.name() + " = "
				, ConfigTags.DataStoreUser.name() + " = "
				, ConfigTags.DataStorePassword.name() + " = "
				, ConfigTags.DataStoreMode.name() + " = "
				, ConfigTags.ApplicationName.name() + " = "
				, ConfigTags.ApplicationVersion.name() + " = "
				, ConfigTags.ActionSelectionAlgorithm.name() + " = "
				, ConfigTags.StateModelStoreWidgets.name() + " = "
				, ConfigTags.ResetDataStore.name() + " = "
				, ""
				, "#################################################################"
				, "# State identifier attributes"
				, "#"
				, "# Specify the widget attributes that you wish to use in constructing"
				, "# the widget and state hash strings. Use a comma separated list."
				, "#################################################################"
				, ""
				, ConfigTags.AbstractStateAttributes.name() + " = "
				, ""
				, "#################################################################"
				, "# WebDriver features"
				, "#"
				, "# ClickableClasses: " + ConfigTags.ClickableClasses.getDescription()
				, "# TypeableClasses: " + ConfigTags.TypeableClasses.getDescription()
				, "# DeniedExtensions: " + ConfigTags.DeniedExtensions.getDescription()
				, "# DomainsAllowed: " + ConfigTags.DomainsAllowed.getDescription()
				, "# FollowLinks: " + ConfigTags.FollowLinks.getDescription()
				, "# BrowserFullScreen: " + ConfigTags.BrowserFullScreen.getDescription()
				, "# SwitchNewTabs: " + ConfigTags.SwitchNewTabs.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.ClickableClasses.name() + " = "
				, ConfigTags.TypeableClasses.name() + " = "
				, ConfigTags.DeniedExtensions.name() + " = "
				, ConfigTags.DomainsAllowed.name() + " = "
				, ConfigTags.FollowLinks.name() + " = "
				, ConfigTags.BrowserFullScreen.name() + " = "
				, ConfigTags.SwitchNewTabs.name() + " = "
				, ""
				, "#################################################################"
				, "# WebDriver Browser Console Oracles"
				, "#"
				, "# WebConsoleErrorOracle: " + ConfigTags.WebConsoleErrorOracle.getDescription()
				, "# WebConsoleErrorPattern: " + ConfigTags.WebConsoleErrorPattern.getDescription()
				, "# WebConsoleWarningOracle: " + ConfigTags.WebConsoleWarningOracle.getDescription()
				, "# WebConsoleWarningPattern: " + ConfigTags.WebConsoleWarningPattern.getDescription()
				, "#################################################################"
				, ""
				, ConfigTags.WebConsoleErrorOracle.name() + " = "
				, ConfigTags.WebConsoleErrorPattern.name() + " = "
				, ConfigTags.WebConsoleWarningOracle.name() + " = "
				, ConfigTags.WebConsoleWarningPattern.name() + " = "
				, ""
				, "#################################################################"
				, "# Override display scale"
				, "#"
				, "# Overrides the displayscale obtained from the system."
				, "# Can solve problems when the mouse clicks are not aligned with"
				, "# the elements on the screen. This can easily be detected when"
				, "# running the spy mode. For example hover over a text element and"
				, "# the popup window should appear with information about the"
				, "# element, if the popup window is not shown or when the mouse is"
				, "# located somewhere else you can try to override the displayscale"
				, "# Values should be provided as doubles (1.5)."
				, "#################################################################"
				, ""
				, ConfigTags.OverrideWebDriverDisplayScale.name() + " = "
				, ""
				, "#################################################################"
				, "# Settings (string) that can be used for user specified protocols"
				, "#################################################################"
				, ""
				, ConfigTags.ProtocolSpecificSetting_1.name() + " = "
				, ConfigTags.ProtocolSpecificSetting_2.name() + " = "
				, ConfigTags.ProtocolSpecificSetting_3.name() + " = "
				, ConfigTags.ProtocolSpecificSetting_4.name() + " = "
				, ConfigTags.ProtocolSpecificSetting_5.name() + " = "
				, ""
				, "#################################################################"
				, "# Yolo settings to configure the location of the project and model"
				, "#################################################################"
				, ""
				, ConfigTags.YoloProjectAbsolutePath.name() + " = "
				, ConfigTags.YoloPythonServiceRelativePath.name() + " = "
				, ConfigTags.YoloModelAbsolutePath.name() + " = "
				, ConfigTags.YoloInputImagesDirectory.name() + " = "
				, ConfigTags.YoloModelOutputDirectory.name() + " = "
				, ""
				, "#################################################################"
				, "# Extended settings file"
				, "#"
				, "# Relative path to extended settings file."
				, "#################################################################"
				, ""
				, ConfigTags.ExtendedSettingsFile.name() + " = "
				, ""
				);

		// Second, create a list of secondary configuration tags settings
		// To add their descriptions to the file
		List<Tag<?>> secondarySettingsList = new ArrayList<>();
		secondarySettingsList.add(ConfigTags.ShowVisualSettingsDialogOnStartup);
		secondarySettingsList.add(ConfigTags.ForceForeground);
		secondarySettingsList.add(ConfigTags.VisualizeActions);
		secondarySettingsList.add(ConfigTags.FormFillingAction);
		secondarySettingsList.add(ConfigTags.SUTProcesses);
		secondarySettingsList.add(ConfigTags.LogLevel);
		secondarySettingsList.add(ConfigTags.OnlySaveFaultySequences);
		secondarySettingsList.add(ConfigTags.ReplayRetryTime);
		secondarySettingsList.add(ConfigTags.StopGenerationOnFault);
		secondarySettingsList.add(ConfigTags.TimeToFreeze);
		secondarySettingsList.add(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay);
		secondarySettingsList.add(ConfigTags.UseSystemActions);
		secondarySettingsList.add(ConfigTags.PathToReplaySequence);
		secondarySettingsList.add(ConfigTags.RefreshSpyCanvas);
		secondarySettingsList.add(ConfigTags.FlashFeedback);
		secondarySettingsList.add(ConfigTags.MaxReward);
		secondarySettingsList.add(ConfigTags.Discount);
		secondarySettingsList.add(ConfigTags.CreateWidgetInfoJsonFile);
		secondarySettingsList.add(ConfigTags.MyClassPath);
		secondarySettingsList.add(ConfigTags.ProtocolCompileDirectory);
		secondarySettingsList.add(ConfigTags.OutputDir);
		secondarySettingsList.add(ConfigTags.TempDir);
		secondarySettingsList.add(ConfigTags.ReportingClass);

		StringJoiner secondaryString = new StringJoiner(System.getProperty("line.separator"));
		for(Tag<?> set : secondarySettingsList) {
			// Make sure it contains a description
			if(!set.getDescription().isEmpty()) {
				secondaryString.add("#################################################################");
				secondaryString.add("# " + set.getDescription());
				secondaryString.add("#################################################################");
				secondaryString.add("");
				secondaryString.add(set.name() + " = ");
				secondaryString.add("");
			}
		}

		// Finally, create the structure for other settings
		StringJoiner otherString = new StringJoiner(System.getProperty("line.separator"));
		otherString.add("#################################################################");
		otherString.add("# Other settings");
		otherString.add("#################################################################");
		otherString.add("");

		return mainString
				.concat(System.getProperty("line.separator") + secondaryString.toString())
				.concat(System.getProperty("line.separator") + otherString.toString());
	}

	private String escapeBackslash(String string){ return string.replace("\\", "\\\\");	}

	/**
	 * This method will check if the provided settings for the concrete and abstract state models are valid.
	 */
	private void verifySettings() {
		// verify the concrete and abstract state settings
		// the values provided should be valid state management tags
		Set<String> allowedStateAttributes = StateManagementTags.getAllTags().stream().map(StateManagementTags::getSettingsStringFromTag).collect(Collectors.toSet());

		// add only the state management tags that are available
		Set<String> stateSet = new HashSet<>();
		try {
			List<String> abstractStateAttributes = get(ConfigTags.AbstractStateAttributes);
			for (String abstractStateAttribute : abstractStateAttributes) {
				if (allowedStateAttributes.contains(abstractStateAttribute)) {
					stateSet.add(abstractStateAttribute);
				}
			}
			set(ConfigTags.AbstractStateAttributes, new ArrayList<>(stateSet));
		}
		catch (NoSuchTagException ex) {
			// no need to do anything, nothing to verify
		}
	}

	private static String getStringSeparator(Tag<?> tag) {
		return tag.equals(ConfigTags.AbstractStateAttributes)
				? "," : ";";
	}
}
