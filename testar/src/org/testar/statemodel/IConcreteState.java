package org.testar.statemodel;

import org.testar.statemodel.persistence.Persistable;

public interface IConcreteState extends Persistable {
  String getId();
  AbstractState getAbstractState();
  boolean canBeDelayed();
}
