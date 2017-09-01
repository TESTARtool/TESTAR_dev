/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2017):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This sample is distributed FREE of charge under the TESTAR license, as an open        *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package nl.ou.testar.a11y.windows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.devices.KBKeys;
import static org.fruit.alayer.windows.UIARoles.*;

import org.fruit.alayer.windows.UIATags;

import es.upv.staq.testar.serialisation.LogSerialiser;

/**
 * Various utilities for evaluating accessibility
 * @author Davy Kager
 *
 */
public class AccessibilityUtil {
	
	private static final String KEY_SEP_REGEX = "[+]{1}";
	private static final String VIRTUAL_KEY_PREFIX = "VK_";
	private static final StdActionCompiler compiler = new StdActionCompiler();
	
	private static final List<Action> AC_LEFT_RIGHT = Arrays.asList(
			parseShortcutKey("Left"),	parseShortcutKey("Right"));
	private static final Role[] R_LEFT_RIGHT = new Role[] {
			UIAMenuItem, UIATabItem, UIATreeItem,
			UIADocument, UIAEdit,
			UIARadioButton, UIASlider
	};
	
	private static final List<Action> AC_UP_DOWN = Arrays.asList(
			parseShortcutKey("Up"),	parseShortcutKey("Down"));
	private static final Role[] R_UP_DOWN = new Role[] {
			UIAListItem, UIAMenu, UIAMenuItem, UIATreeItem,
			UIADocument, UIAEdit,
			UIAComboBox, UIARadioButton, UIASlider, UIASpinner
	};
	
	private static final List<Action> AC_HOME_END = Arrays.asList(
			parseShortcutKey("Home"),	parseShortcutKey("End"),
			parseShortcutKey("Ctrl+Home"),	parseShortcutKey("Ctrl+End"));
	private static final Role[] R_HOME_END = new Role[] {
			UIADocument, UIAEdit
	};
	
	private static final List<Action> AC_DELETE = Arrays.asList(
			parseShortcutKey("Delete"),	parseShortcutKey("Backspace"));
	private static final Role[] R_DELETE = new Role[] {
			UIADocument, UIAEdit, UIAText
	};
	
	public static final Action
		AC_ACTIVATE_WIDGET = parseShortcutKey("Enter"),
		AC_CANCEL = parseShortcutKey("Escape"),
		AC_OPEN_CONTEXT_MENU = parseShortcutKey("ContextMenu"),
		AC_NAVIGATE_NEXT_WIDGET = parseShortcutKey("Tab"),
		AC_NAVIGATE_PREVIOUS_WIDGET = parseShortcutKey("Shift+Tab"),
		AC_NAVIGATE_NEXT_GUI_AREA = parseShortcutKey("F6"),
		AC_NAVIGATE_PREVIOUS_GUI_AREA = parseShortcutKey("Shift+F6"),
		AC_NAVIGATE_NEXT_TAB = parseShortcutKey("Ctrl+Tab"),
		AC_NAVIGATE_PREVIOUS_TAB = parseShortcutKey("Ctrl+Shift+Tab");
	
	public static final String LOG_PREFIX = "[a11y]";
	
	private AccessibilityUtil() {}
	
	/**
	 * Compiles a compound action for the given shortcut key combination
	 * A shortcut key of the form "Ctrl+V" is transformed into the key actions for the Control key and the 'v' key.
	 * The method returns null if the key combination could not be parsed.
	 * @param combination The shortcut key.
	 * @return The compound action, or null if the combination could not be parsed.
	 */
	public static Action parseShortcutKey(String combination) {
		Assert.hasText(combination);
		String[] keyStrings = combination.trim().split(KEY_SEP_REGEX);
		if (keyStrings.length == 0) {
			logA11y("Failed to parse shortcut key <" + combination + ">");
			return null;
		}
		List<KBKeys> keys = new ArrayList<>();
		for (String keyString : keyStrings) {
			if (keyString.isEmpty()) {
				logA11y("Failed to parse part of shortcut key <" + combination + ">");
				return null;
			}
			keyString = keyString.toUpperCase();
			if (AlternativeKeyNames.map.containsKey(keyString))
				keyString = AlternativeKeyNames.map.get(keyString);
			String vkString = VIRTUAL_KEY_PREFIX + keyString;
			if (!KBKeys.contains(vkString)) {
				logA11y("Failed to parse part <" + keyString + "> of shortcut key <" + combination + ">");
				return null;
			}
			keys.add(KBKeys.valueOf(vkString));
		}
		return compiler.hitShortcutKey(keys);
	}
	
	// The methods below can be moved to Tags on the Widget and/or to NativeLinker.
	// This has not yet been done because it requires finding appropriate equivalents
	// for the UIA properties in the other accessibility APIs.
	
	public static boolean isRelevant(Widget w) {
		return !(w instanceof State) // filter out the root of the widget tree
				&& w.get(UIATags.UIAIsContentElement, true)
				&& w.get(UIATags.UIAIsEnabled, true);
	}
	
	public static List<Action> getApplicableActions(Widget w) {
		Role r = getRole(w);
		List<Action> actions = new ArrayList<>();
		if (Role.isOneOf(r, R_LEFT_RIGHT))
			actions.addAll(AC_LEFT_RIGHT);
		if (Role.isOneOf(r, R_UP_DOWN))
			actions.addAll(AC_UP_DOWN);
		if (Role.isOneOf(r, R_HOME_END))
			actions.addAll(AC_HOME_END);
		if (Role.isOneOf(r, R_DELETE))
			actions.addAll(AC_DELETE);
		return actions;
	}
	
	public static boolean canUseUpDown(Widget w) {
		return Role.isOneOf(getRole(w), new Role[] {UIAComboBox, UIADocument, UIAEdit, UIAListItem, UIAMenu, UIAMenuItem, UIARadioButton, UIASlider, UIASpinner, UIATreeItem});
	}
	
	public static boolean canUseLeftRight(Widget w) {
		return Role.isOneOf(getRole(w), new Role[] {UIADocument, UIAEdit, UIAMenuItem, UIARadioButton, UIASlider, UIATabItem, UIATreeItem});
	}
	
	public static boolean canUseEditCommands(Widget w) {
		// this is like NativeLinker.getNativeTypeable()
		// put here for consistency and to avoid coupling
		Role r = getRole(w);
		// TODO: also check if the widget is read-only
		return Role.isOneOf(r, new Role[] {UIADocument, UIAEdit})
				// static text is not editable according to UIA documentation,
				// but sometimes it is, e.g. Windows Calculator
				// static text can only be edited when it can have keyboard focus
				|| (r.isA(UIAText) && isKeyboardFocusable(w));
	}
	
	public static boolean hasKeyboardFocus(Widget w) {
		return w.get(UIATags.UIAHasKeyboardFocus, false);
	}
	
	public static boolean isKeyboardFocusable(Widget w) {
		return w.get(UIATags.UIAIsKeyboardFocusable, false);
	}
	
	public static String getAccessKey(Widget w) {
		return w.get(UIATags.UIAAccessKey, "");
	}
	
	public static String getShortcutKey(Widget w) {
		String key = w.get(UIATags.UIAAcceleratorKey, "");
		if (key != null && !key.isEmpty())
			return key;
		if (getRole(w).isA(UIAMenuItem)) {
			// many menu items contain a shortcut key even if the accelerator key is not set
			// find these by pattern matching (may return the wrong thing, but better than nothing)
			String name = w.get(UIATags.UIAName, "");
			int index = name.lastIndexOf("\t");
			if (index != -1) {
				key = name.substring(index+1);
				logA11y("Discovered shortcut key <" + key + "> in <" + name + ">");
				return key;
			}
		}
		return "";
	}
	
	public static boolean canUseShortcutKeys(Widget w) {
		return !w.root().get(UIATags.UIAIsWindowModal, false)
				&& !Role.isOneOf(getRole(w), new Role[] {UIAMenu, UIAMenuItem});
	}
	
	public static boolean isTabItem(Widget w) {
		return getRole(w).isA(UIATabItem);
	}
	
	private static Role getRole(Widget w) {
		Assert.notNull(w);
		return w.get(Tags.Role, UIAWidget);
	}
	
	// debugging
	
	/**
	 * Prints debug info about a widget that is useful for evaluating accessibility
	 * @param w The widget.
	 */
	public static void printWidgetDebugInfo(Widget w) {
		logA11y("Widget" + (w.get(UIATags.UIAHasKeyboardFocus, false) ? "[focus]" : "")  
				+ ": <" + w.get(Tags.Title, "unknown widget")
				+ ">@" + w.get(Tags.ZIndex) + "/" + w.root().get(Tags.MaxZIndex)
				+ " (" + w.get(Tags.Role, Roles.Invalid) + ")"
				+ ": <" + w.get(UIATags.UIAAccessKey, "no access key")
				+ "> - <" + w.get(UIATags.UIAAcceleratorKey, "no accelerator key") + ">",
				LogSerialiser.LogLevel.Debug);
	}
	
	/**
	 * Logs a message about accessibility
	 * An identifying prefix <code>LOG_PREFIX)</code> is added to the message,
	 * and a newline character is appended.
	 * @param msg The message to log.
	 */
	public static void logA11y(String msg) {
		logA11y(msg, LogSerialiser.LogLevel.Info);
	}
	
	/**
	 * Logs a message about accessibility
	 * An identifying prefix <code>LOG_PREFIX)</code> is added to the message,
	 * and a newline character is appended.
	 * @param msg The message to log.
	 * @param logLevel The log level.
	 */
	public static void logA11y(String msg, LogSerialiser.LogLevel logLevel) {
		LogSerialiser.log(LOG_PREFIX + " " + msg + "\n", logLevel);
	}
	
}
