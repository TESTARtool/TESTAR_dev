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


import java.util.Map;

import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;

import nl.ou.testar.a11y.protocols.AccessibilityProtocol;
import nl.ou.testar.a11y.reporting.A11yTags;
import nl.ou.testar.a11y.reporting.EvaluationResult;
import nl.ou.testar.a11y.reporting.EvaluationResults;
import nl.ou.testar.a11y.wcag2.SuccessCriterion;
import nl.ou.testar.a11y.wcag2.WCAG2EvaluationResult;
import nl.ou.testar.a11y.wcag2.WCAG2ICT;
import nl.ou.testar.a11y.wcag2.WCAG2Tags;

/**
 * An accessibility evaluation protocol based on WCAG2ICT
 * @author Davy Kager
 */
public class Protocol_accessibility_wcag2ict extends AccessibilityProtocol {

	/**
	 * Constructs a new protocol
	 */
	public Protocol_accessibility_wcag2ict() {
		super(new WCAG2ICT());
	}
	
	@Override
	protected Verdict getVerdict(State state) {
		Verdict verdict = super.getVerdict(state);
		EvaluationResults results = state.get(A11yTags.A11yEvaluationResults);
		String s = "";
		for (EvaluationResult r : results.getResults()) {
			WCAG2EvaluationResult result = (WCAG2EvaluationResult)r;
			if (!result.getType().equals(EvaluationResult.Type.OK)) {
				SuccessCriterion sc = result.getSuccessCriterion();
				Widget w = result.getWidget();
				s += (!s.isEmpty() ? ";" : "") + result.getType().name() +
						":" + sc.toString() +
						":" + sc.getLevel().name() +
						":" + sc.getURLSuffix() +
						":" + (w != null ? w.get(Tags.ConcreteID) : "N/A") +
						":" + result.getMessage();
			}
		}
		state.set(WCAG2Tags.WCAG2Violations, s);
		return verdict;
	}
	
	@Override
	protected void writeGraphDBResultsDetails(Map<String, Object> stateProps) {
		html.writeHeading(4, "Violations")
		.writeTableStart()
		.writeTableHeadings("Type", "Criterion", "Level", "Widget", "Message");
		String[] violations = ((String)stateProps.get(WCAG2Tags.WCAG2Violations.name()))
				.split(";");
		for (String violation : violations) {
			String[] violationInfo = violation.split(":");
			String violationType = violationInfo[0],
					criterion = violationInfo[1],
					level = violationInfo[2],
					url = SuccessCriterion.URL_BASE + violationInfo[3],
					widgetTitle = !violationInfo[4].equals("N/A") ?
							getWidgetTitleFromGraphDB(violationInfo[4]) : violationInfo[4],
					message = violationInfo[5];
			html.writeTableRowStart()
			.writeTableCell(violationType)
			.writeTableCellStart().writeLink(criterion, url, true).writeTableCellEnd()
			.writeTableCell(level)
			.writeTableCell(widgetTitle)
			.writeTableCell(message)
			.writeTableRowEnd();
		}
		html.writeTableEnd();
	}
	
	@Override
	protected void writeOfflineEvaluationResultsDetails(EvaluationResults results) {
		html.writeHeading(3, "Violations");
		if (!results.hasViolations()) {
			html.writeParagraph("None");
			return;
		}
		html.writeTableStart()
		.writeTableHeadings("Type", "Criterion", "Level", "Message");
		for (EvaluationResult r : results.getResults()) {
			if (r.getType().equals(EvaluationResult.Type.OK))
				continue;
			WCAG2EvaluationResult result = (WCAG2EvaluationResult)r;
			SuccessCriterion sc = result.getSuccessCriterion();
			String url = SuccessCriterion.URL_BASE + sc.getURLSuffix();
			html.writeTableRowStart()
			.writeTableCell(result.getType().name())
			.writeTableCellStart().writeLink(sc.toString(), url, true).writeTableCellEnd()
			.writeTableCell(sc.getLevel().name())
			.writeTableCell(result.getMessage())
			.writeTableRowEnd();
		}
		html.writeTableEnd();
	}

}
