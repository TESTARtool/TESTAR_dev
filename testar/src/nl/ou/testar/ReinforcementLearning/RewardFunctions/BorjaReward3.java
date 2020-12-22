package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import com.google.common.collect.Iterators;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import java.util.Set;

public class BorjaReward3 implements RewardFunction{

    private float numWidgetsBefore;
    private State previousState=null;

    @Override
    public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState, AbstractAction executedAction, Set<Action> actions) {
        System.out.println("Calculating reward");
        float numWidgetsNow = Iterators.size(state.iterator());
        System.out.println("numWidgetsNow ---> " + Float.toString(numWidgetsNow));
        float rewards = 0f;
        float persistentDecrement = getPersistentDecrement(state);
        System.out.println("persistentDecrement ---> " + Float.toString(persistentDecrement));
        System.out.println("numWidgetsBefore ---> " + Float.toString(numWidgetsBefore));
        if (numWidgetsBefore>0f){

            if(numWidgetsBefore < numWidgetsNow) {
                rewards = persistentDecrement - ((numWidgetsNow - numWidgetsBefore) / numWidgetsBefore);
            }
            else if(numWidgetsBefore > numWidgetsNow) {
                rewards = persistentDecrement + (numWidgetsNow / numWidgetsBefore);
            }
            else{
                rewards = persistentDecrement;
            }
        }
        System.out.println("rewards ---> " + Float.toString(rewards));
        numWidgetsBefore = numWidgetsNow;
        previousState = state;
        return rewards;
    }

    // TODO: Is the reward based on the number of widgets or on the number of actions?
    private int getWidNum(Set<Action> actions) {
        int res = 0;
        for(Action a : actions) {
            res ++;
        }
        return res;
    }
    private float getPersistentDecrement(State state) {
        int persistentWidgetNum = 0;

        for(Widget w : state) {
            String wID = w.get(Tags.AbstractIDCustom);
//            if(lastStateWIDList.contains(wID)) {
            if(previousState == null){
                persistentWidgetNum++;
            }
            else {
                for (Widget pw : previousState) {
                    if (w.get(Tags.AbstractIDCustom).equals(pw.get(Tags.AbstractIDCustom)))
                        persistentWidgetNum++;
                    break;
                }
            }
//            }
        }

        float persistentDecrement = persistentWidgetNum * 0.01f;
        return persistentDecrement;
    }
}
