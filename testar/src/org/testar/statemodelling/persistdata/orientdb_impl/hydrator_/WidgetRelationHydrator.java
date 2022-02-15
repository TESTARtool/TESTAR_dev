package org.testar.statemodelling.persistence.orientdb.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.statemodelling.exception.HydrationException;
import org.testar.statemodelling.persistence.orientdb.Entity.EdgeEntity;
import org.testar.statemodelling.persistence.orientdb.Entity.Property;
import org.testar.statemodelling.persistence.orientdb.Entity.PropertyValue;
import org.testar.statemodelling.persistence.orientdb.Entity.TypeConvertor;

public class WidgetRelationHydrator implements EntityHydrator<EdgeEntity> {

    @Override
    public void hydrate(EdgeEntity target, Object source) throws HydrationException {
        // first make sure the identity property is set
        Property identifier = target.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException();
        }

        // the edge between two widgets needs an identifier to make sure we do not create unnecessary double edges
        // we combine the widget id's from the source and target for this purpose
        Property sourceIdentifier = target.getSourceEntity().getEntityClass().getIdentifier();
        String sourceId = (String)target.getSourceEntity().getPropertyValue(sourceIdentifier.getPropertyName()).getValue();
        Property targetIdentifier = target.getTargetEntity().getEntityClass().getIdentifier();
        String targetId = (String)target.getTargetEntity().getPropertyValue(targetIdentifier.getPropertyName()).getValue();

        String edgeId = sourceId + "-" + targetId;
        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(edgeId.getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException();
        }
        target.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), edgeId));
    }
}
