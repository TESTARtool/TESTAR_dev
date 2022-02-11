package org.testar.statemodel.persistence.orientdb.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.statemodel.exception.HydrationException;
import org.testar.statemodel.persistence.orientdb.Entity.Property;
import org.testar.statemodel.persistence.orientdb.Entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.Entity.VertexEntity;
import org.testar.statemodel.sequence.SequenceNode;

import java.util.Date;

public class SequenceNodeHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity entity, Object source) throws HydrationException {
        if (!(source instanceof SequenceNode)) {
            throw new HydrationException();
        }

        Property identifier = entity.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException("Entityclass did not have a valid identifier property");
        }
        entity.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), ((SequenceNode) source).getNodeId()));

        // add the nodenr and the timestamp
        Date date = new Date(((SequenceNode) source).getTimestamp().toEpochMilli());
        entity.addPropertyValue("timestamp", new PropertyValue(OType.DATETIME, date));
        entity.addPropertyValue("nodeNr", new PropertyValue(OType.INTEGER, ((SequenceNode) source).getNodeNr()));
        entity.addPropertyValue("concreteStateId", new PropertyValue(OType.STRING, ((SequenceNode) source).getConcreteState().getId()));
        entity.addPropertyValue("sequenceId", new PropertyValue(OType.STRING, ((SequenceNode) source).getSequenceId()));

        // check for error messages
        entity.addPropertyValue("containsErrors", new PropertyValue(OType.BOOLEAN, ((SequenceNode) source).containsErrors()));
        if (((SequenceNode) source).containsErrors()) {
            entity.addPropertyValue("errorMessage", new PropertyValue(OType.STRING, ((SequenceNode) source).getErrorMessage()));
        }
    }
}
