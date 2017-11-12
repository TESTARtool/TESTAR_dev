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

import com.tinkerpop.gremlin.java.GremlinPipeline;
import es.upv.staq.testar.serialisation.LogSerialiser;
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
	
	private final static String SCREENSHOT_PATH_PREFIX = "../";
	
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
		.writeHeading(2, "General Information")
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
		Verdict verdict = super.getVerdict(state);
		if (!verdict.equals(Verdict.OK))
			// something went wrong upstream
			return verdict;
		// safe only the relevant widgets to use when computing a verdict and deriving actions
		relevantWidgets = getRelevantWidgets(state);
		EvaluationResults results = evaluator.evaluate(relevantWidgets);
		state.set(A11yTags.A11yResultCount, results.getResultCount());
		state.set(A11yTags.A11yPassCount, results.getPassCount());
		state.set(A11yTags.A11yWarningCount, results.getWarningCount());
		state.set(A11yTags.A11yErrorCount, results.getErrorCount());
		state.set(A11yTags.A11yHasViolations, results.hasViolations());
		if (!settings().get(ConfigTags.GraphDBEnabled))
			// ad-hoc analysis (spammy)
			writeAdHocResults(results);
		return results.getOverallVerdict();
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
		if (settings().get(ConfigTags.GraphDBEnabled))
			// proper offline analysis
			writeGraphDBResults();
		html.writeFooter().close();
	}
	
	/**
	 * Write implementation-specific ad-hoc evaluation result details to the HTML report
	 * Subclasses can override this to retrieve information from a subclass of EvaluationResult.
	 * @param results The evaluation results.
	 */
	protected void writeAdHocResultsDetails(EvaluationResults results) {
		boolean hadViolations = false;
		html.writeHeading(4, "Violations")
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
	 * Write implementation-specific offline evaluation result details to the HTML report
	 * Subclasses can override this to retrieve information from a graph database.
	 */
	protected void writeGraphDBResultsDetails() {}
	
	private List<Widget> getRelevantWidgets(State state) {
		List<Widget> widgets = new ArrayList<>();
		double maxZIndex = state.get(Tags.MaxZIndex);
		for (Widget w : state)
			if (isUnfiltered(w)
					&& w.get(Tags.ZIndex) == maxZIndex
					&& AccessibilityUtil.isRelevant(w))
				widgets.add(w);
		return widgets;
	}
	
	private void writeAdHocResults(EvaluationResults results) {
		html.writeHeading(3, "State: " + state.get(Tags.ConcreteID))
		.writeTableStart()
		.writeTableHeadings("Type", "Count")
		.writeTableRow("Error", Integer.toString(results.getErrorCount()))
		.writeTableRow("Warning", Integer.toString(results.getWarningCount()))
		.writeTableRow("Pass", Integer.toString(results.getPassCount()))
		.writeTableRow("Total", Integer.toString(results.getResultCount()))
		.writeTableEnd();
		writeAdHocResultsDetails(results);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void writeGraphDBResults() {
		GremlinPipeline pipe = new GremlinPipeline(graphDB().getStateVertices());
		html.writeParagraph("Unique states: " + pipe.count());
		
		pipe = new GremlinPipeline(graphDB().getStateVertices());
		// This will retrieve all properties,
		// which may be inefficient when storing many properties to the GraphDB.
		pipe.has(A11yTags.A11yHasViolations.name(), "true").map();
		for (Object props : pipe) {
			Map<String, Object> state = (Map<String, Object>)props;
			html.writeHeading(3,
					"State: " + (String)state.get(Tags.ConcreteID.name()))
			.writeTableStart()
			.writeTableHeadings("Type", "Count")
			.writeTableRow("Error",
					(String)state.get(A11yTags.A11yErrorCount.name()))
			.writeTableRow("Warning",
					(String)state.get(A11yTags.A11yWarningCount.name()))
			.writeTableRow("Pass",
					(String)state.get(A11yTags.A11yPassCount.name()))
			.writeTableRow("Total",
					(String)state.get(A11yTags.A11yResultCount.name()))
			.writeTableEnd()
			.writeHeading(4, "Screenshot")
			.writeImage(SCREENSHOT_PATH_PREFIX + (String)state.get(Tags.ScreenshotPath.name()),
					"State screenshot");
		}
		
		writeGraphDBResultsDetails();
	}

}
