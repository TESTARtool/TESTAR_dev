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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.fruit.Assert;
import org.fruit.FruitException;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

public class Settings extends TaggableBase implements Serializable {

	private static final long serialVersionUID = -1579293663489327737L;

	// begin by urueda
	public static final String SUT_CONNECTOR_WINDOW_TITLE = "SUT_WINDOW_TITLE",
			 				   SUT_CONNECTOR_PROCESS_NAME = "SUT_PROCESS_NAME",
			 				   SUT_CONNECTOR_CMDLINE 	  = "COMMAND_LINE";
	// end by urueda
	
	public static class ConfigParseException extends FruitException{
		private static final long serialVersionUID = -245853379631399673L;
		public ConfigParseException(String message) {
			super(message);
		}
	}

	public static <T> String print(Tag<T> tag, T value){
		if(tag.type().equals(List.class) && !tag.equals(ConfigTags.CopyFromTo)){
			StringBuilder sb = new StringBuilder();
			List<?> l = (List<?>) value;
			
			int i = 0;
			for(Object o : l){
				if(i > 0)
					sb.append(';');
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
		}else if(tag.type().equals(AbstractProtocol.Modes.class)){
			try{
				return (T)AbstractProtocol.Modes.valueOf(stringValue);
			}catch(IllegalArgumentException iae){
				throw new ConfigParseException("Unknown Mode!");
			}
		}else if(tag.type().equals(Integer.class)){
			try{
				return (T)(Integer)Integer.parseInt(stringValue);
			}catch(NumberFormatException nfe){
				throw new ConfigParseException("Unable to parse value for tag " + tag);
			}
		// begin by urueda
		}else if(tag.type().equals(Float.class)){
			try{
				return (T)(Float)Float.parseFloat(stringValue);
			}catch(NumberFormatException nfe){
				throw new ConfigParseException("Unable to parse value for tag " + tag);
			} // end by urueda
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
			return (T)Arrays.asList(stringValue.split(";"));
		}else if(tag.type().equals(List.class) && tag.equals(ConfigTags.CopyFromTo)){
			if(stringValue.trim().length() == 0)
				return (T) new ArrayList<Pair<String, String>>();
			List<String> pathList = Arrays.asList(stringValue.split(";"));
			if(pathList.size() % 2 != 0)
				throw new ConfigParseException("The number of paths must be even!");
			List<Pair<String, String>> ret = new ArrayList<Pair<String, String>>();
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
		// begin by urueda
		FileInputStream fis = new FileInputStream(path);
		InputStreamReader isw = new InputStreamReader(fis, "UTF-8");
		Reader in = new BufferedReader(isw);
		props.load(in);
		in.close();			
		if (isw != null) isw.close();
		if (fis != null) fis.close();
		// end by urueda
		return new Settings(defaults, new Properties(props));
	}

	public Settings(){ this(new Properties()); }

	public Settings(Properties props){ this(new ArrayList<Pair<?, ?>>(), props); }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Settings(List<Pair<?, ?>> defaults, Properties props){
		Assert.notNull(props, defaults);

		for(Pair<?, ?> p : defaults){
			Assert.notNull(p.left(), p.right());
			Assert.isTrue(p.left() instanceof Tag);			
			Tag<Object> t = (Tag<Object>)p.left();
			Object v = p.right();
			Assert.isTrue(t.type().isAssignableFrom(v.getClass()), "Wrong value type for tag " + t.name());
			set(t, v);			
		}

		for(String key : props.stringPropertyNames()){
			String value = props.getProperty(key);
			
			Tag<?> defTag = null;
			
			for(Pair<?, ?> p : defaults){
				Tag<?> t = (Tag<?>)p.left();
				if(t.name().equals(key)){
					defTag = t;
					break;
				}
			}

			if(defTag == null){
				set(Tag.from(key, String.class), value);
			}else{
				set((Tag)defTag, parse(value, defTag));
			}

		}
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
	public String toFileString(){
		StringBuilder sb = new StringBuilder();

		for(Tag<?> t : tags()){			
			sb.append(t.name()).append(" = ").append(escapeBackslash(print((Tag<Object>)t, get(t)))).append(Util.lineSep());
		}
		return sb.toString();
	}
	
	private String escapeBackslash(String string){ return string.replace("\\", "\\\\");	}
}
