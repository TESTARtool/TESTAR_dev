package org.testar;

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
 *   as navigation widgets. The intent is that the action selector prioritizes actions that submit the
 *   form or affect non-text inputs of the form by deprioritizing widgets that would make the SUT navigate to another
 *   part of the application without completing/submitting the form.
 * - Standard actions (every action not tagged as compound text action, or low priority action)
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
 *
 */

public class CompoundTextActionSelector {

    protected float initialProbability, resetProbability, currentProbability, growthRate;
    protected float initialLowPriorityFactor, resetLowPriorityFactor, currentLowPriorityFactor, lowPriorityGrowthRate;
    protected Random rnd;

    public CompoundTextActionSelector ( float initialProbability, float resetProbability, float growthRate,
                                        float initialLowPriorityFactor, float resetLowPriorityFactor, float lowPriorityGrowthRate ) {
        Assert.isTrue(growthRate >= 1.0);
        Assert.isTrue(initialProbability >= resetProbability);
        Assert.isTrue(initialProbability > 0.0 && initialProbability <= 1.0 );
        Assert.isTrue(resetProbability >= 0.0 && resetProbability < 1.0 );
        Assert.isTrue(initialLowPriorityFactor > 0.0 && initialLowPriorityFactor <= 1.0 );
        Assert.isTrue(resetLowPriorityFactor >= 0.0 && resetLowPriorityFactor < 1.0 );
        Assert.isTrue(lowPriorityGrowthRate > 1.0 );
        Assert.isTrue(initialLowPriorityFactor >= resetLowPriorityFactor);
        this.initialProbability = initialProbability;
        this.resetProbability = resetProbability;
        this.growthRate = growthRate;
        this.currentProbability = initialProbability;
        this.initialLowPriorityFactor = initialLowPriorityFactor;
        this.resetLowPriorityFactor = resetLowPriorityFactor;
        this.lowPriorityGrowthRate = lowPriorityGrowthRate;
        this.currentLowPriorityFactor = initialLowPriorityFactor;
        rnd = new Random(System.currentTimeMillis());
    }

    public Action selectAction(Set<Action> actions) {
        Action selectedAction = null;
        boolean selectedCompoundTextAction = false;

        // Separate compoundTextAction from other actions
        ArrayList<Action> standardActions = new ArrayList();
        ArrayList<Action> lowPriorityActions = new ArrayList();
        Action compoundTextAction = null;

        for ( Action action : actions ) {
            if ( action.get(ActionTags.CompoundTextAction, null ) != null ) {
                compoundTextAction = action;
            }
            else if ( action.get(ActionTags.CompoundTextLowPriorityWidget, null ) != null ) {
                lowPriorityActions.add(action);
            }
            else {
                standardActions.add(action);
            }
        }

        float baselineLowPriorityActionProbability  = ((float)(lowPriorityActions.size())) / ((float)( lowPriorityActions.size() + standardActions.size()));
        float effectiveLowPriorityActionProbability = baselineLowPriorityActionProbability * currentLowPriorityFactor;

        System.out.println("Compound text action probability is now " + String.valueOf(currentProbability));
        System.out.println("Number of low priority / standard actions: " + String.valueOf(lowPriorityActions.size()) + " / " + String.valueOf(standardActions.size()));
        System.out.println("Baseline low priority action probability is now " + String.valueOf(baselineLowPriorityActionProbability));
        System.out.println("Effective low priority action probability is now " + String.valueOf(effectiveLowPriorityActionProbability));

        if (compoundTextAction == null ) {
            System.out.println("Compound text action not found ...");
        }

        if ( compoundTextAction != null && rnd.nextFloat() < currentProbability ) {
            System.out.println("Selected compound text action ... ");
            selectedAction =  compoundTextAction;
            selectedCompoundTextAction = true;
        }
        else if ( rnd.nextFloat() < effectiveLowPriorityActionProbability ) {
            System.out.println("Selected low priority action ... ");
            selectedAction = lowPriorityActions.get(rnd.nextInt(lowPriorityActions.size()));
        }
        else {
            System.out.println("Selected standard action ... ");
            selectedAction = standardActions.get(rnd.nextInt(standardActions.size()));
        }

        // Update current compound text action probability and low priority action factor
        if ( selectedCompoundTextAction ) {
            currentProbability = resetProbability;
            currentLowPriorityFactor = resetLowPriorityFactor;
        }
        else if ( currentProbability < initialProbability ) {
            currentProbability = Math.max(initialProbability, currentProbability * growthRate );
            currentLowPriorityFactor = Math.max(initialLowPriorityFactor, currentLowPriorityFactor * lowPriorityGrowthRate);
        }

        return selectedAction;
    }

}