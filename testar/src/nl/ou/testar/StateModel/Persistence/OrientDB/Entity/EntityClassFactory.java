package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import nl.ou.testar.StateModel.Sequence.SequenceNode;

import java.util.HashMap;
import java.util.Map;


public class EntityClassFactory {

    public enum EntityClassName {AbstractState, AbstractAction, AbstractStateModel, Widget, ConcreteState, ConcreteAction, isParentOf, isChildOf, isAbstractedBy,
        BlackHole, UnvisitedAbstractAction, TestSequence, SequenceNode, SequenceStep, Accessed, FirstNode}

    // a repo for generated classes, so we don't execute the same generation code over and over if not needed
    private static Map<EntityClassName, EntityClass> entityClasses = new HashMap<>();

    // mapping for the internal orientdb classname to our enum
    // we do not want the rest of our program to use the classNames directly, but want to be able
    // to look up the enum values when going from orientdb to the state model
    private static Map<String, EntityClassName> classNameMap;

    static {
        classNameMap = new HashMap<>();
        classNameMap.put("AbstractState", EntityClassName.AbstractState);
        classNameMap.put("AbstractAction", EntityClassName.AbstractAction);
        classNameMap.put("AbstractStateModel", EntityClassName.AbstractStateModel);
        classNameMap.put("Widget", EntityClassName.Widget);
        classNameMap.put("ConcreteState", EntityClassName.ConcreteState);
        classNameMap.put("ConcreteAction", EntityClassName.ConcreteAction);
        classNameMap.put("isParentOf", EntityClassName.isParentOf);
        classNameMap.put("isChildOf", EntityClassName.isChildOf);
        classNameMap.put("isAbstractedBy", EntityClassName.isAbstractedBy);
        classNameMap.put("BlackHole", EntityClassName.BlackHole);
        classNameMap.put("UnvisitedAbstractAction", EntityClassName.UnvisitedAbstractAction);
        classNameMap.put("TestSequence", EntityClassName.TestSequence);
        classNameMap.put("SequenceNode", EntityClassName.SequenceNode);
        classNameMap.put("SequenceStep", EntityClassName.SequenceStep);
        classNameMap.put("Accessed", EntityClassName.Accessed);
        classNameMap.put("FirstNode", EntityClassName.FirstNode);
    }

    /**
     * This method will return an EntityClassName if it exists.
     * @param className
     * @return
     */
    public static EntityClassName getEntityClassName(String className) {
        return classNameMap.get(className);
    }

    /**
     * This method generates an EntityClass.
     * @param className
     * @return
     */
    public static EntityClass createEntityClass(EntityClassName className) {
        if (entityClasses.containsKey(className)) {
            return entityClasses.get(className);
        }

        switch (className) {
            case AbstractState:
                return createAbstractStateClass();

            case AbstractAction:
                return createAbstractActionClass();

            case AbstractStateModel:
                return createAbstractStateModelClass();

            case ConcreteState:
                return createConcreteStateClass();

            case ConcreteAction:
                return createConcreteActionClass();

            case Widget:
                return createWidgetClass();

            case isParentOf:
                return createIsParentOfClass();

            case isChildOf:
                return createIsChildOfClass();

            case isAbstractedBy:
                return createIsAbstractedByClass();

            case BlackHole:
                return createBlackHoleClass();
                
            case UnvisitedAbstractAction:
                return createUnvisitedAbstractActionClass();

            case TestSequence:
                return createTestSequenceClass();

            case SequenceNode:
                return createSequenceNodeClass();

            case SequenceStep:
                return createSequenceStepClass();

            case Accessed:
                return createAccessedClass();

            case FirstNode:
                return createFirstNodeClass();

            default:
                return null;
        }
    }

    private static EntityClass createAbstractStateClass() {
        EntityClass abstractStateClass = new EntityClass("AbstractState", EntityClass.EntityType.Vertex);
        Property uniqueId = new Property("uid", OType.STRING);
        uniqueId.setMandatory(true);
        uniqueId.setNullable(false);
        uniqueId.setIdentifier(true);
        uniqueId.setIndexAble(true);
        abstractStateClass.addProperty(uniqueId);
        Property stateId = new Property("stateId", OType.STRING);
        stateId.setMandatory(true);
        stateId.setNullable(false);
        abstractStateClass.addProperty(stateId);
        Property modelIdentifier = new Property("modelIdentifier", OType.STRING);
        modelIdentifier.setMandatory(true);
        modelIdentifier.setNullable(false);
        modelIdentifier.setIndexAble(true);
        abstractStateClass.addProperty(modelIdentifier);
        Property isInitial = new Property("isInitial", OType.BOOLEAN);
        isInitial.setMandatory(true);
        isInitial.setNullable(false);
        abstractStateClass.addProperty(isInitial);
        Property concreteStateIds = new Property("concreteStateIds", OType.EMBEDDEDSET, OType.STRING);
        concreteStateIds.setMandatory(false);
        concreteStateIds.setNullable(false);
        abstractStateClass.addProperty(concreteStateIds);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        abstractStateClass.addProperty(counter);
        entityClasses.put(EntityClassName.AbstractState, abstractStateClass);
        return abstractStateClass;
    }

    private static EntityClass createAbstractActionClass() {
        EntityClass abstractActionClass = new EntityClass("AbstractAction", EntityClass.EntityType.Edge);
        Property uniqueId = new Property("uid", OType.STRING);
        uniqueId.setMandatory(true);
        uniqueId.setNullable(false);
        uniqueId.setIdentifier(true);
        uniqueId.setIndexAble(true);
        abstractActionClass.addProperty(uniqueId);
        Property actionId = new Property("actionId", OType.STRING);
        actionId.setMandatory(true);
        actionId.setNullable(false);
        actionId.setIdentifier(false);
        abstractActionClass.addProperty(actionId);
        Property modelIdentifier = new Property("modelIdentifier", OType.STRING);
        modelIdentifier.setMandatory(true);
        modelIdentifier.setNullable(false);
        modelIdentifier.setIndexAble(true);
        abstractActionClass.addProperty(modelIdentifier);
        Property concreteActionIds = new Property("concreteActionIds", OType.EMBEDDEDSET, OType.STRING);
        concreteActionIds.setMandatory(false);
        concreteActionIds.setNullable(false);
        abstractActionClass.addProperty(concreteActionIds);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        abstractActionClass.addProperty(counter);
        entityClasses.put(EntityClassName.AbstractAction, abstractActionClass);
        return abstractActionClass;
    }

    private static EntityClass createAbstractStateModelClass() {
        EntityClass abstractStateModelClass = new EntityClass("AbstractStateModel", EntityClass.EntityType.Vertex);
        Property id = new Property("modelIdentifier", OType.STRING);
        id.setMandatory(true);
        id.setNullable(false);
        id.setIdentifier(true);
        id.setIndexAble(true);
        abstractStateModelClass.addProperty(id);
        Property abstractionAttributes = new Property("abstractionAttributes", OType.EMBEDDEDSET, OType.STRING);
        abstractionAttributes.setMandatory(true);
        abstractionAttributes.setNullable(false);
        abstractStateModelClass.addProperty(abstractionAttributes);
        Property applicationName = new Property("applicationName", OType.STRING);
        applicationName.setMandatory(false);
        applicationName.setNullable(true);
        applicationName.setIdentifier(false);
        abstractStateModelClass.addProperty(applicationName);
        Property applicationVersion = new Property("applicationVersion", OType.STRING);
        applicationVersion.setMandatory(false);
        applicationVersion.setNullable(true);
        applicationVersion.setIdentifier(false);
        abstractStateModelClass.addProperty(applicationVersion);
        entityClasses.put(EntityClassName.AbstractStateModel, abstractStateModelClass);
        return abstractStateModelClass;
    }

    private static EntityClass createConcreteStateClass() {
        EntityClass concreteStateClass = new EntityClass("ConcreteState", EntityClass.EntityType.Vertex);
        Property uniqueId = new Property("uid", OType.STRING);
        uniqueId.setMandatory(true);
        uniqueId.setNullable(false);
        uniqueId.setIdentifier(true);
        uniqueId.setIndexAble(true);
        concreteStateClass.addProperty(uniqueId);
        Property stateId = new Property("stateId", OType.STRING);
        stateId.setMandatory(true);
        stateId.setNullable(false);
        stateId.setIdentifier(false);
        stateId.setIndexAble(true);
        concreteStateClass.addProperty(stateId);
        Property screenshot = new Property("screenshot", OType.BINARY);
        screenshot.setMandatory(false);
        screenshot.setNullable(true);
        screenshot.setIdentifier(false);
        concreteStateClass.addProperty(screenshot);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        concreteStateClass.addProperty(counter);
        concreteStateClass.setSuperClassName("Widget");
        Property oracleVerdictCode = new Property("oracleVerdictCode", OType.INTEGER);
        oracleVerdictCode.setMandatory(true);
        oracleVerdictCode.setNullable(false);
        oracleVerdictCode.setIdentifier(false);
        oracleVerdictCode.setIndexAble(true);
        concreteStateClass.addProperty(oracleVerdictCode);
        entityClasses.put(EntityClassName.ConcreteState, concreteStateClass);
        return concreteStateClass;
    }

    private static EntityClass createConcreteActionClass() {
        EntityClass concreteActionClass = new EntityClass("ConcreteAction", EntityClass.EntityType.Edge);
        Property uniqueId = new Property("uid", OType.STRING);
        uniqueId.setMandatory(true);
        uniqueId.setNullable(false);
        uniqueId.setIdentifier(true);
        uniqueId.setIndexAble(true);
        concreteActionClass.addProperty(uniqueId);
        Property actionId = new Property("actionId", OType.STRING);
        actionId.setMandatory(true);
        actionId.setNullable(false);
        actionId.setIdentifier(false);
        concreteActionClass.addProperty(actionId);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        concreteActionClass.addProperty(counter);
        entityClasses.put(EntityClassName.ConcreteAction, concreteActionClass);
        return concreteActionClass;
    }

    private static EntityClass createWidgetClass() {
        EntityClass widgetClass = new EntityClass("Widget", EntityClass.EntityType.Vertex);
        Property widgetId = new Property("widgetId", OType.STRING);
        widgetId.setMandatory(true);
        widgetId.setNullable(false);
        widgetId.setIdentifier(true);
        widgetId.setIndexAble(true);
        widgetClass.addProperty(widgetId);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        widgetClass.addProperty(counter);
        entityClasses.put(EntityClassName.Widget, widgetClass);
        return widgetClass;
    }

    private static EntityClass createIsParentOfClass() {
        EntityClass parentClass = new EntityClass("isParentOf", EntityClass.EntityType.Edge);
        Property edgeId = new Property("parentEdgeId", OType.STRING);
        edgeId.setMandatory(true);
        edgeId.setNullable(false);
        edgeId.setIdentifier(true);
        edgeId.setIndexAble(true);
        parentClass.addProperty(edgeId);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        parentClass.addProperty(counter);
        entityClasses.put(EntityClassName.isParentOf, parentClass);
        return parentClass;
    }

    private static EntityClass createIsChildOfClass() {
        EntityClass childClass = new EntityClass("isChildOf", EntityClass.EntityType.Edge);
        Property edgeId = new Property("childEdgeId", OType.STRING);
        edgeId.setMandatory(true);
        edgeId.setNullable(false);
        edgeId.setIdentifier(true);
        edgeId.setIndexAble(true);
        childClass.addProperty(edgeId);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        childClass.addProperty(counter);
        entityClasses.put(EntityClassName.isChildOf, childClass);
        return childClass;
    }

    private static EntityClass createIsAbstractedByClass() {
        EntityClass entityClass = new EntityClass("isAbstractedBy", EntityClass.EntityType.Edge);
        Property edgeId = new Property("abstractedByEdgeId", OType.STRING);
        edgeId.setMandatory(true);
        edgeId.setNullable(false);
        edgeId.setIdentifier(true);
        edgeId.setIndexAble(true);
        entityClass.addProperty(edgeId);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        entityClass.addProperty(counter);
        entityClasses.put(EntityClassName.isAbstractedBy, entityClass);
        return entityClass;
    }

    private static EntityClass createAccessedClass() {
        EntityClass entityClass = new EntityClass("Accessed", EntityClass.EntityType.Edge);
        Property accessId = new Property("accessId", OType.STRING);
        accessId.setMandatory(true);
        accessId.setNullable(false);
        accessId.setIdentifier(true);
        accessId.setIndexAble(true);
        entityClass.addProperty(accessId);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        entityClass.addProperty(counter);
        entityClasses.put(EntityClassName.Accessed, entityClass);
        return entityClass;
    }

    private static EntityClass createBlackHoleClass() {
        EntityClass entityClass = new EntityClass("BlackHole", EntityClass.EntityType.Vertex);
        Property blackHoleId = new Property("blackHoleId", OType.STRING);
        blackHoleId.setMandatory(true);
        blackHoleId.setNullable(false);
        blackHoleId.setIdentifier(true);
        blackHoleId.setIndexAble(true);
        entityClass.addProperty(blackHoleId);
        entityClasses.put(EntityClassName.BlackHole, entityClass);
        return entityClass;
    }

    private static EntityClass createUnvisitedAbstractActionClass() {
        EntityClass entityClass = new EntityClass("UnvisitedAbstractAction", EntityClass.EntityType.Edge);
        Property uniqueId = new Property("uid", OType.STRING);
        uniqueId.setMandatory(true);
        uniqueId.setNullable(false);
        uniqueId.setIdentifier(true);
        uniqueId.setIndexAble(true);
        entityClass.addProperty(uniqueId);
        Property actionId = new Property("actionId", OType.STRING);
        actionId.setMandatory(true);
        actionId.setNullable(false);
        actionId.setIdentifier(false);
        entityClass.addProperty(actionId);
        Property modelIdentifier = new Property("modelIdentifier", OType.STRING);
        modelIdentifier.setMandatory(true);
        modelIdentifier.setNullable(false);
        modelIdentifier.setIndexAble(true);
        entityClass.addProperty(modelIdentifier);
        Property concreteActionIds = new Property("concreteActionIds", OType.EMBEDDEDSET, OType.STRING);
        concreteActionIds.setMandatory(false);
        concreteActionIds.setNullable(false);
        entityClass.addProperty(concreteActionIds);
        entityClasses.put(EntityClassName.UnvisitedAbstractAction, entityClass);
        return entityClass;
    }

    private static EntityClass createTestSequenceClass() {
        EntityClass entityClass = new EntityClass("TestSequence", EntityClass.EntityType.Vertex);
        Property identifier = new Property("sequenceId", OType.STRING);
        identifier.setIdentifier(true);
        identifier.setNullable(false);
        identifier.setMandatory(true);
        identifier.setIndexAble(true);
        entityClass.addProperty(identifier);
        Property startDateTime = new Property("startDateTime", OType.DATETIME);
        startDateTime.setIdentifier(false);
        startDateTime.setNullable(false);
        startDateTime.setMandatory(true);
        startDateTime.setIndexAble(false);
        entityClass.addProperty(startDateTime);
        Property modelIdentifier = new Property("modelIdentifier", OType.STRING);
        modelIdentifier.setMandatory(true);
        modelIdentifier.setNullable(false);
        modelIdentifier.setIndexAble(true);
        entityClass.addProperty(modelIdentifier);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        entityClass.addProperty(counter);
        Property verdict = new Property("verdict", OType.STRING);
        verdict.setMandatory(true);
        verdict.setNullable(false);
        verdict.setIndexAble(true);
        entityClass.addProperty(verdict);
        Property terminationMessage = new Property("terminationMessage", OType.STRING);
        terminationMessage.setMandatory(false);
        terminationMessage.setNullable(true);
        terminationMessage.setIndexAble(false);
        entityClass.addProperty(terminationMessage);
        entityClasses.put(EntityClassName.TestSequence, entityClass);
        return entityClass;
    }

    private static EntityClass createSequenceNodeClass() {
        EntityClass entityClass = new EntityClass("SequenceNode", EntityClass.EntityType.Vertex);
        Property identifier = new Property("nodeId", OType.STRING);
        identifier.setIdentifier(true);
        identifier.setNullable(false);
        identifier.setMandatory(true);
        identifier.setIndexAble(true);
        entityClass.addProperty(identifier);
        Property sequenceId = new Property("sequenceId", OType.STRING);
        sequenceId.setIdentifier(false);
        sequenceId.setNullable(false);
        identifier.setMandatory(true);
        identifier.setIndexAble(true);
        entityClass.addProperty(sequenceId);
        Property nodeNr = new Property("nodeNr", OType.INTEGER);
        nodeNr.setIdentifier(false);
        nodeNr.setNullable(false);
        nodeNr.setMandatory(true);
        nodeNr.setIndexAble(false);
        entityClass.addProperty(nodeNr);
        Property timestamp = new Property("timestamp", OType.DATETIME);
        timestamp.setIdentifier(false);
        timestamp.setNullable(false);
        timestamp.setMandatory(true);
        timestamp.setIndexAble(false);
        entityClass.addProperty(timestamp);
        Property stateId = new Property("concreteStateId", OType.STRING);
        stateId.setMandatory(true);
        stateId.setNullable(false);
        stateId.setIdentifier(false);
        stateId.setIndexAble(true);
        entityClass.addProperty(stateId);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        entityClass.addProperty(counter);
        Property errorMessages = new Property("errorMessage", OType.STRING);
        errorMessages.setMandatory(false);
        errorMessages.setNullable(true);
        errorMessages.setIndexAble(false);
        entityClass.addProperty(errorMessages);
        Property containsErrors = new Property("containsErrors", OType.BOOLEAN);
        containsErrors.setMandatory(true);
        containsErrors.setNullable(false);
        containsErrors.setIndexAble(true);
        entityClass.addProperty(containsErrors);
        entityClasses.put(EntityClassName.SequenceNode, entityClass);
        return entityClass;
    }

    private static EntityClass createSequenceStepClass() {
        EntityClass entityClass = new EntityClass("SequenceStep", EntityClass.EntityType.Edge);
        Property identifier = new Property("stepId", OType.STRING);
        identifier.setIdentifier(true);
        identifier.setNullable(false);
        identifier.setMandatory(true);
        identifier.setIndexAble(true);
        entityClass.addProperty(identifier);
        Property timestamp = new Property("timestamp", OType.DATETIME);
        timestamp.setIdentifier(false);
        timestamp.setNullable(false);
        timestamp.setMandatory(true);
        timestamp.setIndexAble(false);
        entityClass.addProperty(timestamp);
        Property concreteActionId = new Property("concreteActionId", OType.STRING);
        concreteActionId.setMandatory(true);
        concreteActionId.setNullable(false);
        concreteActionId.setIdentifier(false);
        concreteActionId.setIndexAble(true);
        entityClass.addProperty(concreteActionId);
        Property concreteActionUid = new Property("concreteActionUid", OType.STRING);
        concreteActionUid.setMandatory(true);
        concreteActionUid.setNullable(false);
        concreteActionUid.setIdentifier(false);
        concreteActionUid.setIndexAble(true);
        entityClass.addProperty(concreteActionUid);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        entityClass.addProperty(counter);
        Property actionDescription = new Property("actionDescription", OType.STRING);
        actionDescription.setMandatory(false);
        actionDescription.setNullable(true);
        actionDescription.setIndexAble(false);
        entityClass.addProperty(actionDescription);
        entityClasses.put(EntityClassName.SequenceStep, entityClass);
        return entityClass;
    }

    private static EntityClass createFirstNodeClass() {
        EntityClass entityClass = new EntityClass("FirstNode", EntityClass.EntityType.Edge);
        Property edgeId = new Property("firstNodeId", OType.STRING);
        edgeId.setMandatory(true);
        edgeId.setNullable(false);
        edgeId.setIdentifier(true);
        edgeId.setIndexAble(true);
        entityClass.addProperty(edgeId);
        Property counter = new Property("counter", OType.INTEGER);
        counter.setMandatory(true);
        counter.setNullable(false);
        counter.setAutoIncrement(true);
        entityClass.addProperty(counter);
        entityClasses.put(EntityClassName.FirstNode, entityClass);
        return entityClass;
    }

}
