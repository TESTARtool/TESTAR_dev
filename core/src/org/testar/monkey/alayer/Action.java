/***************************************************************************************************
*
* Copyright (c) 2013 - 2025 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
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
package org.testar.monkey.alayer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testar.monkey.alayer.actions.BriefActionRolesMap;
import org.testar.monkey.alayer.exceptions.ActionFailedException;

import org.testar.CodingManager;

/**
 * Actions take a system and a state as parameters and operate on the system (e.g. a left click). They
 * usually use the state to find certain values that might be necessary for their 
 * execution. In addition they might use a system's devices (Mouse, Keyboard, ...)
 * in order to execute a specific task.
 * For example: An action could always click on a menu item with the title
 * "Create New Document". Therefore it has to search the system's state
 * in order to find this menu item. It then needs to find out the item's position
 * and obtain the mouse device from the system in order to actually issue the click.
 * If it fails to do any of these tasks, it raises an exception.
 * Like states and systems, actions can have properties attached to them.
 * 
 * An action should be serializable, so that it can be stored and replayed.
 * 
 * @see SUT
 * @see State
 */
public interface Action extends Taggable, Serializable {
	
	/**
	 * Takes a system and its state as input and executes a certain action (e.g. a click).
	 * The duration parameter indicates how fast the action should be executed. For example:
	 * If the action moves the cursor on a widget before clicking on it, a high value for
	 * this parameter will cause the mouse movement to be slowed down. However, the value
	 * is only a recommendation.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @param duration the duration of the action in seconds
	 * @throws ActionFailedException
	 */
	void run(SUT system, State state, double duration) throws ActionFailedException;

	/**
	 * @param action An action (from unknown state).
	 * @param tab A tabulator for indentation.
	 * @return A string representation for the action.
	 *   [0] = Extended representation
	 *   [1] = Compact representation
	 */
	public static String[] getActionRepresentation(Action action, String tab){
		return getActionRepresentation(null,action,tab);
	}
		
	/**
	 * @param state A SUT state.
	 * @param action A state' action.
	 * @param tab A tabulator for indentation.
	 * @return A string representation for the action.
	 *   [0] = Extended representation
	 *   [1] = Compact representation
	 */
	public static String[] getActionRepresentation(State state, Action action, String tab){
		String[] returnS = new String[]{"",""};

		Role actionRole = action.get(Tags.Role, null);
		if (actionRole != null){
			returnS[0] += tab + "ROLE = " + actionRole.toString() + "\n";
			returnS[1] = String.format("%1$2s ",
				actionRole == null ? "??" : BriefActionRolesMap.map.get(actionRole.toString()));
		}

		if (state != null){
			List<Finder> targets = action.get(Tags.Targets, null);
			if (targets != null){
				String title;
				Role widgetRole;
				Widget w;
				for (Finder f : targets){
					w = f.apply(state);
					if (w != null){
						returnS[0] += tab + "TARGET =\n" + w.getRepresentation("\t\t");				
						widgetRole = w.get(Tags.Role, null);
						title = w.get(Tags.Title, null);
						returnS[1] += String.format("( %1$" + CodingManager.ID_LENTGH + "s, %2$11s, %3$s )",
								w.get(Tags.ConcreteID),
								widgetRole == null ? "???" : widgetRole.toString(),
								title == null ? "\"\"" : title);
					}
				}
			}
		}
			
		String desc = action.get(Tags.Desc, null);
		if (desc != null)
			returnS[0] += tab + "DESCRIPTION = " + desc + "\n";

		returnS[0] += tab + "TEXT = " + action.toString().replaceAll("\\r\\n|\\n", "\n\t\t") + "\n";
		
		String params = action.toParametersString()
				.replaceAll("\\)\\(",",")
				.replaceAll("\\(","")
				.replaceAll("\\)","")
				.replaceAll("BUTTON[1,3]","")
				.replaceAll(",,","")
				.replaceAll(", ",",");
		returnS[1] += " [ " + (params.equals(",") ? "" : params + " ]");

		return returnS;
	}
	
	/**
	 * Returns a short string representation for the action.
	 * @return The short string.
	 */
	String toShortString();
	
	/**
	 * Returns the parameters of the action.
	 * @return A string representation of the action parameters.
	 */
	String toParametersString();
	
	public abstract String toString(Role... discardParameters);

	/**
	 * Map the OriginWidget to the action
	 * 
	 * @param widget
	 */
	default void mapOriginWidget(Widget widget) {
		this.set(Tags.OriginWidget, widget);
	}
}
