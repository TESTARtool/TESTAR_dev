package nl.ou.testar.genetic.programming.strategy;

import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actions.SnAnd;
import nl.ou.testar.genetic.programming.strategy.actions.SnClickAction;
import nl.ou.testar.genetic.programming.strategy.actions.SnDragAction;
import nl.ou.testar.genetic.programming.strategy.actions.SnDragActionsAvailable;
import nl.ou.testar.genetic.programming.strategy.actions.SnEquals;
import nl.ou.testar.genetic.programming.strategy.actions.SnEqualsType;
import nl.ou.testar.genetic.programming.strategy.actions.SnEscape;
import nl.ou.testar.genetic.programming.strategy.actions.SnGreaterThan;
import nl.ou.testar.genetic.programming.strategy.actions.SnHitKeyAction;
import nl.ou.testar.genetic.programming.strategy.actions.SnIfThenElse;
import nl.ou.testar.genetic.programming.strategy.actions.SnLeftClicksAvailable;
import nl.ou.testar.genetic.programming.strategy.actions.SnNot;
import nl.ou.testar.genetic.programming.strategy.actions.SnNumberOfActions;
import nl.ou.testar.genetic.programming.strategy.actions.SnNumberOfActionsOfType;
import nl.ou.testar.genetic.programming.strategy.actions.SnNumberOfDragActions;
import nl.ou.testar.genetic.programming.strategy.actions.SnNumberOfLeftClicks;
import nl.ou.testar.genetic.programming.strategy.actions.SnNumberOfPreviousActions;
import nl.ou.testar.genetic.programming.strategy.actions.SnNumberOfTypeActions;
import nl.ou.testar.genetic.programming.strategy.actions.SnNumberOfUnexecutedDragActions;
import nl.ou.testar.genetic.programming.strategy.actions.SnNumberOfUnexecutedLeftClicks;
import nl.ou.testar.genetic.programming.strategy.actions.SnNumberOfUnexecutedTypeActions;
import nl.ou.testar.genetic.programming.strategy.actions.SnOr;
import nl.ou.testar.genetic.programming.strategy.actions.SnPreviousAction;
import nl.ou.testar.genetic.programming.strategy.actions.SnRandomAction;
import nl.ou.testar.genetic.programming.strategy.actions.SnRandomActionOfType;
import nl.ou.testar.genetic.programming.strategy.actions.SnRandomActionOfTypeOtherThan;
import nl.ou.testar.genetic.programming.strategy.actions.SnRandomLeastExecutedAction;
import nl.ou.testar.genetic.programming.strategy.actions.SnRandomMostExecutedAction;
import nl.ou.testar.genetic.programming.strategy.actions.SnRandomNumber;
import nl.ou.testar.genetic.programming.strategy.actions.SnRandomUnexecutedAction;
import nl.ou.testar.genetic.programming.strategy.actions.SnRandomUnexecutedActionOfType;
import nl.ou.testar.genetic.programming.strategy.actions.SnStateHasNotChanged;
import nl.ou.testar.genetic.programming.strategy.actions.SnTypeAction;
import nl.ou.testar.genetic.programming.strategy.actions.SnTypeActionsAvailable;
import nl.ou.testar.genetic.programming.strategy.actions.SnTypeOfActionOf;
import org.fruit.alayer.exceptions.NoSuchTagException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Stream;

import static org.fruit.monkey.ConfigTags.OutputDir;

public class StrategyFactoryImpl implements StrategyFactory {
    private Queue<AvailableReturnTypes> queue = new LinkedList<>();
    private StrategyActionSelector strategyActionSelector;

    public StrategyFactoryImpl(final String strategy) {
        if (strategy.endsWith(".txt")) {
            makeQueue(getStrategyFromFile(strategy));
        } else {
            System.out.println("Strategy: " + strategy);
            makeQueue(strategy);
        }
    }

    private String getStrategyFromFile(String strategyFile) {
        String strategyFromFile = "";
        try {
            final BufferedReader br = new BufferedReader(new FileReader("settings/" + strategyFile));
            strategyFromFile = br.readLine();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("File not found. Current directory: " + System.getProperty("user.dir"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Strategy from file: " + strategyFromFile);
        return strategyFromFile;
    }

    public String[] getTextInputsFromFile(final String inputFile) {
        try (final Stream<String> lines = Files.lines(new File(inputFile).toPath())) {
            return lines.filter(line -> !line.startsWith("#") && !line.isEmpty()).toArray(String[]::new);
        } catch (IOException e) {
            System.out.println("Error while reading text input file!");
        }
        throw new RuntimeException("The content of the input file seems to be corrupt!");
    }

    @Override
    public void saveMetrics() {
        this.strategyActionSelector.saveMetrics();
    }

    @Override
    public StrategyActionSelector getStrategyActionSelector() {
        final StrategyNode main = getStrategyNode();
        this.strategyActionSelector = new StrategyActionSelectorImpl(main);
        this.strategyActionSelector.print();
        return this.strategyActionSelector;
    }

    public void printMetrics() {
        this.strategyActionSelector.getMetrics();
    }

    private void makeQueue(final String strategy) {
        Arrays.stream(strategy.replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .split(":"))
                .forEach(s -> queue.add(AvailableReturnTypes.valueOf(s.replace("-", "").toUpperCase())));
    }

    private StrategyNode getStrategyNode() {
        final List<StrategyNode> children = new ArrayList<>();

        switch (Objects.requireNonNull(queue.poll())) {
            case AND:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnAnd(children);
            case CLICKACTION:
                return new SnClickAction(children);
            case DRAGACTION:
                return new SnDragAction(children);
            case DRAGACTIONSAVAILABLE:
                return new SnDragActionsAvailable(children);
            case EQUALS:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnEquals(children);
            case EQUALSTYPE:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnEqualsType(children);
            case ESCAPE:
                return new SnEscape(children);
            case GREATERTHAN:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnGreaterThan(children);
            case HITKEYACTION:
                return new SnHitKeyAction(children);
            case IFTHENELSE:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnIfThenElse(children);
            case LEFTCLICKSAVAILABLE:
                return new SnLeftClicksAvailable(children);
            case NOT:
                children.add(getStrategyNode());
                return new SnNot(children);
            case NUMBEROFACTIONS:
                return new SnNumberOfActions(children);
            case NUMBEROFACTIONSOFTYPE:
                children.add(getStrategyNode());
                return new SnNumberOfActionsOfType(children);
            case NUMBEROFDRAGACTIONS:
                return new SnNumberOfDragActions(children);
            case NUMBEROFLEFTCLICKS:
                return new SnNumberOfLeftClicks(children);
            case NUMBEROFPREVIOUSACTIONS:
                return new SnNumberOfPreviousActions(children);
            case NUMBEROFTYPEACTIONS:
                return new SnNumberOfTypeActions(children);
            case NUMBEROFUNEXECUTEDDRAGACTIONS:
                return new SnNumberOfUnexecutedDragActions(children);
            case NUMBEROFUNEXECUTEDLEFTCLICKS:
                return new SnNumberOfUnexecutedLeftClicks(children);
            case NUMBEROFUNEXECUTEDTYPEACTIONS:
                return new SnNumberOfUnexecutedTypeActions(children);
            case OR:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnOr(children);
            case PREVIOUSACTION:
                return new SnPreviousAction(children);
            case RANDOMACTION:
                return new SnRandomAction(children);
            case RANDOMACTIONOFTYPE:
                children.add(getStrategyNode());
                return new SnRandomActionOfType(children);
            case RANDOMACTIONOFTYPEOTHERTHAN:
                children.add(getStrategyNode());
                return new SnRandomActionOfTypeOtherThan(children);
            case RANDOMLEASTEXECUTEDACTION:
                return new SnRandomLeastExecutedAction(children);
            case RANDOMMOSTEXECUTEDACTION:
                return new SnRandomMostExecutedAction(children);
            case RANDOMNUMBER:
                return new SnRandomNumber(children);
            case RANDOMUNEXECUTEDACTION:
                return new SnRandomUnexecutedAction(children);
            case RANDOMUNEXECUTEDACTIONOFTYPE:
                children.add(getStrategyNode());
                return new SnRandomUnexecutedActionOfType(children);
            case STATEHASNOTCHANGED:
                return new SnStateHasNotChanged(children);
            case TYPEACTION:
                return new SnTypeAction(children);
            case TYPEACTIONSAVAILABLE:
                return new SnTypeActionsAvailable(children);
            case TYPEOFACTIONOF:
                children.add(getStrategyNode());
                return new SnTypeOfActionOf(children);
            default:
                throw new RuntimeException("Action not supported");
        }
    }
}
