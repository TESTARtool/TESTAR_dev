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

import org.fruit.alayer.exceptions.WidgetNotFoundException;

/**
 * A Finder's task is to find a particular widget within a widget tree. It starts it's search from a location within the tree
 * (typically the root). Finder's are abstract representations of widgets and implement a particular search strategy, e.g.
 * "find the widget with the title 'Save'" or "the widget which is the 3rd child of another widget of type 'Canvas'".
 * 
 * Finders must be serializable and are often used to implement actions, e.g. "click on the widget with title 'Save'".
 */
public interface Finder extends Serializable {
	
	/**
	 * Apply the search strategy implemented by this finder and start the search from start.
	 * @param start the node from where to start the search
	 * @return a non-null reference to the located widget.
	 * @throws WidgetNotFoundException if no widget has been found
	 */
	Widget apply(Widget start) throws WidgetNotFoundException;
	
	/**
	 * Retrieves cached widget, if caching was activated. 
	 * @return The widget, or 'null'.
	 * @author urueda
	 */
	Widget getCachedWidget();
	
}
