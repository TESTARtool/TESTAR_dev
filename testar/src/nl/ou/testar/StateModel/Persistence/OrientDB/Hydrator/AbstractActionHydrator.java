package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EdgeEntity;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.PropertyValue;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.TypeConvertor;
import nl.ou.testar.StateModel.Util.HydrationHelper;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

import java.util.Set;

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
        String abstractionLevelIdentifier = ((AbstractAction) source).getAbstractionLevelIdentifier();
        edgeEntity.addPropertyValue("abstractionLevelIdentifier", new PropertyValue(OType.STRING, abstractionLevelIdentifier));

        // because an abstract action might have multiple concrete actions tied to it, it is possible that the target

        // for the same abstract action might me different states.
        // we combine the ids from the source and target with the abstract action id for this purpose, and also add the abstraction level identifier
        Property sourceIdentifier = edgeEntity.getSourceEntity().getEntityClass().getIdentifier();
        String sourceId = (String)edgeEntity.getSourceEntity().getPropertyValue(sourceIdentifier.getPropertyName()).getValue();
        Property targetIdentifier = edgeEntity.getTargetEntity().getEntityClass().getIdentifier();
        String targetId = (String)edgeEntity.getTargetEntity().getPropertyValue(targetIdentifier.getPropertyName()).getValue();

        String edgeId = HydrationHelper.createOrientDbActionId(sourceId, targetId, ((AbstractAction) source).getActionId(), abstractionLevelIdentifier);
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
