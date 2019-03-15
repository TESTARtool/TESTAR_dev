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


package nl.ou.testar.a11y.windows;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps key names to the VK_* names from KBKeys
 * @author Davy Kager
 *
 */
final class AlternativeKeyNames {
	
	/**
	 * The map from alternative key names to standard key names
	 * Based on the en-US (International) keyboard layout, all uppercase.
	 */
	static final Map<String,String> map = new HashMap<String,String>() {
		private static final long serialVersionUID = 4670847371224012836L;

		{
			put("ESC", "ESCAPE");
			put("CAPSLOCK", "CAPS_LOCK");
			put("CTL", "CONTROL");
			put("CTR", "CONTROL");
			put("CTRL", "CONTROL");
			put("WIN", "WINDOWS");
			put("SPACEBAR", "SPACE");
			put("CONTEXTMENU", "CONTEXT_MENU");
			put("APPLICATION", "CONTEXT_MENU");
			put("LEFT ARROW", "LEFT");
			put("RIGHT ARROW", "RIGHT");
			put("UP ARROW", "UP");
			put("DOWN ARROW", "DOWN");
			put("RETURN", "ENTER");
			put("BACKSPACE", "BACK_SPACE");
			put(",", "COMMA");
			put("<", "COMMA");
			put(".", "PERIOD");
			put(">", "PERIOD");
			put("/", "SLASH");
			put("?", "SLASH");
			put(";", "SEMICOLON");
			put("SEMI", "SEMICOLON");
			put(":", "SEMICOLON");
			put("[", "OPEN_BRACKET");
			put("{", "OPEN_BRACKET");
			put("]", "CLOSE_BRACKET");
			put("}", "CLOSE_BRACKET");
			put("\\", "BACK_SLASH");
			put("BACKSLASH", "BACK_SLASH");
			put("|", "BACK_SLASH");
			put("=", "EQUALS");
			put("+", "EQUALS");
			put("-", "MINUS");
			put("_", "MINUS");
			put("!", "1");
			put("@", "2");
			put("#", "3");
			put("$", "4");
			put("%", "5");
			put("^", "6");
			put("&", "7");
			put("*", "8");
			put("(", "9");
			put(")", "0");
		}
	};
	
	private AlternativeKeyNames() {}

}
