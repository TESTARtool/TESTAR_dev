package nl.ou.testar.genetic.programming.strategy;

import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.exceptions.NoSuchTagException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Set;

import static org.fruit.monkey.ConfigTags.OutputDir;

public class StrategyActionSelectorImpl implements StrategyActionSelector {

    private StrategyNodeAction strategyTree;
    private StrategyGuiStateImpl stateManager;

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
        final Action action = strategyTree.getAction(stateManager)
                .orElse(stateManager.getRandomAction());
        this.updateState(action, state);
        System.out.printf("The selected action is of type %s \n", action.get(Tags.Role));

        return action;
    }

    @Override
    public void getMetrics() {
        System.out.printf("Total number of actions %d \n", stateManager.getTotalNumberOfActions());
        System.out.printf("Total number of unique actions %d \n", stateManager.getTotalNumberOfUniqueExecutedActions());
        stateManager.printActionWithTimeExecuted();
        System.out.printf("Total number of states visited %d \n", stateManager.getTotalVisitedStates());
        System.out.printf("Total number of unique states %d \n", stateManager.getTotalNumberOfUniqueStates());
    }

    @Override
    public void saveMetrics() {
        try {
            final String filename = this.getFileName();
            final PrintStream ps = this.getPrintStream(filename);
            final String headers = this.getHeaders();
            final String metrics = this.getContent();
            ps.println(headers);
            ps.print(metrics);
            ps.close();
            System.out.println(headers + "\n" + metrics);
        } catch (NoSuchTagException | FileNotFoundException e) {
            LogSerialiser.log("Metrics serialisation exception" + e.getMessage(), LogSerialiser.LogLevel.Critical);
        }
    }

    @Override
    public void setTags(final Tag<String> stateTag, final Tag<String> actionTag) {
        stateManager.setStateTag(stateTag);
        stateManager.setActionTag(actionTag);
    }

    @Override
    public void clear() {
        stateManager.clear();
    }

    private void updateState(final Action action, final State state) {
        stateManager.addActionToPreviousActions(action);
        stateManager.addStateToPreviousStates(state);
    }

    private String getHeaders() {
        return String.format("%1$7s,%2$5s,%3$9s,%4$8s",
                "States",               // # of abstract states visited
                "Actions",              // # of actions executed
                "UniqueStates",         // # of unique states visited
                "UniqueActions"         // # of unique actions executed
        );
    }

    private String getContent() {
        return String.format("%1$7s,%2$5s,%3$9s,%4$9s",
                this.stateManager.getTotalVisitedStates(),
                this.stateManager.getTotalNumberOfActions(),
                this.stateManager.getTotalNumberOfUniqueStates(),
                this.stateManager.getTotalNumberOfUniqueExecutedActions()
        );
    }

    private PrintStream getPrintStream(final String filename) throws FileNotFoundException {
        return new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(
                OutputDir + File.separator + "metrics" + File.separator + filename + ".csv"))));
    }

    private String getFileName() {
        return (System.getProperty("Dcounter") == null) ? "ecj_" + "test" : "ecj_sequence" + System.getProperty("Dcounter");
    }
}
