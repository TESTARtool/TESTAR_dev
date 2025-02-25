/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.statemodel.actionselector.model;

import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;

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
