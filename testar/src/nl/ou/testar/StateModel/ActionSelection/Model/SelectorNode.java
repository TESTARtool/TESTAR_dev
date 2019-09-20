package nl.ou.testar.StateModel.ActionSelection.Model;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SelectorNode {

    // the abstract state connected to this node
    private AbstractState abstractState;

    // the abstract action connected to this node
    private AbstractAction abstractAction;

    // this node's child nodes
    private List<SelectorNode> children;

    // this node's parent node
    private SelectorNode parent;

    // the tree this node is a part of
    private SelectorTree tree;

    // the depth of this node in the tree
    private int depth;

    /**
     * Constructor
     * @param abstractState
     * @param abstractAction
     */
    public SelectorNode(AbstractState abstractState, AbstractAction abstractAction, int depth, SelectorNode parent) {
        this.abstractState = abstractState;
        this.abstractAction = abstractAction;
        children = new ArrayList<>();
        this.depth = depth;
        this.parent = parent;
    }

    /**
     * This method adds a child node.
     * @param node
     */
    public void addChild(SelectorNode node) {
        children.add(node);
    }

    /**
     * Returns the parent node.
     * @return
     */
    public SelectorNode getParent() {
        return parent;
    }

    /**
     * Retrieve this node's abstract state.
     * @return
     */
    public AbstractState getAbstractState() {
        return abstractState;
    }

    /**
     * Retrieve this node's abstract action.
     * @return
     */
    public AbstractAction getAbstractAction() {
        return abstractAction;
    }

    /**
     * Retrieve this node's children.
     * @return
     */
    public List<SelectorNode> getChildren() {
        return children;
    }

    /**
     * Returns true if this node is the tree's root node.
     * @return
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Returns true if this node is one of the tree
     * @return
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * Set the selector tree this node is attached to.
     * @param tree
     */
    public void setTree(SelectorTree tree) {
        this.tree = tree;
        this.tree.notifyNodeAdded(this);
    }

    /**
     * Retrieves the tree this node is a part of.
     * @return
     */
    public SelectorTree getTree() {
        return tree;
    }

    /**
     * Returns the node's depth in the tree.
     * @return
     */
    public int getDepth() {
        return depth;
    }

    /**
     * THis method returns all the nodes on the path from the root to this node.
     * @return
     */
    public LinkedList<SelectorNode> getNodePath() {
        LinkedList<SelectorNode> nodeList = new LinkedList<>();
        if (parent != null) {
            nodeList.addAll(parent.getNodePath());
        }
        nodeList.add(this);
        return nodeList;
    }

}
