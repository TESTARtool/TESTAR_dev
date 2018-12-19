package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.TypeConvertor;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.VertexEntity;
import nl.ou.testar.StateModel.Persistence.OrientDB.Util.Validation;
import nl.ou.testar.StateModel.Widget;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

public class WidgetHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity target, Object source) throws HydrationException {
        if (!(source instanceof Widget)) {
            throw new HydrationException("Instance of widget expected, " + source.getClass().toString() + " given.");
        }

        // first make sure the identity property is set
        Property identifier = target.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException("No identifying properties were provided for entity class " + target.getEntityClass().getClassName());
        }

        // we are going to combine the identifier for the concrete state and the concrete widget id into one joint identifier.
        String stateId = ((Widget) source).getRootWidget().getId();
        String widgetId = ((Widget) source).getId();
        String uniqueId = stateId + "-" + widgetId;

        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(uniqueId.getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException();
        }
        target.addPropertyValue(identifier.getPropertyName(), identifier.getPropertyType(), uniqueId);

        // loop through the tagged attributes for this state and add them
        TaggableBase attributes = ((Widget) source).getAttributes();
        for (Tag<?> tag :attributes.tags()) {
            // we simply add a property for each tag
            target.addPropertyValue(Validation.sanitizeAttributeName(tag.name()), TypeConvertor.getInstance().getOrientDBType(attributes.get(tag).getClass()), attributes.get(tag));
        }
    }
}
