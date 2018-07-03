/**
 * 
 */

package nl.ou.testar.tgherkin.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test WidgetConditionEvaluator class.
 * This JUnit test verifies whether a widget tree condition is evaluated correctly.
 *
 */
public class ParameterBaseTest {

	/**
	 * Quote.
	 */
	static final String QUOTE = "\"";

	private DataTable  dataTable;
	
	/**
	 * Set up test.
	 * @throws Exception if a problem occurs
	 */
	@Before
	public void setUp() throws Exception {
		// Create data table
		List<TableRow> tableRows = new ArrayList<TableRow>();
		List<TableCell> tableCells = new ArrayList<TableCell>();
		tableCells.add(new TableCell("header1"));
		tableCells.add(new TableCell("header2"));
		tableCells.add(new TableCell("header3"));
		tableRows.add(new TableRow(tableCells));
		tableCells = new ArrayList<TableCell>();
		tableCells.add(new TableCell("Title1"));
		tableCells.add(new TableCell("true"));
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
		ParameterBase parameterBase = new ParameterBase();
		parameterBase.setValue(Parameters.UNCHECKED, true);
		assertEquals(parameterBase.get(Parameters.UNCHECKED, null),true);
		// Test case 2
		parameterBase = new ParameterBase();
		parameterBase.setValue(Parameters.TEXT, "test");
		assertEquals(parameterBase.get(Parameters.TEXT, null),"test");
		// Test case 3
		parameterBase = new ParameterBase();
		parameterBase.setValue(Parameters.TEXT, "test");
		assertEquals(parameterBase.get(Parameters.TEXT, dataTable),"test");
		// Test case 4
		parameterBase = new ParameterBase();
		parameterBase = new ParameterBase();
		parameterBase.setPlaceholder(Parameters.TEXT, "header1");
		assertEquals(parameterBase.get(Parameters.TEXT, dataTable),"Title1");
		// Test case 5
		parameterBase = new ParameterBase();
		parameterBase = new ParameterBase();
		parameterBase.setPlaceholder(Parameters.UNCHECKED,  "header2");
		assertEquals(parameterBase.get(Parameters.UNCHECKED, dataTable),true);
		// Test case 6
		parameterBase = new ParameterBase();
		parameterBase = new ParameterBase();
		parameterBase.setPlaceholder(Parameters.UNCHECKED, "header2");
		parameterBase.setValue(Parameters.KBKEYS, "KEY_FIRST");
		assertEquals(parameterBase.getList(Parameters.KBKEYS, dataTable).get(0),"KEY_FIRST");
	}

}
