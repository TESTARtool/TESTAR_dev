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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.exceptions.NoSuchTagException;

public class TaggableBase implements Taggable, Serializable {
	private static final long serialVersionUID = 3941511707954247582L;
	private Map<Tag<?>, Object> tagValues = Util.newHashMap();
	boolean allFetched;
	
	public final <T> T get(Tag<T> tag) throws NoSuchTagException {
		T ret = get(tag, null);
		if(ret == null)
			throw new NoSuchTagException(tag);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public final <T> T get(Tag<T> tag, T defaultValue) {
		Assert.notNull(tag);
		T ret = (T) tagValues.get(tag);
		if(ret == null && !allFetched && !tagValues.containsKey(tag)){
			ret = fetch(tag);
			tagValues.put(tag, ret);
		}
		return ret == null ? defaultValue : ret;
	}

	public final Iterable<Tag<?>> tags() {
		Set<Tag<?>> ret =new HashSet<Tag<?>>();
		
		if(!allFetched){
			for(Tag<?> t : tagDomain())
				get(t, null);
			allFetched = true;
		}
		
		for(Tag<?> t : tagValues.keySet()){
			if(tagValues.get(t) != null)
				ret.add(t);
		}
		return ret;
	}

	protected <T> T fetch(Tag<T> tag){ return null; }
	protected Set<Tag<?>> tagDomain(){ return Collections.emptySet(); }

	public <T> void set(Tag<T> tag, T value) {
		Assert.notNull(tag, value);
		Assert.isTrue(tag.type().isInstance(value), "Value not of type required by this tag!");
		tagValues.put(tag, value);
	}

	public void remove(Tag<?> tag) { tagValues.put(Assert.notNull(tag), null); }
}