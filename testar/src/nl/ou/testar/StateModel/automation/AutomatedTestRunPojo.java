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

    @CsvBindByName(column = "exception_thrown")
    private String exceptionThrown;

    @CsvBindByName(column = "exception_message")
    private String exceptionMessage;

    @CsvBindByName(column = "stack_trace")
    private String strackTrace;

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
}
