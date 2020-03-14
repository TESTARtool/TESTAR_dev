package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.*;
import nl.ou.testar.StateModel.Util.HydrationHelper;
import org.fruit.alayer.Tag;

import java.util.HashSet;

public class AbstractStateModelHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity vertexEntity, Object source) throws HydrationException {
        if (!(source instanceof AbstractStateModel)) {
            throw new HydrationException("Invalid source object was provided to AbstractStateModel hydrator");
        }

        // first make sure the identity property is set
        Property identifier = vertexEntity.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException();
        }

        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(((AbstractStateModel) source).getModelIdentifier().getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException();
        }
        vertexEntity.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), ((AbstractStateModel) source).getModelIdentifier()));

        // add the tags that were used in the creation of the unique states of the state model
        Property abstractionAttributes = HydrationHelper.getProperty(vertexEntity.getEntityClass().getProperties(), "abstractionAttributes");
        if (abstractionAttributes == null) {
            throw new HydrationException();
        }
        HashSet<String> attributeSet = new HashSet<>();
        for (Tag<?> tag : ((AbstractStateModel) source).getTags()) {
            attributeSet.add(tag.name());
        }
        vertexEntity.addPropertyValue(abstractionAttributes.getPropertyName(), new PropertyValue(abstractionAttributes.getPropertyType(), attributeSet));

        // add the application name and version
        vertexEntity.addPropertyValue("applicationName", new PropertyValue(OType.STRING, ((AbstractStateModel) source).getApplicationName()));
        vertexEntity.addPropertyValue("applicationVersion", new PropertyValue(OType.STRING, ((AbstractStateModel) source).getApplicationVersion()));
        vertexEntity.addPropertyValue("extractionMode", new PropertyValue(OType.STRING, ((AbstractStateModel) source).getExtractionMode()));
    }
}
