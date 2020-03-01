package nl.ou.testar.StateModel.Persistence.OrientDB.Stats;

public class ModelStats {

    private int nrOfAbstractStates = 0;

    private int nrOfAbstractActions = 0;

    private int nrOfConcreteStates = 0;

    private int nrOfConcreteActions = 0;

    private int nrOfUnvisitedActions = 0;

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
