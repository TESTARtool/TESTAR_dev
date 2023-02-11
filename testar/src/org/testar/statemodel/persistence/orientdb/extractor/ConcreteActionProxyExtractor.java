package org.testar.statemodel.persistence.orientdb.extractor;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.ConcreteActionProxy;
import org.testar.statemodel.exceptions.ExtractionException;
import org.testar.statemodel.persistence.orientdb.entity.DocumentEntity;
import org.testar.statemodel.persistence.orientdb.entity.EdgeEntity;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;

public class ConcreteActionProxyExtractor implements EntityExtractor<ConcreteActionProxy, AbstractStateModel> {

  @Override
  public ConcreteActionProxy extract(DocumentEntity entity, AbstractStateModel model) throws ExtractionException {
    if (!(entity instanceof EdgeEntity)) {
      throw new ExtractionException("Concrete action extractor expects an edge entity. Instance of " + entity.getClass().toString() + " was given.");
    }
    if (!entity.getEntityClass().getClassName().equals("ConcreteAction")) {
      throw new ExtractionException("Entity of class AbstractAction or UnvisitedAbstractAction expected. Class " + entity.getEntityClass().getClassName() + " given.");
    }

    PropertyValue propertyValue;

    // get the action id
    propertyValue = entity.getPropertyValue("actionId");
    if (propertyValue.getType() != OType.STRING) {
      throw new ExtractionException("Expected string value for actionId attribute. Type " + propertyValue.getType().toString() + " given.");
    }
    String concreteActionId = propertyValue.getValue().toString();

    // get the abstract action id
    propertyValue = entity.getPropertyValue("abstractActionId");
    if (propertyValue.getType() != OType.STRING) {
      throw new ExtractionException("Expected string value for abstractActionId attribute. Type " + propertyValue.getType().toString() + " given.");
    }
    String abstractActionId = propertyValue.getValue().toString();
    AbstractAction abstractAction = model.getAbstractAction(abstractActionId);
    if (abstractAction == null) {
      throw new ExtractionException(String.format("A concrete action %s belongs to an abstract action %s that's not (yet) available in the model",
        concreteActionId, abstractActionId));
    }

    return new ConcreteActionProxy(concreteActionId, abstractAction);
  }
}
