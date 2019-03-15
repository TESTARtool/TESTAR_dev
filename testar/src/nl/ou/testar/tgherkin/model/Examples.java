package nl.ou.testar.tgherkin.model;

import org.fruit.Assert;

/**
 * Representation of a Tgherkin examples entity that is a container for a data table.
 *
 */
public class Examples {

  private final String title;
  private final String narrative;
  private DataTable dataTable;

  /**
  * Examples constructor.
  * @param title summary description
  * @param narrative detailed description
  * @param dataTable data table that can be referenced by placeholders
  */
  public Examples(String title, String narrative, DataTable dataTable) {
    Assert.notNull(dataTable);
    this.title = title;
    this.narrative = narrative;
    this.dataTable = dataTable;
  }

  /**
  * Retrieve title.
  * @return title
  */
  public String getTitle() {
    return title;
  }

  /**
  * Retrieve narrative.
  * @return narrative
  */
  public String getNarrative() {
    return narrative;
  }

  /**
  * Retrieve data table.
  * @return data table
  */
  public DataTable getDataTable() {
    return dataTable;
  }

  /**
   * Set data table.
   * @param dataTable data table
   */
  public void setDataTable(DataTable dataTable) {
      this.dataTable = dataTable;
  }

  /**
  * Check whether more sequences exist.
  * @return true if more sequences exist, otherwise false
  */
  public boolean moreSequences() {
    return getDataTable().moreSequences();
  }

  /**
  * Begin sequence.
  */
  public void beginSequence() {
    getDataTable().beginSequence();
  }

  /**
  * Reset examples.
  */
  public void reset() {
    getDataTable().reset();
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    // keyword
    result.append(getClass().getSimpleName());
    result.append(":");
    if (getTitle() != null) {
      result.append(getTitle());
    }
    result.append(System.getProperty("line.separator"));
    if (getNarrative() != null) {
      result.append(getNarrative());
      result.append(System.getProperty("line.separator"));
    }
    if (getDataTable() != null) {
      result.append(getDataTable().toString());
    }
    return result.toString();
  }
}
