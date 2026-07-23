package org.testar.oracle.workspace;

import java.util.List;

import org.junit.Test;
import org.testar.core.Assert;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.Oracle;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public class TestWebInvariantDuplicatedRowsInAriaTable extends WorkspaceOracleTestSupport {

    @Test
    public void test_detection_web_invariant_duplicated_rows_div_grid() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantDuplicatedRowsInAriaTable");
        StateStub state = new StateStub();
        WidgetStub grid = createDivGrid(state, "gridid");

        addDivGridHeaderRow(grid, "Training Name", "Partner", "Language", "Number of Seats", "Price");
        addDivGridDataRow(grid, "Advanced Developer Training", "LCSE", "Polish", "8", "EUR 1400");
        addDivGridDataRow(grid, "Advanced Developer Training", "LCSE", "Polish", "8", "EUR 1400");

        List<Verdict> verdicts = oracle.getVerdicts(state);
        Assert.isEquals(1, verdicts.size());

        Verdict verdict = verdicts.get(0);
        Assert.isTrue(verdict.verdictSeverityTitle()
                .equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
        Assert.isTrue(verdict.info().contains(
                "Detected duplicated data row in a table/grid: Advanced Developer Training_LCSE_Polish_8_EUR 1400"));
        Assert.isEquals(2, verdict.visualizer().getShapes().size());
    }

    @Test
    public void test_undetection_web_invariant_unique_rows_div_grid() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantDuplicatedRowsInAriaTable");
        StateStub state = new StateStub();
        WidgetStub grid = createDivGrid(state, "gridid");

        addDivGridHeaderRow(grid, "Training Name", "Partner", "Language", "Number of Seats", "Price");
        addDivGridDataRow(grid, "Advanced Developer Training", "LCSE", "Polish", "8", "EUR 1400");
        addDivGridDataRow(grid, "Intermediate Developer Training", "LCSE", "Polish", "8", "EUR 1300");

        assertOk(oracle.getVerdicts(state));
    }

    @Test
    public void test_header_row_is_not_compared_as_data_div_grid() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantDuplicatedRowsInAriaTable");
        StateStub state = new StateStub();
        WidgetStub grid = createDivGrid(state, "gridid");

        // Two identical header rows must not be treated as duplicated data rows.
        addDivGridHeaderRow(grid, "Training Name", "Partner");
        addDivGridHeaderRow(grid, "Training Name", "Partner");
        addDivGridDataRow(grid, "Advanced Developer Training", "LCSE");

        assertOk(oracle.getVerdicts(state));
    }

    @Test
    public void test_empty_rows_are_ignored_div_grid() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantDuplicatedRowsInAriaTable");
        StateStub state = new StateStub();
        WidgetStub grid = createDivGrid(state, "gridid");

        addDivGridDataRow(grid, "", "");
        addDivGridDataRow(grid, "", "");

        assertOk(oracle.getVerdicts(state));
    }

    @Test
    public void test_detection_web_invariant_duplicated_rows_native_table_regression() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantDuplicatedRowsInAriaTable");
        StateStub state = new StateStub();
        WidgetStub table = createNativeTable(state, "tableid");

        addNativeRow(table, "header_content", "data_content");
        addNativeRow(table, "header_content", "data_content");

        List<Verdict> verdicts = oracle.getVerdicts(state);
        Assert.isEquals(1, verdicts.size());

        Verdict verdict = verdicts.get(0);
        Assert.isTrue(verdict.verdictSeverityTitle()
                .equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
        Assert.isTrue(verdict.info().contains(
                "Detected duplicated data row in a table/grid: header_content_data_content"));
        Assert.isEquals(2, verdict.visualizer().getShapes().size());
    }

    @Test
    public void test_detection_web_invariant_duplicated_rows_multiple_grids() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantDuplicatedRowsInAriaTable");
        StateStub state = new StateStub();

        WidgetStub gridOne = createDivGrid(state, "grid1");
        addDuplicatedDivGridRowPair(gridOne, "rowA", "dataA");
        addDuplicatedDivGridRowPair(gridOne, "rowB", "dataB");

        WidgetStub gridTwo = createDivGrid(state, "grid2");
        addDuplicatedDivGridRowPair(gridTwo, "rowC", "dataC");
        addDuplicatedDivGridRowPair(gridTwo, "rowD", "dataD");

        List<Verdict> verdicts = oracle.getVerdicts(state);
        Assert.isEquals(4, verdicts.size());
        for (Verdict verdict : verdicts) {
            Assert.isTrue(verdict.verdictSeverityTitle()
                    .equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
            Assert.isEquals(2, verdict.visualizer().getShapes().size());
        }
    }

    private WidgetStub createDivGrid(StateStub state, String gridId) {
        WidgetStub grid = new WidgetStub();
        grid.set(Tags.Role, WdRoles.WdDIV);
        grid.set(WdTags.WebAriaRole, "grid");
        grid.set(WdTags.WebId, gridId);
        state.addChild(grid);
        grid.setParent(state);
        return grid;
    }

    private void addDivGridHeaderRow(WidgetStub grid, String... values) {
        WidgetStub row = new WidgetStub();
        row.set(Tags.Role, WdRoles.WdDIV);
        row.set(WdTags.WebAriaRole, "row");
        grid.addChild(row);

        for (String value : values) {
            WidgetStub header = new WidgetStub();
            header.set(Tags.Role, WdRoles.WdDIV);
            header.set(WdTags.WebAriaRole, "columnheader");
            header.set(WdTags.WebTextContent, value);
            row.addChild(header);
        }
    }

    private void addDivGridDataRow(WidgetStub grid, String... values) {
        WidgetStub row = new WidgetStub();
        row.set(Tags.Role, WdRoles.WdDIV);
        row.set(WdTags.WebAriaRole, "row");
        row.set(Tags.Shape, Rect.fromCoordinates(0, 0, 10, 10));
        grid.addChild(row);

        for (String value : values) {
            WidgetStub cell = new WidgetStub();
            cell.set(Tags.Role, WdRoles.WdDIV);
            cell.set(WdTags.WebAriaRole, "gridcell");
            cell.set(WdTags.WebTextContent, value);
            row.addChild(cell);
        }
    }

    private void addDuplicatedDivGridRowPair(WidgetStub grid, String firstValue, String secondValue) {
        addDivGridDataRow(grid, firstValue, secondValue);
        addDivGridDataRow(grid, firstValue, secondValue);
    }

    private WidgetStub createNativeTable(StateStub state, String tableId) {
        WidgetStub table = new WidgetStub();
        table.set(Tags.Role, WdRoles.WdTABLE);
        table.set(WdTags.WebId, tableId);
        state.addChild(table);
        table.setParent(state);
        return table;
    }

    private void addNativeRow(WidgetStub table, String headerText, String dataText) {
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

    private void assertOk(List<Verdict> verdicts) {
        Assert.isEquals(1, verdicts.size());
        Verdict verdict = verdicts.get(0);
        Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
        Assert.isTrue(verdict.info().equals("No problem detected."));
    }
}