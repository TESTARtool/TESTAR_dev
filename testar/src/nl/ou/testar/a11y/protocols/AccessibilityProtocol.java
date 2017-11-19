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

package nl.ou.testar.a11y.protocols;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import nl.ou.testar.GraphDB.GremlinStart;
import nl.ou.testar.a11y.reporting.A11yTags;
import nl.ou.testar.a11y.reporting.EvaluationResult;
import nl.ou.testar.a11y.reporting.EvaluationResults;
import nl.ou.testar.a11y.reporting.HTMLReporter;
import nl.ou.testar.a11y.windows.AccessibilityUtil;

/**
 * Accessibility evaluation protocol
 * @author Davy Kager
 *
 */
public class AccessibilityProtocol extends DefaultProtocol {
	
	public static final String HTML_FILENAME_PREFIX = "accessibility_report_",
			HTML_EXTENSION = ".html";
	
	private static final String SCREENSHOT_PATH_PREFIX = "../";
	
	/**
	 * The accessibility evaluator
	 */
	protected final Evaluator evaluator;
	
	/**
	 * The relevant widgets
	 * This needs to be updated after every state change.
	 */
	protected List<Widget> relevantWidgets;
	
	/**
	 * The HTML reporter to store the evaluation results
	 */
	protected HTMLReporter html = null;

	/**
	 * Constructs a new WCAG2ICT test protocol
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
	protected void beginSequence() {
		super.beginSequence();
		try {
			html = new HTMLReporter(
					settings().get(ConfigTags.OutputDir) + File.separator +
					HTML_FILENAME_PREFIX + sequenceCount() + HTML_EXTENSION);
		}
		catch (Exception e) {
			LogSerialiser.log("Failed to open the HTML report: " + e.getMessage(),
					LogSerialiser.LogLevel.Critical);
		}
		html.writeHeader()
		.writeHeading(2, "General information")
		.writeParagraph("Report type: " +
				(settings().get(ConfigTags.GraphDBEnabled) ? "GraphDB" : "Ad-hoc"))
		.writeParagraph("Guidelines version: " + evaluator.getImplementationVersion())
		.writeParagraph("Sequence number: " + sequenceCount());
	}

	/**
	 * Protocol method: evaluates the given state
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
		if (!settings().get(ConfigTags.GraphDBEnabled))
			// ad-hoc analysis (spammy)
			writeAdHocResults(results);
		return upstreamProblem ? verdict : results.getOverallVerdict();
	}

	/**
	 * Protocol method: derives the follow-up actions from the given state
	 * @param state The state.
	 * @return The set of actions.
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		// first store all relevant widgets to the graph database
		String concreteID = state.get(Tags.ConcreteID);
		for (Widget w : relevantWidgets)
			storeWidget(concreteID, w);
		
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
			// proper offline analysis
			writeGraphDBResults();
			offlineAnalysis();
		}
		html.writeFooter().close();
	}
	
	/**
	 * Perform offline analysis, e.g. with a graph database
	 */
	protected void offlineAnalysis() {
		EvaluationResults results = evaluator.query(graphDB());
		writeOfflineAnalysisResults(results);
	}
	
	/**
	 * Write implementation-specific ad-hoc evaluation result details to the HTML report
	 * @param results The evaluation results.
	 */
	protected void writeAdHocResultsDetails(EvaluationResults results) {
		boolean hadViolations = false;
		html.writeHeading(3, "Violations")
		.writeUListStart();
		for (EvaluationResult result : results.getResults()) {
			if (!result.getType().equals(EvaluationResult.Type.OK)) {
				html.writeListItem(result.toString());
				hadViolations = true;
			}
		}
		if (!hadViolations)
			html.writeListItem("None");
		html.writeUListEnd();
	}
	
	/**
	 * Write implementation-specific evaluation result details from a graph database to the HTML report
	 * @param stateProps The map of state properties, indexed by tag name.
	 */
	protected void writeGraphDBResultsDetails(Map<String, Object> stateProps) {}
	
	/**
	 * Write implementation-specific offline analysis result details to the HTML report
	 */
	protected void writeOfflineAnalysisResultsDetails(EvaluationResults results) {}
	
	/**
	 * Gets the title of the widget with the given concrete ID from a graph database
	 * @param concreteID The concrete ID of the widget.
	 * @return The widget title, or null if the widget is not in the graph database.
	 */
	protected String getWidgetTitleFromGraphDB(String concreteID) {
		String gremlinWidget = "_().has('@class','Widget').has('" +
				Tags.ConcreteID.name() + "','" + concreteID +"').Title";
		List<Object> widgets = graphDB().getObjectsFromGremlinPipe(gremlinWidget,
				GremlinStart.VERTICES);
		if (widgets.size() != 1) // too many or too few widgets
			return null;
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
	
	private void writeAdHocResults(EvaluationResults results) {
		html.writeHeading(2, "State: " + state.get(Tags.ConcreteID))
		.writeTableStart()
		.writeTableHeadings("Type", "Count")
		.writeTableRow("Error", Integer.toString(results.getErrorCount()))
		.writeTableRow("Warning", Integer.toString(results.getWarningCount()))
		.writeTableRow("Pass", Integer.toString(results.getPassCount()))
		.writeTableRow("Total", Integer.toString(results.getResultCount()))
		.writeTableEnd();
		writeAdHocResultsDetails(results);
	}
	
	@SuppressWarnings("unchecked")
	private void writeGraphDBResults() {
		// This will retrieve all properties,
		// which may be inefficient when storing many properties to the GraphDB.
		String gremlinStateProperties = "_().has('@class','State').has('" +
				A11yTags.A11yHasViolations.name() + "',true).map";
		List<Object> stateMaps = graphDB().getObjectsFromGremlinPipe(gremlinStateProperties,
				GremlinStart.VERTICES);
		html.writeParagraph("Unique states: " + stateMaps.size())
		.writeHeading(2, "States with violations");
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
	
	private void writeOfflineAnalysisResults(EvaluationResults results) {
		html.writeHeading(2, "Offline analysis");
		writeGeneralOfflineAnalysisResults(results);
		writeOfflineAnalysisResultsDetails(results);
	}
	
	private void writeGeneralOfflineAnalysisResults(EvaluationResults results) {
		html.writeHeading(3, "General information")
		.writeTableStart()
		.writeTableHeadings("Type", "Count")
		.writeTableRow("Error", results.getErrorCount())
		.writeTableRow("Warning", results.getWarningCount())
		.writeTableRow("Pass", results.getPassCount())
		.writeTableRow("Total", results.getResultCount())
		.writeTableEnd();
	}
	
}
