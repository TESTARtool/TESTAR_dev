package org.testar.statemodel.reinforcementlearning;

import org.testar.protocols.experiments.WriterExperiments;
import org.testar.protocols.experiments.WriterExperimentsParams;
import org.testar.reinforcementlearning.qfunctions.QFunction;
import org.testar.reinforcementlearning.rewardfunctions.RewardFunction;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.statemodel.exceptions.ActionNotFoundException;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;

import java.util.Set;

public class QLearningModelManager extends RLModelManager implements StateModelManager {

    public QLearningModelManager(AbstractStateModel abstractStateModel, ActionSelector actionSelector, PersistenceManager persistenceManager, Set<Tag<?>> concreteStateTags, SequenceManager sequenceManager, boolean storeWidgets, RewardFunction rewardFunction, QFunction qFunction, Tag<Float> tag) {
        super(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager, storeWidgets, rewardFunction, qFunction, tag);
    }

    @Override
    public Action getAbstractActionToExecute(Set<Action> actions) {
        // First update Q, then select action
        updateQValue(null, actions);
        Action selectedAction = null;
        // set previousActionUnderExecute to current abstractActionToExecute for the next iteration
        try {
            selectedAction = super.getAbstractActionToExecute(actions);
            if(selectedAction != null) {
                previouslyExecutedAbstractAction = currentAbstractState.getAction(selectedAction.get(Tags.AbstractIDCustom, ""));
                previouslyExecutedTestarAction = selectedAction;
            }

            previousTestarActions = actions;
        }
        catch (ActionNotFoundException e){
            logger.debug("Update of previous action failed because: '{}'", e.getMessage());
        }

        return selectedAction;
    }

    protected float calculateQValue(AbstractAction selectedAbstractAction, float reward, Set<Action> actions){
        return qFunction.getQValue(this.tag, previouslyExecutedAbstractAction, selectedAbstractAction, reward, currentAbstractState, actions);
    }

    protected float calculateReward(AbstractAction selectedAbstractAction, Set<Action> actions){
        float reward = rewardFunction.getReward(state, getCurrentConcreteState(), currentAbstractState, previouslyExecutedTestarAction, previouslyExecutedAbstractAction, selectedAbstractAction, actions);
        logger.info("reward={} found for sequenceNumber={} and actionNumber={}", reward,
                getSequenceManager().getCurrentSequenceNr(),
                getSequenceManager().getCurrentSequence().getNodes().size());
        // Write metrics information inside rlRewardMetrics.txt file to be stored in the centralized file server
        final String information = String.format("sequenceNumber | %s | actionNumber | %s | reward | %s | ID | %s",
                getSequenceManager().getCurrentSequenceNr(),
                getSequenceManager().getCurrentSequence().getNodes().size(),
                reward,
                selectedAbstractAction == null ? null : selectedAbstractAction.getId());
        WriterExperiments.writeMetrics(new WriterExperimentsParams.WriterExperimentsParamsBuilder()
                .setFilename("rlRewardMetrics")
                .setInformation(information)
                .build());

        return reward;
    }

    protected void persistQValue(float qValue){
        if(previouslyExecutedAbstractAction != null) {
            previouslyExecutedAbstractAction.addAttribute(tag, qValue);
            System.out.println("qFunction.getClass().getName(): " + qFunction.getClass().getName());
//            if (qFunction.getClass().getName().contains("QBorjaFunction2")) equalizeQValues(qValue, actions);

            //*** FOR DEBUGGING PURPOSES
//            float lastQValue = previouslyExecutedAbstractAction.getAttributes().get(this.tag);
//            qValuesList.add(lastQValue);
//            System.out.println("qValuesList: " + qValuesList);
            //*** FOR DEBUGGING PURPOSES

        }
    }

    /**
     * Update the Q-value for an {@link Action}
     *
     * @param selectedAbstractAction, can be null
     */
    protected void updateQValue(final AbstractAction selectedAbstractAction, final Set<Action> actions) {
        // get reward and Q-value
        System.out.println("UpdateQValue RLModelManager");
        float reward = calculateReward(selectedAbstractAction, actions);
        System.out.println("REWARD: " + Float.toString(reward));
        final float qValue = calculateQValue(selectedAbstractAction, reward, actions);

        // set attribute for saving in the graph database
        persistQValue(qValue);


        //*** FOR DEBUGGING PURPOSES - QBorjaFunction2
//        if (previousAbstractState != null && qFunction.getClass().getName().contains("QBorjaFunction2")) {
//            System.out.println(". . . CURRENT ACTIONS:");
//            for (Action a : actions) {
//                org.testar.monkey.alayer.Widget w = a.get(Tags.OriginWidget);
//                AbstractAction absAction;
//                try {
//                    absAction = currentAbstractState.getAction(a.get(Tags.AbstractIDCustom, ""));
//                    System.out.println(a.get(Tags.OriginWidget).get(Tags.Desc) + ". QValue: "
//                            + absAction.getAttributes().get(RLTags.QBorja, 0f));
//                } catch (ActionNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
        //*** FOR DEBUGGING PURPOSES - QBorjaFunction2

    }

}
