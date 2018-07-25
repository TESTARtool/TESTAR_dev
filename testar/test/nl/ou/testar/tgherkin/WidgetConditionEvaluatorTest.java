/**
 * 
 */
package nl.ou.testar.tgherkin;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fruit.alayer.State;
import org.fruit.alayer.StdWidget;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.junit.Before;
import org.junit.Test;

import nl.ou.testar.tgherkin.gen.WidgetConditionParser;
import nl.ou.testar.tgherkin.model.DataTable;
import nl.ou.testar.tgherkin.model.ProtocolProxyMock;
import nl.ou.testar.tgherkin.model.TableCell;
import nl.ou.testar.tgherkin.model.TableRow;
import nl.ou.testar.tgherkin.model.StateMock;

/**
 * Test WidgetConditionEvaluator class.
 * This JUnit test verifies whether a widget condition is evaluated correctly. 
 */
public class WidgetConditionEvaluatorTest {

	private Map<String, Boolean> testMap = new HashMap<String, Boolean>();
	private Settings settings;
	private State state;
	private Widget widget;
	private DataTable dataTable;
	
	/**
	 * Set up test.
	 * @throws Exception if a problem occurs
	 */
	@Before
	public void setUp() throws Exception {
		setUpGeneral();
		setUpTestCases();
	}
	
	private void setUpGeneral() {
		// settings
		settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// Create test widget
		List<Widget> widgets = new ArrayList<Widget>();
		widget = new StdWidget();
		widget.set(Tags.ConcreteID, "ConcreteID1");
		widget.set(Tags.Desc, "Desc1");
		widget.set(Tags.Title, "Title1");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID1");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID1");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID1");
		widgets.add(widget);
		// Create state
		state = new StateMock(widgets);		
		// Create test data table
		List<TableRow> tableRows = new ArrayList<TableRow>();
		List<TableCell> tableCells = new ArrayList<TableCell>();
		tableCells.add(new TableCell("header1"));
		tableCells.add(new TableCell("header2"));
		tableCells.add(new TableCell("header3"));
		tableRows.add(new TableRow(tableCells));
		tableCells = new ArrayList<TableCell>();
		tableCells.add(new TableCell("Desc1"));
		tableCells.add(new TableCell("Title1"));
		tableCells.add(new TableCell("true"));
		tableRows.add(new TableRow(tableCells));		
		dataTable = new DataTable(tableRows);
		dataTable.beginSequence();
	}
	
	private void setUpTestCases() {
		// Create map with to be tested expression and expected result
		testMap.put("1 = 1", true);
		testMap.put("1 + 2 = 3", true);
		testMap.put("1 > 2", false);
		testMap.put("1 < 2", true);
		testMap.put("(1 < 2)", true);
		testMap.put("(1 + 4 < 2)", false);
		testMap.put("(1 + 4 < 2 * 4)", true);
		testMap.put("(1 + 4 * 5 > 22)", false);
		testMap.put("(1 + 8 / 4 > 2.99)", true);
		testMap.put("(2 / 4 * 8 > 3.99)", true);
		testMap.put("((2 / 4) * 8 > 3.99)", true);
		testMap.put("(2 > 1) or (4 > 15)", true);
		testMap.put("(2 > 1) and (4 > 15)", false);
		testMap.put("true", true);
		testMap.put("(true) and (false)", false);
		testMap.put("(true) and (false or true)", true);
		testMap.put("true and false or true", true);
		testMap.put("not true", false);
		testMap.put("not false", true);
		testMap.put("- 2 + 8 > 5", true);
		testMap.put("-2 + 8 > 5", true);
		testMap.put("\"test\" =  \"proef\"", false);
		testMap.put("\"test\" =  \"test\"", true);
		testMap.put("1 = 1.0", true);
		testMap.put("1 = 1.00", true);
		testMap.put("$Title =  \"Title1\"", true);
		testMap.put("matches($Title,\"Tit.*\")", true);
		testMap.put("$Title =  <header2>", true);
		testMap.put("$Title =  \"Title1\" and $Desc =  \"Desc1\"", true);
		testMap.put("$Title =  \"Title1\" and $Desc <>  \"Desc1\"", false);
	}

	/**
	 * Execute test.
	 */
	@Test
	public void test() {
		Iterator<Entry<String,Boolean>> iterator = testMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String,Boolean> entry = iterator.next();
			String expression = entry.getKey();
			Boolean expectedResult = entry.getValue();
			WidgetConditionParser parser = Utils.getWidgetConditionParser(expression);
			Boolean result = false;
			result = (Boolean)new WidgetConditionEvaluator(new ProtocolProxyMock(settings, state), widget, dataTable).visit(parser.widget_condition());
			assertEquals(expectedResult, result);
		}
		
	}

}
