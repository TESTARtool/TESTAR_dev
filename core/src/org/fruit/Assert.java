/***************************************************************************************************
*
* Copyright (c) 2013 - 2020 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2020 Open Universiteit - www.ou.nl
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
package org.fruit;

import java.util.Collection;

public final class Assert {
	private Assert(){}
	
	public static void isTrue(boolean expression, String text){
		if(!expression)
			throw new IllegalArgumentException(text);
	}
	
	public static void isTrue(boolean expression){
		if(!expression)
			throw new IllegalArgumentException("You passed illegal parameters!");
	}
		
	public static void notNull(Object object, String text){
		if(object == null)
			throw new IllegalArgumentException(text);
	}

	public static <T> T notNull(T object){
		if(object == null)
			throw new IllegalArgumentException("You passed null as a parameter!");
		return object;
	}
	
	public static void notNull(Object o1, Object o2){
		if(o1 == null || o2 == null)
			throw new IllegalArgumentException("You passed null as a parameter!");
	}

	public static void notNull(Object o1, Object o2, Object o3){
		if(o1 == null || o2 == null || o3 == null)
			throw new IllegalArgumentException("You passed null as a parameter!");
	}

	public static void hasText(String string){
		if(string == null || string.length() == 0)
			throw new IllegalArgumentException("You passed a null or empty string!");
	}

	public static void hasTextSetting(String string, String settingName){
		if(string == null || string.length() == 0) {
			String message = "Non valid setting value!\n"
					+ String.format("It seems that setting %s as null or empty string!\n", settingName)
					+ "Please provide a correct string value using TESTAR GUI or test.setting file";
			throw new IllegalArgumentException(message);
		}
	}

	public static void collectionContains(Collection<String> collection, String value) {
	    if(!collection.contains(value)) {
	        String message = String.format("Collection %s doesn't contain desired value %s", collection.toString(), value);
	        throw new IllegalArgumentException(message);
	    }
	}

	public static void collectionNotContains(Collection<String> collection, String value) {
	    if(collection.contains(value)) {
	        String message = String.format("Collection %s contains undesired value %s", collection.toString(), value);
	        throw new IllegalArgumentException(message);
	    }
	}
	
	public static void collectionSize(Collection<String> collection, int size) {
	    if(collection.size() != size) {
	        String message = String.format("Collection %s has undesired size %s", collection.toString(), size);
	        throw new IllegalArgumentException(message);
	    }
	}
}
