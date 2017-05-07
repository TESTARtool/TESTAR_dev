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
 */
public final class Tag<T> implements Serializable{
	private final static ConcurrentHashMap<Tag<?>, Tag<?>> existingTags = new ConcurrentHashMap<Tag<?>, Tag<?>>();

	/**
	 * Returns a tag object which is identified by <code>name</code> and <code>valueType</code>. 
	 * @param name The name of the tag
	 * @param valueType The type of the values that are associated with this tag.
	 * @return A tag object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Tag<T> from(String name, Class<T> valueType){
		Assert.notNull(name, valueType);
		Tag<T> ret = new Tag<T>(name, valueType);
		Tag<T> existing = (Tag<T>)existingTags.putIfAbsent(ret, ret);
		if(existing != null)
			return existing;
		return ret;
	}

	private static final long serialVersionUID = -1215427100999751182L;
	private final Class<T> clazz;
	private final String name;
	private int hashcode;

	private Tag(String name, Class<T> clazz){
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
	public Class<T> type() { return clazz; }
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
		if(other instanceof Tag){
			Tag<?> ot = (Tag<?>) other;
			return name.equals(ot.name) && clazz.equals(ot.clazz);
		}
		return false;
	}
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
	}
	
	private Object readResolve() throws ObjectStreamException{
		Tag<?> existing = existingTags.putIfAbsent(this, this);		
		return existing == null ? this : existing;
	}
	
	// by urueda
	public boolean isOneOf(Tag<?>... oneOf){
		Assert.notNull(this, oneOf);
		for(Tag<?> o : oneOf){
			if(this.equals(o))
				return true;
		}
		return false;
	}	
	
}