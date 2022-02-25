package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;

import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.PropertyValue;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.VertexEntity;

public class NonDeterministicHoleHydrator implements EntityHydrator<VertexEntity> {
	
    @Override
    public void hydrate(VertexEntity target, Object source) throws HydrationException {
        // there is only one NonDeterministic vertex in the data store
        Property identifier = target.getEntityClass().getIdentifier();
        String nonDeterministicId = "TheUniqueNonDeterministicId";
        target.addPropertyValue(identifier.getPropertyName(), new PropertyValue(OType.STRING, nonDeterministicId));
    }

}
