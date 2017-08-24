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
import java.util.List;
import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
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
	
	public static final String ACCESS_KEY_MODIFIER = "Alt";
	public static final Action
		ACTIVATE_CONTEXT_MENU = parseShortcutKey("Shift+F10"),
		ACTIVATE_APP_BAR = parseShortcutKey("Windows+Z"),
		ACTIVATE_MENU_BAR = parseShortcutKey("Alt"),
		NAVIGATE_NEXT_WIDGET = parseShortcutKey("Tab"),
		NAVIGATE_PREVIOUS_WIDGET = parseShortcutKey("Shift+Tab"),
		NAVIGATE_NEXT_TAB = parseShortcutKey("Ctrl+Tab"),
		NAVIGATE_PREVIOUS_TAB = parseShortcutKey("Ctrl+Shift+Tab"),
		NAVIGATE_NEXT_AREA = parseShortcutKey("F6"),
		NAVIGATE_PREVIOUS_AREA = parseShortcutKey("Shift+F6");
	
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
			LogSerialiser.log("Failed to parse shortcut key " + combination);
			return null;
		}
		List<KBKeys> keys = new ArrayList<>();
		for (String keyString : keyStrings) {
			if (keyString.isEmpty()) {
				LogSerialiser.log("Failed to parse part of shortcut key " + combination);
				return null;
			}
			keyString = keyString.toUpperCase();
			if (AlternativeKeyNames.map.containsKey(keyString))
				keyString = AlternativeKeyNames.map.get(keyString);
			String vkString = VIRTUAL_KEY_PREFIX + keyString;
			if (!KBKeys.contains(vkString)) {
				LogSerialiser.log("Failed to parse part " + keyString + " of shortcut key " + combination);
				return null;
			}
			keys.add(KBKeys.valueOf(vkString));
		}
		return compiler.hitShortcutKey(keys);
	}
	
	// The methods below can be moved to Tags on the Widget and/or to NativeLinker.
	// This has not yet been done because it requires finding appropriate equivalents
	// for the UIA properties in the other accessibility APIs.
	
	public static String getAccessKey(Widget w) {
		String key = w.get(UIATags.UIAAccessKey, "");
		if (key != null && !key.isEmpty())
			return ACCESS_KEY_MODIFIER + "+" + key;
		return "";
	}
	
	public static String getShortcutKey(Widget w) {
		return w.get(UIATags.UIAAcceleratorKey, "");
	}
	
	public static boolean canUseUpDown(Widget w) {
		return Role.isOneOf(getRole(w), new Role[] {UIAComboBox, UIADocument, UIAEdit, UIAListItem, UIAMenuItem, UIARadioButton, UIASlider, UIASpinner, UIATreeItem});
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
	
	public static boolean canUseShortcutKeys(Widget w) {
		if (w.root().get(UIATags.UIAIsWindowModal, false))
			return true;
		// TODO: set a tag on the root widget instead of walking all widgets in search of a menu bar
		for (Widget child : w.root())
			if (isMenuBar(child))
				return true;
		return false;
	}
	
	public static boolean hasKeyboardFocus(Widget w) {
		return w.get(UIATags.UIAHasKeyboardFocus, false);
	}
	
	public static boolean isKeyboardFocusable(Widget w) {
		return w.get(UIATags.UIAIsKeyboardFocusable, false);
	}
	
	public static boolean isEnabled(Widget w) {
		return w.get(UIATags.UIAIsEnabled, true);
	}
	
	public static boolean isAppBar(Widget w) {
		return getRole(w).isA(UIAAppBar);
	}
	
	public static boolean isMenuBar(Widget w) {
		return getRole(w).isA(UIAMenuBar);
	}
	
	public static boolean isTabItem(Widget w) {
		return getRole(w).isA(UIATabItem);
	}
	
	private static Role getRole(Widget w) {
		Assert.notNull(w);
		return w.get(Tags.Role, UIAWidget);
	}
	
}
