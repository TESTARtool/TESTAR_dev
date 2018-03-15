package nl.ou.testar.tgherkin.model;

import org.fruit.Assert;

/**
 * Tgherkin TableCell.
 *
 */
public class TableCell{

	private final String value;

    /**
     * TableCell constructor.
     * @param value given value
     */
    public TableCell(String value) {
    	Assert.notNull(value);
    	this.value = value;
    }

    /**
     * Retrieve value.
     * @return value
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
    	return getValue();    	
    }
    
}
