/***************************************************************************************************
*
* Copyright (c) 2016 - 2020 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2020 Open Universiteit - www.ou.nl
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


package es.upv.staq.testar;

import java.util.*;
import java.util.zip.CRC32;

import org.fruit.alayer.*;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.exceptions.NoSuchTagException;

/**
 * Core coding manager.
 */
public class CodingManager {

	public static final int ID_LENTGH = 24; // 2 (prefixes) + 7 (MAX_RADIX) + 5 (max expected text length) + 10 (CRC32)

	public static final String CONCRETE_ID = "ConcreteID";
	public static final String CONCRETE_ID_CUSTOM = "ConcreteIDCustom";
	// actions abstraction
	public static final String ABSTRACT_ID = "AbstractID";
	public static final String ABSTRACT_ID_CUSTOM = "AbstractIDCustom";

	// widgets abstraction
	public static final String ABSTRACT_R_ID = "Abs(R)ID"; // ROLE
	public static final String ABSTRACT_R_T_ID = "Abs(R,T)ID"; // ROLE, TITLE
	public static final String ABSTRACT_R_T_P_ID = "Abs(R,T,P)ID"; // ROLE, TITLE, PATH

	// two arrays to hold the tags that will be used in constructing the concrete and abstract state id's
	private static Tag<?>[] customTagsForConcreteId = new Tag<?>[]{};
	private static Tag<?>[] customTagsForAbstractId = new Tag<?>[]{};
	private static Tag<?>[] defaultAbstractStateTags = new Tag<?>[] {StateManagementTags.WidgetControlType};

	private static IDGenerator idGenerator = new DefaultIDGenerator();
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

	public synchronized static void setIdGenerator(IDGenerator g) {
		idGenerator = g;
	}
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
		idGenerator.buildIDs(widget);
	}
	
	/**
	 * Builds IDs (abstract, concrete) for a set of actions.
	 * @param state Current State of the SUT
	 * @param actions The actions.
	 */
	public static synchronized void buildIDs(State state, Set<Action> actions){
		idGenerator.buildIDs(state, actions);
	}

	/**
	 * Builds IDs (abstract, concrete, precise) for an action.
	 * @param action An action.
	 */
	public static synchronized void buildIDs(State state, Action action){
		idGenerator.buildIDs(state, action);
	}

	/**
	 * Builds IDs (abstract, concrete, precise) for an environment action.
	 * @param action An action.
	 */
	public static synchronized void buildEnvironmentActionIDs(State state, Action action){
		idGenerator.buildEnvironmentActionIDs(state, action);
	}

	public static String getAbstractStateModelHash(String applicationName, String applicationVersion) {
		return idGenerator.getAbstractStateModelHash(applicationName, applicationVersion);
	}
}
