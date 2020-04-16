package nl.ou.testar.StateModel.automation;

import com.opencsv.bean.CsvBindByName;

public class AutomatedTestRunPojo {

    @CsvBindByName(column = "test_run_id")
    private int testRunId;

    @CsvBindByName(column = "application_id")
    private int applicationId;

    @CsvBindByName(column = "configured_sequences")
    private int configuredSequences;

    @CsvBindByName(column = "configured_steps")
    private int configuredSteps;

    @CsvBindByName(column = "reset_data_store_before_run")
    private String resetDataStoreBeforeRun;

    @CsvBindByName(column = "starting_time_ms")
    private String startingTimeMs;

    @CsvBindByName(column = "ending_time_ms")
    private String endingTimeMs;

    @CsvBindByName(column = "nr_of_steps_executed")
    private int nrOfStepsExecuted;

    @CsvBindByName(column = "deterministic_model")
    private String deterministicModel;

    @CsvBindByName(column = "nr_of_non_deterministic_actions")
    private String nrOfNonDeterministicActions;

    @CsvBindByName(column = "exception_thrown")
    private String exceptionThrown;

    @CsvBindByName(column = "exception_message")
    private String exceptionMessage;

    @CsvBindByName(column = "stack_trace")
    private String strackTrace;

    @CsvBindByName(column = "nr_of_abstract_states_after_run")
    private int nrOfAbstractStates;

    @CsvBindByName(column = "nr_of_abstract_actions_after_run")
    private int nrOfAbstractActions;

    @CsvBindByName(column = "nr_of_concrete_states_after_run")
    private int nrOfConcreteStates;

    @CsvBindByName(column = "nr_of_concrete_actions_after_run")
    private int nrOfConcreteActions;

    @CsvBindByName(column = "nr_of_unvisited_abstract_actions_after_run")
    private int nrOfUnvisitedActions;

    public int getTestRunId() {
        return testRunId;
    }

    public void setTestRunId(int testRunId) {
        this.testRunId = testRunId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getConfiguredSequences() {
        return configuredSequences;
    }

    public void setConfiguredSequences(int configuredSequences) {
        this.configuredSequences = configuredSequences;
    }

    public int getConfiguredSteps() {
        return configuredSteps;
    }

    public void setConfiguredSteps(int configuredSteps) {
        this.configuredSteps = configuredSteps;
    }

    public String getResetDataStoreBeforeRun() {
        return resetDataStoreBeforeRun;
    }

    public void setResetDataStoreBeforeRun(String resetDataStoreBeforeRun) {
        this.resetDataStoreBeforeRun = resetDataStoreBeforeRun;
    }

    public String getStartingTimeMs() {
        return startingTimeMs;
    }

    public void setStartingTimeMs(String startingTimeMs) {
        this.startingTimeMs = startingTimeMs;
    }

    public String getEndingTimeMs() {
        return endingTimeMs;
    }

    public void setEndingTimeMs(String endingTimeMs) {
        this.endingTimeMs = endingTimeMs;
    }

    public int getNrOfStepsExecuted() {
        return nrOfStepsExecuted;
    }

    public void setNrOfStepsExecuted(int nrOfStepsExecuted) {
        this.nrOfStepsExecuted = nrOfStepsExecuted;
    }

    public String getDeterministicModel() {
        return deterministicModel;
    }

    public String getNrOfNonDeterministicActions() {
        return nrOfNonDeterministicActions;
    }

    public void setNrOfNonDeterministicActions(String nrOfNonDeterministicActions) {
        this.nrOfNonDeterministicActions = nrOfNonDeterministicActions;
    }

    public void setDeterministicModel(String deterministicModel) {
        this.deterministicModel = deterministicModel;
    }

    public String getExceptionThrown() {
        return exceptionThrown;
    }

    public void setExceptionThrown(String exceptionThrown) {
        this.exceptionThrown = exceptionThrown;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getStrackTrace() {
        return strackTrace;
    }

    public void setStrackTrace(String strackTrace) {
        this.strackTrace = strackTrace;
    }

    public int getNrOfAbstractStates() {
        return nrOfAbstractStates;
    }

    public void setNrOfAbstractStates(int nrOfAbstractStates) {
        this.nrOfAbstractStates = nrOfAbstractStates;
    }

    public int getNrOfAbstractActions() {
        return nrOfAbstractActions;
    }

    public void setNrOfAbstractActions(int nrOfAbstractActions) {
        this.nrOfAbstractActions = nrOfAbstractActions;
    }

    public int getNrOfConcreteStates() {
        return nrOfConcreteStates;
    }

    public void setNrOfConcreteStates(int nrOfConcreteStates) {
        this.nrOfConcreteStates = nrOfConcreteStates;
    }

    public int getNrOfConcreteActions() {
        return nrOfConcreteActions;
    }

    public void setNrOfConcreteActions(int nrOfConcreteActions) {
        this.nrOfConcreteActions = nrOfConcreteActions;
    }

    public int getNrOfUnvisitedActions() {
        return nrOfUnvisitedActions;
    }

    public void setNrOfUnvisitedActions(int nrOfUnvisitedActions) {
        this.nrOfUnvisitedActions = nrOfUnvisitedActions;
    }
}
