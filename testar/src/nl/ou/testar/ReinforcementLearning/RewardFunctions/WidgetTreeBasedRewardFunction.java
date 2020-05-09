package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.fruit.Util;
import org.fruit.alayer.Roles;
import org.fruit.alayer.Widget;

import java.util.HashSet;
import java.util.Set;

import static org.fruit.alayer.Tags.Desc;
import static org.fruit.alayer.Tags.Role;

public class WidgetTreeBasedRewardFunction implements RewardFunction {

    Set<String> actionsInPreviousState = new HashSet<>();

    /**
     * Gets the reward by dividing the number of new actions in a state by the total number of actions
     * @param currentAbstractState The {@link AbstractState} the SUT is in
     * @param executedAction The {@link AbstractAction} that was executed
     * @return The calculated reward
     */
    @Override
    public double getReward(final AbstractState currentAbstractState, final AbstractAction executedAction) {
//        for(Widget widget : state){
//            // indent
//            for(int i = 0; i < Util.depth(widget); i++)
//                System.out.print("  ");
//
//            // print widget info
//            System.out.printf("%s  %s\n",
//                    widget.get(Role, Roles.Widget),
//                    widget.get(Desc, "<desc unavailable>"));
//        }

        final Set<String> actionsInCurrentState = currentAbstractState.getActionIds();
        final Long count = actionsInCurrentState.stream()
                .filter(abstractAction -> !actionsInPreviousState.contains(abstractAction))
                .count();
        actionsInPreviousState = actionsInCurrentState;
        return count.doubleValue() / actionsInCurrentState.size();
    }
}
