package org.testar.statemodel.persistence.orientdb.extractor;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.statemodel.*;
import org.testar.statemodel.exceptions.ExtractionException;
import org.testar.statemodel.exceptions.StateModelException;
import org.testar.statemodel.persistence.orientdb.entity.DocumentEntity;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.entity.VertexEntity;

public class ConcreteStateProxyExtractor implements EntityExtractor<ConcreteStateProxy, AbstractStateModel> {
  @Override
  public ConcreteStateProxy extract(DocumentEntity entity, AbstractStateModel model) throws ExtractionException {
    if (!(entity instanceof VertexEntity)) {
      throw new ExtractionException("Abstract state extractor expects a vertex entity. Instance of " + entity.getClass().toString() + " was given.");
    }
    if (!entity.getEntityClass().getClassName().equals("ConcreteState")) {
      throw new ExtractionException("Entity of class ConcreteState expected. Class " + entity.getEntityClass().getClassName() + " given.");
    }

    PropertyValue propertyValue;

    // get the action id
    propertyValue = entity.getPropertyValue("stateId");
    if (propertyValue.getType() != OType.STRING) {
      throw new ExtractionException("Expected string value for ststeId attribute. Type " + propertyValue.getType().toString() + " given.");
    }
    String concreteStateId = propertyValue.getValue().toString();

    // get the abstract action id
    propertyValue = entity.getPropertyValue("abstractStateId");
    if (propertyValue.getType() != OType.STRING) {
      throw new ExtractionException("Expected string value for abstractStateId attribute. Type " + propertyValue.getType().toString() + " given.");
    }
    String abstractStateId = propertyValue.getValue().toString();
    try {
      AbstractState abstractState = model.getAbstractState(abstractStateId);
      if (abstractState == null) {
        throw new ExtractionException(String.format("A concrete state %s belongs to an abstract state %s that's not (yet) available in the model",
          concreteStateId, abstractStateId));
      }
      return new ConcreteStateProxy(concreteStateId, abstractState);
    } catch (StateModelException e) {
      throw new ExtractionException(e.getMessage());
    }
  }
}
