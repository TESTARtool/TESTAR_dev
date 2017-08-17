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

package nl.ou.testar.a11y;

import java.util.ArrayList;
import java.util.List;
import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.devices.KBKeys;

/**
 * Various utilities for evaluating accessibility
 * @author Davy Kager
 *
 */
public class AccessibilityUtil {
	
	private static final String KEY_SEP_REGEX = "[+]{1}";
	private static final String VIRTUAL_KEY_PREFIX = "VK_";
	
	private AccessibilityUtil() {}
	
	/**
	 * Compiles a compound action for the given shortcut key combination
	 * A shortcut key of the form "Ctrl+V" is transformed into the key actions for the Control key and the 'v' key.
	 * The method returns null if the key combination could not be parsed.
	 * @param combination The shortcut key.
	 * @return The compound action, or null if the combination could not be parsed.
	 */
	public static Action parseShortcutKey(String combination) {
		Assert.hasText(Assert.notNull(combination));
		String[] keyStrings = combination.trim().split(KEY_SEP_REGEX);
		if (keyStrings.length == 0) {
			System.out.println("Failed to parse shortcut key " + combination);
			return null;
		}
		List<KBKeys> keys = new ArrayList<>();
		for (String keyString : keyStrings) {
			if (keyString.isEmpty()) {
				System.out.println("Failed to parse part of shortcut key " + combination);
				return null;
			}
			keyString = keyString.toUpperCase();
			if (AlternativeKeyNames.map.containsKey(keyString))
				keyString = AlternativeKeyNames.map.get(keyString);
			String vkString = VIRTUAL_KEY_PREFIX + keyString;
			if (!KBKeys.contains(vkString)) {
				System.out.println("Failed to parse part " + keyString + " of shortcut key " + combination);
				return null;
			}
			keys.add(KBKeys.valueOf(vkString));
		}
		StdActionCompiler compiler = new StdActionCompiler();
		return compiler.hitShortcutKey(keys);
	}
	
}
