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

import org.fruit.alayer.exceptions.NoSuchTagException;

/**
 * <code>Taggable</code>'s are a essential part of the FRUIT library. A taggable object can carry <code>Tag</code>'s which
 * convey information about it. <code>Action</code>'s <code>State</code>'s and <code>SUT</code>'s are taggable. For example:
 * A widget has many tags such as <code>Enabled</code>, <code>Role</code> and <code>Shape</code> attached it.
 * You can also add additional tags to or remove specific ones from a taggable object.
 * 
 * You can think of <code>Taggable</code>'s as Java's <code>Map</code>'s. The difference is that they can contain objects
 * of different types, yet type safety is still enforced.
 * 
 * @see Tag
 * @see SUT
 * @see State
 * @see Action
 */
public interface Taggable {
	
	/** Retrieves the value of <code>tag</code> if it is attached to this object.
	 * If this object does not have the corresponding tag, then this method
	 * throws a <code>NoSuchTagException</code>
	 * @param tag tag to retrieve
	 * @return the value associated with <code>tag</code>
	 * @throws NoSuchTagException if the tag is not attached to this object
	 */
	<T> T get(Tag<T> tag) throws NoSuchTagException;
	
	/**
	 * Retrieves the value of <code>tag</code> or returns <code>defaultValue</code>
	 * if the tag is not attached to this object.
	 * @param tag the tag for which to retrieve the value
	 * @param defaultValue the value that is returned if the tag is not available
	 * @return the value associated with <code>tag</code> or <code>defaultValue</code>.
	 */
	<T> T get(Tag<T> tag, T defaultValue);
	
	/**
	 * Returns an <code>Iterable</code> over all tags currently attached to this object.
	 * @return Iterable over all attached tags
	 */
    Iterable<Tag<?>> tags();
    
    /**
     * Attach <code>tag</code> to this object and associate it with <code>value</code>
     * @param tag tag to attach to this object
     * @param value the value to associate with <code>tag</code>
     */
    <T> void set(Tag<T> tag, T value);
    
    /**
     * Removes <code>tag</code> and its value from this object
     * @param tag tag to remove
     */
    void remove(Tag<?> tag);
}