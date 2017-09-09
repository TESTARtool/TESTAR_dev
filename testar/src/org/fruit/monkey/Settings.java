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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
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
