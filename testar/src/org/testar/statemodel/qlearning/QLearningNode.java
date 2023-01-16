package org.testar.statemodel.qlearning;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.ConcreteAction;
import org.testar.statemodel.ConcreteState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QLearningNode {

  private AbstractState abstractState;
  private String concreteStateId;

  protected Map<String, QLearningEdge> incomingEdges;
  protected Map<String, QLearningEdge> outgoingEdges;

  public QLearningNode(AbstractState abstractState, String concreteStateId) {
    this.abstractState = abstractState;
    this.concreteStateId = concreteStateId;
    this.incomingEdges = new HashMap<>();
    this.outgoingEdges = new HashMap<>();
  }

  public AbstractState getAbstractState() {
    return abstractState;
  }

  public String getConcreteStateId() {
    return concreteStateId;
  }

  public Map<String, QLearningEdge> getIncomingEdges() {
    return incomingEdges;
  }

  public Map<String, QLearningEdge> getOutgoingEdges() {
    return outgoingEdges;
  }

  public QLearningEdge addChild(QLearningNode childNode, AbstractAction abstractAction, String concreteActionId) {
    if (!outgoingEdges.containsKey(concreteActionId)) {
      QLearningEdge edge = new QLearningEdge(this, childNode, abstractAction, concreteActionId);
      childNode.outgoingEdges.put(concreteActionId, edge);
      incomingEdges.put(concreteActionId, edge);
      return edge;
    }
    return outgoingEdges.get(concreteActionId);
  }

  public void detach() {
    for (QLearningEdge incomingEdge: incomingEdges.values()) {
      incomingEdge.getSourceNode().outgoingEdges.remove(incomingEdge);
    }
    for (QLearningEdge outgoingEdge : outgoingEdges.values()) {
      outgoingEdge.getTargetNode().incomingEdges.remove(outgoingEdge);
    }

    incomingEdges.clear();
    outgoingEdges.clear();
  }
}
