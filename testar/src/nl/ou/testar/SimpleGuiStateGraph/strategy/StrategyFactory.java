package nl.ou.testar.SimpleGuiStateGraph.strategy;

import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnAnd;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnClickAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnDragAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnDragActionsAvailable;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnEquals;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnEqualsType;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnEscape;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnGreaterThan;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnHitKeyAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnIfThenElse;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnLeftClicksAvailable;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNot;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfActionsOfType;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfDragActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfLeftClicks;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfPreviousActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfTypeActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfUnexecutedDragActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfUnexecutedLeftClicks;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfUnexecutedTypeActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnOr;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnPreviousAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomActionOfType;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomActionOfTypeOtherThan;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomLeastExecutedAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomMostExecutedAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomNumber;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomUnexecutedAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomUnexecutedActionOfType;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnStateHasNotChanged;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnTypeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnTypeActionsAvailable;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnTypeOfActionOf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class StrategyFactory {
    private Queue<AvailableReturnTypes> queue = new LinkedList<>();

    public StrategyFactory(final String strategy) {
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

    public StrategyActionSelector getStrategyActionSelector() {
        final StrategyNode main = getNode();
        StrategyActionSelector selector = new StrategyActionSelectorImpl(main);
        selector.print();
        return selector;
    }

    private StrategyNode getNode() {
        final List<StrategyNode> children = new ArrayList<>();

        switch (Objects.requireNonNull(queue.poll())) {
            case AND:
                children.add(getNode());
                children.add(getNode());
                return new SnAnd(children);
            case CLICKACTION:
                return new SnClickAction(children);
            case DRAGACTION:
                return new SnDragAction(children);
            case DRAGACTIONSAVAILABLE:
                return new SnDragActionsAvailable(children);
            case EQUALS:
                children.add(getNode());
                children.add(getNode());
                return new SnEquals(children);
            case EQUALSTYPE:
                children.add(getNode());
                children.add(getNode());
                return new SnEqualsType(children);
            case ESCAPE:
                return new SnEscape(children);
            case GREATERTHAN:
                children.add(getNode());
                children.add(getNode());
                return new SnGreaterThan(children);
            case HITKEYACTION:
                return new SnHitKeyAction(children);
            case IFTHENELSE:
                children.add(getNode());
                children.add(getNode());
                children.add(getNode());
                return new SnIfThenElse(children);
            case LEFTCLICKSAVAILABLE:
                return new SnLeftClicksAvailable(children);
            case NOT:
                children.add(getNode());
                return new SnNot(children);
            case NUMBEROFACTIONS:
                return new SnNumberOfActions(children);
            case NUMBEROFACTIONSOFTYPE:
                children.add(getNode());
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
                children.add(getNode());
                children.add(getNode());
                return new SnOr(children);
            case PREVIOUSACTION:
                return new SnPreviousAction(children);
            case RANDOMACTION:
                return new SnRandomAction(children);
            case RANDOMACTIONOFTYPE:
                children.add(getNode());
                return new SnRandomActionOfType(children);
            case RANDOMACTIONOFTYPEOTHERTHAN:
                children.add(getNode());
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
                children.add(getNode());
                return new SnRandomUnexecutedActionOfType(children);
            case STATEHASNOTCHANGED:
                return new SnStateHasNotChanged(children);
            case TYPEACTION:
                return new SnTypeAction(children);
            case TYPEACTIONSAVAILABLE:
                return new SnTypeActionsAvailable(children);
            case TYPEOFACTIONOF:
                children.add(getNode());
                return new SnTypeOfActionOf(children);
            default:
                throw new RuntimeException("Action not supported");
        }


    }

    private void makeQueue(final String strategy) {
        Arrays.stream(strategy.replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .split(":"))
                .forEach(s -> queue.add(AvailableReturnTypes.valueOf(s.replace("-", "").toUpperCase())));
    }
}
