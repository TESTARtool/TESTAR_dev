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


package nl.ou.testar.a11y.wcag2;

import java.util.List;

import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import nl.ou.testar.a11y.reporting.EvaluationResults;
import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;
import nl.ou.testar.a11y.windows.AccessibilityUtil;

/**
 * A WCAG 2.0 guideline.
 * @author Davy Kager
 *
 */
public final class TextAlternativesGuideline extends AbstractGuideline {
	
	private static final long serialVersionUID = -1355000724862045143L;

	TextAlternativesGuideline(AbstractPrinciple parent) {
		super(1, "Text Alternatives", parent);
		criteria.add(new SuccessCriterion(1, "Non-text Content",
				this, Level.A, "text-equiv-all"));
	}
	
	@Override
	public EvaluationResults evaluate(List<Widget> widgets) {
		EvaluationResults results = new EvaluationResults();
		SuccessCriterion sc = getSuccessCriterionByName("Non-text Content");
		for (Widget w : widgets) {
			if (AccessibilityUtil.isImage(w) && w.get(Tags.Title, "").isEmpty()) {
				if (AccessibilityUtil.isKeyboardFocusable(w)) {// focusable images must have a text alternative
					results.add(new WCAG2EvaluationResult(sc, WCAG2EvaluationResult.Type.ERROR,
							"Missing text alternative", w));
				} else {
					results.add(new WCAG2EvaluationResult(sc, WCAG2EvaluationResult.Type.WARNING,
							"Possible missing text alternative", w));
					}
				
			} else {
				results.add(evaluationPassed(sc));
			}
		}
		return results;
	}

}
