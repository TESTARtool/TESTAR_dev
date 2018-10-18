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


package nl.ou.testar.a11y.protocols;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.DefaultProtocol;
import org.fruit.monkey.Settings;

import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.GremlinStart;
import nl.ou.testar.a11y.reporting.A11yTags;
import nl.ou.testar.a11y.reporting.EvaluationResult;
import nl.ou.testar.a11y.reporting.EvaluationResults;
import nl.ou.testar.a11y.reporting.HTMLReporter;
import nl.ou.testar.a11y.windows.AccessibilityUtil;

/**
 * Accessibility evaluation protocol.
 * @author Davy Kager
 *
 */
public class AccessibilityProtocol extends DefaultProtocol {

	public static final String HTML_FILENAME_PREFIX = "accessibility_report_";
	public static final String HTML_EXTENSION = ".html";

	private static final String SCREENSHOT_PATH_PREFIX = "../";

	/**
	 * The accessibility evaluator.
	 */
	protected final Evaluator evaluator;

	/**
	 * The relevant widgets.
	 * This needs to be updated after every state change.
	 */
	protected List<Widget> relevantWidgets = new ArrayList<>();

	/**
	 * The HTML reporter to store the evaluation results.
	 */
	protected HTMLReporter html = null;

	/**
	 * Constructs a new accessibility test protocol.
	 */
	public AccessibilityProtocol(Evaluator evaluator) {
		super();
		this.evaluator = evaluator;
	}

	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);
	}

	@Override
	protected void beginSequence(SUT system, State state) {
		super.beginSequence(system, state);
		try {
			html = new HTMLReporter(
				settings().get(ConfigTags.OutputDir) + File.separator
				+ HTML_FILENAME_PREFIX + sequenceCount() + HTML_EXTENSION);
		} catch (Exception e) {
			LogSerialiser.log("Failed to open the HTML report: " + e.getMessage(),
				LogSerialiser.LogLevel.Critical);
			System.exit(-1);
		}
		html.writeHeader()
		.writeHeading(2, "General Information")
		.writeUListStart()
		.writeListItem("Report time: " + Util.dateString("yyyy-MM-dd HH:mm:ss"))
		.writeListItem("Report type: "
			+ (settings().get(ConfigTags.GraphDBEnabled) ? "GraphDB" : "On-the-fly"))
		.writeListItem("Accessibility standard implementation: "
			+ evaluator.getImplementationVersion())
		.writeListItem("Sequence number: " + sequenceCount())
		.writeUListEnd();
	}

	/**
	 * Protocol method: evaluates the given state.
	 * @param state The state.
	 * @return The verdict.
	 */
	@Override
	protected Verdict getVerdict(State state) {
		EvaluationResults results;
		Verdict verdict = super.getVerdict(state);
		boolean upstreamProblem = !verdict.equals(Verdict.OK);
		if (upstreamProblem) {
			results = new EvaluationResults();
		}
		else {
			// safe only the relevant widgets to use when computing a verdict and deriving actions
			relevantWidgets = getRelevantWidgets(state);
			results = evaluator.evaluate(relevantWidgets);
		}
		state.set(A11yTags.A11yEvaluationResults, results);
		state.set(A11yTags.A11yResultCount, results.getResultCount());
		state.set(A11yTags.A11yPassCount, results.getPassCount());
		state.set(A11yTags.A11yWarningCount, results.getWarningCount());
		state.set(A11yTags.A11yErrorCount, results.getErrorCount());
		state.set(A11yTags.A11yHasViolations, results.hasViolations());
		if (!settings().get(ConfigTags.GraphDBEnabled)) {
			writeOnTheFlyEvaluationResults(results);
		}
		return upstreamProblem ? verdict : results.getOverallVerdict();
	}

	/**
	 * Protocol method: derives the follow-up actions from the given state.
	 * @param state The state.
	 * @return The set of actions.
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		// first store all relevant widgets to the graph database
		String concreteID = state.get(Tags.ConcreteID);
		for (Widget w : relevantWidgets) {
			storeWidget(concreteID, w);
		}

		Set<Action> actions = super.deriveActions(system, state);
		if (actions.isEmpty()) {
			// no upstream actions, so evaluate accessibility
			actions = evaluator.deriveActions(relevantWidgets);
		}
		return actions;
	}

	@Override
	protected void finishSequence(File recordedSequence) {
		super.finishSequence(recordedSequence);
		if (settings().get(ConfigTags.GraphDBEnabled)) {
			writeGraphDBResults();
			offlineEvaluation();
		}
		html.writeFooter().close();
	}

	/**
	 * Perform offline evaluation, e.g. with a graph database.
	 */
	protected void offlineEvaluation() {
		EvaluationResults results = evaluator.query(graphDB());
		writeOfflineEvaluationResults(results);
	}

	/**
	 * Write implementation-specific on-the-fly evaluation result details to the HTML report.
	 * @param results The evaluation results.
	 */
	protected void writeOnTheFlyEvaluationResultsDetails(EvaluationResults results) {
		boolean hadViolations = false;
		html.writeHeading(3, "Violations")
		.writeUListStart();
		for (EvaluationResult result : results.getResults()) {
			if (!result.getType().equals(EvaluationResult.Type.OK)) {
				html.writeListItem(result.toString());
				hadViolations = true;
			}
		}
		if (!hadViolations) {
			html.writeListItem("None");
		}
		html.writeUListEnd();
	}

	/**
	 * Write implementation-specific evaluation result details from a graph database to the HTML report.
	 * @param stateProps The map of state properties, indexed by tag name.
	 */
	protected void writeGraphDBResultsDetails(Map<String, Object> stateProps) {}
	
	/**
	 * Write implementation-specific offline evaluation result details to the HTML report.
	 * @param results evaluation results
	 */
	protected void writeOfflineEvaluationResultsDetails(EvaluationResults results) {}

	/**
	 * Gets the title of the widget with the given concrete ID from a graph database.
	 * @param concreteID The concrete ID of the widget.
	 * @return The widget title, or null if the widget is not in the graph database.
	 */
	protected String getWidgetTitleFromGraphDB(String concreteID) {
		String gremlinWidget = "_().has('@class','Widget').has('"
			+ Tags.ConcreteID.name() + "','" + concreteID + "').Title";
		List<Object> widgets = graphDB().getObjectsFromGremlinPipe(gremlinWidget,
				GremlinStart.VERTICES);
		if (widgets.size() != 1) { // no matches or too many matches
			return "N/A";
		}
		return (String)widgets.get(0);
	}

	private List<Widget> getRelevantWidgets(State state) {
		List<Widget> widgets = new ArrayList<>();
		double maxZIndex = state.get(Tags.MaxZIndex);
		for (Widget w : state) {
			if (isUnfiltered(w)
					&& w.get(Tags.ZIndex) == maxZIndex
					&& AccessibilityUtil.isRelevant(w)) {
				//AccessibilityUtil.printWidgetDebugInfo(w);
				widgets.add(w);
			}
		}
		return widgets;
	}

	private void writeOnTheFlyEvaluationResults(EvaluationResults results) {
		html.writeHeading(2, "State: " + state.get(Tags.ConcreteID))
		.writeTableStart()
		.writeTableHeadings("Type", "Count")
		.writeTableRow("Error", Integer.toString(results.getErrorCount()))
		.writeTableRow("Warning", Integer.toString(results.getWarningCount()))
		.writeTableRow("Pass", Integer.toString(results.getPassCount()))
		.writeTableRow("Total", Integer.toString(results.getResultCount()))
		.writeTableEnd();
		writeOnTheFlyEvaluationResultsDetails(results);
	}

	@SuppressWarnings("unchecked")
	private void writeGraphDBResults() {
		// This will retrieve all properties,
		// which may be inefficient when storing many properties to the GraphDB.
		String gremlinStateProperties = "_().has('@class','State').has('"
			+ A11yTags.A11yHasViolations.name() + "',true).map";
		List<Object> stateMaps = graphDB().getObjectsFromGremlinPipe(gremlinStateProperties,
				GremlinStart.VERTICES);
		html.writeHeading(2, "States with Violations")
		.writeParagraph("Unique states with violations: " + stateMaps.size());
		for (Object stateMap : stateMaps) {
			Map<String, Object> stateProps = (Map<String, Object>)stateMap;
			writeGeneralGraphDBResults(stateProps);
			writeGraphDBResultsDetails(stateProps);
		}
	}

	private void writeGeneralGraphDBResults(Map<String, Object> stateProps) {
		html.writeHeading(3,
				"State: " + stateProps.get(Tags.ConcreteID.name()))
		.writeTableStart()
		.writeTableHeadings("Type", "Count")
		.writeTableRow("Error",
				stateProps.get(A11yTags.A11yErrorCount.name()))
		.writeTableRow("Warning",
				stateProps.get(A11yTags.A11yWarningCount.name()))
		.writeTableRow("Pass",
				stateProps.get(A11yTags.A11yPassCount.name()))
		.writeTableRow("Total",
				stateProps.get(A11yTags.A11yResultCount.name()))
		.writeTableEnd()
		.writeHeading(4, "Screenshot")
		.writeLink("Open screenshot in a new window",
				SCREENSHOT_PATH_PREFIX + stateProps.get(Tags.ScreenshotPath.name()), true);
	}

	private void writeOfflineEvaluationResults(EvaluationResults results) {
		html.writeHeading(2, "Offline Evaluation");
		writeGeneralOfflineEvaluationResults(results);
		writeOfflineEvaluationResultsDetails(results);
	}

	private void writeGeneralOfflineEvaluationResults(EvaluationResults results) {
		html.writeHeading(3, "General Information")
		.writeTableStart()
		.writeTableHeadings("Type", "Count")
		.writeTableRow("Error", results.getErrorCount())
		.writeTableRow("Warning", results.getWarningCount())
		.writeTableRow("Pass", results.getPassCount())
		.writeTableRow("Total", results.getResultCount())
		.writeTableEnd();
	}
}
