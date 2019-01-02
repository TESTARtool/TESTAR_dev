package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.DocumentEntity;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.TypeConvertor;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.VertexEntity;
import nl.ou.testar.StateModel.Util.HydrationHelper;
import org.fruit.alayer.Tag;

import java.util.HashSet;
import java.util.Set;

public class AbstractStateModelHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity target, Object source) throws HydrationException {
        if (!(source instanceof AbstractStateModel)) {
            throw new HydrationException("Invalid source object was provided to AbstractStateModel hydrator");
        }

        // first make sure the identity property is set
        Property identifier = target.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException();
        }

        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(((AbstractStateModel) source).getAbstractionLevelIdentifier().getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException();
        }
        target.addPropertyValue(identifier.getPropertyName(), identifier.getPropertyType(), ((AbstractStateModel) source).getAbstractionLevelIdentifier());

        // add the tags that were used in the creation of the unique states of the state model
        Property abstractionAttributes = HydrationHelper.getProperty(target.getEntityClass().getProperties(), "abstractionAttributes");
        if (abstractionAttributes == null) {
            throw new HydrationException();
        }
        HashSet<String> attributeSet = new HashSet<>();
        for (Tag<?> tag : ((AbstractStateModel) source).getTags()) {
            attributeSet.add(tag.name());
        }
        target.addPropertyValue(abstractionAttributes.getPropertyName(), abstractionAttributes.getPropertyType(), attributeSet);
    }
}
