package nl.ou.testar.SimpleGuiStateGraph;

import nl.ou.testar.RandomActionSelector;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GuiStateGraphWithVisitedActions {
    protected Set<IdBasedGuiState> idBasedGuiStates;
    protected String startingStateConcreteId;
    protected String previousStateConcreteId;
    protected String previousActionConcreteId;

    public GuiStateGraphWithVisitedActions() {
        idBasedGuiStates = new HashSet<IdBasedGuiState>();
    }

    public Set<String> getConcreteIdsOfUnvisitedActions(State state){
        return getIdBasedGuiState(state.get(Tags.ConcreteID)).getUnvisitedActionIds();
    }

    //TODO move into a new action selector:
    public Action selectAction(State state, Set<Action> actions){
        // saving the starting node of the graph:
        if(startingStateConcreteId==null){
            startingStateConcreteId=state.get(Tags.ConcreteID);
        }
        // adding state transition to the graph: previous state + previous action = current state
        if(previousStateConcreteId!=null && previousActionConcreteId != null){
            IdBasedGuiState previousState = getIdBasedGuiState(previousStateConcreteId);
            if(previousState==null){
                System.out.println(this.getClass()+": ERROR: GuiStateGraphWithVisitedActions did not find previous state!");
            }else{
                idBasedGuiStates.remove(previousState);
//                System.out.println(this.getClass()+": new state transition: previousStateId="+previousStateConcreteId+", targetStateId="+state.get(Tags.ConcreteID)+", previousActionConcreteId="+previousActionConcreteId);
                previousState.addStateTransition(new GuiStateTransition(previousStateConcreteId,state.get(Tags.ConcreteID),previousActionConcreteId));
                idBasedGuiStates.add(previousState);
            }
        }
        Action returnAction = null;
        IdBasedGuiState currentIdBasedGuiState = getIdBasedGuiState(state.get(Tags.ConcreteID));

        if(currentIdBasedGuiState==null) { // did not contain the state ID -> a new state
            // new state:
//            System.out.println(this.getClass()+": selectAction(): new state");
            currentIdBasedGuiState = createIdBasedGuiState(state, actions);
        }

        //checking if there are state based special instructions (for example for file system window)
        //TODO implement a way to give application specific rules like: if title=="Select one or multiple files", then always select cancel and mark all actions visited

        // the action selection:
        if(currentIdBasedGuiState.getUnvisitedActionIds().size()==actions.size()){
            // all actions unvisited -> new state -> randomly select action:
            System.out.println(this.getClass()+": new state, selecting randomly from "+actions.size()+" available actions");
            returnAction = RandomActionSelector.selectAction(actions);
        }else{
            // already visited state
            if(currentIdBasedGuiState.getUnvisitedActionIds().size()==0){
                System.out.println(this.getClass()+":  all actions visited! Finding the action leading to a state with most unvisited actions");
                String actionId = getConcreteIdOfActionLeadingToStateWithMostUnvisitedActions(currentIdBasedGuiState.getConcreteStateId());
                returnAction =getActionWithConcreteId(actions, actionId);
            }else{
                System.out.println(this.getClass()+": selectAction(): existing state, ID="+currentIdBasedGuiState.getConcreteStateId()+", available action count="+actions.size()+",unvisited action count="+currentIdBasedGuiState.getUnvisitedActionIds().size());
                long graphTime = System.currentTimeMillis();
                Random rnd = new Random(graphTime);
                ArrayList<String> unvisitedActions = new ArrayList<String>(currentIdBasedGuiState.getUnvisitedActionIds());
                String concreteIdOfRandomUnvisitedAction = unvisitedActions.get(rnd.nextInt(unvisitedActions.size()));
//                System.out.println(this.getClass()+": unvisitedAction.size="+unvisitedActions.size()+", random Id="+concreteIdOfRandomUnvisitedAction);
                returnAction = getActionWithConcreteId(actions, concreteIdOfRandomUnvisitedAction);
            }
        }
        if(returnAction==null){
            // backup if action selection did not find an action:
            System.out.println(this.getClass()+": selectAction(): no unvisited actions found! Getting purely random action.");
            returnAction = RandomActionSelector.selectAction(actions);
        }
        //updating the list of states:
        idBasedGuiStates.remove(currentIdBasedGuiState); // should not be a problem if state not there (new state)?
        currentIdBasedGuiState.addVisitedAction(returnAction.get(Tags.ConcreteID));
        idBasedGuiStates.add(currentIdBasedGuiState);
        // saving the state and action for state transition after knowing the target state:
        previousActionConcreteId = returnAction.get(Tags.ConcreteID);
        previousStateConcreteId = state.get(Tags.ConcreteID);
        return returnAction;
    }

    /**
     * returns null if action with given ID is not found
     *
     * @param actions
     * @param concreteActionId
     * @return
     */
    protected Action getActionWithConcreteId(Set<Action> actions, String concreteActionId){
        for(Action action:actions){
            // find the action with concreteId:
            if(action.get(Tags.ConcreteID).equals(concreteActionId)){
                return action;
            }
        }
        return null;
    }

    protected String getConcreteIdOfActionLeadingToStateWithMostUnvisitedActions(String currentStateId){
        IdBasedGuiState currentState = getIdBasedGuiState(currentStateId);
        if(currentState==null || currentState.getUnvisitedActionIds()==null){
            System.out.println(this.getClass()+": ERROR, current state or transitions is null!");
        }
        int numberOfMostUnvisitedActions = 0;
        String returnActionId = null;
        for(GuiStateTransition transition:currentState.getStateTransitions()){
            if(transition==null || transition.getSourceStateConcreteId()==null){
                System.out.println(this.getClass()+": ERROR, transition or source state id is null!");
            }
            if(transition.getSourceStateConcreteId().equals(currentStateId)){
                //source state is the same as current id, as it should be if no errors
                if(transition.getTargetStateConcreteId()==null){
                    System.out.println(this.getClass()+": ERROR, target state ID is null!");
                }
                if(currentState.equals(transition.getTargetStateConcreteId())){
                    // source state id == target state id -> no actual state transition with this action
                    System.out.println(this.getClass()+": not actually a state transition.");
                }else{
                    IdBasedGuiState targetState = getIdBasedGuiState(transition.getTargetStateConcreteId());
                    if(targetState==null){
                        System.out.println(this.getClass()+": ERROR, target state is null!");
                    }
                    if(targetState.getUnvisitedActionIds().size()>numberOfMostUnvisitedActions){
                        numberOfMostUnvisitedActions = targetState.getUnvisitedActionIds().size();
                        System.out.println(this.getClass()+": unvisited actions = "+numberOfMostUnvisitedActions);
                        returnActionId = transition.getActionConcreteId();
                    }
                }
            }else{
                System.out.println(this.getClass()+": ERROR in state transitions, source state id NOT same as current state id!");
            }
        }
        if(returnActionId==null){
            System.out.println(this.getClass()+": all actions in all target states have been visited! TODO:implement more depth");
            //TODO implement recursive search for state with unvisited actions
            return "not found, depth=1";
        }
        return returnActionId;
    }

    /**
     * Returns null if not found
     *
     * @param concreteStateId
     * @return
     */
    protected IdBasedGuiState getIdBasedGuiState(String concreteStateId){
        for(IdBasedGuiState state:idBasedGuiStates){
            if(state.getConcreteStateId().equals(concreteStateId)){
                return state;
            }
        }
        return null;
    }

    protected IdBasedGuiState createIdBasedGuiState(State state, Set<Action> actions){
        Set<String> actionIds = new HashSet<>();
        for(Action action:actions){
            actionIds.add(action.get(Tags.ConcreteID));
        }
        return new IdBasedGuiState(state.get(Tags.ConcreteID),actionIds);
    }

    protected boolean containsStateId(String stateId){
        for(IdBasedGuiState state:idBasedGuiStates){
            if(state.getConcreteStateId().equals(stateId)){
                return true;
            }
        }
        return false;
    }

}
