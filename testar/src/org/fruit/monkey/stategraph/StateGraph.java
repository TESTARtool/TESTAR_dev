package org.fruit.monkey.stategraph;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StateGraph {
    public class Node {
        private State state;
        private Map<Action, Node> incoming;
        private Map<Action, Node> outgoing;

        Node(State state) {
            this.state = state;
            this.incoming = new HashMap<>();
            this.outgoing = new HashMap<>();
        }

        public State getState() {
            return  state;
        }

        public Map<Action, Node> getIncoming() {
            return incoming;
        }

        public Map<Action, Node> getOutgoing() {
            return outgoing;
        }

        void addEdge(Action action, Node node) {
            node.incoming.put(action, this);
            outgoing.put(action, node);
        }
    }

    private Node root;

    private Map<State, Node> stateMap;
    private Map<Action, Pair<Node, Node>> actionMap;

    public Node getRoot() {
        return root;
    }

    public StateGraph(StateGraph.Node root) {
        this.root = root;
    }

    public Set<Node> getAllNodes() {
        return new HashSet<>(stateMap.values());
    }

    public Node getNode(State state) {
        return stateMap.get(state);
    }

    public Pair<Node, Node> getEdge(Action action) {
        return actionMap.get(action);
    }

    public Node add(Node node, Action action, State state) {
        Node newNode = new Node(state);
        node.addEdge(action, newNode);
        actionMap.put(action, new Pair<>(node, newNode));
        stateMap.put(state, newNode);
        return newNode;
    }


    public void removeEdge(Action action) {
        Pair<Node, Node> edge = actionMap.get(action);
        if (edge != null) {
            actionMap.remove(action);
            Node nodeFrom = edge.left();
            Node nodeTo = edge.right();
            nodeFrom.outgoing.remove(action);
            nodeTo.incoming.remove(action);
            removeIfOrphaned(nodeFrom);
            removeIfOrphaned(nodeTo);
        }
    }

    public void removeNode(State state) {
        Node node = stateMap.get(state);
        if (node != null) {
            stateMap.remove(node);
            for (Map.Entry<Action, Node> incomingEdge: node.incoming.entrySet()) {
                Action action = incomingEdge.getKey();
                actionMap.remove(action);
                Node nodeFrom = incomingEdge.getValue();
                nodeFrom.outgoing.remove(action);
                removeIfOrphaned(nodeFrom);
            }
            for (Map.Entry<Action, Node> outgoingEdge: node.outgoing.entrySet()) {
                Action action = outgoingEdge.getKey();
                actionMap.remove(action);
                Node nodeTo = outgoingEdge.getValue();
                nodeTo.incoming.remove(action);
                removeIfOrphaned(nodeTo);
            }
        }
    }

    private void removeIfOrphaned(Node node) {
        if (node.incoming.isEmpty() && node.outgoing.isEmpty()) {
            removeNode(node.getState());
        }
    }
}
