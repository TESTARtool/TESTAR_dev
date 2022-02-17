package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import nl.ou.testar.StateModel.Widget;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.*;

public class ABTBasedRewardFunction implements RewardFunction {

    Map<Tag<?>, Object>  attributesInPreviousState = new HashMap<>();
    ConcreteState previousState = null;

    /**K
     * Gets the reward by dividing the number of new actions in a state by the total number of actions
     *
     * @param currentConcreteState The {@link ConcreteState} the SUT is in
     * @param currentAbstractState The {@link AbstractState} the SUT is in
     * @param executedAction The {@link AbstractAction} that was executed
     * @param actions
     * @return The calculated reward
     * TODO: Change reward to be calculated from alayer state to reduce size of models
     */
    @Override
    public float getReward(State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final Action executedAction, final AbstractAction executedAbstractAction, final AbstractAction selectedAction, Set<Action> actions) {
        final Map<Tag<?>, Object>  tags = currentConcreteState.getAttributes().getTagValues();
//        System.out.println("\n+++++------------------------------");
//        System.out.println(currentConcreteState.getAttributes().getTagValues());
//        System.out.println("-----------------");
//        System.out.println(currentConcreteState.getChildren().size());
//        System.out.println(currentConcreteState.getId());
//        for (Widget childWidget : currentConcreteState.getChildren()) {
//
//            System.out.println(childWidget);
//            System.out.println(childWidget.getId());
//            System.out.println(childWidget.getAttributes().get(Tags.Title, "default"));
//            System.out.println(childWidget.getChildren().size());
//            for (Widget childWidget2 : childWidget.getChildren()) {
//                System.out.println(childWidget2.getAttributes().get(Tags.Title, "default"));
//            }
//        }
//        System.out.println("*****------------------------------\n");


        final float reward = diff_state(currentConcreteState, currentAbstractState);

        previousState = currentConcreteState;
        System.out.println("reward determined: " + reward);
        return Float.isNaN(reward) ? 0 : reward;
    }

    private List<Widget> getChildren(Widget w){
        List<Widget> listWidgets = new ArrayList<>();
        Deque<Widget> queueWidgets = new ArrayDeque<>();
        System.out.println("---- Traversing state ------");
        System.out.println(w.getChildren().size());
        queueWidgets.add(w);

        while (!queueWidgets.isEmpty()) {
            Widget queued = queueWidgets.peek();
            for (Widget subw : queued.getChildren()) {
                listWidgets.add(subw);
                queueWidgets.add(subw);
            }
            queueWidgets.remove(queued);
        }
        System.out.println(listWidgets.size());
        return listWidgets;
    }

    public float diff_widget(Widget w1, Widget w2){
        System.out.println("Comparing widget" + w1.getAttributes().get(Tags.Title, "unknown") + " and " + w2.getAttributes().get(Tags.Title, "unknown"));
        final Map<Tag<?>, Object>  newTags = w1.getAttributes().getTagValues();
        final Map<Tag<?>, Object>  oldTags = w2.getAttributes().getTagValues();
        double diff = newTags.keySet().stream()
                .mapToInt(key -> attNotEqual(key, newTags, oldTags))
                .sum();
        diff += oldTags.keySet().stream()
                .mapToInt(key -> attNotEqual(key, oldTags, newTags))
                .sum();
        System.out.println("New: " + diff);
//        System.out.println("Lost: " + noOfLostElements);
        System.out.println("Diff ratio: " + (float) (diff) / (newTags.size() + oldTags.size()));
        System.out.println(newTags.size());
        System.out.println(oldTags.size());
        return (float) (diff) / (newTags.size() + oldTags.size());
    }

    public boolean same_abstract_widget(Widget w1, Widget w2){

        // Temporal
        return w1.getAttributes().get(Tags.AbstractIDCustom, null).equals(w2.getAttributes().get(Tags.AbstractIDCustom, null));
    }

    public float diff_state(final ConcreteState currentConcreteState, final AbstractState currentAbstractState){
        if(previousState==null){
            return 0;
        }
        else{
            float diff = 0;
            System.out.println(currentConcreteState);
            System.out.println(currentConcreteState.getChildren());
            System.out.println(currentConcreteState.getAttributes());
            List<Widget> currentChildChildren = getChildren(currentConcreteState);
            List<Widget> previousChildChildren = getChildren(previousState);
            for(Widget currentWidget : currentChildChildren){
                boolean found_widget = false;
                for(Widget previousWidget : previousChildChildren) {
                    if (same_abstract_widget(currentWidget, previousWidget)){
                        System.out.println("Same widgets: " + currentWidget.getAttributes().get(Tags.Title, "unknown" + " and " + previousWidget.getAttributes().get(Tags.Title, "unknown")));
                        found_widget = true;
                        diff += diff_widget(currentWidget, previousWidget);
                        break;
                    }
                }
                if(!found_widget){
                    System.out.println("New widget!: " + currentWidget.getAttributes().get(Tags.Title));
                    diff += 1;
                }
            }
            return diff / currentChildChildren.size();
        }
    }

    private int attNotEqual(final Object key, final Map<Tag<?>, Object> newTagValues, final Map<Tag<?>, Object> OldTagValues) {
        final Object newAttributeObject = newTagValues.getOrDefault(key, null);
        final Object oldAttributeObject = OldTagValues.getOrDefault(key, null);
        if(!OldTagValues.containsKey(key) || !newTagValues.get(key).equals(OldTagValues.get(key))){
            return 1;
        }
        else{
            return 0;
        }
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }
}
