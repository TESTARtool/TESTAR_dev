package nl.ou.testar.StateModel.ActionSelection.Model;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;

import java.util.ArrayList;
import java.util.List;

public class SelectorTree {

    /**
     * The root node of the tree.
     */
    private SelectorNode rootNode;

    /**
     * A list of nodes that are currently the tree's leaves.
     */
    private List<SelectorNode> leafNodes;

    public SelectorTree(SelectorNode rootNode) {
        this.rootNode = rootNode;
        leafNodes = new ArrayList<>();
        rootNode.setTree(this);
    }

    void notifyNodeAdded(SelectorNode node) {
        if (leafNodes.isEmpty()) {
            leafNodes.add(node);
            return;
        }

        // check if this node is as deep or deeper than the current list of leaves
        int currentTreeDepth = leafNodes.get(0).getDepth();
        if (node.getDepth() == currentTreeDepth) {
            leafNodes.add(node);
        }
        else if (node.getDepth() > currentTreeDepth) {
            // new depth reached. Discard the old leave nodes
            leafNodes = new ArrayList<>();
            leafNodes.add(node);
        }
    }

    /**
     * Returns all the nodes that are currently leaves of the tree.
     * @return
     */
    public List<SelectorNode> getLeafNodes() {
        return leafNodes;
    }

    /**
     * Returns the maximum depth for this tree.
     * @return
     */
    public int getMaxTreeDepth() {
        return leafNodes.get(0).getDepth();
    }

}
