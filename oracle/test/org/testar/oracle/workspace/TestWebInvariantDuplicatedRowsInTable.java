package org.testar.oracle.workspace;

import java.util.List;

import org.junit.Test;
import org.testar.core.Assert;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;
import org.testar.oracle.Oracle;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestWebInvariantDuplicatedRowsInTable extends WorkspaceOracleTestSupport {

	@Test
	public void test_detection_web_invariant_duplicated_rows_table() {
		Oracle oracle = loadWorkspaceOracle("WebInvariantDuplicatedRowsInTable");
		StateStub state = new StateStub();
		WidgetStub widgetTable = new WidgetStub();
		widgetTable.set(Tags.Role, WdRoles.WdTABLE);
		widgetTable.set(WdTags.WebId, "tableid");
		state.addChild(widgetTable);
		widgetTable.setParent(state);

		WidgetStub firstRow = new WidgetStub();
		firstRow.set(Tags.Role, WdRoles.WdTR);
		firstRow.set(Tags.Shape, Rect.fromCoordinates(0, 0, 10, 10));
		widgetTable.addChild(firstRow);

		WidgetStub firstHeader = new WidgetStub();
		firstHeader.set(Tags.Role, WdRoles.WdTH);
		firstHeader.set(WdTags.WebTextContent, "header_content");
		firstRow.addChild(firstHeader);

		WidgetStub firstData = new WidgetStub();
		firstData.set(Tags.Role, WdRoles.WdTD);
		firstData.set(WdTags.WebTextContent, "data_content");
		firstRow.addChild(firstData);

		WidgetStub secondRow = new WidgetStub();
		secondRow.set(Tags.Role, WdRoles.WdTR);
		secondRow.set(Tags.Shape, Rect.fromCoordinates(0, 0, 10, 10));
		widgetTable.addChild(secondRow);

		WidgetStub secondHeader = new WidgetStub();
		secondHeader.set(Tags.Role, WdRoles.WdTH);
		secondHeader.set(WdTags.WebTextContent, "header_content");
		secondRow.addChild(secondHeader);

		WidgetStub secondData = new WidgetStub();
		secondData.set(Tags.Role, WdRoles.WdTD);
		secondData.set(WdTags.WebTextContent, "data_content");
		secondRow.addChild(secondData);

		// Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
		List<Verdict> verdicts = oracle.getVerdicts(state);
		Assert.isEquals(1, verdicts.size());

		Verdict verdict = verdicts.get(0);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
		Assert.isTrue(verdict.info().contains("Detected duplicated row in a Table for the widget: _header_content_data_content"));
		Assert.isEquals(2, verdict.visualizer().getShapes().size());
	}

	@Test
	public void test_undetection_invariant_duplicated_rows_table() {
		Oracle oracle = loadWorkspaceOracle("WebInvariantDuplicatedRowsInTable");
		StateStub state = new StateStub();
		WidgetStub widgetTable = new WidgetStub();
		widgetTable.set(Tags.Role, WdRoles.WdTABLE);
		widgetTable.set(WdTags.WebId, "tableid");
		state.addChild(widgetTable);
		widgetTable.setParent(state);

		WidgetStub firstRow = new WidgetStub();
		firstRow.set(Tags.Role, WdRoles.WdTR);
		widgetTable.addChild(firstRow);

		WidgetStub firstHeader = new WidgetStub();
		firstHeader.set(Tags.Role, WdRoles.WdTH);
		firstHeader.set(WdTags.WebTextContent, "first_header_content");
		firstRow.addChild(firstHeader);

		WidgetStub firstData = new WidgetStub();
		firstData.set(Tags.Role, WdRoles.WdTD);
		firstData.set(WdTags.WebTextContent, "data_content");
		firstRow.addChild(firstData);

		WidgetStub secondRow = new WidgetStub();
		secondRow.set(Tags.Role, WdRoles.WdTR);
		widgetTable.addChild(secondRow);

		WidgetStub secondHeader = new WidgetStub();
		secondHeader.set(Tags.Role, WdRoles.WdTH);
		secondHeader.set(WdTags.WebTextContent, "second_header_content");
		secondRow.addChild(secondHeader);

		WidgetStub secondData = new WidgetStub();
		secondData.set(Tags.Role, WdRoles.WdTD);
		secondData.set(WdTags.WebTextContent, "data_content");
		secondRow.addChild(secondData);

		// Assert the oracle verdict is OK
		List<Verdict> verdicts = oracle.getVerdicts(state);
		Assert.isEquals(1, verdicts.size());
		Verdict verdict = verdicts.get(0);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
		Assert.isTrue(verdict.info().equals("No problem detected."));
	}

	@Test
	public void test_detection_web_invariant_duplicated_rows_multiple_tables() {
		Oracle oracle = loadWorkspaceOracle("WebInvariantDuplicatedRowsInTable");
		StateStub state = new StateStub();

		WidgetStub tableOne = createTable(state, "table1");
		addDuplicatedRowPair(tableOne, "headerA", "dataA");
		addDuplicatedRowPair(tableOne, "headerB", "dataB");

		WidgetStub tableTwo = createTable(state, "table2");
		addDuplicatedRowPair(tableTwo, "headerC", "dataC");
		addDuplicatedRowPair(tableTwo, "headerD", "dataD");

		List<Verdict> verdicts = oracle.getVerdicts(state);
		Assert.isEquals(4, verdicts.size());
		for (Verdict verdict : verdicts) {
			Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
			Assert.isEquals(2, verdict.visualizer().getShapes().size());
		}
	}

	private WidgetStub createTable(StateStub state, String tableId) {
		WidgetStub widgetTable = new WidgetStub();
		widgetTable.set(Tags.Role, WdRoles.WdTABLE);
		widgetTable.set(WdTags.WebId, tableId);
		state.addChild(widgetTable);
		widgetTable.setParent(state);
		return widgetTable;
	}

	private void addDuplicatedRowPair(WidgetStub table, String headerText, String dataText) {
		addRow(table, headerText, dataText);
		addRow(table, headerText, dataText);
	}

	private void addRow(WidgetStub table, String headerText, String dataText) {
		WidgetStub row = new WidgetStub();
		row.set(Tags.Role, WdRoles.WdTR);
		row.set(Tags.Shape, Rect.fromCoordinates(0, 0, 10, 10));
		table.addChild(row);

		WidgetStub header = new WidgetStub();
		header.set(Tags.Role, WdRoles.WdTH);
		header.set(WdTags.WebTextContent, headerText);
		row.addChild(header);

		WidgetStub data = new WidgetStub();
		data.set(Tags.Role, WdRoles.WdTD);
		data.set(WdTags.WebTextContent, dataText);
		row.addChild(data);
	}
}
