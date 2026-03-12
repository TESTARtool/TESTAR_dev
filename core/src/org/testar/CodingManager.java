/***************************************************************************************************
*
* Copyright (c) 2016 - 2024 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2024 Open Universiteit - www.ou.nl
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

import java.util.*;
import java.util.zip.CRC32;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;

/**
 * Core coding manager.
 */
public class CodingManager {

	public static final int ID_LENTGH = 24; // 2 (prefixes) + 7 (MAX_RADIX) + 5 (max expected text length) + 10 (CRC32)

	// Concrete and Abstract identifiers used for widgets, states, and actions
	public static final String CONCRETE_ID = "ConcreteID";
	public static final String ABSTRACT_ID = "AbstractID";

	public static final String ABSTRACT_R_ID = "Abs(R)ID"; // ROLE
	public static final String ABSTRACT_R_T_ID = "Abs(R,T)ID"; // ROLE, TITLE
	public static final String ABSTRACT_R_T_P_ID = "Abs(R,T,P)ID"; // ROLE, TITLE, PATH

	public static final String ID_PREFIX_CONCRETE = "C";
	public static final String ID_PREFIX_ABSTRACT = "A";
	public static final String ID_PREFIX_ABSTRACT_R = "R";
	public static final String ID_PREFIX_ABSTRACT_R_T = "T";
	public static final String ID_PREFIX_ABSTRACT_R_T_P = "P";
	
	public static final String ID_PREFIX_STATE = "S";
	public static final String ID_PREFIX_WIDGET = "W";
	public static final String ID_PREFIX_ACTION = "A";

	private static final Tag<?>[] TAGS_ABSTRACT_R_ID = new Tag<?>[]{Tags.Role};
	private static final Tag<?>[] TAGS_ABSTRACT_R_T_ID = new Tag<?>[]{Tags.Role,Tags.Title};
	private static final Tag<?>[] TAGS_ABSTRACT_R_T_P_ID = new Tag<?>[]{Tags.Role,Tags.Title,Tags.Path};

	public static final Role[] ROLES_ABSTRACT_ACTION = new Role[]{ // discard parameters
		/// ActionRoles.MouseMove, 
		ActionRoles.Type,
		ActionRoles.KeyDown,
		ActionRoles.KeyUp
	};

	// two arrays to hold the tags that will be used in constructing the concrete and abstract state id's
	private static Tag<?>[] customTagsForConcreteId = new Tag<?>[]{};
	private static Tag<?>[] customTagsForAbstractId = new Tag<?>[]{};
	private static Tag<?>[] defaultAbstractStateTags = new Tag<?>[] {StateManagementTags.WidgetControlType};

    /**
     * Set the array of tags that should be used in constructing the concrete state id's.
     *
     * @param tags array
     */
	public static synchronized void setCustomTagsForConcreteId(Tag<?>[] tags) {
		customTagsForConcreteId = tags;
		Arrays.sort(customTagsForConcreteId,Comparator.comparing(Tag::name));
	}

    /**
     * Set the array of tags that should be used in constructing the abstract state id's.
     *
     * @param tags
     */
	public static synchronized void setCustomTagsForAbstractId(Tag<?>[] tags) {
		customTagsForAbstractId = tags;
		Arrays.sort(customTagsForAbstractId, Comparator.comparing(Tag::name));
	}

	/**
	 * Returns the tags that are currently being used to create a custom abstract state id
	 * @return
	 */
	public static Tag<?>[] getCustomTagsForAbstractId() {
		return customTagsForAbstractId;
	}

	/**
	 * Returns the tags that are currently being used to create a custom abstract state id
	 * @return
	 */
	public static Tag<?>[] getCustomTagsForConcreteId() { return customTagsForConcreteId;}

	/**
	 * Returns the default tags for use in creating the abstract state id
	 * @return
	 */
	public static Tag<?>[] getDefaultAbstractStateTags() {return defaultAbstractStateTags;}

	// ###########################################
	//  Widgets/States and Actions IDs management
	// ###########################################
	
	/**
	 * Builds IDs for a widget or state.
	 * @param widget A widget or a State (widget-tree, or widget with children)
	 * 
	 * An identifier (alphanumeric) for a state is built as: f(w1 + ... + wn),
	 * where wi (i=1..n) is the identifier for a widget in the widget-tree
	 * and the + operator is the concatenation of identifiers (alphanumeric).
	 * The order of the widgets in f is determined by the UI structure.
	 * f is a formula that converts, with low collision, a text of varying length
	 * to a shorter representation: hashcode(text) + length(text) + crc32(text).
	 * 
	 * An identifier (alphanumeric) for a widget is calculated based on
	 * the concatenation of a set of accessibility properties (e.g. ROLE, TITLE, ENABLED and PATH).
	 * An example for an enabled "ok" button could be: Buttonoktrue0,0,1 ("0,0,1" being the path in the widget-tree).
 	 *
	 */
	public static synchronized void buildIDs(Widget widget){
		if (widget.parent() != null){
			widget.set(Tags.ConcreteID, ID_PREFIX_WIDGET + ID_PREFIX_CONCRETE + CodingManager.codify(widget, customTagsForConcreteId));
			widget.set(Tags.AbstractID, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT + CodingManager.codify(widget, customTagsForAbstractId));
			widget.set(Tags.Abstract_R_ID, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT_R + CodingManager.codify(widget, CodingManager.TAGS_ABSTRACT_R_ID));
			widget.set(Tags.Abstract_R_T_ID, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT_R_T + CodingManager.codify(widget, CodingManager.TAGS_ABSTRACT_R_T_ID));
			widget.set(Tags.Abstract_R_T_P_ID, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT_R_T_P + CodingManager.codify(widget, CodingManager.TAGS_ABSTRACT_R_T_P_ID));
		} else if (widget instanceof State) { // UI root
			StringBuilder concreteId, abstractId, abstractRoleId, abstractRoleTitleId, abstractRoleTitlePathId;
			concreteId = new StringBuilder(abstractId = new StringBuilder(abstractRoleId = new StringBuilder(abstractRoleTitleId = new StringBuilder(abstractRoleTitlePathId = new StringBuilder()))));
			for (Widget childWidget : (State) widget){
				if (childWidget != widget){
					buildIDs(childWidget);
					concreteId.append(childWidget.get(Tags.ConcreteID));
					abstractId.append(childWidget.get(Tags.AbstractID));
					abstractRoleId.append(childWidget.get(Tags.Abstract_R_ID));
					abstractRoleTitleId.append(childWidget.get(Tags.Abstract_R_T_ID));
					abstractRoleTitlePathId.append(childWidget.get(Tags.Abstract_R_T_P_ID));
				}
			}
			widget.set(Tags.ConcreteID, ID_PREFIX_STATE + ID_PREFIX_CONCRETE + CodingManager.lowCollisionID(concreteId.toString()));
			widget.set(Tags.AbstractID, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT + CodingManager.lowCollisionID(abstractId.toString()));
			widget.set(Tags.Abstract_R_ID, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT_R + CodingManager.lowCollisionID(abstractRoleId.toString()));
			widget.set(Tags.Abstract_R_T_ID, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT_R_T + CodingManager.lowCollisionID(abstractRoleTitleId.toString()));
			widget.set(Tags.Abstract_R_T_P_ID, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT_R_T_P + CodingManager.lowCollisionID(abstractRoleTitlePathId.toString()));
		}	
	}
	
	/**
	 * Builds IDs (abstract, concrete) for a set of actions.
	 * @param state Current State of the SUT
	 * @param actions The actions.
	 */
	public static synchronized void buildIDs(State state, Set<Action> actions){
	    for (Action a : actions) {
	    	// Create the Action AbstractID based on: State AbstractID + Widget AbstractID + Action Role
	    	a.set(Tags.AbstractID, ID_PREFIX_ACTION + ID_PREFIX_ABSTRACT +
	    		lowCollisionID(state.get(Tags.AbstractID) + a.get(Tags.OriginWidget).get(Tags.AbstractID) + a.get(Tags.Role, ActionRoles.Action)));
	    	// Create the Action ConcreteID based on: State ConcreteID + Widget ConcreteID + Action Role + Action description
	        a.set(Tags.ConcreteID, ID_PREFIX_ACTION + ID_PREFIX_CONCRETE +
	        	lowCollisionID(state.get(Tags.ConcreteID) + a.get(Tags.OriginWidget).get(Tags.ConcreteID) + a.get(Tags.Role, ActionRoles.Action) + a.toString()));

	    }

		// for the abstract action identifier, we first sort the actions by their path in the widget tree
		// and then set their ids using incremental counters
		/*
		Map<Role, Integer> roleCounter = new HashMap<>();
		actions.stream().
				filter(action -> {
					try {
						action.get(Tags.OriginWidget).get(Tags.Path);
						return true;
					}
					catch (NoSuchTagException ex) {
						System.out.println("Coding Action AbstractID: No origin widget found for action role: " + action.get(Tags.Role));
						System.out.println("Coding Action AbstractID: " + action.get(Tags.Desc));
						return false;
					}
				}).
				sorted(Comparator.comparing(action -> action.get(Tags.OriginWidget).get(Tags.Path))).
				forEach(
					action -> {
						updateRoleCounter(action, roleCounter);
						action.set(Tags.AbstractID, ID_PREFIX_ACTION + ID_PREFIX_ABSTRACT +
							lowCollisionID(state.get(Tags.AbstractID) + getAbstractActionIdentifier(action, roleCounter)));
				}
		);
		*/
	}

	/**
	 * Builds IDs (abstract, concrete, precise) for an environment action.
	 * @param action An action.
	 */
	public static synchronized void buildEnvironmentActionIDs(State state, Action action){		
		action.set(Tags.ConcreteID, ID_PREFIX_ACTION + ID_PREFIX_CONCRETE +
				   CodingManager.codify(state.get(Tags.ConcreteID), action));
		action.set(Tags.AbstractID, ID_PREFIX_ACTION + ID_PREFIX_ABSTRACT +
				   CodingManager.codify(state.get(Tags.AbstractID), action, ROLES_ABSTRACT_ACTION));
	}

	/**
	 * This method will increment or initialize a role counter mapping for a given action.
	 * @param action
	 * @param roleCounter
	 */
	private static void updateRoleCounter(Action action, Map<Role, Integer> roleCounter) {
		Role role;
		try {
			role = action.get(Tags.OriginWidget).get(Tags.Role);
		}
		catch (NoSuchTagException e) {
			role = action.get(Tags.Role, Roles.Invalid);
		}
		// if the role as key is not present, this will initialize with 1, otherwise it will increment with 1
		roleCounter.merge(role, 1, Integer::sum);
	}

	/**
	 * This method will return a string that identifies each action (abstractly).
	 * @param action
	 * @param roleCounter
	 * @return
	 */
	private static String getAbstractActionIdentifier(Action action, Map<Role, Integer> roleCounter) {
		Role role;
		try {
			role = action.get(Tags.OriginWidget).get(Tags.Role);
		}
		catch (NoSuchTagException e) {
			role = action.get(Tags.Role, Roles.Invalid);
		}
		return role.toString() + roleCounter.getOrDefault(role, 999);
	}
	
	// ###############
	//  STATES CODING
	// ###############
	
	private static String codify(Widget state, Tag<?>... tags){
		return lowCollisionID(getTaggedString(state, tags));
	}

	private static String getTaggedString(Widget leaf, Tag<?>... tags){
		StringBuilder sb = new StringBuilder();
		for(Tag<?> t : tags) {
			sb.append(leaf.get(t, null));
			// check if we are dealing with a state management tag and, if so, if it has child tags
			// that we need to incorporate
			if (StateManagementTags.isStateManagementTag(t) && StateManagementTags.getTagGroup(t).equals(StateManagementTags.Group.ControlPattern)) {
				StateManagementTags.getChildTags(t).stream().sorted(Comparator.comparing(Tag::name)).forEach(tag -> sb.append(leaf.get(tag, null)));
			}
		}
		return sb.toString();
	}

	// ################
	//  ACTIONS CODING
	// ################

	private static String codify(String stateID, Action action, Role... discardParameters){
		return lowCollisionID(stateID + action.toString(discardParameters));
	}	
	
	// ############
	//  IDS CODING
	// ############

	private static String lowCollisionID(String text){ // reduce ID collision probability
		CRC32 crc32 = new CRC32();
		crc32.update(text.getBytes());
		return Integer.toUnsignedString(text.hashCode(), Character.MAX_RADIX) +
			   Integer.toHexString(text.length()) +
			   crc32.getValue();
	}

	// #####################################
	// ## New abstract state model coding ##
	// #####################################

	/**
	 * This method will return the unique hash to identify the abstract state model
	 * @return String A unique hash
	 */
	public static String getAbstractStateModelHash(String applicationName, String applicationVersion) {
		// we calculate the hash using the tags that are used in constructing the abstract state id
		// for now, an easy way is to order them alphabetically by name
		Tag<?>[] abstractTags = getCustomTagsForAbstractId().clone();
		Arrays.sort(abstractTags, Comparator.comparing(Tag::name));
		StringBuilder hashInput = new StringBuilder();
		for (Tag<?> tag : abstractTags) {
			hashInput.append(tag.name());
		}
		// we add the application name and version to the hash input
		hashInput.append(applicationName);
		hashInput.append(applicationVersion);
		return lowCollisionID(hashInput.toString());
	}
	
}
