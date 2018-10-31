package nl.ou.testar.SimpleGuiStateGraph;

import nl.ou.testar.RandomActionSelector;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class QLearningActionSelector {
    private double R_MAX;
    private double gammaDiscount;
    private GuiStateGraphForQlearning graph;

    public QLearningActionSelector(double R_MAX, double gammaDiscount){
        System.out.println("DEBUG: creating Q-learning action selector, R-MAX="+R_MAX+", gammaDiscount="+gammaDiscount);
        this.R_MAX = R_MAX;
        this.gammaDiscount=gammaDiscount;
        graph = new GuiStateGraphForQlearning(R_MAX,gammaDiscount);
    }

    public Action selectAction(State state, Set<Action> actions) {
        // saving the starting node of the graph:
        if(graph.startingStateConcreteId==null){
            graph.startingStateConcreteId=state.get(Tags.ConcreteID);
        }

        // Finding the current state from the previous states:
        QlearningGuiState currentQlearningGuiState = graph.getStateByConcreteId(state.get(Tags.ConcreteID));

        // If it's a new state:
        if(currentQlearningGuiState==null) { // did not contain the state ID -> a new state
            // new state:
//            System.out.println(this.getClass()+": selectAction(): new state");
            currentQlearningGuiState = graph.createQlearningGuiState(state, actions);
        }
       // for(String id:currentQlearningGuiState.concreteActionIdsAndQValues.keySet()

        // adding state transition to the graph: previous state + previous action = current state
        if(graph.previousStateConcreteId!=null && graph.previousActionConcreteId != null){ //else the first action and there is no transition yet
            QlearningGuiState previousState = graph.getStateByConcreteId(graph.previousStateConcreteId);
            if(previousState==null){
                System.out.println(this.getClass()+": ERROR: GuiStateGraphWithVisitedActions did not find previous state!");
            }else{
                graph.qlearningGuiStates.remove(previousState);//removing the old version of the state
//                System.out.println(this.getClass()+": new state transition: previousStateId="+previousStateConcreteId+", targetStateId="+state.get(Tags.ConcreteID)+", previousActionConcreteId="+previousActionConcreteId);
                previousState.addStateTransition(new GuiStateTransition(graph.previousStateConcreteId,state.get(Tags.ConcreteID),graph.previousActionConcreteId),gammaDiscount,currentQlearningGuiState.getMaxQValueOfTheState());
                graph.qlearningGuiStates.add(previousState);//adding the updated version of the state
            }
        }
        Action returnAction = null;
        ArrayList<String> actionIdsWithMaxQvalue = currentQlearningGuiState.getActionsIdsWithMaxQvalue();
        if(actionIdsWithMaxQvalue.size()==0){
            System.out.println("ERROR: Qlearning did not find actions with max Q value!");
            returnAction = RandomActionSelector.selectAction(actions);
        }else{
            //selecting randomly of the actionIDs that have max Q value:
            long graphTime = System.currentTimeMillis();
            Random rnd = new Random(graphTime);
            String concreteIdOfRandomAction = actionIdsWithMaxQvalue.get(rnd.nextInt(actionIdsWithMaxQvalue.size()));
            returnAction = graph.getActionWithConcreteId(actions, concreteIdOfRandomAction);
        }

        if(returnAction==null){
            // backup if action selection did not find an action:
            System.out.println("ERROR: QlearningActionSelector.selectAction(): no action found! Getting purely random action.");
            returnAction = RandomActionSelector.selectAction(actions);
        }
        //updating the list of states:
        graph.qlearningGuiStates.remove(currentQlearningGuiState); // should not be a problem if state not there (new state)?
        currentQlearningGuiState.addVisitedAction(returnAction.get(Tags.ConcreteID));
        graph.qlearningGuiStates.add(currentQlearningGuiState);
        // saving the state and action for state transition after knowing the target state:
        graph.previousActionConcreteId = returnAction.get(Tags.ConcreteID);
        graph.previousStateConcreteId = state.get(Tags.ConcreteID);
        return returnAction;
    }
}
