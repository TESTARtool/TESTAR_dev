package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.PropertyValue;
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

        // fetch the root widget, being the concrete state
        ConcreteState concreteState = ((Widget) source).getRootWidget();
        if (concreteState == null) {
            throw new HydrationException("Could not find a concrete state root widget for widget with id " + ((Widget) source).getId());
        }
        // then fetch the abstract state, as it has our model identifier
        AbstractState abstractState = concreteState.getAbstractState();
        if (abstractState == null) {
            throw new HydrationException("No abstract state is connected to the concrete state with id " + concreteState.getId());
        }

        // we are going to combine the identifier for the concrete state and the concrete widget id into one joint identifier.
        String stateId = concreteState.getId();
        String widgetId = ((Widget) source).getId();
        String modelIdentifier = abstractState.getModelIdentifier();
        String uniqueId = modelIdentifier + "-" + stateId + "-" + widgetId;

        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(uniqueId.getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException();
        }
        target.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), uniqueId));

        // loop through the tagged attributes for this state and add them
        TaggableBase attributes = ((Widget) source).getAttributes();
        for (Tag<?> tag :attributes.tags()) {
            // we simply add a property for each tag
            target.addPropertyValue(Validation.sanitizeAttributeName(tag.name()), new PropertyValue(TypeConvertor.getInstance().getOrientDBType(attributes.get(tag).getClass()), attributes.get(tag)));
        }
    }
}
