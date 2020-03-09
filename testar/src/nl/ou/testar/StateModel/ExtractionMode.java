package nl.ou.testar.StateModel;

// enum with the 4 different extraction modes
public enum ExtractionMode {
    SINGLE_STATE("single_state"),
    PREVIOUS_STATE("previous_state"),
    ALL_STATES("all_states"),
    INCOMING_ACTION("incoming action");

    private String label;

    /**
     * Constructor
     * @param label
     */
    ExtractionMode(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
