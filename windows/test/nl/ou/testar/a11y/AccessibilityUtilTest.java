/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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
