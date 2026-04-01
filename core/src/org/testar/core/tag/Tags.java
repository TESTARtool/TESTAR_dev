/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.tag;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.testar.core.CodingManager;
import org.testar.core.Pair;
import org.testar.core.action.Action;
import org.testar.core.alayer.Finder;
import org.testar.core.alayer.HitTester;
import org.testar.core.alayer.Position;
import org.testar.core.alayer.Role;
import org.testar.core.alayer.Shape;
import org.testar.core.devices.Keyboard;
import org.testar.core.devices.Mouse;
import org.testar.core.process.ProcessHandle;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.visualizers.Visualizer;
import org.testar.core.verdict.Verdict;

public final class Tags extends TagsBase {

    private Tags() {
    }

    /** Usually attached to widgets. Returns a <code>HitTester</code> object which allows to
     * is visible at a specific location determine whether the widget */
    public static final Tag<HitTester> HitTester = from("HitTester", HitTester.class);
    
    
    /** Usually attached to widgets. Returns a <code>Shape</code> object which allows to 
     * draw the widget's shape and to determine whether a point lies within the its shape.
     * However, a shape of a widget does not tell whether or not this widget is obscured
     * by other widgets (use the <code>HitTester</code> Tag instead!) */
    public static final Tag<Shape> Shape = from("Shape", Shape.class);

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
    
    /** Maximum observed ZIndex for a UI state */
    public static final Tag<Double> MaxZIndex = from("MaxZIndex", Double.class);
    /** Minimum observed ZIndex for a UI state */
    public static final Tag<Double> MinZIndex = from("MinZIndex", Double.class);
    
    /** Related to slides (from_Position -&lt; to_Position) */
    public static final Tag<Position[]> Slider = from("Slider", Position[].class);
    
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

    /** The text of a widget (e.g. the text within a text box) */
    public static final Tag<String> Text = from("Text", String.class);

    /** The pattern value of a web-widget : hyperlink or text in input field */
    public static final Tag<String> ValuePattern = from("ValuePattern", String.class);

    public static final Tag<String> Path = from("Path", String.class);
    public static final Tag<String> ConcreteID = from(CodingManager.CONCRETE_ID, String.class);
    public static final Tag<String> AbstractID = from(CodingManager.ABSTRACT_ID, String.class);
    public static final Tag<String> Abstract_R_ID = from(CodingManager.ABSTRACT_R_ID, String.class);
    public static final Tag<String> Abstract_R_T_ID = from(CodingManager.ABSTRACT_R_T_ID, String.class);
    public static final Tag<String> Abstract_R_T_P_ID = from(CodingManager.ABSTRACT_R_T_P_ID, String.class);

    /** A visualizer, which visualizes a widget or an action, so that one can get an idea of what the action will do when executed */
    public static final Tag<Visualizer> Visualizer = from("Visualizer", Visualizer.class);
    
    /** Usually attached to actions, determines the widgets that this action will operate on */
    @SuppressWarnings("unchecked")
    public static final Tag<List<Finder>> Targets = from("Targets", (Class<List<Finder>>)(Class<?>)List.class);
    
    /** For actions that apply to a single target, keep the target ID (abstract) */
    public static final Tag<String> TargetID = from("TargetID", String.class);
    
    /** The Process ID. Usually attached to systems. */
    public static final Tag<Long> PID = from("PID", Long.class);
    
    /** A handle identifier to a window */
    public static final Tag<Long> HWND = from("HWND", Long.class);

    /** The Process HANDLE. Usually attached to systems. */
    public static final Tag<Long> HANDLE = from("HANDLE", Long.class);
    
    /** Refers to the time of an event. E.g.: If attached to an {@link State} it could refer to the time
     * the state has been recorded. Likewise, if attached to an {@link Action}, it could refer to the point
     * in time when the action has been executed.
     */
    public static final Tag<Long> TimeStamp = from("TimeStamp", Long.class);
    
    /** Usually attached to an object of {@link State}. The value is a screenshot of the state. */
    public static final Tag<String> ScreenshotPath = from("ScreenshotPath", String.class);

    /** Usually attached to a {@link State} object. The value is a list of outcomes of test oracles for that state. */
    @SuppressWarnings("unchecked")
    public static final Tag<List<Verdict>> OracleVerdicts = from("OracleVerdicts", (Class<List<Verdict>>)(Class<?>)List.class);

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
    public static final Tag<String> Representation = from("Representation", String.class);

    /** A list of process handles. Usually attached to a system. Process handles allow to  obtain information about and stop the processes that they refer to. */ 
    @SuppressWarnings("unchecked")
    public static final Tag<Iterator<ProcessHandle>> ProcessHandles = from("ProcessHandles", (Class<Iterator<ProcessHandle>>)(Class<?>)Iterator.class);

    /** A list of currently running processes and their names. Usually attached to a state. */ 
    @SuppressWarnings("unchecked")
    public static final Tag<List<Pair<Long, String>>> RunningProcesses = from("RunningProcesses", (Class<List<Pair<Long, String>>>)(Class<?>)List.class);

    /** Usually attached to systems. A system activator can bring a system to the foreground. E.g.: If another process is currently in the foreground,
     * the SystemActivator makes sure that the SUT gets focused again. */
    @SuppressWarnings("unchecked")
    public static final Tag<Runnable> SystemActivator = from("SystemActivator", (Class<Runnable>)(Class<?>)Runnable.class);

    /**
     * The original widget that can be attached to things like actions
     */
    public static final Tag<Widget> OriginWidget = from("OriginWidget", Widget.class);

    /**
     * The input text for a type or paste action
     */
    public static final Tag<String> InputText = from("InputText", String.class);

    /**
     * Provides timing information about the state rendering process,
     * measuring when the main content becomes visible or ready for interaction.
     */
    public static final Tag<Double> StateRenderTime = from("StateRenderTime", Double.class);

    /**
     * A link reference associated to the widget or state
     */
    public static final Tag<String> LinkReference = from("LinkReference", String.class);

    /**
     * This method will return all the static tags that are available for use.
     * @return
     */
    public static Set<Tag<?>> getAllTags() {
        return tagSet;
    }
}
