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

package org.fruit.alayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.fruit.Assert;
import org.fruit.Util;

/**
 * Tags are labels that can be attached to <code>Taggable</code> objects. They are similar to keys in <code>Map</code>'s.
 * They have a name and a type and are associated with values who must be of that type. 
 * @param <T> Tag
 */
public final class Tag<T> implements Serializable {
	private static final ConcurrentHashMap<Tag<?>, Tag<?>> EXISTING_TAGS = new ConcurrentHashMap<Tag<?>, Tag<?>>();

	/**
	 * Returns a tag object which is identified by <code>name</code> and <code>valueType</code>. 
	 * @param name The name of the tag
	 * @param valueType The type of the values that are associated with this tag.
	 * @return A tag object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Tag<T> from(String name, Class<T> valueType) {
		Assert.notNull(name, valueType);
		Tag<T> ret = new Tag<T>(name, valueType);
		Tag<T> existing = (Tag<T>)EXISTING_TAGS.putIfAbsent(ret, ret);
		if (existing != null) {
			return existing;
		}
		return ret;
	}

	private static final long serialVersionUID = -1215427100999751182L;
	private final Class<T> clazz;
	private final String name;
	private int hashcode;

	private Tag(String name, Class<T> clazz) {
		this.clazz = clazz;
		this.name = name;
	}

	/**
	 * The name of the tag.
	 * @return the name of the tag
	 */
	public String name() { 
		return name; 
	}
	
	
	/**
	 * The type of the values associated with this tag (e.g. <code>String</code>).
	 * @return value type
	 */
	public Class<T> type() { 
		return clazz; 
	}
	
	public String toString() { 
		return name; 
	}
	
	public int hashCode() {
		int ret = hashcode;
		if (ret == 0) {
			ret = name.hashCode() + 31 * Util.hashCode(clazz.getCanonicalName());   
			//Class<T>.hashCode() is not stable across serializations!!
			hashcode = ret;
		}
		return hashcode;
	}

	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other instanceof Tag) {
			Tag<?> ot = (Tag<?>) other;
			return name.equals(ot.name) && clazz.equals(ot.clazz);
		}
		return false;
	}
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
	}
	
	private Object readResolve() throws ObjectStreamException {
		Tag<?> existing = EXISTING_TAGS.putIfAbsent(this, this);		
		return existing == null ? this : existing;
	}
	
	// by urueda
	public boolean isOneOf(Tag<?>... oneOf) {
		Assert.notNull(this, oneOf);
		for (Tag<?> o : oneOf) {
			if (this.equals(o)) {
				return true;
			}
		}
		return false;
	}		
}
