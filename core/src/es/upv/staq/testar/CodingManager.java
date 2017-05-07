/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2016):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 *          and the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open      *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar;

import java.util.Set;

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

	public static final int ID_LENTGH = 12;
	
	public static final String CONCRETE_ID = "ConcreteID";
	// actions abstraction
	public static final String ABSTRACT_ID = "AbstractID";
	// widgets abstraction
	public static final String ABSTRACT_R_ID = "Abs(R)ID"; // ROLE
	public static final String ABSTRACT_R_T_ID = "Abs(R,T)ID"; // ROLE, TITLE
	public static final String ABSTRACT_R_T_P_ID = "Abs(R,T,P)ID"; // ROLE, TITLE, PATH

	public static final char ID_PREFIX_CONCRETE = 'c';
	public static final char ID_PREFIX_ABSTRACT_R = 'r';
	public static final char ID_PREFIX_ABSTRACT_R_T = 't';
	public static final char ID_PREFIX_ABSTRACT_R_T_P = 'p';
	public static final char ID_PREFIX_ABSTRACT = 'a';
	
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
	
	// ###########################################
	//  Widgets/States and Actions IDs management
	// ###########################################
	
	/**
	 * Builds IDs (abstract, concrete, precise) for a widget or state.
	 * @param widget A widget or a State.
	 */
	public static synchronized void buildIDs(Widget widget){
		if (widget.parent() != null){
			widget.set(Tags.ConcreteID, ID_PREFIX_CONCRETE + CodingManager.codify(widget, false, CodingManager.TAGS_CONCRETE_ID));
			widget.set(Tags.Abstract_R_ID, ID_PREFIX_ABSTRACT_R + CodingManager.codify(widget, false, CodingManager.TAGS_ABSTRACT_R_ID));
			widget.set(Tags.Abstract_R_T_ID, ID_PREFIX_ABSTRACT_R_T + CodingManager.codify(widget, false, CodingManager.TAGS_ABSTRACT_R_T_ID));
			widget.set(Tags.Abstract_R_T_P_ID, ID_PREFIX_ABSTRACT_R_T_P + CodingManager.codify(widget, false, CodingManager.TAGS_ABSTRACT_R_T_P_ID));
		} else if (widget instanceof State) { // UI root
			String cid = "", a_R_id = "", a_R_T_id = "", a_R_T_P_id = "";
			for (Widget w : (State) widget){
				if (w != widget){
					buildIDs(w);
					cid += w.get(Tags.ConcreteID);
					a_R_id += w.get(Tags.Abstract_R_ID);
					a_R_T_id += w.get(Tags.Abstract_R_T_ID);
					a_R_T_P_id += w.get(Tags.Abstract_R_T_P_ID);
				}
			}
			widget.set(Tags.ConcreteID, ID_PREFIX_CONCRETE + CodingManager.toID(cid));
			widget.set(Tags.Abstract_R_ID, ID_PREFIX_ABSTRACT_R + CodingManager.toID(a_R_id));
			widget.set(Tags.Abstract_R_T_ID, ID_PREFIX_ABSTRACT_R_T + CodingManager.toID(a_R_T_id));
			widget.set(Tags.Abstract_R_T_P_ID, ID_PREFIX_ABSTRACT_R_T_P + CodingManager.toID(a_R_T_P_id));
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
		action.set(Tags.ConcreteID, ID_PREFIX_CONCRETE + CodingManager.codify(state.get(Tags.ConcreteID), action));
		action.set(Tags.AbstractID, ID_PREFIX_ABSTRACT + CodingManager.codify(state.get(Tags.ConcreteID), action, ROLES_ABSTRACT_ACTION));
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
	
	private static String positiveHash(int hash){
		if (hash > 0)
			return new Integer(hash).toString();
		else if (hash < 0)
			return "n" + new Integer(Math.abs(hash)).toString();
		else
			return "0";
	}
	
	private static String toID(String text){
		return positiveHash(text/*.intern()*/.hashCode()); // intern => avoid potential IDs collision (what about IDs preserve along batch executions?)
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
		}
		for (Widget w : state){
			if (widgetID.equals(w.get(t)))
				return w;
		}
		return null; // not found
	}
	
}
