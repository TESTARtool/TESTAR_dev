package nl.ou.testar.tgherkin.model;

import java.util.Collections;
import java.util.List;

import org.fruit.Assert;

/**
 * Tgherkin DataTable.
 * The first row contains column names.
 *
 */
public class DataTable {
    private final List<TableRow> tableRows;
    private int index; 

    /**
     * DataTable constructor.
     * @param tableRows given list of table rows
     */
    public DataTable(List<TableRow> tableRows) {
    	Assert.notNull(tableRows);
        this.tableRows = Collections.unmodifiableList(tableRows);
        index = 0;
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
     * Retrieve placeholder value of current table row.
     * @param columnName given column name
     * @return placeholder value
     */
    public String getPlaceholderValue(String columnName) {
    	// retrieve column index based on column name 
    	return currentTableRow().getTableCells().get(getColumnIndex(columnName)).getValue();
    }
    
    /**
     * Check whether a placeholder is a column name.
     * @param placeholderName given placeholder name
     * @return boolean true if placeholder is a column name, otherwise false
     */
    public boolean isColumnName(String placeholderName) {
    	return getColumnIndex(placeholderName) != -1;
    }

    /**
	 * Check whether more sequences exist.
	 * @return boolean true if more sequences exist, otherwise false
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
    	for (TableRow tableRow : getTableRows()) {
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
