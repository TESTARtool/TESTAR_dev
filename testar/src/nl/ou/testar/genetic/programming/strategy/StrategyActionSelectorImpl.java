package nl.ou.testar.genetic.programming.strategy;

import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class StrategyActionSelectorImpl implements StrategyActionSelector {

    private StrategyNodeAction strategyTree;
    private StrategyGuiStateImpl stateManager;
    private Date startDate;
    private Date endDate;
    private int currentSequence = 0;
    private Verdict verdict;
    private final Map<Integer, Integer> actionResults = new TreeMap<>();
    private static final Logger logger = LoggerFactory.getLogger(StrategyActionSelector.class);

    StrategyActionSelectorImpl(final StrategyNode strategy) {
        logger.debug("creating genetic programming strategy");
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
        logger.info("The selected action is of type {}", action.get(Tags.Role));
        return action;
    }

    @Override
    public void printMetrics() {
        stateManager.printActionWithTimeExecuted();
        logger.info("Total number of actions {}", stateManager.getTotalNumberOfActions());
        logger.info("Total number of unique actions {}", stateManager.getTotalNumberOfUniqueExecutedActions());
        logger.info("Total number of states visited {}", stateManager.getTotalVisitedStates());
        logger.info("Total number of unique states {}", stateManager.getTotalNumberOfUniqueStates());
        logger.info("Total number of irregular actions {}", stateManager.getNumberOfIrregularActions());
        logger.info("Total number of unavailable actions {}", stateManager.getNumberOfActionsNotFound());
    }

    public void setVerdict(final Verdict verdict) {
        this.verdict = verdict;
    }

    @Override
    public void postExecuteAction() {
        logger.info("POST execution: adding action to map: {}", this.actionResults);
        this.actionResults.put(stateManager.getTotalNumberOfActions(), stateManager.getTotalNumberOfUniqueStates());
    }

    @Override
    public Map<Integer, Integer> getActionMetrics() {
        return this.actionResults;
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
        logger.debug("It took %d seconds to execute {}", this.sequenceDuration());
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
