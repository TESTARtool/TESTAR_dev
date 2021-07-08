package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import nl.ou.testar.StateModel.Widget;
import org.apache.commons.math3.analysis.function.Divide;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.HashMap;
import java.util.Map;

public class ABTBasedRewardFunction implements RewardFunction {

    Map<Tag<?>, Object>  attributesInPreviousState = new HashMap<>();
    ConcreteState previousState = null;

    /**K
     * Gets the reward by dividing the number of new actions in a state by the total number of actions
     *
     * @param currentConcreteState The {@link ConcreteState} the SUT is in
     * @param currentAbstractState The {@link AbstractState} the SUT is in
     * @param executedAction The {@link AbstractAction} that was executed
     * @return The calculated reward
     */
    @Override
    public float getReward(State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final AbstractAction executedAction) {
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

    public float diff_widget(Widget w1, Widget w2){
        System.out.println("\n*****------------------------------");
        System.out.println("Comparing widget" + w1.getAttributes().get(Tags.Title, "unknown") + " and " + w2.getAttributes().get(Tags.Title, "unknown"));
        final Map<Tag<?>, Object>  newTags = w1.getAttributes().getTagValues();
        final Map<Tag<?>, Object>  oldTags = w2.getAttributes().getTagValues();
        final double noOfNewElements = newTags.keySet().stream()
                .mapToInt(key -> isAttributeEqualToAttributeInPreviousState(key, newTags, oldTags))
                .sum();
//        final double noOfLostElements = oldTags.keySet().stream()
//                .mapToInt(key -> isAttributeEqualToAttributeInPreviousState(key, newTags, oldTags))
//                .sum();
        System.out.println("New: " + noOfNewElements);
//        System.out.println("Lost: " + noOfLostElements);
        System.out.println("Diff: " + (float) (noOfNewElements) / (newTags.size() ));
        System.out.println(newTags.size());
        System.out.println(oldTags.size());
        return (float) (noOfNewElements) / (newTags.size());
    }

    public boolean same_abstract_widget(Widget w1, Widget w2){

        // Temporal
        return w1.getAttributes().get(Tags.AbstractIDCustom, null).equals(w2.getAttributes().get(Tags.AbstractIDCustom, null));
    }

    public float diff_state(final ConcreteState currentConcreteState, final AbstractState currentAbstractState){
        if(previousState==null && currentConcreteState.getChildren().size() == 1){
            Widget currentChild = currentConcreteState.getChildren().get(0);
            return currentChild.getChildren().size();
        }
        else if(currentConcreteState.getChildren().size() == 1 && previousState.getChildren().size() == 1){
            float diff = 0;
            Widget currentChild = currentConcreteState.getChildren().get(0);
            Widget previousChild = previousState.getChildren().get(0);
            for(Widget currentWidget : currentChild.getChildren()){
                boolean found_widget = false;
                for(Widget previousWidget : previousChild.getChildren()) {
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
            return diff / currentConcreteState.getChildren().size();
        }
        else{
            System.out.println("\nSize not 1");
            System.out.println(previousState==null);
            System.out.println(currentConcreteState.getChildren().size() == 1);
            if(previousState!=null) {
                System.out.println(previousState.getChildren().size() == 1);
            }
        }
        return 0;
    }

    private int isAttributeEqualToAttributeInPreviousState(final Object key, final Map<Tag<?>, Object> newTagValues, final Map<Tag<?>, Object> OldTagValues) {
        final Object newAttributeObject = newTagValues.getOrDefault(key, null);
        final Object oldAttributeObject = OldTagValues.getOrDefault(key, null);

        if (newAttributeObject.equals(oldAttributeObject)) {
            return 0;
        }
        else{
            System.out.println("Tag difference: " + key);
            System.out.println(newAttributeObject);
            System.out.println(oldAttributeObject);
        }

        return 1;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    }
}
