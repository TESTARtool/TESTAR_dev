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
import nl.ou.testar.tgherkin.gen.WidgetConditionParser;
import nl.ou.testar.tgherkin.model.DataTable;
import nl.ou.testar.tgherkin.model.TableCell;
import nl.ou.testar.tgherkin.model.TableRow;
import org.junit.Before;
import org.junit.Test;

/**
 * Test WidgetConditionValidator class.
 * This JUnit test verifies whether an expression is a valid widget condition expression. 
 *
 */
public class WidgetConditionValidatorTest {

	private Map<String, Boolean> testMap = new HashMap<String, Boolean>();
	private DataTable dataTable;
	
	/**
	 * Set up test.
	 * @throws Exception if a problem occurs
	 */
	@Before
	public void setUp() throws Exception {
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
		// Create map with to be tested expression and expected validation result: 
		// is expression a valid expression for the given data table?
		testMap.put("1 = 1", true);
		testMap.put("1 + 2 = 3", true);
		testMap.put("$Title =  \"Title1\"", true);
		testMap.put("matches($Title,\"Tit.*\")", true);
		testMap.put("$Title =  <header2>", true);
		testMap.put("$Title =  <header99>", false);
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
			WidgetConditionValidator validator = new WidgetConditionValidator(dataTable);
			validator.visit(parser.widget_condition());
			Boolean result = validator.getErrorList().size() == 0;
			assertEquals(expectedResult, result);
		}
		
	}

}
