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


package nl.ou.testar.a11y.reporting;

import java.io.Serializable;

import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;

/**
 * The result of evaluating an accessibility rule.
 * @author Davy Kager
 *
 */
public class EvaluationResult implements Serializable {

	private static final long serialVersionUID = -51527046346987231L;

	public enum Type {
		/**
		 * No problem found.
		 */
		OK,
		
		/**
		 * A warning
		 * This is a potential problem that can only be detected semi-automatically and needs expert confirmation.
		 */
		WARNING,
		
		/**
		 * An error
		 * This is a definite problem that can be detected automatically.
		 */
		ERROR;
	}

	private final Type type;
	private final String message;
	private final Widget widget;

	/**
	 * Constructs a new evaluation result that does not apply to a single widget.
	 * @param type The problem type.
	 * @param message The problem description.
	 */
	public EvaluationResult(Type type, String message) {
		this(type, message, null);
	}

	/**
	 * Constructs a new evaluation result that applies to a single widget.
	 * @param type The problem type.
	 * @param message The problem description.
	 * @param widget The widget this evaluation result applies to.
	 */
	public EvaluationResult(Type type, String message, Widget widget) {
		this.type = type;
		this.message = message;
		this.widget = widget;
	}

	/**
	 * Gets the problem type.
	 * @return The problem type.
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Gets the problem message.
	 * @return The problem message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the widget that this evaluation result applies to.
	 * @return The widget.
	 */
	public Widget getWidget() {
		return widget;
	}
	
	/**
	 * Computes the Verdict severity for the result.
	 * @return The severity.
	 */
	public double getVerdictSeverity() {
		return type.equals(Type.OK) ? Verdict.SEVERITY_OK : Verdict.SEVERITY_FAIL;
	}

	@Override
	public String toString() {
		String ret = type.name() + ": " + message;
		if (widget != null) {
			ret += " [Widget: \"" + widget.get(Tags.Title, "") + "\" (" + widget.get(Tags.Role) + ")]";
		}
		return ret;
	}
}