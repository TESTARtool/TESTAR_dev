package nl.ou.testar.StateModel.ActionSelection;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.ActionSelection.Model.SelectorNode;
import nl.ou.testar.StateModel.ActionSelection.Model.SelectorTree;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImprovedUnvisitedActionSelector implements ActionSelector {

    Logger logger;

    /**
     * A list of actions to execute in order.
     */
    private LinkedList<AbstractAction> executionPath;

    /**
     * The maximum nr of times that the flow can be altered.
     */
    private int maxNrOfAlterations = 5;

    /**
     * The number of times that the flow was altered.
     */
    private int nrOfFlowAlterations;

    /**
     * Constructor
     */
    ImprovedUnvisitedActionSelector(Integer maxNrOfAlterations) {
        executionPath = new LinkedList<>();
        nrOfFlowAlterations = 0;
        logger = LoggerFactory.getLogger(ImprovedUnvisitedActionSelector.class);
        System.out.println("Class: " + ImprovedUnvisitedActionSelector.class);
        if (maxNrOfAlterations != null) {
            this.maxNrOfAlterations = maxNrOfAlterations;
        }
    }

    @Override
    public AbstractAction selectAction(AbstractState currentState, AbstractStateModel abstractStateModel) throws ActionNotFoundException {
        logger.debug("Calling selectionAction");
        logger.debug("stateId: {}", currentState.getStateId());
        logger.debug("nr of visited actions: {}", currentState.getVisitedActions().size());
        logger.debug("nr of unvisited actions: {}", currentState.getUnvisitedActions().size());
        logger.debug("nr of items in execution path: {}", executionPath.size());
        logger.debug("nr of flow alterations: {}", nrOfFlowAlterations);
        // if the flow was altered, this could be because of non-determinism in the model
        // when that is the case, this action selector is not really useful anymore, because it can get stuck in a loop
        // there are several smart ways to fix this, but we opt for an easy one for now:
        // we throw an exception, so the random action selection algorithm can take over
        if (nrOfFlowAlterations >= maxNrOfAlterations) {
            System.out.println("Too many alterations in the flow. Throwing exception.");
            logger.debug("Too many alterations in the flow. Throwing exception.");
            throw new ActionNotFoundException();
        }

        // check if we currently have an active execution path that we are on
        if (!executionPath.isEmpty()) {
            logger.debug("Retrieving action from execution path");
            // check if the first action is available in the current state
            // if not, our model did not match the situation that Testar encountered during execution
            AbstractAction nextInLine = executionPath.removeFirst();
            logger.debug("Retrieved action id: {}", nextInLine.getActionId());
            if (currentState.getActionIds().contains(nextInLine.getActionId())) {
                logger.debug("Returning retrieved action");
                return nextInLine;
            }

            // something went wrong, output a message
            System.out.println("Action selection expected to be able to return action with id: " + nextInLine.getActionId() + " , but the flow was altered");
            logger.debug("Action selection expected to be able to return action with id: " + nextInLine.getActionId() + " , but the flow was altered");
            nrOfFlowAlterations++;
            executionPath = new LinkedList<>();
        }

        // retrieve a new execution path
        logger.debug("Fetching new execution path");
        SelectorNode rootNode = new SelectorNode(currentState, null, 0, null);
        logger.debug("Root node state id: {}", rootNode.getAbstractState().getStateId());
        SelectorTree tree = new SelectorTree(rootNode);
        executionPath = retrieveUnvisitedActions(tree, abstractStateModel, new HashSet<>());

        if (executionPath.isEmpty()) {
            logger.debug("Could not find an execution path.");
            throw new ActionNotFoundException();
        }
        System.out.println("New execution path: " + executionPath.stream().map(AbstractAction::getActionId).reduce("", (base, next) -> base + ", " + next));
        logger.debug("New execution path: " + executionPath.stream().map(AbstractAction::getActionId).reduce("", (base, next) -> base + ", " + next));
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
        logger.debug("Retrieving unvisited actions");
        // check if one of the tree's leaves currently has unvisited abstract actions
        // if so, we want to retrieve the entire path of actions that has to be taken to arrive at that unvisited abstract action
        List<LinkedList<SelectorNode>> nodePaths = tree.getLeafNodes().stream().filter(node -> node.getAbstractState() != null && !node.getAbstractState().getUnvisitedActions().isEmpty())
                .map(SelectorNode::getNodePath).collect(Collectors.toList());
        logger.debug("Nr of nodepaths found: {}", nodePaths.size());

        if (!nodePaths.isEmpty()) {
            // we have found some paths to abstract states with unvisited abstract actions
            // order them by the amount of unvisited actions they have, with the highest amount of actions first
            nodePaths.sort((listA, listB) -> {
                int sizeOfA = listA.getLast().getAbstractState().getUnvisitedActions().size();
                int sizeOfB = listB.getLast().getAbstractState().getUnvisitedActions().size();
                return sizeOfB - sizeOfA;
            });


            LinkedList<SelectorNode> nodePath = nodePaths.get(0);
            logger.debug("First nodepath has {} nodes", nodePath.size());
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

        tree.getLeafNodes().forEach(node -> { // fetch all the outgoing transitions from this state where we have not visited the target state yet
            abstractStateModel.getOutgoingTransitionsForState(node.getAbstractState().getStateId()).stream().filter(transition -> !visitedStateIds.contains(transition.getTargetStateId())).forEach(
                    transition -> {
                        visitedStateIds.add(transition.getTargetStateId());

                        // create a new node and add it
                        SelectorNode childNode = new SelectorNode(transition.getTargetState(), transition.getAction(), node.getDepth() + 1, node);
                        node.addChild(childNode);
                        childNode.setTree(node.getTree());
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
