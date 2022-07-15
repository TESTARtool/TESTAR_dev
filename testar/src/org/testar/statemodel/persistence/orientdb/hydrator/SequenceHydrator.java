package org.testar.statemodel.persistence.orientdb.hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.statemodel.exceptions.HydrationException;
import org.testar.statemodel.persistence.orientdb.entity.Property;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.entity.VertexEntity;
import org.testar.statemodel.sequence.Sequence;
import org.testar.statemodel.sequence.SequenceVerdict;

import java.util.Date;

public class SequenceHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity entity, Object source) throws HydrationException {
        if (!(source instanceof Sequence)) {
            throw new HydrationException("Instance of Sequence expected. Class " +source.getClass() + " was given.");
        }

        // add the sequence identifier
        Property identifier = entity.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException("Entityclass did not have a valid identifier property");
        }

        entity.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), ((Sequence) source).getCurrentSequenceId()));

        // add the starting date and time
        // use of Date is not recommended, but OrientDB expects it
        Date date = new Date(((Sequence) source).getStartDateTime().toEpochMilli());
        entity.addPropertyValue("startDateTime", new PropertyValue(OType.DATETIME, date));

        // fetch the abstract level identifier for the current state model
        String modelIdentifier = ((Sequence) source).getModelIdentifier();
        entity.addPropertyValue("modelIdentifier", new PropertyValue(OType.STRING, modelIdentifier));

        // fetch the sequence's verdict and if needed, a termination message
        SequenceVerdict verdict = ((Sequence) source).getSequenceVerdict();
        entity.addPropertyValue("verdict", new PropertyValue(OType.STRING, verdict.toString()));

        if (verdict == SequenceVerdict.INTERRUPTED_BY_ERROR) {
            entity.addPropertyValue("terminationMessage", new PropertyValue(OType.STRING, ((Sequence) source).getTerminationMessage()));
        }
    }
}
