package org.testar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testar.action.priorization.ActionTags;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Action;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

/**
 * An action selector that favours compound text input actions, but favours other
 * actions immediately after selecting a compound text action, to reduce the probability that
 * a protocol actually completes and submits forms rather than navigating around between them.
 *
 * This action selector recognizes three types of action, based on tags that should be set
 * in the deriveActions method in the protocol:
 * - Compound text action: an action that enters text in all text widgets (at most one action can be a compound text action).
 * - Low priority actions: actions that are marked by the protocol as low priority after entering text, such
 *   as navigation widgets (the compound text action does not achieve anything if the protocol navigates
 *   to another page before submitting the form data)
 * - High priority actions: actions that are marked by the protocol as high priority after entering text, such
 *   as submit buttons.
 * - Standard actions (every action not tagged as compound text action, low priority action or high priority action)
 *
 * Assumptions that this selector makes:
 * - At most one action is tagged as compound text action
 * - Every action has at most one compound text action selector tag (i.e. compound text, low priority or high priority)
 *
 * The action selection is influenced by several parameters:
 * - The probability of selecting the compound text action is initially set to initialProbability. After the
 *   compound text action is selected, it is set to resetProbability. After each subsequent action it is increased
 *   by growthRate until it reaches initialProbability.
 * - Similarly, the relative priority of low priority actions is affected by initialLowPriorityFactor. The probability
 *   of selecting a low priority factor is initially the same as selecting a standard action if initialLowerPriorityFactor
 *   is 1.0. If initialLowPriorityFactor is less than 1.0, the probability of selecting a low priority action is adjusted
 *   accordingly. The low priority factor is also reset to resetLowPriorityFactor after selecting a compound text action,
 *   and increased by lowPriorityGrowthRate after each subsequent action until it reaches initialLowPriorityFactor again.
 * - The logic for high priority actions works analogously to low priority factor, except that the factor increases after
 *   a compound text action, and then shrinks back to the initial value.
 */

public class CompoundTextActionSelector {

    protected float initialProbability, resetProbability, currentProbability, growthRate;
    protected float initialLowPriorityFactor, resetLowPriorityFactor, currentLowPriorityFactor, lowPriorityGrowthRate;
    protected float initialHighPriorityFactor, resetHighPriorityFactor, currentHighPriorityFactor, highPriorityShrinkRate;
    protected Logger logger;
    protected Random rnd;

    public CompoundTextActionSelector ( float initialProbability, float resetProbability, float growthRate,
                                        float initialLowPriorityFactor, float resetLowPriorityFactor, float lowPriorityGrowthRate,
                                        float initialHighPriorityFactor, float resetHighPriorityFactor, float highPriorityShrinkRate ) {
        // Compound text action settings
        Assert.isTrue(growthRate >= 1.0);
        Assert.isTrue(initialProbability >= resetProbability);
        Assert.isTrue(initialProbability > 0.0 && initialProbability <= 1.0 );
        Assert.isTrue(resetProbability >= 0.0 && resetProbability < 1.0 );
        this.initialProbability = initialProbability;
        this.resetProbability = resetProbability;
        this.growthRate = growthRate;
        this.currentProbability = initialProbability;

        // Low priority action settings
        Assert.isTrue(initialLowPriorityFactor > 0.0 && initialLowPriorityFactor <= 1.0 );
        Assert.isTrue(resetLowPriorityFactor >= 0.0 && resetLowPriorityFactor < 1.0 );
        Assert.isTrue(lowPriorityGrowthRate > 1.0 );
        Assert.isTrue(initialLowPriorityFactor >= resetLowPriorityFactor);
        this.initialLowPriorityFactor = initialLowPriorityFactor;
        this.resetLowPriorityFactor = resetLowPriorityFactor;
        this.lowPriorityGrowthRate = lowPriorityGrowthRate;
        this.currentLowPriorityFactor = initialLowPriorityFactor;

        // High priority action settings
        Assert.isTrue(initialHighPriorityFactor >= 0.0);
        Assert.isTrue(resetHighPriorityFactor >= 0.0 );
        Assert.isTrue(highPriorityShrinkRate >= 0.0 && highPriorityShrinkRate < 1.0 );
        Assert.isTrue(initialHighPriorityFactor <= resetHighPriorityFactor);
        this.initialHighPriorityFactor = initialHighPriorityFactor;
        this.resetHighPriorityFactor = resetHighPriorityFactor;
        this.highPriorityShrinkRate = highPriorityShrinkRate;
        this.currentHighPriorityFactor = initialHighPriorityFactor;

        rnd = new Random(System.currentTimeMillis());
        logger = LogManager.getLogger();
    }

    public Action selectAction(Set<Action> actions) {
        Action selectedAction = null;
        boolean selectedCompoundTextAction = false;

        // Separate compoundTextAction from other actions
        ArrayList<Action> standardActions = new ArrayList();
        ArrayList<Action> lowPriorityActions = new ArrayList();
        ArrayList<Action> highPriorityActions = new ArrayList();
        Action compoundTextAction = null;

        for ( Action action : actions ) {
            if ( action.get(ActionTags.CompoundTextAction, null ) != null ) {
                compoundTextAction = action;
            }
            else if ( action.get(ActionTags.CompoundTextLowPriorityWidget, null ) != null ) {
                lowPriorityActions.add(action);
            }
            else if ( action.get(ActionTags.CompoundTextHighPriorityWidget, null ) != null ) {
                highPriorityActions.add(action);
            }
            else {
                standardActions.add(action);
            }
        }

        float lowPriorityWeight =  lowPriorityActions.size() * currentLowPriorityFactor;
        float highPriorityWeight = highPriorityActions.size() * currentHighPriorityFactor;
        float standardWeight = standardActions.size();
        float totalWeight = lowPriorityWeight + highPriorityWeight + standardWeight;
        float lowPriorityProbability = lowPriorityWeight / totalWeight;
        float highPriorityProbability = highPriorityWeight / totalWeight;
        float standardProbability = standardWeight / totalWeight;


        logger.info("Compound text action probability is now " + String.valueOf(currentProbability));
        logger.info("Probability standard: " + String.valueOf(standardProbability));
        logger.info("Probability low: " + String.valueOf(lowPriorityProbability));
        logger.info("Probability high: " + String.valueOf(highPriorityProbability));

        float prioritySelector = rnd.nextFloat();

        if (compoundTextAction == null ) {
            logger.info("No compound text action found ...");
        }

        if ( compoundTextAction != null && rnd.nextFloat() < currentProbability ) {
            logger.info("Selected compound text action ... ");
            selectedAction =  compoundTextAction;
            selectedCompoundTextAction = true;
        }
        else if ( prioritySelector < lowPriorityProbability ) {
            logger.info("Selected low priority action ... ");
            selectedAction = lowPriorityActions.get(rnd.nextInt(lowPriorityActions.size()));
        }
        else if ( prioritySelector < lowPriorityProbability + highPriorityProbability ) {
            logger.info("Selected high priority action ... ");
            selectedAction = highPriorityActions.get(rnd.nextInt(highPriorityActions.size()));
        }
        else {
            logger.info("Selected standard action ... ");
            selectedAction = standardActions.get(rnd.nextInt(standardActions.size()));
        }

        // Update current compound text action probability and low priority action factor
        if ( selectedCompoundTextAction ) {
            currentProbability = resetProbability;
            currentLowPriorityFactor = resetLowPriorityFactor;
            currentHighPriorityFactor = resetHighPriorityFactor;
        }
        else if ( currentProbability < initialProbability ) {
            currentProbability = Math.max(initialProbability, currentProbability * growthRate );
            currentLowPriorityFactor = Math.max(initialLowPriorityFactor, currentLowPriorityFactor * lowPriorityGrowthRate);
            currentHighPriorityFactor = Math.min(initialHighPriorityFactor, currentHighPriorityFactor * highPriorityShrinkRate);
        }

        return selectedAction;
    }

}