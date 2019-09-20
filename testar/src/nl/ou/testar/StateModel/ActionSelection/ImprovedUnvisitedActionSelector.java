package nl.ou.testar.StateModel.ActionSelection;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.ActionSelection.Model.SelectorNode;
import nl.ou.testar.StateModel.ActionSelection.Model.SelectorTree;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImprovedUnvisitedActionSelector implements ActionSelector {

    /**
     * A list of actions to execute in order.
     */
    private LinkedList<AbstractAction> executionPath;

    /**
     * Constructor
     */
    public ImprovedUnvisitedActionSelector() {
        executionPath = new LinkedList<>();
    }

    @Override
    public AbstractAction selectAction(AbstractState currentState, AbstractStateModel abstractStateModel) throws ActionNotFoundException {
        // check if we currently have an active execution path that we are on
        if (!executionPath.isEmpty()) {
            // check if the first action is available in the current state
            // if not, our model did not match the situation that Testar encountered during execution
            AbstractAction nextInLine = executionPath.removeFirst();
            if (currentState.getActionIds().contains(nextInLine.getActionId())) {
                return nextInLine;
            }

            // something went wrong, output a message
            System.out.println("Action selection expected to be able to return action with id: " + nextInLine.getActionId() + " , but the flow was altered");
            executionPath = new LinkedList<>();
        }

        // retrieve a new execution path
        SelectorNode rootNode = new SelectorNode(currentState, null, 0, null);
        SelectorTree tree = new SelectorTree(rootNode);
        executionPath = retrieveUnvisitedActions(tree, abstractStateModel, new HashSet<>());

        if (executionPath.isEmpty()) {
            throw new ActionNotFoundException();
        }
        // remove the first action in the execution path
        return executionPath.removeFirst();
    }

    /**
     * This recursive function will perform a breadth first search of the selection tree that will be generated from the current abstract action.
     * @param tree
     * @param abstractStateModel
     * @param visitedStateIds
     * @return
     */
    private LinkedList<AbstractAction> retrieveUnvisitedActions(SelectorTree tree, AbstractStateModel abstractStateModel, Set<String> visitedStateIds) {

        // check if one of the tree's leaves currently has unvisited abstract actions
        // if so, we want to retrieve the entire path of actions that has to be taken to arrive at that unvisited abstract action
        List<LinkedList<SelectorNode>> nodePaths = tree.getLeafNodes().stream().filter(node -> node.getAbstractState() != null && !node.getAbstractState().getUnvisitedActions().isEmpty())
                .map(SelectorNode::getNodePath).collect(Collectors.toList());

        if (!nodePaths.isEmpty()) {
            // we have found some paths to abstract states with unvisited abstract actions
            // order them by the amount of unvisited actions they have, with the highest amount of actions first
            nodePaths.sort((listA, listB) -> {
                int sizeOfA = listA.getLast().getAbstractState().getUnvisitedActions().size();
                int sizeOfB = listB.getLast().getAbstractState().getUnvisitedActions().size();
                return sizeOfB - sizeOfA;
            });

            LinkedList<SelectorNode> nodePath = nodePaths.get(0);
            // collect a random unvisited action and append it to the path we need to traverse to get to the state where
            // it is executable
            Stream<AbstractAction> actionStream = nodePath.stream().map(SelectorNode::getAbstractAction).filter(Objects::nonNull);

            // get a random unvisited action from the last node
            long graphTime = System.currentTimeMillis();
            Random rnd = new Random(graphTime);
            List<AbstractAction> unvisitedActions = new ArrayList<>(nodePath.getLast().getAbstractState().getUnvisitedActions());
            AbstractAction unvisitedAction  = unvisitedActions.get(rnd.nextInt(unvisitedActions.size()));

            // now merge the two streams and return the linkedlist
            return Stream.concat(actionStream, Stream.of(unvisitedAction)).collect(Collectors.toCollection(LinkedList::new));
        }

        // if we made it here, this means the states in the current leaves of our selector tree did not contain
        // anymore unvisited actions. This means we have to follow all the abstract actions from those states
        // to get to the next ring in our breadth first search

        // add the state ids that we have visited to our list
        tree.getLeafNodes().forEach(node -> visitedStateIds.add(node.getAbstractState().getStateId()));
        // check the current depth of the tree
        int currentTreeDepth = tree.getMaxTreeDepth();

        tree.getLeafNodes().forEach(node -> { // fetch all the outgoing transitions from this state
            abstractStateModel.getOutgoingTransitionsForState(node.getAbstractState().getStateId()).forEach(
                    transition -> { // for each transition, check if we have not yet visited the target state
                        if  (!visitedStateIds.contains(transition.getTargetStateId())) {
                            visitedStateIds.add(transition.getTargetStateId());

                            // create a new node and add it
                            SelectorNode childNode = new SelectorNode(transition.getTargetState(), transition.getAction(), node.getDepth() + 1, node);
                            node.addChild(childNode);
                            childNode.setTree(node.getTree());
                        }
                    }
            );
        });

        if (tree.getMaxTreeDepth() == currentTreeDepth) {
            // no more new leaves were added. Return an empty list
            return new LinkedList<>();
        }

        // we were able to gain a new level in our selector tree, so let's try again
        return retrieveUnvisitedActions(tree, abstractStateModel, visitedStateIds);
    }
}
