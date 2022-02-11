package org.testar.statemodel.persistence.orientdb.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.exception.HydrationException;
import org.testar.statemodel.persistence.orientdb.Entity.EdgeEntity;
import org.testar.statemodel.persistence.orientdb.Entity.Property;
import org.testar.statemodel.persistence.orientdb.Entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.Entity.TypeConvertor;
import org.testar.statemodel.util.HydrationHelper;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TaggableBase;

public class AbstractActionHydrator implements EntityHydrator<EdgeEntity> {

    @Override
    public void hydrate(EdgeEntity edgeEntity, Object source) throws HydrationException {
        if (!(source instanceof AbstractAction)) {
            throw new HydrationException("Object provided to the abstract action hydrator was not an abstract action");
        }

        // first make sure the identity property is set
        Property identifier = edgeEntity.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException();
        }

        // fetch the abstract level identifier for the current state model
        String modelIdentifier = ((AbstractAction) source).getModelIdentifier();
        edgeEntity.addPropertyValue("modelIdentifier", new PropertyValue(OType.STRING, modelIdentifier));

        // because an abstract action might have multiple concrete actions tied to it, it is possible that the target

        // for the same abstract action might me different states.
        // we combine the ids from the source and target with the abstract action id for this purpose, and also add the abstraction level identifier
        Property sourceIdentifier = edgeEntity.getSourceEntity().getEntityClass().getIdentifier();
        String sourceId = (String)edgeEntity.getSourceEntity().getPropertyValue(sourceIdentifier.getPropertyName()).getValue();
        Property targetIdentifier = edgeEntity.getTargetEntity().getEntityClass().getIdentifier();
        String targetId = (String)edgeEntity.getTargetEntity().getPropertyValue(targetIdentifier.getPropertyName()).getValue();

        String edgeId = HydrationHelper.createOrientDbActionId(sourceId, targetId, ((AbstractAction) source).getActionId(), modelIdentifier);
        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(edgeId.getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException("Wrong type specified for action identifier");
        }
        edgeEntity.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), edgeId));

        // add the action id
        edgeEntity.addPropertyValue("actionId", new PropertyValue(OType.STRING, ((AbstractAction) source).getActionId()));

        // loop through the tagged attributes for this state and add them
        TaggableBase attributes = ((AbstractAction) source).getAttributes();
        for (Tag<?> tag :attributes.tags()) {
            // we simply add a property for each tag
            edgeEntity.addPropertyValue(tag.name(), new PropertyValue(TypeConvertor.getInstance().getOrientDBType(attributes.get(tag).getClass()), attributes.get(tag)));
        }

        // we need to store the concrete action ids that are connected to this abstract action id
        Property concreteActionIds = HydrationHelper.getProperty(edgeEntity.getEntityClass().getProperties(), "concreteActionIds");
        if (concreteActionIds == null) {
            throw new HydrationException();
        }
        if (!((AbstractAction) source).getConcreteActionIds().isEmpty()) {
            edgeEntity.addPropertyValue(concreteActionIds.getPropertyName(), new PropertyValue(concreteActionIds.getPropertyType(), ((AbstractAction) source).getConcreteActionIds()));
        }
    }
}
