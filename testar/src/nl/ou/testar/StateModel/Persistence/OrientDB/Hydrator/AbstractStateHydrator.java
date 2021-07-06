package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.PropertyValue;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.TypeConvertor;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.VertexEntity;
import nl.ou.testar.StateModel.Util.HydrationHelper;

import org.fruit.Environment;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

public class AbstractStateHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity target, Object source) throws HydrationException {
        if (!(source instanceof AbstractState)) {
            throw new HydrationException();
        }

        // first make sure the identity property is set
        Property identifier = target.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException("No identifying properties were provided for entity class " + target.getEntityClass().getClassName());
        }

        // add the two identifying properties
        String stateId = null;
        String modelIdentifier = null;
        for (Property prop : target.getEntityClass().getProperties()) {
            if (prop.getPropertyName().equals("stateId")) {
                OType propertyType = TypeConvertor.getInstance().getOrientDBType(((AbstractState) source).getStateId().getClass());
                if (propertyType != prop.getPropertyType()) {
                    throw new HydrationException();
                }
                target.addPropertyValue(prop.getPropertyName(), new PropertyValue(prop.getPropertyType(), ((AbstractState) source).getStateId()));
                stateId = ((AbstractState) source).getStateId();
            }
            else if (prop.getPropertyName().equals("modelIdentifier")) {
                OType propertyType = TypeConvertor.getInstance().getOrientDBType(((AbstractState) source).getModelIdentifier().getClass());
                if (propertyType != prop.getPropertyType()) {
                    throw new HydrationException();
                }
                target.addPropertyValue(prop.getPropertyName(), new PropertyValue(prop.getPropertyType(), ((AbstractState) source).getModelIdentifier()));
                modelIdentifier = ((AbstractState) source).getModelIdentifier();
            }
        }

        // we need to combine the stateId and the abstractionLevelidentifier into a unique id
        if (stateId == null || modelIdentifier == null) {
            throw new HydrationException("Missing identity property in abstract state hydrator");
        }
        String uniqueId = HydrationHelper.lowCollisionID(modelIdentifier + "--" + stateId);
        target.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), uniqueId));

        String nodeName = "unknown";
        try {
          nodeName = System.getenv("HOSTNAME");
        }
        catch (Exception e){}
        target.addPropertyValue("discoveredBy", new PropertyValue(OType.STRING, nodeName));

        // loop through the tagged attributes for this state and add them
        TaggableBase attributes = ((AbstractState) source).getAttributes();
        for (Tag<?> tag :attributes.tags()) {
            // we simply add a property for each tag
            target.addPropertyValue(tag.name(), new PropertyValue(TypeConvertor.getInstance().getOrientDBType(attributes.get(tag).getClass()), attributes.get(tag)));
        }

        // is it an initial, meaning starting, state?
        Property isInitial = HydrationHelper.getProperty(target.getEntityClass().getProperties(), "isInitial");
        if (isInitial == null) {
            throw new HydrationException("Property `isInitial` was not found in abstract state class");
        }
        target.addPropertyValue(isInitial.getPropertyName(), new PropertyValue(isInitial.getPropertyType(), ((AbstractState) source).isInitial()));

        // we need to add the concrete state ids
        Property concreteStateIds = HydrationHelper.getProperty(target.getEntityClass().getProperties(), "concreteStateIds");
        if (concreteStateIds == null) {
            throw new HydrationException();
        }
        
        if (!((AbstractState) source).getConcreteStateIds().isEmpty()) {
            target.addPropertyValue(concreteStateIds.getPropertyName(), new PropertyValue(concreteStateIds.getPropertyType(), ((AbstractState) source).getConcreteStateIds()));
        }

    }


}
