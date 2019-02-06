package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.PropertyValue;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.VertexEntity;

public class BlackHoleHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity target, Object source) throws HydrationException {
        // there is only one black hole vertex in the data store
        // we always use the same id value
        Property identifier = target.getEntityClass().getIdentifier();
        target.addPropertyValue(identifier.getPropertyName(), new PropertyValue(OType.STRING, "THERE_CAN_BE_ONLY_ONE"));
    }
}
