/***************************************************************************************************
*
* Copyright (c) 2016 - 2022 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2022 Open Universiteit - www.ou.nl
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


package org.testar.managers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A management utility for SUT UI input values data.
 */
public class DataManager {

	public static final String XML_UI_FILTER_VERSION = "1.0.20170322";
    public static final String DATA_FILE = "input_values.xml";
	
	// Widget filter types / data types
	public static final int WIDGET_ACTION_TABU_FILTER =		Integer.MIN_VALUE,
							WIDGET_ACTION_WHITE_FILTER = 	Integer.MIN_VALUE + 1,
							PRIMITIVE_DATA_TYPE_TEXT =		-1,
							PRIMITIVE_DATA_TYPE_NUMBER =	-2,
							ANY_DATA_TYPE =					-427,
							BASIC_DATA_TYPE_DATE = 			-13451473,
							BASIC_DATA_TYPE_WEBURL =		-134513313,
							BASIC_DATA_TYPE_EMAIL =			-1345133417;
	
    public static LinkedHashMap<String,Integer> DATA_TYPES;
	public static HashMap<Integer,Set<String>> INPUT_VALUES;
	private static Integer[] INPUT_VALUES_ARRAY;
        
	private static final String XML_TAG_DATA = 			"data",
								XML_TAG_INPUT = 		"input",
								XML_ATTRIBUTE_TYPE = 	"type",
								XML_ATTRIBUTE_DESC = 	"desc",
								XML_ATTRIBUTE_EXAMPLE = "example",
								XML_ATTRIBUTE_VALUE = 	"value";
    
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;  
	
	private static Random rnd = new Random(System.currentTimeMillis());
	private static final int MAX_TEXT_LENGTH = 16, // 64
    						 LETTER_COUNT = 'z' - 'a' + 1;
	
	/**
	 * Computes a random number
	 * @return The random number.
	 */
	public static String getRandomPrimitiveDataTypeNumber(){
		return new Integer(rnd.nextInt()).toString();		
	}
	
	/**
	 * Computes a random text. 
	 * @return The random text.
	 */
	public static String getRandomPrimitiveDataTypeText(){
    	int textLength = rnd.nextInt(MAX_TEXT_LENGTH) + 1;
    	StringBuffer sb = new StringBuffer(textLength);
    	for (int i=0; i<textLength; i++)
    		sb.append((char)('a' + rnd.nextInt(LETTER_COUNT)));    				
    	return sb.toString();
	}
	
	/**
	 * Gets a random data.
	 * @param data Data sample from which to select randomly.
	 * @return The selected random data.
	 */
    public static String getRandom(Set<String> data){
    	return (String) data.toArray()[rnd.nextInt(data.size())];    	
    }
    
    /**
     * Computes a random text data from the available data types.
     * @return The random data.
     */
    public static String getRandomData(){
    	switch( rnd.nextInt(3)){
    	case 0: // primitive text
    		return DataManager.getRandomPrimitiveDataTypeText();
    	case 1: // primitive number
    		return DataManager.getRandomPrimitiveDataTypeNumber();
    	default:
    		return DataManager.getRandom(INPUT_VALUES.get(INPUT_VALUES_ARRAY[rnd.nextInt(INPUT_VALUES_ARRAY.length)]));
    	}
    }
	
	public DataManager(){
		populateDefaultData();
		try{
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
		} catch(ParserConfigurationException e){
			e.printStackTrace();
		}    	
	}
	
	@SuppressWarnings("serial")
	private void populateDefaultData(){
		DATA_TYPES = new LinkedHashMap<String,Integer>();
		DATA_TYPES.put("ANY",				ANY_DATA_TYPE);
		DATA_TYPES.put("PRIMITIVE_TEXT", 	PRIMITIVE_DATA_TYPE_TEXT);
		DATA_TYPES.put("PRIMITIVE_NUMBER", 	PRIMITIVE_DATA_TYPE_NUMBER);
		DATA_TYPES.put("BASIC_DATE", 		BASIC_DATA_TYPE_DATE);
		DATA_TYPES.put("BASIC_EMAIL", 		BASIC_DATA_TYPE_EMAIL);
		DATA_TYPES.put("BASIC_WEBURL",		BASIC_DATA_TYPE_WEBURL);		
		INPUT_VALUES = new HashMap<Integer,Set<String>>();
		INPUT_VALUES.put(BASIC_DATA_TYPE_DATE, new HashSet<String>(){{
    		add("22-03-2017");
    		add("03-22-2017");
    		add("2017-03-22");
    		add("2017-22-03");
        	add("00-00-1900");
        	add("12-12-7357");
        	add("01-01-01");
        	add("2017");
        	add("November");
        	add("31 December");
        	add("1st January");
        	add("2001 February");
    	}});
    	INPUT_VALUES.put(BASIC_DATA_TYPE_EMAIL, new HashSet<String>() {{
    		add("foo@boo.org");
    		add("boo@foo.org");
    		add("fooboo@org.com");
    		add("foo-boo@foo.com");
    	}});
    	INPUT_VALUES.put(BASIC_DATA_TYPE_WEBURL, new HashSet<String>(){{
    		add("www.foo.com");
    		add("www.boo.com");
    		add("www.fooboo.com");
    		add("www.foo.org");
    		add("www.boo.org");
    		add("www.fooboo.org");
    		add("www.testar.org");
    	}});
    	INPUT_VALUES_ARRAY = INPUT_VALUES.keySet().toArray(new Integer[INPUT_VALUES.size()]);
	}
		
	public void loadInputValues(){
		File f = new File(DATA_FILE);
		if (f.exists()){
			BufferedInputStream stream = null;
			try {
				stream = new BufferedInputStream(new FileInputStream(f));
				Document doc = docBuilder.parse(stream);					
		        Node node; Element element;

		        NodeList nList = doc.getElementsByTagName(XML_TAG_DATA);
		        String typeS, desc, example;
		        for (int nidx = 0; nidx < nList.getLength(); nidx++){
		        	node = nList.item(nidx);
		        	if (node.getNodeType() == Node.ELEMENT_NODE){
		        		element = (Element) node;
		        		typeS = element.getAttribute(XML_ATTRIBUTE_TYPE);
		        		desc = element.getAttribute(XML_ATTRIBUTE_DESC);
		        		example = element.getAttribute(XML_ATTRIBUTE_EXAMPLE);
		        		if (typeS != null && desc != null && example != null &&
		        			typeS.length() > 0 && desc.length() > 0 && example.length() > 0){
		        			DATA_TYPES.put(desc + "(" + example + ")", new Integer(typeS));
		        		} else
		        			System.out.println("DataManager: Wrong datatype.");
		        	}
				}
		        		        
		        nList = doc.getElementsByTagName(XML_TAG_INPUT);
		        Integer key;
		        Set<String> typeValues;
		        String valueS;
		        for (int nidx = 0; nidx < nList.getLength(); nidx++){
		        	node = nList.item(nidx);
		        	if (node.getNodeType() == Node.ELEMENT_NODE){
		        		element = (Element) node;
		        		typeS = element.getAttribute(XML_ATTRIBUTE_TYPE);
		        		valueS = element.getAttribute(XML_ATTRIBUTE_VALUE);
		        		if (typeS != null && valueS != null && typeS.length() > 0 && valueS.length() > 0){
		        			key = new Integer(typeS);
		        			typeValues = INPUT_VALUES.get(key);
		        			if (typeValues == null){
		        				typeValues = new HashSet<String>();
		        				INPUT_VALUES.put(key, typeValues);		        				
		        			}
		        			typeValues.add(valueS);
		        		} else
		        			System.out.println("DataManager: Wrong input value");
		        	}
		        }
				printData();		        
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {}
			}
		}
	}
	
	public static void printData(){
		System.out.println("DATA TYPES (description x type_number):");
		for (String key : DATA_TYPES.keySet())
			System.out.println("\t" + key + ": " + DATA_TYPES.get(key).intValue());
		System.out.println("DATA VALUES (type_number x value):");
		Set<String> values;
		for (Integer key : INPUT_VALUES.keySet()){
			values = INPUT_VALUES.get(key);
			for (String v : values)
				System.out.println("\t" + key + ": " + v);
		}
	}
	
}
