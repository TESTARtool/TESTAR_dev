package nl.ou.testar.tgherkin.model;

import java.util.Collections;
import java.util.List;

import org.fruit.Assert;



/**
 * Representation of a Tgherkin data table row.
 *
 */
public class TableRow {
    private final List<TableCell> tableCells;

    /**
     * TableRow constructor.
     * @param tableCells list of table cells
     */
    public TableRow(List<TableCell> tableCells) {
    	Assert.notNull(tableCells);
    	this.tableCells = tableCells;
    }

    /**
     * Retrieve table cells.
     * @return list of table cells
     */
    public List<TableCell> getTableCells() {
        return tableCells;
    }

    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	if (getTableCells() != null) {
	    	result.append("|");	    	
    		for (TableCell tableCell : getTableCells()) {
	    		result.append(tableCell.toString());
		    	result.append("|");
	    	}
	    	result.append(System.getProperty("line.separator"));
    	}
    	return result.toString();    	
    }
    
}
