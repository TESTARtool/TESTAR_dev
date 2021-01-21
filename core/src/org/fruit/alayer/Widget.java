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

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.Drag;

import es.upv.staq.testar.CodingManager;

/**
 * A Widget is usually a control element of an <code>SUT</code>.
 * Widgets have exactly one parent and can have several children.
 * They are attached to a <code>State</code> and form a Widget Tree.
 * In fact a <code>State</code> is a Widget itself and is the root
 * of the Widget Tree.
 * 
 * @see State
 */
public interface Widget extends Taggable, Serializable {

	State root();
	Widget parent();
	Widget child(int i);
	int childCount();
	void remove();
	void moveTo(Widget p, int idx);
	Widget addChild();
	
	/**
	 * For scrollable widgets, compute drag segments of scrolling options.
	 * @param scrollArrowSize The size of scrolling arrows.
	 * @param scrollThick The scroller thickness.
	 * @return 'null' for non-scrollable widgets or a set of drags, from (x1,y1) to (x2,y2), otherwise.
	 * @author: urueda
	 */
	Drag[] scrollDrags(double scrollArrowSize, double scrollThick);
	
	/**
	 * @param tab tabulator for indentation.
	 * @return Computes a string representation for the widget.
	 * @author urueda
	 */
	// OLD IMPLEMENTATION
	public String getRepresentation(String tab);
	
	/**
	 * Get the Abstract Tags from a Widget (defined by users) 
	 * to prepare a string that represents it at an abstract level. 
	 * 
	 * @return String that represents the widget at an abstract level.
	 */
	default String getAbstractRepresentation() {
		StringBuilder repr = new StringBuilder();
		repr.append("AbstractIDCustom=" + this.get(Tags.AbstractIDCustom));
		for(Tag<?> tag : CodingManager.getCustomTagsForAbstractId()) {
			if(this.get(tag, null) != null) {
				repr.append("," + tag.name() + "=" + this.get(tag));
			}
		}

		final String abstractRepresentation = repr.toString();

		// When no custom tag values can be retrieved then log a warning
		if (StringUtils.equals(abstractRepresentation, "AbstractIDCustom=null")) {
			LogManager.getLogger(Widget.class).warn("Widget has no custom tags, default abstractRepresentation is returned");
		}

		return abstractRepresentation;
	}
	
	public abstract String toString(Tag<?>... tags);
	
}
