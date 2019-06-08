package nl.ou.testar.StateModel.Analysis.Representation;

public class TestSequence {

    public TestSequence(String sequenceId, String startDateTime, String numberOfSteps) {
        this.sequenceId = sequenceId;
        this.startDateTime = startDateTime;
        this.numberOfSteps = numberOfSteps;
    }

    /**
     * The id for the test sequence.
     */
    private String sequenceId;

    /**
     * The starting date and time of the sequence.
     */
    private String startDateTime;

    /**
     * The number of steps executed in this sequence.
     */
    private String numberOfSteps;

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getNumberOfSteps() {
        return numberOfSteps;
    }

    public void setNumberOfSteps(String numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }
}
