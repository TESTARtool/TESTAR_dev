package nl.ou.testar.SimpleGuiStateGraph;

import java.util.HashSet;
import java.util.Set;

public class IdBasedGuiState {
    protected String concreteStateId;
    protected Set<String> concreteActionIds;
    protected Set<String> unvisitedActionIds;
    protected Set<GuiStateTransition> stateTransitions;

    public IdBasedGuiState(String concreteStateId, Set<String> concreteActionIds) {
        this.concreteStateId = concreteStateId;
        this.concreteActionIds = concreteActionIds;
        this.unvisitedActionIds = concreteActionIds; // all are unvisited when creating
        stateTransitions = new HashSet<GuiStateTransition>();
    }

    public void addStateTransition(GuiStateTransition newTransition) {
        if (stateTransitions.size()>0) {
            //if existing transitions, checking for identical ones:
            for (GuiStateTransition guiStateTransition:stateTransitions) {
                if (guiStateTransition.getSourceStateConcreteId().equals(newTransition.getSourceStateConcreteId())) {
                    // the same source state, as it should be:
                    if (guiStateTransition.getActionConcreteId().equals(newTransition.getActionConcreteId())) {
                        // also the action is the same:
                        if (guiStateTransition.getTargetStateConcreteId().equals(newTransition.getTargetStateConcreteId())) {
                            // also the target state is the same -> identical transition
                            System.out.println(this.getClass()+": addStateTransition: identical transition found - no need to save again");
                            return;
                        } else {
                            // same source state and same action, but different target state -> some external factor or the data values affect the behaviour
                            System.out.println(this.getClass()+": addStateTransition: WARNING: same source state, same action, but different target state!");
                        }
                    }
                } else {
                    System.out.println(this.getClass()+": ERROR, source state is NOT same as in other state transitions from the same state!");
                }
            }
        }
        // otherwise adding the new state transition:
//        System.out.println(this.getClass()+": addStateTransition: adding the new state transition");
        stateTransitions.add(newTransition);
    }

    public void addVisitedAction(String concreteActionId) {
        if (unvisitedActionIds.contains(concreteActionId)) {
            System.out.println(this.getClass()+": addVisitedAction: action removed from the unvisited actions");
            unvisitedActionIds.remove(concreteActionId);
        } else {
            System.out.println(this.getClass()+": addVisitedAction: action not found from the unvisited actions");
        }
    }

    public Set<GuiStateTransition> getStateTransitions() {
        return stateTransitions;
    }

    public String getConcreteStateId() {
        return concreteStateId;
    }

    public void setConcreteStateId(String concreteStateId) {
        this.concreteStateId = concreteStateId;
    }

    public Set<String> getConcreteActionIds() {
        return concreteActionIds;
    }

    public void setConcreteActionIds(Set<String> concreteActionIds) {
        this.concreteActionIds = concreteActionIds;
    }

    public Set<String> getUnvisitedActionIds() {
        return unvisitedActionIds;
    }

    public void setUnvisitedActionIds(Set<String> unvisitedActionIds) {
        this.unvisitedActionIds = unvisitedActionIds;
    }
}
