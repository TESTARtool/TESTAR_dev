package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EdgeEntity;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.PropertyValue;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.TypeConvertor;
import nl.ou.testar.StateModel.Sequence.SequenceNode;
import nl.ou.testar.StateModel.Sequence.SequenceStep;

import java.util.Date;

public class SequenceStepHydrator implements EntityHydrator<EdgeEntity> {

    @Override
    public void hydrate(EdgeEntity edgeEntity, Object source) throws HydrationException {
        if (!(source instanceof SequenceStep)) {
            throw new HydrationException();
        }

        // first make sure the identity property is set
        Property identifier = edgeEntity.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException();
        }

        // the edge between two classes needs an identifier to make sure we do not create unnecessary double edges
        // we combine the ids from the source and target for this purpose
        Property sourceIdentifier = edgeEntity.getSourceEntity().getEntityClass().getIdentifier();
        String sourceId = (String)edgeEntity.getSourceEntity().getPropertyValue(sourceIdentifier.getPropertyName()).getValue();
        Property targetIdentifier = edgeEntity.getTargetEntity().getEntityClass().getIdentifier();
        String targetId = (String)edgeEntity.getTargetEntity().getPropertyValue(targetIdentifier.getPropertyName()).getValue();

        String edgeId = sourceId + "-" + targetId;
        // make sure the java and orientdb property types are compatible
        OType identifierType = TypeConvertor.getInstance().getOrientDBType(edgeId.getClass());
        if (identifierType != identifier.getPropertyType()) {
            throw new HydrationException();
        }
        edgeEntity.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), edgeId));

        // add the timestamp
        Date date = new Date(((SequenceNode) source).getTimestamp().toEpochMilli());
        edgeEntity.addPropertyValue("timestamp", new PropertyValue(OType.DATETIME, date));

        // add the concrete action id
        edgeEntity.addPropertyValue("concreteActionId", new PropertyValue(OType.STRING, ((SequenceStep) source).getConcreteAction()));
    }
}
