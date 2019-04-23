package nl.ou.testar.genetic.programming.strategy;

import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.*;

import java.util.Date;
import java.util.Set;

public class StrategyActionSelectorImpl implements StrategyActionSelector {

    private StrategyNodeAction strategyTree;
    private StrategyGuiStateImpl stateManager;
    private Date startDate;
    private Date endDate;
    private int currentSequence = 0;
    private Verdict verdict;

    StrategyActionSelectorImpl(final StrategyNode strategy) {
        System.out.println("DEBUG: creating genetic programming strategy");
        if (strategy instanceof StrategyNodeAction) {
            strategyTree = (StrategyNodeAction) strategy;
        } else {
            throw new RuntimeException("strategy is not of type 'StrategyNodeAction'!");
        }
        stateManager = new StrategyGuiStateImpl();
    }

    @Override
    public void print() {
        strategyTree.print(0);
    }

    @Override
    public Action selectAction(final State state, final Set<Action> actions) {
        stateManager.updateState(state, actions);
        final Action action = strategyTree.getAction(stateManager).orElseGet(() -> this.stateManager.getAlternativeAction());
        this.updateState(action, state);
        System.out.printf("The selected action is of type %s \n", action.get(Tags.Role));

        return action;
    }

    @Override
    public void printMetrics() {
        stateManager.printActionWithTimeExecuted();
        System.out.printf("Total number of actions %d \n", stateManager.getTotalNumberOfActions());
        System.out.printf("Total number of unique actions %d \n", stateManager.getTotalNumberOfUniqueExecutedActions());
        System.out.printf("Total number of states visited %d \n", stateManager.getTotalVisitedStates());
        System.out.printf("Total number of unique states %d \n", stateManager.getTotalNumberOfUniqueStates());
        System.out.printf("Total number of irregular actions %d \n", stateManager.getNumberOfIrregularActions());
        System.out.printf("Total number of unavailable actions %d \n", stateManager.getNumberOfActionsNotFound());
    }

    public void setVerdict(final Verdict verdict) {
        this.verdict = verdict;
    }

    @Override
    public Metric getMetrics() {
        return new Metric(
                this.currentSequence,
                this.sequenceDuration(),
                stateManager.getTotalVisitedStates(),
                stateManager.getTotalNumberOfActions(),
                stateManager.getTotalNumberOfUniqueStates(),
                stateManager.getTotalNumberOfUniqueExecutedActions(),
                stateManager.getNumberOfActionsNotFound(),
                stateManager.getNumberOfIrregularActions(),
                verdict.severity()
        );
    }

    public void printSequenceExecutionDuration() {
        System.out.printf("It took %d seconds to execute \n", this.sequenceDuration());
    }

    private long sequenceDuration() {
        return (endDate.getTime() - startDate.getTime()) / 1000;
    }

    @Override
    public void prepareForSequence() {
        this.startDate = new Date();
        currentSequence++;
    }

    @Override
    public void postSequence() {
        this.endDate = new Date();
    }

    @Override
    public int getCurrentSequence() {
        return this.currentSequence;
    }

    @Override
    public void setTags(final Tag<String> stateTag) {
        stateManager.setStateTag(stateTag);
    }

    @Override
    public void clear() {
        stateManager.clear();
    }

    private void updateState(final Action action, final State state) {
        stateManager.addActionToPreviousActions(action);
        stateManager.addStateToPreviousStates(state);
    }
}
