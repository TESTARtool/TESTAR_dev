/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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


package nl.ou.testar.a11y.wcag2;

import java.io.Serializable;

import org.fruit.Assert;

/**
 * Base class for a WCAG item (principle, guideline, success criterion)
 * @author Davy Kager
 *
 */
abstract class ItemBase implements Serializable {
	
	private static final long serialVersionUID = 3593969061710272565L;

	/**
	 * This item's numbe
	 */
	protected final int nr;
	
	/**
	 * This item's name.
	 */
	protected final String name;
	
	/**
	 * This item's parent
	 * This can be null if the item has no parent.
	 */
	protected final ItemBase parent;
	
	/**
	 * Constructs a new item
	 * @param nr Item number
	 * @param name Item name
	 */
	protected ItemBase(int nr, String name) {
		this(nr, name, null);
	}
	
	/**
	 * Constructs a new item
	 * @param nr Item number
	 * @param name Item name
	 * @param parent The parent, may be null
	 */
	protected ItemBase(int nr, String name, ItemBase parent) {
		Assert.hasText(name);
		this.nr = nr;
		this.name = name;
		this.parent = parent;
	}

	/**
	 * Gets the number
	 * If the item is not at the top of the hierarchy, this will also include the parent's number.
	 * For example: 1.2.3
	 * @return This item's number as a String
	 */
	public String getNr() {
		return parent == null ? Integer.toString(nr) : parent.getNr() + "." + nr;
	}

	/**
	 * Gets the name
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getNr() + " " + getName();
	}
	
}
