package org.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.StateModel.AbstractState;
import org.testar.StateModel.Exception.HydrationException;
import org.testar.StateModel.Persistence.OrientDB.Entity.Property;
import org.testar.StateModel.Persistence.OrientDB.Entity.PropertyValue;
import org.testar.StateModel.Persistence.OrientDB.Entity.VertexEntity;
import org.testar.StateModel.Util.HydrationHelper;

public class BlackHoleHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity target, Object source) throws HydrationException {
        if (!(source instanceof AbstractState)) {
            throw new HydrationException("Expected instance of AbstractState. " + source.getClass() + " was given.");
        }
        // there is only one black hole vertex in the data store
        // we always use the same id value for a given abstraction level
        Property identifier = target.getEntityClass().getIdentifier();
        String blackholeId = HydrationHelper.lowCollisionID(((AbstractState) source).getModelIdentifier() + "--THERE_CAN_BE_ONLY_ONE");
        target.addPropertyValue(identifier.getPropertyName(), new PropertyValue(OType.STRING, blackholeId));
    }
}
