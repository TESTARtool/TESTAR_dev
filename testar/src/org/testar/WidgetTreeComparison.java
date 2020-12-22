/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

package org.testar;

import java.util.HashSet;
import java.util.Set;

import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.webdriver.enums.WdMapping;
import org.fruit.alayer.windows.UIAMapping;

import com.google.common.collect.Iterators;

import es.upv.staq.testar.CodingManager;

/**
 * Class intended to:
 * Compare two TESTAR States, AbstractStates or ConcreteStates, 
 * to obtain a difference report about Abstract or Concrete widget properties.
 * For AbstractStates and ConcreteStates cases, is necessary to use the widget tree.
 * 
 * Utilities:
 * - Number of different Widgets
 * - IF same widget tree structure with different properties: Abstract properties difference
 * (Compare two widget with the same tree-path)
 * 
 * State Model difference report (Abstract difference):
 * - Get the different AbstractState
 * - Get one ConcreteState (because we are comparing at Abstract level doesn't matter which one)
 * - Get the widget tree of this Concrete State (should have been saved in state model extraction)
 */
public class WidgetTreeComparison {

	private State previousState;
	private State newState;
	private Tag<?>[] customTagsForAbstractId;

	public WidgetTreeComparison(State previousState, State newState) {
		this.previousState = previousState;
		this.newState = newState;

		// Clone current StateManagement Tags
		this.customTagsForAbstractId = CodingManager.getCustomTagsForAbstractId().clone();

		// TODO: Improve and move to additional method
		// Maybe check the Mapping Tag to set an understandable Tag at Widget level
		// Example: Change 'WidgetControlType' to 'UIAControlType'
		for(int i = 0; i < this.customTagsForAbstractId.length; i++) {
			Tag<?> windowsTag = UIAMapping.getMappedStateTag(this.customTagsForAbstractId[i]);
			if(windowsTag != null) {
				this.customTagsForAbstractId[i] = windowsTag;
			}
			Tag<?> webdriverTag = WdMapping.getMappedStateTag(this.customTagsForAbstractId[i]);
			if(webdriverTag != null) {
				this.customTagsForAbstractId[i] = webdriverTag;
			}
		}
	}

	/**
	 * Check if two Abstract States (Abstract Widget-Trees) are the same.
	 * This takes into account the Abstract properties defined in the settings (AbstractStateAttributes).
	 * 
	 * @return true or false
	 */
	public boolean isSameAbstractState() {
		return previousState.get(Tags.AbstractIDCustom,"NopeOne").equals(newState.get(Tags.AbstractIDCustom,"NopeTwo"));
	}

	/**
	 * Check if two Concrete States (Concrete Widget-Trees) are the same.
	 * This takes into account all existing properties for all the widgets.
	 * 
	 * @return true or false
	 */
	public boolean isSameConcreteState() {
		return previousState.get(Tags.ConcreteIDCustom,"NopeOne").equals(newState.get(Tags.ConcreteIDCustom,"NopeTwo"));
	}

	/**
	 * Compares two States (Widget-Trees) to determine the number of new or loss Widgets.
	 * Positive number if newState has more number of Widgets compared with previousState.
	 * Negative number if newState has less number of Widgets compared with previousState.
	 * 0 if both States have the same number of Widgets (this doesn't mean that they are the same).
	 * 
	 * @return number of different Widgets
	 */
	public int numberOfDifferentChilds() {
		return (numberOfExistingWidgetForAState(previousState) - numberOfExistingWidgetForAState(newState));
	}

	/**
	 * Number of existing widgets for a State
	 * 
	 * @param state
	 * @return number of widgets
	 */
	private int numberOfExistingWidgetForAState(State state) {
		return Iterators.size(state.iterator());
	}

	public Set<String> getAbstractPropertiesDifference(){
		Set<String> setOfProperties = new HashSet<>();
		abstractDescendingComparator(previousState, newState, setOfProperties);
		return setOfProperties;
	}

	private void abstractDescendingComparator(Widget previousWidget, Widget newWidget, Set<String> setOfProperties) {
		// Iterate over the Abstract Tags to check his value on the Widgets
		for(Tag<?> abstractTag : customTagsForAbstractId) {
			String reportDifference = compareAbstractTag(previousWidget, newWidget, abstractTag);
			if(!reportDifference.isEmpty()) {
				setOfProperties.add(reportDifference);
			}

		}

		// TODO: Implement Descending for Not-Ordered Widget-Tree nodes
		/*if(previousWidget.childCount() > 0 || newWidget.childCount() > 0) {
			int numberOfChildrens = previousWidget.childCount();
			if(newWidget.childCount() > numberOfChildrens) {numberOfChildrens = newWidget.childCount();}
			for (int i = 0; i < numberOfChildrens; i++) {
				abstractDescendingComparator(previousWidget.child(i), newWidget.child(i), setOfProperties);
			}
		}*/
		
		// Now we take in account that the number of widgets-nodes have the same structure
		// Same "widget path", Example: compare two widget with path [0,1,0,2,3]
		// And the only change is the properties of nodes (widgets)
		if(previousWidget.childCount() > 0 && newWidget.childCount() > 0 &&
				previousWidget.childCount() == newWidget.childCount()) {
			for (int i = 0; i < previousWidget.childCount(); i++) {
				abstractDescendingComparator(previousWidget.child(i), newWidget.child(i), setOfProperties);
			}
		}

	}

	private String compareAbstractTag(Widget previousWidget, Widget newWidget, Tag<?> abstractTag) {
		// Both null
		if(previousWidget.get(abstractTag, null) == null && newWidget.get(abstractTag, null) == null) {
			// nothing, can this happen ?
			return "";
		}

		// Completely new property
		else if(previousWidget.get(abstractTag, null) == null && newWidget.get(abstractTag, null) != null) {
			return String.format("New property : '%s' detected. Value '%s'",
					abstractTag.toString(), newWidget.get(abstractTag).toString());
		}

		// Disappeared property
		else if (previousWidget.get(abstractTag, null) != null && newWidget.get(abstractTag, null) == null) {
			return String.format("Disappeared property : '%s' detected. Value '%s'",
					abstractTag.toString(), previousWidget.get(abstractTag).toString());
		}

		// Specific property changed
		else if(!previousWidget.get(abstractTag).equals(newWidget.get(abstractTag))) {
			return String.format("Previous property : '%s' has changed. From '%s' to '%s'",
					abstractTag.toString(), previousWidget.get(abstractTag).toString(), newWidget.get(abstractTag).toString());
		}

		return "";
	}

}
