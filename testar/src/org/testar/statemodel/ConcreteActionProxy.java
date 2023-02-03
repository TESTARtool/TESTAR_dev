package org.testar.statemodel;

/**
 * Simplified non-widget representation of a concrete action
 */
public class ConcreteActionProxy implements IConcreteAction {

  /**
   * The concrete action id.
   */
  private String actionId;

  /**
   * The abstract action that abstracts this concrete action.
   */
  private AbstractAction abstractAction;

  /**
   * Constructor.
   * @param actionId
   */
  public ConcreteActionProxy(String actionId, AbstractAction abstractAction) {
    this.actionId = actionId;
    this.abstractAction = abstractAction;
  }

  public String getActionId() {
    return actionId;
  }

  public AbstractAction getAbstractAction() {
    return abstractAction;
  }
}
