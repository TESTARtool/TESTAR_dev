/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.fruit.Pair;
import org.fruit.Proc;
import org.fruit.UnFunc;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.devices.ProcessHandle;

import es.upv.staq.testar.CodingManager;

public final class Tags {
	private static final Set<Tag<?>> tagSet = new HashSet<Tag<?>>();

	private Tags(){}

	//public static final Tag<Boolean> Checked = from("Checked", Boolean.class);
	//public static final Tag<String> RoleDesc = from("RoleDesc", String.class);
	//public static final Tag<Shape> ThumbShape = from("ThumbShape", Shape.class);
	//public static final Tag<Shape> TrackShape = from("TrackShape", Shape.class);
	//public static final Tag<Boolean> Selected = from("Selected", Boolean.class);
//	@SuppressWarnings("unchecked")
//	public static final Tag<UnProc<Widget>> WindowActivator = from("WindowActivator", (Class<UnProc<Widget>>)(Class<?>)UnProc.class);
//	@SuppressWarnings("unchecked")
//	public static final Tag<UnProc<Widget>> WindowCloser = from("WindowCloser", (Class<UnProc<Widget>>)(Class<?>)UnProc.class);

	/** Usually attached to widgets. Returns a <code>HitTester</code> object which allows to 
	 * is visible at a specific location determine whether the widget */
	public static final Tag<HitTester> HitTester = from("HitTester", HitTester.class);
	
	
	/** Usually attached to widgets. Returns a <code>Shape</code> object which allows to 
	 * draw the widget's shape and to determine whether a point lies within the its shape.
	 * However, a shape of a widget does not tell whether or not this widget is obscured
	 * by other widgets (use the <code>HitTester</code> Tag instead!) */
	public static final Tag<Shape> Shape = from("Shape", Shape.class);	

	/** Usually attached to widgets. It tells whether this widget this widget is rendered.
	 * If so, it does not necessarily mean that it is also visible, since it could still
	 * be obfuscated by other controls. */
	public static final Tag<Boolean> Rendered = from("Rendered", Boolean.class);
	
	/** A short human-readable descriptive text for a widget / action / or system */
	public static final Tag<String> Desc = from("Desc", String.class);
	
	/** determines whether a widget is enabled (e.g. in many GUI-Frameworks disabled items are
	 * greyed out) */
	public static final Tag<Boolean> Enabled = from("Enabled", Boolean.class);
	
	/** determines whether a system is running */
	public static final Tag<Boolean> IsRunning = from("IsRunning", Boolean.class);
	
	/** determines whether a widget is blocked by anything or whether it is accessible. For example:
	 * If there is a modal message box on the screen, this message box usually blocks other widgets
	 * which are consequently not clickable. */
	public static final Tag<Boolean> Blocked = from("Blocked", Boolean.class);
	
	/** determines whether a widget / system / window is in the foreground */
	public static final Tag<Boolean> Foreground = from("Foreground", Boolean.class);
	
	/** the title of a widget (usually visible text, such as on a button) */
	public static final Tag<String> Title = from("Title", String.class);
	
	/** ZIndex of a widget (objects with higher values are drawn on top of objects with lower values) */
	public static final Tag<Double> ZIndex = from("ZIndex", Double.class);
	
	// begin by urueda
	/** Maximum observed ZIndex for a UI state */
	public static final Tag<Double> MaxZIndex = from("MaxZIndex", Double.class);
	/** Minimum observed ZIndex for a UI state */
	public static final Tag<Double> MinZIndex = from("MaxZIndex", Double.class);
	
	/** Related to slides (from_Position -&lt; to_Position) */
	public static final Tag<Position[]> Slider = from("Slider", Position[].class);;
	// end by urueda
	
	/** Usually attached to systems. Determines whether a system is non-responsive (hanging)
	 * This does not necessarily mean that the system crashed, it could just process certain tasks. */
	public static final Tag<Boolean> NotResponding = from("NotResponding", Boolean.class);

	/** If attached to a widget, it indicates the Role of a widget, e.g. a 'Button' or a 'TextField'.
	 * If attached to an action, it indicates the Role of an action, e.g. a mouse action or a keyboard action, or a combination. */
	public static final Tag<Role> Role = from("Role", Role.class);
	
	/** The value of the help text usually attached to many widgets (e.g. when you hover over an item and a little box with a description appears) */
	public static final Tag<String> ToolTipText = from("ToolTipText", String.class);
	
	/** Usually attached to window widgets. Determines whether the widget is modal (blocks other widgets) */ 
	public static final Tag<Boolean> Modal = from("Modal", Boolean.class);
	
	/** Usually attached to sliders or scrollbars. Determines the orientation (horizontal, vertical or an arbitrary angle) */
	public static final Tag<Double> Angle = from("Angle", Double.class);
	
	/** Whether a widget (e.g. a text box) has keyboard focus and will receive keyboard input */
	public static final Tag<Boolean> KeyboardFocus = from("KeyboardFocus", Boolean.class);
	
	/** The text of a widget (e.g. the text within a text box) */
	public static final Tag<String> Text = from("Text", String.class);

	public static final Tag<WidgetMap> WidgetMap = from("WidgetMap", WidgetMap.class);

	/** A unique identifier for a widget / actions / system */
	public static final Tag<UID> UID = from("UID", UID.class);
	
	// begin by urueda
	public static final Tag<String> Path = from("Path", String.class);
	public static final Tag<String> ConcreteID = from(CodingManager.CONCRETE_ID, String.class);
	public static final Tag<String> AbstractID = from("AbstractID", String.class);
	public static final Tag<String> Abstract_R_ID = from(CodingManager.ABSTRACT_R_ID, String.class);
	public static final Tag<String> Abstract_R_T_ID = from(CodingManager.ABSTRACT_R_T_ID, String.class);
	public static final Tag<String> Abstract_R_T_P_ID = from(CodingManager.ABSTRACT_R_T_P_ID, String.class);
	// end by urueda
	
	@SuppressWarnings("unchecked")
	public static final Tag<UnFunc<SUT, String>> DynDesc = from("DynDesc", (Class<UnFunc<SUT, String>>)(Class<?>)UnFunc.class);

	/** A visualizer, which visualizes a widget or an action, so that one can get an idea of what the action will do when executed */
	public static final Tag<Visualizer> Visualizer = from("Visualizer", Visualizer.class);
	
	/** Usually attached to actions, determines the widgets that this action will operate on */
	@SuppressWarnings("unchecked")
	public static final Tag<List<Finder>> Targets = from("Targets", (Class<List<Finder>>)(Class<?>)List.class);
	
	// by urueda
	/** For actions that apply to a single target, keep the target ID (abstract) */
	public static final Tag<String> TargetID = from("TargetID", String.class);
	
	/** The Process ID. Usually attached to systems. */
	public static final Tag<Long> PID = from("PID", Long.class);

	// by urueda
	/** The Process HANDLE. Usually attached to systems. */
	public static final Tag<Long> HANDLE = from("HANDLE", Long.class);
	
	/** Refers to the time of an event. E.g.: If attached to an {@link State} it could refer to the time
	 * the state has been recorded. Likewise, if attached to an {@link Action}, it could refer to the point
	 * in time when the action has been executed.
	 */
	public static final Tag<Long> TimeStamp = from("TimeStamp", Long.class);
	
	/** Usually attached to an object of {@link State}. The value is a screenshot of the state. */
	//public static final Tag<Image> Screenshot = from("Screenshot", Image.class);
	public static final Tag<String> ScreenshotPath = from("ScreenshotPath", String.class); // by urueda
		
	/** Usually attached to a {@link State} object. The value is an outcome of a test oracle for that state. It is
	 * used to mark states as 'suspicious' or 'erroneous' */
	public static final Tag<Verdict> OracleVerdict = from("OracleVerdict", Verdict.class);

	/** The standard mouse object. Usually attached to systems */
	public static final Tag<Mouse> StandardMouse = from("StandardMouse", Mouse.class);

	/** The standard keyboard object. Usually attached to systems */
	public static final Tag<Keyboard> StandardKeyboard = from("StandardKeyboard", Keyboard.class);
	
	/** Whether the system has a standard mouse. Usually attached to states. */
	public static final Tag<Boolean> HasStandardMouse = from("HasStandardMouse", Boolean.class);
	
	/** Whether the system has a standard keyboard. Usually attached to states. */
	public static final Tag<Boolean> HasStandardKeyboard = from("HasStandardKeyboard", Boolean.class);
	
	public static final Tag<InputStream> StdOut = from("StdOut", InputStream.class);
	public static final Tag<InputStream> StdErr = from("StdErr", InputStream.class);
	public static final Tag<OutputStream> StdIn = from("StdIn", OutputStream.class);	
	public static final Tag<State> SystemState = from("SystemState", State.class);

	@SuppressWarnings("unchecked")
	public static final Tag<Set<Action>> ActionSet = from("ActionSet", (Class<Set<Action>>)(Class<?>)Set.class);
	public static final Tag<Action> ExecutedAction = from("ExecutedAction", Action.class);
	public static final Tag<Double> ActionDuration = from("ActionDuration", Double.class);
	public static final Tag<Double> ActionDelay = from("ActionDelay", Double.class);

	/** A list of process handles. Usually attached to a system. Process handles allow to  obtain information about and stop the processes that they refer to. */ 
	@SuppressWarnings("unchecked")
	public static final Tag<Iterator<ProcessHandle>> ProcessHandles = from("ProcessHandles", (Class<Iterator<ProcessHandle>>)(Class<?>)Iterator.class);

	/** A list of currently running processes and their names. Usually attached to a state. */ 
	@SuppressWarnings("unchecked")
	public static final Tag<List<Pair<Long, String>>> RunningProcesses = from("RunningProcesses", (Class<List<Pair<Long, String>>>)(Class<?>)List.class);

	/** Usually attached to systems. A system activator can bring a system to the foreground. E.g.: If another process is currently in the foreground,
	 * the SystemActivator makes sure that the SUT gets focused again. */
	@SuppressWarnings("unchecked")
	public static final Tag<Proc> SystemActivator = from("SystemActivator", (Class<Proc>)(Class<?>)Proc.class);


	private static <T> Tag<T> from(String name, Class<T> valueType){
		Tag<T> ret = Tag.from(name, valueType);
		tagSet.add(ret);
		return ret;
	}
		
	public static Set<Tag<?>> tagSet(){ return Collections.unmodifiableSet(tagSet); }
}