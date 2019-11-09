/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.StateManagementTags;
import org.fruit.Assert;
import org.fruit.FruitException;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.exceptions.NoSuchTagException;

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
	public String toFileString() throws IOException{
		StringBuilder sb = new StringBuilder();

		try {
			//test.setting default structure
			sb.append("#################################################################\n"
					+"# TESTAR mode\n"
					+"#\n"
					+"# Set the mode you want TESTAR to start in: Spy, Generate, Replay\n"
					+"#################################################################\n"
					+"\n"
					+"Mode =" + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# Connect to the System Under Test (SUT)\n"
					+"#\n"
					+"# Indicate how you want to connect to the SUT:\n"
					+"#\n"
					+"# SUTCONNECTOR = COMMAND_LINE, SUTCONNECTORValue property must be a command line that\n"
					+"# starts the SUT.\n"
					+"# It should work from a Command Prompt terminal window (e.g. java - jar SUTs/calc.jar ).\n"
					+"# For web applications, follow the next format: web_browser_path SUT_URL.\n"
					+"#\n"
					+"# SUTCONNECTOR = SUT_WINDOW_TITLE, then SUTCONNECTORValue property must be the title displayed\n"
					+"# in the SUT main window. The SUT must be manually started and closed.\n"
					+"#\n"
					+"# SUTCONNECTOR = SUT_PROCESS_NAME: SUTCONNECTORValue property must be the process name of the SUT.\n"
					+"# The SUT must be manually started and closed.\n"
					+"#################################################################\n"
					+"SUTConnector = " + Util.lineSep()
					+"SUTConnectorValue = " + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# Java Swing applications & Access Bridge Enabled\n"
					+"#\n"
					+"# Activate the Java Access Bridge in your Windows System:\n"
					+"#		(Control Panel / Ease of Access / Ease of Access Center / Make the computer easier to see)\n"
					+"#\n"
					+"# Enable the variable Access Bridge Enabled in TESTAR as true\n"
					+"#################################################################\n"
					+"\n"
					+"AccessBridgeEnabled = " + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# Sequences\n"
					+"#\n"
					+"# Number of sequences and the length of these sequences\n"
					+"#################################################################\n"
					+"\n"
					+"Sequences = " + Util.lineSep()
					+"SequenceLength = " + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# Oracles based on suspicious titles\n"
					+"#\n"
					+"# Regular expression\n"
					+"#################################################################\n"
					+"\n"
					+"SuspiciousTitles = " + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# Oracles based on Suspicious Outputs detected by Process Listeners\n"
					+"#\n"
					+"# (Only available for desktop applications through COMMAND_LINE)\n"
					+"#\n"
					+"# Regular expression defines the suspicious outputs\n"
					+"#################################################################\n"
					+"\n"
					+"ProcessListenerEnabled = " + Util.lineSep()
					+"SuspiciousProcessOutput = " + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# Process Logs\n"
					+"#\n"
					+"# Required ProcessListenerEnabled\n"
					+"# (Only available for desktop applications through COMMAND_LINE)\n"
					+"#\n"
					+"# Allow TESTAR to store in its logs other possible matches found in the process\n"
					+"# Use the regular expression .*.* if you want to store all the possible outputs of the process\n"
					+"#################################################################\n"
					+"\n"
					+"ProcessLogs = " + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# Actionfilter\n"
					+"#\n"
					+"# Regular expression. More filters can be added in Spy mode,\n"
					+"# these will be added to the protocol_filter.xml file.\n"
					+"#################################################################\n"
					+"\n"
					+"ClickFilter = " + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# Processfilter\n"
					+"#\n"
					+"# Regular expression. Kill the processes that your SUT can start up\n"
					+"# but that you do not want to test.\n"
					+"#################################################################\n"
					+"\n"
					+"SUTProcesses =" + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# Protocolclass\n"
					+"#\n"
					+"# Indicate the location of the protocol class for your specific SUT.\n"
					+"#################################################################\n"
					+"\n"
					+"ProtocolClass = " + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# Graphdatabase settings (experimental)\n"
					+"#################################################################\n"
					+"GraphDBEnabled = false" + Util.lineSep()
					+"GraphDBUrl =" + Util.lineSep()
					+"GraphDBUser =" + Util.lineSep()
					+"GraphDBPassword =" + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# State model inference settings\n"
					+"#################################################################\n"
					+"StateModelEnabled = false" + Util.lineSep()
					+"DataStore = OrientDB" + Util.lineSep()
					+"DataStoreType = remote" + Util.lineSep()
					+"DataStoreServer = localhost" + Util.lineSep()
					+"DataStoreDirectory =" + Util.lineSep()
					+"DataStoreDB =" + Util.lineSep()
					+"DataStoreUser =" + Util.lineSep()
					+"DataStorePassword =" + Util.lineSep()
					+"DataStoreMode = instant" + Util.lineSep()
					+"ApplicationName = Buggy calculator" + Util.lineSep()
					+"ApplicationVersion = 1.0.0" + Util.lineSep()
					+"ActionSelectionAlgorithm =" + Util.lineSep()
					+"StateModelStoreWidgets =" + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# State identifier attributes\n"
					+"#\n"
					+"# Specify the widget attributes that you wish to use in constructing\n"
					+"# the widget and state hash strings. Use a comma separated list.\n"
                    +"#################################################################\n"
			        +"AbstractStateAttributes =" + Util.lineSep()
					+"\n"
					+"#################################################################\n"
					+"# Other more advanced settings\n"
					+"#################################################################\n");


			for(Tag<?> t : tags()){
				
				int ini = sb.indexOf(t.name()+" =");
				int end = sb.indexOf(System.lineSeparator(), ini);

				//Forced the hiding of prolog config option
				//TODO: Take out entirely after prolog has been eradicated from TESTAR
				if (!t.name().equals("PrologActivated")) {
					if (ini != -1) { // Overwrite default tags with the new value

						sb = sb.delete(ini, end);
						sb.insert(ini, t.name() + " = " + escapeBackslash(print((Tag<Object>) t, get(t))));

					} else { // This tag is new a variable

						sb.append(t.name()).append(" = ").append(escapeBackslash(print((Tag<Object>) t, get(t)))).append(Util.lineSep());
					}
				}
			}
			
		}catch(Exception e){System.out.println("Error trying to save current settings "+e);}

		return sb.toString();
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
