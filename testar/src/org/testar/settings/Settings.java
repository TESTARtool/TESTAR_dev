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

package org.testar.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.*;

import org.testar.llm.LlmUtils;
import org.testar.monkey.*;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TaggableBase;
import org.testar.monkey.alayer.exceptions.FruitException;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;
import org.testar.settings.extended.ExtendedSettingFile;
import org.testar.settings.extended.ExtendedSettingsFactory;

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

	/**
	 * Load the default settings for all the configurable settings and add/overwrite with those from the file
	 * This is needed because the user might not have set all the possible settings in the test.settings file.
	 * @param argv
	 * @param file
	 * @return An instance of Settings
	 * @throws IOException
	 */
	public static Settings loadSettings(String[] argv, String filePath) throws IOException {
		if (filePath == null || filePath.isEmpty()) {
			throw new IllegalArgumentException("Settings.loadSettings: filePath cannot be null or empty");
		}

		if (!new File(filePath).exists()) {
			throw new IOException("Settings.loadSettings: The specified file does not exist: " + filePath);
		}

		// Initialize the settings with the configured values
		// Prioritize the argv > file > default
		Settings settings = loadSettings(SettingsDefaults.getSettingsDefaults(), filePath, argv);

		try{
			settings.get(ConfigTags.ExtendedSettingsFile);
		} catch (NoSuchTagException e){
			settings.set(ConfigTags.ExtendedSettingsFile, filePath.replace(Main.SETTINGS_FILE, ExtendedSettingFile.FileName));
		}

		ExtendedSettingsFactory.Initialize(settings.get(ConfigTags.ExtendedSettingsFile));

		List<String> testGoalsList = settings.get(ConfigTags.LlmTestGoals, Collections.emptyList());
		settings.set(ConfigTags.LlmTestGoals, LlmUtils.readTestGoalsFromFile(testGoalsList));

		return settings;
	}

	private static Settings loadSettings(List<Pair<?, ?>> defaults, String filePath, String[] argv) throws IOException {
		// If user use command line to input properties, mix file settings with cmd properties
		if(argv.length > 0) {
			try {
				return fromFileCmd(defaults, filePath, argv);
			}catch(IOException e) {
				System.out.println("Error with command line properties. Examples:");
				System.out.println("testar SUTConnectorValue=\"C:\\\\Windows\\\\System32\\\\notepad.exe\" Sequences=11 SequenceLength=12 SuspiciousTags=.*aaa.*");
				System.out.println("SUTConnectorValue=\" \"\"C:\\\\Program Files\\\\Internet Explorer\\\\iexplore.exe\"\" \"\"https://www.google.es\"\" \"");
			}
			//SUTConnectorValue=" ""C:\\Program Files\\Internet Explorer\\iexplore.exe"" ""https://www.google.es"" "
			//SUTConnectorValue="C:\\Windows\\System32\\notepad.exe"
		}

		return fromFile(defaults, filePath);
	}

	private static Settings fromFile(List<Pair<?, ?>> defaults, String filePath) throws IOException {
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(filePath);
		InputStreamReader isw = new InputStreamReader(fis, "UTF-8");
		Reader in = new BufferedReader(isw);
		props.load(in);
		in.close();			
		if (isw != null) isw.close();
		if (fis != null) fis.close();

		return new Settings(defaults, new Properties(props));
	}

	private static Settings fromFileCmd(List<Pair<?, ?>> defaults, String filePath, String[] argv) throws IOException {
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(filePath);
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

		SettingsVerification.verifySettings(this);
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
		StringBuilder sb = new StringBuilder(SettingsFileStructure.getTestSettingsStructure());
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

	private String escapeBackslash(String string){ return string.replace("\\", "\\\\");	}

	private static String getStringSeparator(Tag<?> tag) {
		return tag.equals(ConfigTags.AbstractStateAttributes)
				? "," : ";";
	}

}
