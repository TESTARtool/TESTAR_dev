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

import org.fruit.alayer.exceptions.NoSuchTagException;

/**
 * <code>Taggable</code>'s are a essential part of the FRUIT library. 
 * A taggable object can carry <code>Tag</code>'s which
 * convey information about it. <code>Action</code>'s <code>State</code>'s 
 * and <code>SUT</code>'s are taggable. For example:
 * A widget has many tags such as <code>Enabled</code>, <code>Role</code> 
 * and <code>Shape</code> attached it.
 * You can also add additional tags to or remove specific ones from a taggable object.
 * 
 * <p>You can think of <code>Taggable</code>'s as Java's <code>Map</code>'s. 
 * The difference is that they can contain objects
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
	 * Attach <code>tag</code> to this object and associate it with <code>value</code>.
	 * @param tag tag to attach to this object
	 * @param value the value to associate with <code>tag</code>
	 */
	<T> void set(Tag<T> tag, T value);
    
	/**
	 * Removes <code>tag</code> and its value from this object.
	 * @param tag tag to remove
	 */
	void remove(Tag<?> tag);
}
