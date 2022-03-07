package nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator;

import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

import com.orientechnologies.orient.core.metadata.schema.OType;

import nl.ou.testar.StateModel.PredictedAction;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EdgeEntity;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.PropertyValue;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.TypeConvertor;
import nl.ou.testar.StateModel.Util.HydrationHelper;

public class PredictedActionHydrator implements EntityHydrator<EdgeEntity> {

	@Override
	public void hydrate(EdgeEntity edgeEntity, Object source) throws HydrationException {
		if (!(source instanceof PredictedAction)) {
			throw new HydrationException("Object provided to the predicted action hydrator was not a predicted action");
		}

		// first make sure the identity property is set
		Property identifier = edgeEntity.getEntityClass().getIdentifier();
		if (identifier == null) {
			throw new HydrationException();
		}

		// fetch the abstract level identifier for the current state model
		String modelIdentifier = ((PredictedAction) source).getModelIdentifier();
		edgeEntity.addPropertyValue("modelIdentifier", new PropertyValue(OType.STRING, modelIdentifier));

		// because a predicted action might have multiple concrete actions tied to it, it is possible that the target
		// for the same predicted action might me different states.
		// we combine the ids from the source and target with the predicted action id for this purpose, and also add the abstraction level identifier
		Property sourceIdentifier = edgeEntity.getSourceEntity().getEntityClass().getIdentifier();
		String sourceId = (String)edgeEntity.getSourceEntity().getPropertyValue(sourceIdentifier.getPropertyName()).getValue();
		Property targetIdentifier = edgeEntity.getTargetEntity().getEntityClass().getIdentifier();
		String targetId = (String)edgeEntity.getTargetEntity().getPropertyValue(targetIdentifier.getPropertyName()).getValue();

		String edgeId = HydrationHelper.createOrientDbActionId(sourceId, targetId, ((PredictedAction) source).getActionId(), modelIdentifier);
		// make sure the java and orientdb property types are compatible
		OType identifierType = TypeConvertor.getInstance().getOrientDBType(edgeId.getClass());
		if (identifierType != identifier.getPropertyType()) {
			throw new HydrationException("Wrong type specified for action identifier");
		}
		edgeEntity.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), edgeId));

		// add the action id
		edgeEntity.addPropertyValue("actionId", new PropertyValue(OType.STRING, ((PredictedAction) source).getActionId()));

		// loop through the tagged attributes for this state and add them
		TaggableBase attributes = ((PredictedAction) source).getAttributes();
		for (Tag<?> tag :attributes.tags()) {
			// we simply add a property for each tag
			edgeEntity.addPropertyValue(tag.name(), new PropertyValue(TypeConvertor.getInstance().getOrientDBType(attributes.get(tag).getClass()), attributes.get(tag)));
		}
	}

}
