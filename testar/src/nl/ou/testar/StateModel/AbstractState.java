package nl.ou.testar.StateModel.AbstractStateModel;

import java.util.HashSet;
import java.util.Set;

public class AbstractState {

    private String stateId;
    private Set<String> actionIds;
    private Set<String> unvisitedActionIds;

    public AbstractState(String stateId, Set<String> actionIds) {
        this.stateId = stateId;
        this.actionIds = actionIds;
        this.unvisitedActionIds = actionIds; // all are unvisited when creating
    }
}
