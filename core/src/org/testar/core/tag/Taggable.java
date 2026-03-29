/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.tag;

import org.testar.core.exceptions.NoSuchTagException;

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
