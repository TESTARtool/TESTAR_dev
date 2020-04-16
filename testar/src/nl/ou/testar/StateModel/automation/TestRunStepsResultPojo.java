package nl.ou.testar.StateModel.automation;

public class TestRunStepsResultPojo {

    private int nrOfStepsExecuted;

    private int nrOfAbstractStates;

    private int nrOfAbstractActions;

    private int nrOfConcreteStates;

    private int nrOfConcreteActions;

    private int nrOfUnvisitedActions;

    private int nrOfNonDeterministicActions;

    private String comboIdentifier;

    public int getNrOfStepsExecuted() {
        return nrOfStepsExecuted;
    }

    public void setNrOfStepsExecuted(int nrOfStepsExecuted) {
        this.nrOfStepsExecuted = nrOfStepsExecuted;
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

    public String getComboIdentifier() {
        return comboIdentifier;
    }

    public void setComboIdentifier(String comboIdentifier) {
        this.comboIdentifier = comboIdentifier;
    }

    public int getNrOfNonDeterministicActions() {
        return nrOfNonDeterministicActions;
    }

    public void setNrOfNonDeterministicActions(int nrOfNonDeterministicActions) {
        this.nrOfNonDeterministicActions = nrOfNonDeterministicActions;
    }
}
