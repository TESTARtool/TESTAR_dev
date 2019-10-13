package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.ConcreteAction;
import nl.ou.testar.StateModel.ConcreteState;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.*;
import nl.ou.testar.StateModel.Sequence.SequenceNode;
import nl.ou.testar.StateModel.Sequence.SequenceStep;
import nl.ou.testar.StateModel.Util.HydrationHelper;

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
        Date date = new Date(((SequenceStep) source).getTimestamp().toEpochMilli());
        edgeEntity.addPropertyValue("timestamp", new PropertyValue(OType.DATETIME, date));

        ConcreteState concreteStateSource = ((SequenceStep) source).getSourceNode().getConcreteState();
        ConcreteState concreteStateTarget = ((SequenceStep) source).getTargetNode().getConcreteState();
        ConcreteAction concreteAction = ((SequenceStep) source).getConcreteAction();

        // in order to obtain the unique identifier for the concrete state, we need to hydrate them.
        // create entities for the target and source states
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.ConcreteState);
        VertexEntity sourceVertexEntity = new VertexEntity(entityClass);
        VertexEntity targetVertexEntity = new VertexEntity(entityClass);

        try {
            EntityHydrator stateHydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_CONCRETE_STATE);
            stateHydrator.hydrate(sourceVertexEntity, concreteStateSource);
            stateHydrator.hydrate(targetVertexEntity, concreteStateTarget);
        } catch (HydrationException e) {
            //@todo add some meaningful logging here
            return;
        }

        Property ConcreteStateIdentifier = entityClass.getIdentifier();
        String concreteStateSourceId = (String)sourceVertexEntity.getPropertyValue(ConcreteStateIdentifier.getPropertyName()).getValue();
        String concreteStateTargetId = (String)targetVertexEntity.getPropertyValue(ConcreteStateIdentifier.getPropertyName()).getValue();

        // add the concrete action id
        edgeEntity.addPropertyValue("concreteActionId", new PropertyValue(OType.STRING, concreteAction.getActionId()));

        // construct the unique action id
        String concreteActionUid = HydrationHelper.createOrientDbActionId(concreteStateSourceId, concreteStateTargetId, concreteAction.getActionId(), null);
        edgeEntity.addPropertyValue("concreteActionUid", new PropertyValue(OType.STRING, concreteActionUid));

        // add the description of the action performed
        edgeEntity.addPropertyValue("actionDescription", new PropertyValue(OType.STRING, ((SequenceStep) source).getActionDescription()));
    }
}
