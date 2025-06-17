package org.testar.oracles.web.invariants;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;
import org.testar.oracles.OracleSelection;
import org.testar.settings.Settings;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestWebInvariantDuplicatedRowsInTable {

	private static List<Oracle> extendedOraclesList = new ArrayList<>();

	@BeforeClass
	public static void setup_web_invariant_duplicated_rows_table() {
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.ExtendedOracles, "WebInvariantDuplicatedRowsInTable"));

		Settings settings = new Settings(tags, new Properties());

		extendedOraclesList = OracleSelection.loadExtendedOracles(settings.get(ConfigTags.ExtendedOracles));
	}

	@Test
	public void test_detection_web_invariant_duplicated_rows_table() {
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
		firstHeader.set(WdTags.WebTextContent, "header_content");
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
		secondHeader.set(WdTags.WebTextContent, "header_content");
		secondRow.addChild(secondHeader);

		WidgetStub secondData = new WidgetStub();
		secondData.set(Tags.Role, WdRoles.WdTD);
		secondData.set(WdTags.WebTextContent, "data_content");
		secondRow.addChild(secondData);

		// Assert the oracle is correctly loaded
		Assert.isTrue(extendedOraclesList.size() == 1);
		Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantDuplicatedRowsInTable);

		// Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
		Verdict verdict = extendedOraclesList.get(0).getVerdict(state);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
		Assert.isTrue(verdict.info().contains("Detected a duplicated rows in a Table for the widgets: [_header_content_data_content, _header_content_data_content]"));
	}

	@Test
	public void test_undetection_invariant_duplicated_rows_table() {
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

		// Assert the oracle is correctly loaded
		Assert.isTrue(extendedOraclesList.size() == 1);
		Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantDuplicatedRowsInTable);

		// Assert the oracle verdict is OK
		Verdict verdict = extendedOraclesList.get(0).getVerdict(state);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
		Assert.isTrue(verdict.info().equals("No problem detected."));
	}
}
