package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.fruit.Assert;

/**
 * Representation of a Tgherkin data table.
 * The first row contains column names.
 *
 */
public class DataTable {
  private List<TableRow> tableRows;
  private int index;

  /**
   * DataTable constructor.
   * @param tableRows list of table rows
   */
  public DataTable(List<TableRow> tableRows) {
    Assert.notNull(tableRows);
    this.tableRows = tableRows;
    index = 0;
  }

  /**
   * Set index of current row in data table.
   * @param index current index of row in data table
   */
  public void setIndex(int index) {
    this.index = index;
  }

  /**
   * Retrieve table rows.
   * @return list of table rows
   */
  public List<TableRow> getTableRows() {
    return tableRows;
  }

  /**
   * Retrieve data table head.
   * @return head table row
   */
  public TableRow getHead() {
    return tableRows.get(0);
  }

  /**
   * Retrieve data table tail.
   * @return list of tail table rows
   */
  public List<TableRow> getTail() {
    return tableRows.subList(1, tableRows.size());
  }

  /**
   * Shuffle data rows.
   * Random row as first data row
   */
  public void shuffle() {
    int maxdiI = tableRows.size();
    int diI = ThreadLocalRandom.current().nextInt(1, maxdiI);
    List<TableRow> dummyRows = new ArrayList<TableRow>();
    dummyRows.add(getHead());
    dummyRows.add(tableRows.get(diI));
    for (int i = 1; i < maxdiI; i++) {
      if (i != diI) {
        dummyRows.add(tableRows.get(i));
      }
    }
    tableRows = dummyRows;
  }

  /**
   * Retrieve placeholder value of current table row.
   * @param columnName name of the table column involved
   * @return placeholder value
   */
  public String getPlaceholderValue(String columnName) {
    // retrieve column index based on column name
    return currentTableRow().getTableCells().get(getColumnIndex(columnName)).getValue();
  }

  /**
   * Check whether a placeholder is a column name.
   * @param placeholderName to be checked placeholder name
   * @return true if placeholder name is a column name, otherwise false
   */
  public boolean isColumnName(String placeholderName) {
    return getColumnIndex(placeholderName) != -1;
  }

  /**
   * Check whether more sequences exist.
   * @return true if more sequences exist, otherwise false
   */
  public boolean moreSequences() {
    return hasNextTableRow();
  }

  /**
   * Begin sequence.
   */
  public void beginSequence() {
    nextTableRow();
  }

  /**
   * Reset data table.
   */
  public void reset() {
    index = 0;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (TableRow tableRow: getTableRows()) {
      result.append(tableRow.toString());
    }
    return result.toString();
  }

  private int getColumnIndex(String placeholderName) {
    int columnIndex = 0;
    for (TableCell cell: getHead().getTableCells()) {
      if (cell.getValue().equals(placeholderName)) {
        return columnIndex;
      }
      columnIndex++;
    }
    return -1;
  }

  private boolean hasNextTableRow() {
    return index + 1 < tableRows.size();
  }

  private TableRow nextTableRow() {
    index++;
    return tableRows.get(index);
  }

  private TableRow currentTableRow() {
    if (index < 1 || index >= tableRows.size()) {
      return null;
    }
    return tableRows.get(index);
  }
}
