/***************************************************************************************************
*
* Copyright (c) 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.CRC32;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.ActionRoles;

/**
 * Core coding manager.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
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

	public static final String ID_PREFIX_CONCRETE = "C";
	public static final String ID_PREFIX_ABSTRACT_R = "R";
	public static final String ID_PREFIX_ABSTRACT_R_T = "T";
	public static final String ID_PREFIX_ABSTRACT_R_T_P = "P";
	public static final String ID_PREFIX_ABSTRACT = "A";
	
	public static final String ID_PREFIX_STATE = "S";
	public static final String ID_PREFIX_WIDGET = "W";
	public static final String ID_PREFIX_ACTION = "A";
	
	private static final Tag<?>[] TAGS_CONCRETE_ID = new Tag<?>[]{Tags.Role,Tags.Title,/*Tags.Shape,*/Tags.Enabled, Tags.Path};
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

    /**
     * Set the array of tags that should be used in constructing the concrete state id's.
     *
     * @param tags array
     */
	public static synchronized void setCustomTagsForConcreteId(Tag<?>[] tags) {
		customTagsForConcreteId = tags;
	}

    /**
     * Set the array of tags that should be used in constructing the abstract state id's.
     *
     * @param tags
     */
	public static synchronized void setCustomTagsForAbstractId(Tag<?>[] tags) {
		customTagsForAbstractId = tags;
	}

	/**
	 * Returns the tags that are currently being used to create a custom abstract state id
	 * @return
	 */
	public static Tag<?>[] getCustomTagsForAbstractId() {
		return customTagsForAbstractId;
	}

	// this map holds the state tags that should be provided to the coding manager
    // for use in constructing concrete and abstract state id's
	public static HashMap<String, Tag<?>> allowedStateTags = new HashMap<String, Tag<?>>() {
		{
			put("Role", Tags.Role);
			put("Title", Tags.Title);
			put("Path", Tags.Path);
			put("Enabled", Tags.Enabled);
		}
	};
	
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
			widget.set(Tags.ConcreteID, ID_PREFIX_WIDGET + ID_PREFIX_CONCRETE + CodingManager.codify(widget, false, CodingManager.TAGS_CONCRETE_ID));
			widget.set(Tags.Abstract_R_ID, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT_R + CodingManager.codify(widget, false, CodingManager.TAGS_ABSTRACT_R_ID));
			widget.set(Tags.Abstract_R_T_ID, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT_R_T + CodingManager.codify(widget, false, CodingManager.TAGS_ABSTRACT_R_T_ID));
			widget.set(Tags.Abstract_R_T_P_ID, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT_R_T_P + CodingManager.codify(widget, false, CodingManager.TAGS_ABSTRACT_R_T_P_ID));
			widget.set(Tags.ConcreteIDCustom, ID_PREFIX_WIDGET + ID_PREFIX_CONCRETE + CodingManager.codify(widget, false, customTagsForConcreteId));
			widget.set(Tags.AbstractIDCustom, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT + CodingManager.codify(widget, false, customTagsForAbstractId));
		} else if (widget instanceof State) { // UI root
			String cid, a_R_id, a_R_T_id, a_R_T_P_id, concreteIdCustom, abstractIdCustom;
			cid = a_R_id = a_R_T_id = a_R_T_P_id = concreteIdCustom = abstractIdCustom = "";
			for (Widget w : (State) widget){
				if (w != widget){
					buildIDs(w);
					cid += w.get(Tags.ConcreteID);
					a_R_id += w.get(Tags.Abstract_R_ID);
					a_R_T_id += w.get(Tags.Abstract_R_T_ID);
					a_R_T_P_id += w.get(Tags.Abstract_R_T_P_ID);
					concreteIdCustom += w.get(Tags.ConcreteIDCustom);
					abstractIdCustom += w.get(Tags.AbstractIDCustom);
				}
			}
			widget.set(Tags.ConcreteID, ID_PREFIX_STATE + ID_PREFIX_CONCRETE + CodingManager.toID(cid));
			widget.set(Tags.Abstract_R_ID, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT_R + CodingManager.toID(a_R_id));
			widget.set(Tags.Abstract_R_T_ID, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT_R_T + CodingManager.toID(a_R_T_id));
			widget.set(Tags.Abstract_R_T_P_ID, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT_R_T_P + CodingManager.toID(a_R_T_P_id));
			widget.set(Tags.ConcreteIDCustom, ID_PREFIX_STATE + ID_PREFIX_CONCRETE + CodingManager.toID(concreteIdCustom));
			widget.set(Tags.AbstractIDCustom, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT + CodingManager.toID(abstractIdCustom));
		}	
	}
	
	/**
	 * Builds IDs (abstract, concrete) for a set of actions.
	 * @param state Current State of the SUT
	 * @param actions The actions.
	 */
	public static synchronized void buildIDs(State state, Set<Action> actions){
		for (Action a : actions)
			CodingManager.buildIDs(state,a);
	}
	
	/**
	 * Builds IDs (abstract, concrete, precise) for an action.
	 * @param action An action.
	 */
	public static synchronized void buildIDs(State state, Action action){		
		action.set(Tags.ConcreteID, ID_PREFIX_ACTION + ID_PREFIX_CONCRETE +
				   CodingManager.codify(state.get(Tags.ConcreteID), action));
		action.set(Tags.AbstractID, ID_PREFIX_ACTION + ID_PREFIX_ABSTRACT +
				   CodingManager.codify(state.get(Tags.ConcreteID), action, ROLES_ABSTRACT_ACTION));
		action.set(Tags.AbstractIDCustom, ID_PREFIX_ACTION + ID_PREFIX_ABSTRACT +
					CodingManager.codify(state.get(Tags.AbstractIDCustom), action, ROLES_ABSTRACT_ACTION));
	}
	
	// ###############
	//  STATES CODING
	// ###############
	
	private static String codify(Widget state, boolean codifyContext, Tag<?>... tags){
		return toID(getWidgetString(state,codifyContext,tags));
	}
	
	private static String getWidgetString(Widget widget, boolean codifyContext, Tag<?>... tags){
		String ws = getTaggedString(widget,tags);
		if (codifyContext)
			ws += "#" + getWidgetContextString(widget);
		return ws;
	}
	
	private static String getTaggedString(Widget leaf, Tag<?>... tags){
		StringBuilder sb = new StringBuilder();
		for(Tag<?> t : tags)
			sb.append(leaf.get(t, null));
		return sb.toString();
	}
	
	private static String getWidgetContextString(Widget widget){
		return "";
		/*int depth = Util.depth(widget), lvls = 0;
		switch(depth){
		case 0:
		case 1:
			return "";
		case 2:
		case 3:
			lvls = depth - 1;
			break;
		default:
			lvls = 3;
		}
		String ctx = "";
		List<Widget> ancestors = Util.ancestors(widget,lvls);
		for (Widget ancestor : ancestors){
			ctx += getTaggedString(ancestor,Tags.Role);
		}
		ctx += "@" + getAncestorsContext(ancestors);
		//ctx += "$" + getIndexPathContext(widget,lvls);
		return ctx;*/
	}
	
	/*private static String getAncestorsContext(List<Widget> ancestors){
		String ctx = "";
		for (Widget ancestor : ancestors){
			ctx += getAncestorContext(ancestor);
		}
		return ctx;
	}*/	
	
	/*private static String getAncestorContext(Widget ancestor){
		Widget child;
		Role role;
		TreeSet<Role> childrenTags = new TreeSet<Role>(new Comparator<Role>(){
			@Override
			public int compare(Role o1, Role o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		for (int i=0; i<ancestor.childCount(); i++){
			child = ancestor.child(i);
			role = child.get(Tags.Role, null);
			if (role != null && !childrenTags.contains(role))
				childrenTags.add(role);
		}
		String ctx = "";
		for (Role r : childrenTags)
			ctx += r.toString();
		return ctx;
	}*/
	
	/*private static String getIndexPathContext(Widget widget, int levels){
		String idxCtx = "";
		int[] idxPath = Util.indexPath(widget);
		for (int i=idxPath.length - levels; i<idxPath.length; i++){
			idxCtx += "[" + idxPath[i] + "]";
		}
		return idxCtx;
	}*/	
		
	// ################
	//  ACTIONS CODING
	// ################

	private static String codify(String stateID, Action action, Role... discardParameters){
		return toID(stateID + action.toString(discardParameters));
	}	
	
	// ############
	//  IDS CODING
	// ############

	private static String lowCollisionID(String text){ // reduce ID collision probability
		CRC32 crc32 = new CRC32(); crc32.update(text.getBytes());
		return Integer.toUnsignedString(text.hashCode(), Character.MAX_RADIX) +
			   Integer.toHexString(text.length()) +
			   crc32.getValue();
	}
	
	/*private static final boolean DEBUG_ID_COLLISIONS = false;
	private static Map<String,String> idMap = new HashMap<String,String>(); // id x text
	private static int idCollisions = 0;
	private static int debugCounter = 0;*/
	
	private static String toID(String text){
		/*if (DEBUG_ID_COLLISIONS){
			String id = lowCollisionID(text);
			String t = idMap.get(id);
			if (t == null)
				idMap.put(id, text);
			else if (!t.equals(text))
				idCollisions++;
			if (idCollisions > 0 && debugCounter++ > 1000){
				debugCounter = 0;
				System.out.println(idCollisions + " ID collisions! (" + idMap.size() + ")");
			}
			return id;
		} else*/
			return lowCollisionID(text);
	}

	// #####################################
	// ## New abstract state model coding ##
	// #####################################

	/**
	 * This method will return the unique hash to identify the abstract state model
	 * @return String A unique hash
	 */
	public static String getAbstractStateModelHash() {
		// we calculate the hash using the tags that are used in constructing the custom abstract state id
		// for now, an easy way is to order them alphabetically by name
		Tag<?>[] abstractTags = getCustomTagsForAbstractId().clone();
		Arrays.sort(abstractTags, (Tag<?> tagA, Tag<?> tagB) -> {
			return tagA.name().compareTo(tagB.name());
		});
		String hashInput = "";
		for (Tag<?> tag : abstractTags) {
			hashInput += tag.name();
		}
		return lowCollisionID(hashInput);
	}

	// #################
	//  Utility methods
	// #################
	
	public static Widget find(State state, String widgetID, String idType){
		Tag<String> t = null;
		switch(idType){
			case CodingManager.CONCRETE_ID:
				t = Tags.ConcreteID;
				break;
			case CodingManager.ABSTRACT_R_ID:
				t = Tags.Abstract_R_ID;
				break;
			case CodingManager.ABSTRACT_R_T_ID:
				t = Tags.Abstract_R_T_ID;
				break;
			case CodingManager.ABSTRACT_R_T_P_ID:
				t = Tags.Abstract_R_T_P_ID;
				break;
			case CodingManager.CONCRETE_ID_CUSTOM:
				t = Tags.ConcreteIDCustom;
				break;
			case CodingManager.ABSTRACT_ID_CUSTOM:
				t = Tags.AbstractIDCustom;
				break;
		}

		for (Widget w : state){
			if (widgetID.equals(w.get(t)))
				return w;
		}
		return null; // not found
	}


	
}
