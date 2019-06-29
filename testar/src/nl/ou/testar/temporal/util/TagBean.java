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
package nl.ou.testar.temporal.util;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.fruit.Assert;
import org.fruit.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tags are labels that can be attached to <code>Taggable</code> objects. They are similar to keys in <code>Map</code>'s.
 * They have a name and a type and are associated with values who must be of that type. 
 */
public final class TagBean<T> implements Serializable{  // copy of TAg class, but that was final, could not read/write to files
	private final static ConcurrentHashMap<TagBean<?>, TagBean<?>> existingTags = new ConcurrentHashMap<TagBean<?>, TagBean<?>>();

	/**
	 * Returns a tag object which is identified by <code>name</code> and <code>valueType</code>.
	 * @param name The name of the tag
	 * @param valueType The type of the values that are associated with this tag.
	 * @return A tag object.
	 */


	private  Class<?> clazz;
	private  String name;
	private int hashcode;

	public TagBean() {  //for JSON handler
	}


	@SuppressWarnings("unchecked")
	public static <T> TagBean<T> from(String name, Class<?> valueType){
		Assert.notNull(name, valueType);
		TagBean<T> ret = new TagBean<T>(name, valueType);
		TagBean<T> existing = (TagBean<T>)existingTags.putIfAbsent(ret, ret);
		if(existing != null)
			return existing;
		return ret;
	}


	private static final long serialVersionUID = -1215427100999751182L;

	public Class<?> getClazz() {
		return clazz;
	}
	@JsonSetter("clazz")
	public void setTheClazz(String claz)  {
		try {

			Class cl = Class.forName(claz);
			this.clazz = (Class<?>) cl;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	@JsonGetter("clazz")
	public String getTheClazz() {
	return this.clazz.getName();
	}


	public String getName() {
		return name;
	}


	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	public void setName(String name) {
		this.name = name;
	}



	public TagBean(String name, Class<?> clazz){  // was originally private
		this.clazz = clazz;
		this.name = name;
	}

	/**
	 * The name of the tag
	 * @return the name of the tag
	 */
	public String name() { return name; }
	
	
	/**
	 * The type of the values associated with this tag (e.g. <code>String</code>)
	 * @return value type
	 */
	public Class<?> type() { return clazz; }
	public String toString(){ return name; }
	
	public int hashCode(){
		int ret = hashcode;
		if(ret == 0){
			ret = name.hashCode() + 31 * Util.hashCode(clazz.getCanonicalName());   //Class<T>.hashCode() is not stable across serializations!!
			hashcode = ret;
		}
		return hashcode;
	}

	public boolean equals(Object other){
		if(other == this)
			return true;
		if(other instanceof TagBean){
			TagBean<?> ot = (TagBean<?>) other;
			return name.equals(ot.name) && clazz.equals(ot.clazz);
		}
		return false;
	}
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
	}
	
	private Object readResolve() throws ObjectStreamException{
		TagBean<?> existing = existingTags.putIfAbsent(this, this);
		return existing == null ? this : existing;
	}
	
	// by urueda
	public boolean isOneOf(TagBean<?>... oneOf){
		Assert.notNull(this, oneOf);
		for(TagBean<?> o : oneOf){
			if(this.equals(o))
				return true;
		}
		return false;
	}	
	
}
