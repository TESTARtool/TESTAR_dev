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

import java.util.HashMap;
import java.util.Map;

/**
 * Maps key names to the VK_* names from KBKeys
 * @author Davy Kager
 *
 */
public final class AlternativeKeyNames {
	
	// based on en-US (International) keyboard, all uppercase
	public static final Map<String,String> map = new HashMap<String,String>() {
		private static final long serialVersionUID = 4670847371224012836L;

		{
			put("ESC", "ESCAPE");
			put("CAPSLOCK", "CAPS_LOCK");
			put("CTL", "CONTROL");
			put("CTR", "CONTROL");
			put("CTRL", "CONTROL");
			put("WIN", "WINDOWS");
			put("SPACEBAR", "SPACE");
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
