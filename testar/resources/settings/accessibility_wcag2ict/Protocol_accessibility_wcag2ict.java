/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2017):                                                                          *
 * Universitat Politecnica de Valencia                                                        *
 * Camino de Vera, s/n                                                                        *
 * 46022 Valencia, Spain                                                                      *
 * www.upv.es                                                                                 *
 *                                                                                            * 
 * D I S C L A I M E R:                                                                       *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)          *
 * in the context of the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open           *
 * source project under the BSD3 license (http://opensource.org/licenses/BSD-3-Clause)        *                                                                                        * 
 *                                                                                            *
 **********************************************************************************************/

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
					widgetTitle = this.getWidgetTitleFromGraphDB(violationInfo[4]),
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
	protected void writeOfflineAnalysisResultsDetails(EvaluationResults results) {
		html.writeHeading(3, "Violations")
		.writeTableStart()
		.writeTableHeadings("Type", "Criterion", "Level", "Message");
		for (EvaluationResult r : results.getResults()) {
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
