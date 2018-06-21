/**
 * 
 */
package nl.ou.testar.tgherkin.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.fruit.alayer.State;
import org.fruit.alayer.StdWidget;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.junit.Before;
import org.junit.Test;

/**
 * Test WidgetConditionEvaluator class.
 * This JUnit test verifies whether a widget tree condition is evaluated correctly.
 *
 */
public class WidgetTreeConditionTest {

	/**
	 * Quote.
	 */
	static final String QUOTE = "\"";

	private Settings settings;
	private State state;
	private DataTable  dataTable;
	
	/**
	 * Set up test.
	 * @throws Exception if a problem occurs
	 */
	@Before
	public void setUp() throws Exception {
		// settings
		settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.StoreTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ConfidenceThreshold, 0.7);
		settings.set(ConfigTags.TgherkinReportIncludeOCR, false);
		settings.set(ConfigTags.TgherkinReportIncludeImageRecognition, false);
		settings.set(ConfigTags.TgherkinNrOfNOPRetries, 1);
		settings.set(ConfigTags.TgherkinExecutionMode, Document.getRegisteredExecutionModes()[0]);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		List<Widget> testWidgets = new ArrayList<Widget>();
		// Create test widget
		Widget widget = new StdWidget();
		widget.set(Tags.ConcreteID, "ConcreteID1");
		widget.set(Tags.Desc, "Desc1");
		widget.set(Tags.Title, "Title1");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID1");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID1");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID1");
		testWidgets.add(widget);
		// Create test widget
		widget = new StdWidget();
		widget.set(Tags.ConcreteID, "ConcreteID2");
		widget.set(Tags.Desc, "Desc2");
		widget.set(Tags.Title, "Title2");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID2");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID2");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID2");
		testWidgets.add(widget);
		// Create state
		state = new StateMock(testWidgets);		
		// Create data table
		List<TableRow> tableRows = new ArrayList<TableRow>();
		List<TableCell> tableCells = new ArrayList<TableCell>();
		tableCells.add(new TableCell("header1"));
		tableCells.add(new TableCell("header2"));
		tableCells.add(new TableCell("header3"));
		tableRows.add(new TableRow(tableCells));
		tableCells = new ArrayList<TableCell>();
		tableCells.add(new TableCell("Title1"));
		tableCells.add(new TableCell("Title2"));
		tableCells.add(new TableCell("Title3"));
		tableRows.add(new TableRow(tableCells));		
		dataTable = new DataTable(tableRows);
		dataTable.beginSequence();
	}
	
	/**
	 * Execute test.
	 */
	@Test
	public void test() {
		// Test case 1
		List<WidgetCondition> conditions = new ArrayList<WidgetCondition>();
		conditions.add(new WidgetCondition("$Title="+ QUOTE + "Title2" + QUOTE));
		WidgetTreeCondition widgetTreeCondition = new WidgetTreeCondition(conditions);
		assertEquals(widgetTreeCondition.evaluate(new ProtocolProxyMock(settings, state), null),true);
		// Test case 2
		conditions = new ArrayList<WidgetCondition>();
		conditions.add(new WidgetCondition("$Title="+ QUOTE + "Title3" + QUOTE));
		widgetTreeCondition = new WidgetTreeCondition(conditions);
		assertEquals(widgetTreeCondition.evaluate(new ProtocolProxyMock(settings, state), null),false);
		// Test case 3
		conditions = new ArrayList<WidgetCondition>();
		conditions.add(new WidgetCondition("$Title="+ QUOTE + "Title1" + QUOTE));
		conditions.add(new WidgetCondition(WidgetCondition.Type.ALSO, "$Title="+ QUOTE + "Title2" + QUOTE));
		widgetTreeCondition = new WidgetTreeCondition(conditions);
		assertEquals(widgetTreeCondition.evaluate(new ProtocolProxyMock(settings, state), null),true);
		// Test case 4
		conditions = new ArrayList<WidgetCondition>();
		conditions.add(new WidgetCondition("$Title="+ QUOTE + "Title1" + QUOTE));
		conditions.add(new WidgetCondition(WidgetCondition.Type.EITHER, "$Title="+ QUOTE + "TitleUnknown" + QUOTE));
		widgetTreeCondition = new WidgetTreeCondition(conditions);
		assertEquals(widgetTreeCondition.evaluate(new ProtocolProxyMock(settings, state), null),true);
		// Test case 5
		conditions = new ArrayList<WidgetCondition>();
		conditions.add(new WidgetCondition("$Title="+ QUOTE + "Title1" + QUOTE));
		conditions.add(new WidgetCondition(WidgetCondition.Type.ALSO,"$Title="+ QUOTE + "Title2" + QUOTE));
		conditions.add(new WidgetCondition(WidgetCondition.Type.EITHER, "$Title="+ QUOTE + "TitleUnknown" + QUOTE));
		widgetTreeCondition = new WidgetTreeCondition(conditions);
		assertEquals(widgetTreeCondition.evaluate(new ProtocolProxyMock(settings, state), null),true);
		// Test case 6
		conditions = new ArrayList<WidgetCondition>();
		conditions.add(new WidgetCondition("$Title="+ QUOTE + "Title1" + QUOTE));
		conditions.add(new WidgetCondition(WidgetCondition.Type.ALSO,"$Title="+ QUOTE + "TitleUnknown" + QUOTE));
		conditions.add(new WidgetCondition(WidgetCondition.Type.EITHER, "$Title="+ QUOTE + "Title2" + QUOTE));
		conditions.add(new WidgetCondition(WidgetCondition.Type.ALSO, "$Title="+ QUOTE + "TitleUnknown" + QUOTE));
		widgetTreeCondition = new WidgetTreeCondition(conditions);
		assertEquals(widgetTreeCondition.evaluate(new ProtocolProxyMock(settings, state), null),false);
		// Test case 7
		conditions = new ArrayList<WidgetCondition>();
		conditions.add(new WidgetCondition("$Title="+ QUOTE + "Title1" + QUOTE));
		conditions.add(new WidgetCondition(WidgetCondition.Type.ALSO,"$Title="+ QUOTE + "TitleUnknown" + QUOTE));
		conditions.add(new WidgetCondition(WidgetCondition.Type.EITHER, "$Title="+ QUOTE + "Title2" + QUOTE));
		conditions.add(new WidgetCondition(WidgetCondition.Type.ALSO, "$Title="+ QUOTE + "Title1" + QUOTE));
		widgetTreeCondition = new WidgetTreeCondition(conditions);
		assertEquals(widgetTreeCondition.evaluate(new ProtocolProxyMock(settings, state), null),true);
		// Test case 8
		conditions = new ArrayList<WidgetCondition>();
		conditions.add(new WidgetCondition("$Title=<header1>"));
		conditions.add(new WidgetCondition(WidgetCondition.Type.ALSO, "$Title=<header2>"));
		widgetTreeCondition = new WidgetTreeCondition(conditions);
		assertEquals(widgetTreeCondition.evaluate(new ProtocolProxyMock(settings, state), dataTable),true);
	}

}
