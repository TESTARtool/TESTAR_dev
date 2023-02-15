package org.testar.statemodel.persistence.orientdb;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.ConcreteState;
import org.testar.statemodel.ConcreteStateTransition;
import org.testar.statemodel.exceptions.ExtractionException;
import org.testar.statemodel.exceptions.StateModelException;
import org.testar.statemodel.persistence.orientdb.entity.*;
import org.testar.statemodel.persistence.orientdb.extractor.EntityExtractor;
import org.testar.statemodel.persistence.orientdb.extractor.ExtractorFactory;
import org.testar.statemodel.util.EventHelper;
import org.testar.statemodel.util.ExtendedStateModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OrientDBManagerExtended extends OrientDBManager<ExtendedStateModel> {
  /**
   * Constructor
   *
   * @param eventHelper
   * @param entityManager
   */
  public OrientDBManagerExtended(EventHelper eventHelper, EntityManager entityManager) {
    super(eventHelper, entityManager);
  }

  @Override
  protected void populateStateModel(ExtendedStateModel stateModel, String stateModelClassName, VertexEntity stateModelEntity) {
    super.populateStateModel(stateModel, stateModelClassName, stateModelEntity);

    // see if there are concrete states present in the data store that are tied to this extended state model
    EntityClass concreteStateClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.ConcreteState);
    if (concreteStateClass == null) throw new RuntimeException("Error occurred: could not retrieve a concrete state entity class.");

    Map<String, PropertyValue> entityProperties = new HashMap<>();

    try (ODatabaseSession db = entityManager.getConnection().getDatabaseSession()) {
      Set<DocumentEntity> retrievedDocuments = entityManager.retrieveAllOfClass(concreteStateClass, entityProperties, db);
      if (retrievedDocuments.isEmpty()) {
        System.out.println("Could not find concrete states in the model");
      }
      else {
        // we need to create the concrete states from the returned document entities
        try {
          EntityExtractor<ConcreteState, AbstractStateModel> concreteStateExtractor = ExtractorFactory.getExtractor(ExtractorFactory.EXTRACTOR_CONCRETE_STATE);
          for (DocumentEntity documentEntity: retrievedDocuments) {
            ConcreteState concreteState = concreteStateExtractor.extract(documentEntity, stateModel);
            stateModel.addConcreteState(concreteState);
          }
        } catch (ExtractionException | StateModelException e) {
          e.printStackTrace();
        }
      }

      // fetch the transitions from the database
      EntityClass concreteActionClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.ConcreteAction);
      if (concreteActionClass == null) throw new RuntimeException("Error occurred: could not retrieve a concrete action entity class");

      retrievedDocuments = entityManager.retrieveAllOfClass(concreteActionClass, entityProperties, db);
      if (retrievedDocuments.isEmpty()) {
        System.out.println("Could not find abstract actions in the model");
      }
      else {
        // we need to create the transitions from the returned document entities
        try {
          EntityExtractor<ConcreteStateTransition, AbstractStateModel> concreteStateTransitionExtractor = ExtractorFactory.getExtractor(ExtractorFactory.EXTRACTOR_CONCRETE_STATE_TRANSITION);
          for (DocumentEntity documentEntity : retrievedDocuments) {
            ConcreteStateTransition concreteStateTransition = concreteStateTransitionExtractor.extract(documentEntity, stateModel);
            stateModel.addConcreteTransition(concreteStateTransition.getSourceState(), concreteStateTransition.getTargetState(), concreteStateTransition.getAction());
          }
        } catch (ExtractionException | StateModelException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
