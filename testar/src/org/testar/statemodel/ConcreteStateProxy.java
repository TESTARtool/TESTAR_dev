package org.testar.statemodel;

import org.testar.monkey.alayer.Tag;

import java.util.Set;

public class ConcreteStateProxy implements IConcreteState {

  // a set of tags that was used in creating the concrete state id
  private String id;
  private Set<Tag<?>> tags;

  /**
   * The abstract state that is abstracted from this concrete state.
   */
  private AbstractState abstractState;

  public ConcreteStateProxy(String id, Set<Tag<?>> tags, AbstractState abstractState) {
    this.id = id;
    this.tags = tags;
    this.abstractState = abstractState;
  }

  /**
   * Returns the concrete state ID
   * @return
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the abstract state attached to this concrete state
   * @return
   */
  public AbstractState getAbstractState() {
    return abstractState;
  }

  @Override
  public boolean canBeDelayed() {
    return true;
  }
}
