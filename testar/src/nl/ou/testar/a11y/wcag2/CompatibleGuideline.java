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
 * A WCAG 2.0 guideline
 * @author Davy Kager
 *
 */
public final class CompatibleGuideline extends AbstractGuideline {

	private static final long serialVersionUID = -8328191457523354317L;

	CompatibleGuideline(AbstractPrinciple parent) {
		super(1, "Compatible", parent);
		criteria.add(new SuccessCriterion(1, "Parsing",
				this, Level.A, "ensure-compat-parses"));
		criteria.add(new SuccessCriterion(2, "Name, Role, Value",
				this, Level.A, "ensure-compat-rsv"));
	}
	
	@Override
	public EvaluationResults evaluate(List<Widget> widgets) {
		EvaluationResults results = new EvaluationResults();
		SuccessCriterion sc = getSuccessCriterionByName("Name, Role, Value");
		for (Widget w : widgets) {
			// exclude images, they are handled by guideline "Text Alternatives"
			if (!AccessibilityUtil.isImage(w) && w.get(Tags.Title, "").isEmpty()) {
				results.add(new WCAG2EvaluationResult(sc, WCAG2EvaluationResult.Type.ERROR,
						"Missing name", w));
			}
			else {
				results.add(evaluationPassed(sc));
			}
			if (AccessibilityUtil.isRoleUnknown(w)) {
				results.add(new WCAG2EvaluationResult(sc, WCAG2EvaluationResult.Type.ERROR,
						"Unknown widget role \"" + w.get(Tags.Role).name() +
						"\" for \"" + w.get(Tags.Title) + "\"", w));
			}
			else {
				results.add(evaluationPassed(sc));
			}
		}
		return results;
	}

}
