package nl.ou.testar.a11y;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.ou.testar.a11y.windows.AccessibilityUtil;

public class AccessibilityUtilTest {

	@Test
	public void testParseShortcutKey() {
		String exp;
		
		// single keys
		exp =
				"(VK_F)" // press
				+ "(VK_F)"; // release
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("F").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("  F  ").toParametersString());
		exp =
				"(VK_ESCAPE)" // press
				+ "(VK_ESCAPE)"; // release
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("ESC").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("esc").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("Escape").toParametersString());
		exp =
				"(VK_CONTROL)" // press
				+ "(VK_CONTROL)"; // release
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("Ctrl").toParametersString());
		
		// key combinations
		exp =
				"(VK_CONTROL)(VK_SHIFT)(VK_F)" // press
				+ "(VK_F)(VK_SHIFT)(VK_CONTROL)"; // release
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("Ctrl+Shift+F").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("  Ctrl+Shift+F  ").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("ctrl+shift+f").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("CTRL+SHIFT+F").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("Control+Shift+F").toParametersString());
		
		// keys that have different symbols when Shift is pressed
		exp =
				"(VK_CONTROL)(VK_SHIFT)(VK_OPEN_BRACKET)" // press
				+ "(VK_OPEN_BRACKET)(VK_SHIFT)(VK_CONTROL)"; // release
		// the Action should send the non-Shift'ed key
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("Control+Shift+[").toParametersString());
		assertEquals(exp, AccessibilityUtil.parseShortcutKey("Control+Shift+{").toParametersString());
		
		// invalid strings
		assertEquals(null, AccessibilityUtil.parseShortcutKey("  "));
		assertEquals(null, AccessibilityUtil.parseShortcutKey("FF"));
		assertEquals(null, AccessibilityUtil.parseShortcutKey("f0"));
		assertEquals(null, AccessibilityUtil.parseShortcutKey("Ctrl-Shift-F"));
		assertEquals(null, AccessibilityUtil.parseShortcutKey("Ctrl+Shift+FF"));
		assertEquals(null, AccessibilityUtil.parseShortcutKey("Ctrl+Shift++F"));
	}

}
