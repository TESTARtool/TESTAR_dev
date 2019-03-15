package nl.ou.testar.tgherkin.model;

import org.fruit.Assert;

/**
 * Representation of a Tgherkin data table element.
 *
 */
public class TableCell{

  private final String value;

    /**
     * TableCell constructor.
     * @param value value contained in the table cell
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
