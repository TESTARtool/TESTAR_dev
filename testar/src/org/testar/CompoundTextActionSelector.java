package org.testar;

import org.testar.action.priorization.ActionTags;
import org.testar.monkey.alayer.Action;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

/** An action selector that favours compound text actions, but favours other
 * actions immediately after selecting a compound text action
 */

public class CompoundTextActionSelector {

    protected float initialProbability, resetProbability, currentProbability, growthRate;
    protected Random rnd;

    public CompoundTextActionSelector ( float initialProbability, float resetProbability, float growthRate) {
        assert(growthRate > 1.0);
        assert(initialProbability <= resetProbability);
        this.initialProbability = initialProbability;
        this.resetProbability = resetProbability;
        this.growthRate = growthRate;
        this.currentProbability = initialProbability;
        rnd = new Random(System.currentTimeMillis());
    }

    public Action selectAction(Set<Action> actions) {
        Action selectedAction = null;
        boolean selectedCompoundTextAction = false;

        // Separate compoundTextAction from other actions
        ArrayList<Action> otherActions = new ArrayList();
        Action compoundTextAction = null;
        for ( Action action : actions ) {
            if ( action.get(ActionTags.CompoundTextAction, null ) == null ) {
                otherActions.add(action);
            }
            else {
                compoundTextAction = action;
            }
        }

        System.out.println("Compound text action probability is now " + String.valueOf(currentProbability));

        if (compoundTextAction == null ) {
            System.out.println("Compound text action not found ...");
        }

        if ( compoundTextAction != null && rnd.nextFloat() < currentProbability ) {
            System.out.println("Selected compound text action ... ");
            selectedAction =  compoundTextAction;
            selectedCompoundTextAction = true;
        }
        else {
            System.out.println("Selected other action ... ");
            selectedAction = otherActions.get(rnd.nextInt(otherActions.size()));
        }

        // Update current probability
        if ( selectedCompoundTextAction ) {
            currentProbability = resetProbability;
        }
        else if ( currentProbability < initialProbability ) {
            currentProbability = Math.max(initialProbability, currentProbability * growthRate );
        }

        return selectedAction;
    }

}