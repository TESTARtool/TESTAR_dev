package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.PropertyValue;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.VertexEntity;
import nl.ou.testar.StateModel.Sequence.Sequence;
import nl.ou.testar.StateModel.Sequence.SequenceVerdict;

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
