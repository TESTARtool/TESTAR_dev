package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
//import nl.ou.testar.StateModel.Widget;
import org.fruit.alayer.Widget;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.webdriver.enums.WdTags;

import java.util.*;

public class ABTBasedRewardFunction implements RewardFunction {

    Map<Tag<?>, Object>  attributesInPreviousState = new HashMap<>();
    State previousState = null;

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


        final float reward = diff_state(state, currentAbstractState);

        previousState = state;
        System.out.println("reward determined: " + reward);
        return Float.isNaN(reward) ? 0 : reward;
    }

    private List<Widget> getChildren(State w){
        List<Widget> listWidgets = new ArrayList<>();
        Deque<Widget> queueWidgets = new ArrayDeque<>();
        System.out.println("---- Traversing state ------");
        System.out.println(w.childCount());
        queueWidgets.add(w);

        while (!queueWidgets.isEmpty()) {
            Widget queued = queueWidgets.peek();
            for (int i=0; i < queued.childCount(); i++) {
                listWidgets.add(queued.child(i));
                queueWidgets.add(queued.child(i));
            }
            queueWidgets.remove(queued);
        }
        System.out.println(listWidgets.size());
        return listWidgets;
    }

    public float diff_widget(Widget w1, Widget w2){
        System.out.println("Comparing widget" + w1.get(Tags.Title, "unknown") + " and " + w2.get(Tags.Title, "unknown"));
        final Iterable<Tag<?>>  newTags = w1.tags();
        final Iterable<Tag<?>>  oldTags = w2.tags();
        double diff = 0;
        int newTagsSize = 0;
        int oldTagsSize = 0;

        for (Tag<?> tag: newTags) {
            diff+=attNotEqual(tag,  w1, w2);
            newTagsSize += 1;
        }

        for (Tag<?> tag: oldTags) {
            diff+=attNotEqual(tag,  w2, w1);
            oldTagsSize += 1;
        }

        System.out.println("New: " + diff);
//        System.out.println("Lost: " + noOfLostElements);
        System.out.println("Diff ratio: " + (float) (diff) / (newTagsSize + oldTagsSize));
        System.out.println(newTagsSize);
        System.out.println(oldTagsSize);
        return (float) (diff) / (newTagsSize + oldTagsSize);
    }

    public boolean same_abstract_widget(Widget w1, Widget w2){

        // Temporal
        return w1.get(Tags.AbstractIDCustom).equals(w2.get(Tags.AbstractIDCustom));
    }

    public float diff_state(final State currentConcreteState, final AbstractState currentAbstractState){
        if(previousState==null){
            return 0;
        }
        else{
            float diff = 0;
            System.out.println(currentConcreteState);
            System.out.println(currentConcreteState.childCount());
//            System.out.println(currentConcreteState.getAttributes());
            List<Widget> currentChildChildren = getChildren(currentConcreteState);
            List<Widget> previousChildChildren = getChildren(previousState);
            for(Widget currentWidget : currentChildChildren){
                boolean found_widget = false;
                for(Widget previousWidget : previousChildChildren) {
                    if (same_abstract_widget(currentWidget, previousWidget)){
                        System.out.println(previousWidget.get(Tags.AbstractIDCustom));
                        System.out.println(currentWidget.get(Tags.AbstractIDCustom));
                        System.out.println(previousWidget.get(WdTags.WebTextContent));
                        System.out.println(currentWidget.get(WdTags.WebTextContent));
                        System.out.println(previousWidget.get(WdTags.WebId));
                        System.out.println(currentWidget.get(WdTags.WebId));
                        System.out.println("Same widgets: " + currentWidget.get(Tags.Title, "unknown" + " and " + previousWidget.get(Tags.Title, "unknown")));
                        found_widget = true;
                        diff += diff_widget(currentWidget, previousWidget);
                        break;
                    }
                }
                if(!found_widget){
                    System.out.println("New widget!: " + currentWidget.get(Tags.Title));
                    diff += 1;
                }
            }
            return diff / currentChildChildren.size();
        }
    }

    private int attNotEqual(final Tag<?> key, final Widget w1, final Widget w2) {
        final Object newAttributeObject = w1.get(key, null);
        final Object oldAttributeObject = w2.get(key, null);
        if(!newAttributeObject.equals(oldAttributeObject)){
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
