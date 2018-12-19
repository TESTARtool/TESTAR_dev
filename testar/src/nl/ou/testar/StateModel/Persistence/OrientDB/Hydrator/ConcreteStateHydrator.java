package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.ConcreteState;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.TypeConvertor;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.VertexEntity;
import nl.ou.testar.StateModel.Persistence.OrientDB.Util.Validation;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

public class ConcreteStateHydrator implements EntityHydrator<VertexEntity>{

    @Override
    public void hydrate(VertexEntity target, Object source) throws HydrationException {
        if (!(source instanceof ConcreteState)) {
            throw new HydrationException();
        }

        // first make sure the identity property is set
        Property identifier = target.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException("No identifying properties were provided for entity class " + target.getEntityClass().getClassName());
        }

        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(((ConcreteState) source).getId().getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException();
        }
        target.addPropertyValue(identifier.getPropertyName(), identifier.getPropertyType(), ((ConcreteState) source).getId());

        // we need to add a widgetId property, as this is inherited from the orientdb base class
        //@todo this is hardcoded in now to fix an inheritance issue. Ideally, this needs to be solved in the entity classes
        String widgetId = ((ConcreteState) source).getId() + "-" + ((ConcreteState) source).getId();
        target.addPropertyValue("widgetId", OType.STRING, widgetId);

        // loop through the tagged attributes for this state and add them
        TaggableBase attributes = ((ConcreteState) source).getAttributes();
        for (Tag<?> tag :attributes.tags()) {
            // we simply add a property for each tag
            target.addPropertyValue(Validation.sanitizeAttributeName(tag.name()), TypeConvertor.getInstance().getOrientDBType(attributes.get(tag).getClass()), attributes.get(tag));
        }
    }
}
