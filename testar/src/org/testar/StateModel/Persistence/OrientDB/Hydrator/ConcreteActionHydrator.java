package org.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.StateModel.ConcreteAction;
import org.testar.StateModel.Exception.HydrationException;
import org.testar.StateModel.Persistence.OrientDB.Entity.EdgeEntity;
import org.testar.StateModel.Persistence.OrientDB.Entity.Property;
import org.testar.StateModel.Persistence.OrientDB.Entity.PropertyValue;
import org.testar.StateModel.Persistence.OrientDB.Entity.TypeConvertor;
import org.testar.StateModel.Persistence.OrientDB.Util.Validation;
import org.testar.StateModel.Util.HydrationHelper;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TaggableBase;

public class ConcreteActionHydrator implements EntityHydrator<EdgeEntity> {

    @Override
    public void hydrate(EdgeEntity edgeEntity, Object source) throws HydrationException {
        if (!(source instanceof ConcreteAction)) {
            throw new HydrationException("Object provided to the concrete action hydrator was not a concrete action");
        }

        // first make sure the identity property is set
        Property identifier = edgeEntity.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException();
        }

        // because an abstract action might have multiple concrete actions tied to it, it is possible that the target
        // for the same abstract action might me different states.
        // we combine the ids from the source and target with the abstract action id for this purpose, and also add the abstraction level identifier
        Property sourceIdentifier = edgeEntity.getSourceEntity().getEntityClass().getIdentifier();
        String sourceId = (String)edgeEntity.getSourceEntity().getPropertyValue(sourceIdentifier.getPropertyName()).getValue();
        Property targetIdentifier = edgeEntity.getTargetEntity().getEntityClass().getIdentifier();
        String targetId = (String)edgeEntity.getTargetEntity().getPropertyValue(targetIdentifier.getPropertyName()).getValue();

        String edgeId = HydrationHelper.createOrientDbActionId(sourceId, targetId, ((ConcreteAction) source).getActionId(), null);
        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(edgeId.getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException("Wrong type specified for action identifier");
        }
        edgeEntity.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), edgeId));

        // add the action id
        edgeEntity.addPropertyValue("actionId", new PropertyValue(OType.STRING, ((ConcreteAction) source).getActionId()));

        // loop through the tagged attributes for this state and add them
        TaggableBase attributes = ((ConcreteAction) source).getAttributes();
        for (Tag<?> tag :attributes.tags()) {
            // we simply add a property for each tag
            edgeEntity.addPropertyValue(Validation.sanitizeAttributeName(tag.name()), new PropertyValue(TypeConvertor.getInstance().getOrientDBType(attributes.get(tag).getClass()), attributes.get(tag)));
        }
    }
}
