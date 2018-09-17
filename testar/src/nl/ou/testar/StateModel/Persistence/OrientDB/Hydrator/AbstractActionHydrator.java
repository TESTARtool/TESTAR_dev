package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EdgeEntity;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.TypeConvertor;
import nl.ou.testar.StateModel.Util.HydrationHelper;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

public class AbstractActionHydrator implements EntityHydrator<EdgeEntity> {

    @Override
    public void hydrate(EdgeEntity target, Object source) throws HydrationException {
        if (!(source instanceof AbstractAction)) {
            throw new HydrationException("Object provided to the abstract action hydrator was not an abstract action");
        }

        // first make sure the identity property is set
        Property identifier = target.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException();
        }
        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(((AbstractAction) source).getActionId().getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException();
        }
        target.addPropertyValue(identifier.getPropertyName(), identifier.getPropertyType(), ((AbstractAction) source).getActionId());

        // we want to loop through the properties on the entity class
        for (Property property : target.getEntityClass().getProperties()) {
            //@todo for now we assume that the tag name and the property names match
            // ideally we want to add a mapper for this later
            // also, this way of looking for tags is terribly inefficient due to the use of taggablebase,
            // which makes it so we use a loop within a loop basically.
            // we should refactor this at some point in time
            if (property.isReadOnly()) {
                continue;
            }

            Tag tag = HydrationHelper.getTag(((AbstractAction) source).getAttributes(), property.getPropertyName());
            if (tag == null && (property.isMandatory() || !property.isNullable())) {
                throw new HydrationException();
            }
            if (tag != null && ((AbstractAction) source).getAttributes().get(tag) == null && !property.isNullable()) {
                throw new HydrationException();
            }

            if (!HydrationHelper.typesMatch(property, ((AbstractAction) source).getAttributes().get(tag).getClass())) {
                throw new HydrationException();
            }

            target.addPropertyValue(property.getPropertyName(), property.getPropertyType(), ((AbstractAction) source).getAttributes().get(tag));
        }

        // now the reverse for those state attributes that are not yet set on the entity, meaning they are not part of
        // the orientdb schema, which is perfectly fine
        TaggableBase attributes = ((AbstractAction) source).getAttributes();
        for (Tag<?> tag :attributes.tags()) {
            if (HydrationHelper.getProperty(target.getEntityClass().getProperties(), tag.name()) != null) {
                // skip, we already processed this one
                continue;
            }

            // we simply add a property for each tag
            target.addPropertyValue(tag.name(), TypeConvertor.getInstance().getOrientDBType(attributes.get(tag).getClass()), attributes.get(tag));
        }

        // we need to store the concrete action ids that are connected to this abstract action id
        Property concreteActionIds = HydrationHelper.getProperty(target.getEntityClass().getProperties(), "concreteActionIds");
        if (concreteActionIds == null) {
            throw new HydrationException();
        }
        if (!((AbstractAction) source).getConcreteActionIds().isEmpty()) {
            target.addPropertyValue(concreteActionIds.getPropertyName(), concreteActionIds.getPropertyType(), ((AbstractAction) source).getConcreteActionIds());
        }
    }
}
