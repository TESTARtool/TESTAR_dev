package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.sequence.OSequence;
import com.orientechnologies.orient.core.metadata.sequence.OSequenceLibrary;
import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.OBlob;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import nl.ou.testar.StateModel.Exception.EntityNotFoundException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Util.DependencyHelper;
import org.fruit.alayer.Visualizer;

import java.util.*;
import java.util.stream.Collectors;

public class EntityManager {

    // the connection object holding the datastore instance and the connection configuration information
    private Connection connection;

    /**
     * Constructor
     * @param config
     */
    public EntityManager(Config config) {
        String connectionString = config.getConnectionType() + ":" + (config.getConnectionType().equals("remote") ?
                config.getServer() : config.getDatabaseDirectory()) + "/";
        OrientDB orientDB = new OrientDB(connectionString, OrientDBConfig.defaultConfig());
        connection = new Connection(orientDB, config);
        init();
    }

    /**
     * This method tells the Entity Manager to close its connection.
     * Should be called before the entity manager itself becomes unused.
     */
    public void releaseConnection() {
        connection.releaseConnection();
    }

    /**
     * Initialization method for this entity manager
     */
    private void init() {
        //init code here
        // check if the database needs to be reset
        if (connection.getConfig().resetDataStore()) {
            try (ODatabaseSession db = connection.getDatabaseSession()) {
                // drop all the classes. This will drop all the records for these classes.
                OSchema schema = db.getMetadata().getSchema();
                OSequenceLibrary sequenceLibrary = db.getMetadata().getSequenceLibrary();
                for (OClass oClass : DependencyHelper.sortDependenciesForDeletion(schema.getClasses())) {
                    if ((oClass.isEdgeType() || oClass.isVertexType()) && !(oClass.getName().equals("V") || oClass.getName().equals("E"))) {
                        System.out.println("Dropping class " + oClass.getName());
                        db.command("TRUNCATE CLASS " + oClass.getName() + " UNSAFE");
                        schema.dropClass(oClass.getName());
                    }
                    // also drop the OSequence table
                    if (oClass.getName().equals("OSequence")) {
                        System.out.println("Dropping class " + oClass.getName());
                        // first, drop all the sequences
                        sequenceLibrary.getSequenceNames().forEach(sequenceLibrary::dropSequence);
                        schema.dropClass(oClass.getName());
                    }
                }
            }
        }
    }

    /**
     * Method returns true if the vertex is already present in the data store.
     * @param vertexEntity
     * @param db
     * @return
     */
    private boolean vertexExists(VertexEntity vertexEntity, ODatabaseSession db) {
        boolean hasVertex = true;
        try {
            retrieveVertex(vertexEntity, db);
        }
        catch (EntityNotFoundException e) {
            hasVertex = false;
        }
        return hasVertex;
    }

    /**
     * Method retrieves a vertex from the data store.
     * @param vertexEntity
     * @param db
     * @return
     * @throws EntityNotFoundException
     */
    private OVertex retrieveVertex(VertexEntity vertexEntity, ODatabaseSession db) throws EntityNotFoundException {
        Property identifier = vertexEntity.getEntityClass().getIdentifier();
        if (identifier == null) {
            // cannot retrieve a vertex without identifier
            // technically we should use a different kind of exception for this
            throw new EntityNotFoundException();
        }

        // first we prepare the statement to execute
        String className = vertexEntity.getEntityClass().getClassName();
        String idField = identifier.getPropertyName();
        String stmt = "SELECT FROM " + className + " WHERE " + idField + " = :" + idField;
        // get the id parameter ready
        Map<String, Object> params = new HashMap<>();
        params.put(idField, vertexEntity.getPropertyValue(idField).getValue());
        //execute the query using statement and parameters
        OResultSet rs = db.query(stmt, params);

        // process the results
        if (!rs.hasNext()) {
            throw new EntityNotFoundException();
        }

        Optional<OVertex> op = rs.next().getVertex();
        if (op.isPresent()) {
            return op.get();
        }

        // if we made it here, no vertex was found
        throw new EntityNotFoundException();
    }

    /**
     * Method retrieves an edge from the data store.
     * @param edgeEntity
     * @param db
     * @return
     * @throws EntityNotFoundException
     */
    private OEdge retrieveEdge(EdgeEntity edgeEntity, ODatabaseSession db) throws EntityNotFoundException {
        // when looking for an edge, there are 2 options.
        // an edge can have an Id field, in which case we will just look for the id, as it will be indexed and unique.
        // If an edge does not have an Id field, we will attempt to look for an edge between the source and target vertices.
        Property identifier = edgeEntity.getEntityClass().getIdentifier();
        OResultSet rs;
        if (identifier != null) {
            rs = retrieveEdgeWithId(edgeEntity, db);
        }
        else {
            rs = retrieveEdgeWithoutId(edgeEntity, db);
        }

        // process the results
        if (!rs.hasNext()) {
            throw new EntityNotFoundException();
        }

        Optional<OEdge> op = rs.next().getEdge();
        if (op.isPresent()) {
            return op.get();
        }

        // if we made it here, no edge was found
        throw new EntityNotFoundException();
    }

    /**
     * Method retrieves an edge from the data store based on the value of a unique id field.
     * @param edgeEntity
     * @param db
     * @return
     * @throws EntityNotFoundException
     */
    private OResultSet retrieveEdgeWithId(EdgeEntity edgeEntity, ODatabaseSession db) throws EntityNotFoundException {
        Property identifier = edgeEntity.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw  new EntityNotFoundException();
        }
        // first we prepare the statement to execute
        String className = edgeEntity.getEntityClass().getClassName();
        String idField = identifier.getPropertyName();
        String stmt = "SELECT FROM " + className + " WHERE " + idField + " = :" + idField;
        // get the id parameter ready
        Map<String, Object> params = new HashMap<>();
        params.put(idField, edgeEntity.getPropertyValue(idField).getValue());
        //execute the query using statement and parameters
        OResultSet rs = db.query(stmt, params);
        return rs;
    }

    /**
     * Method retrieves an edge from the data store based on its endpoint vertices.
     * @param edgeEntity
     * @param db
     * @return
     * @throws EntityNotFoundException
     */
    private OResultSet retrieveEdgeWithoutId(EdgeEntity edgeEntity, ODatabaseSession db) throws EntityNotFoundException {
        // for this one we need a more tricky query using specific graph query capabilities
        // first, we need to make sure we have been provided the source and target vertices, otherwise there is nothing to search
        if (edgeEntity.getSourceEntity() == null || edgeEntity.getTargetEntity() == null) {
            throw new EntityNotFoundException();
        }

        Property sourceIdentifier = edgeEntity.getSourceEntity().getEntityClass().getIdentifier();
        Property targetIdentifier = edgeEntity.getTargetEntity().getEntityClass().getIdentifier();
        if (sourceIdentifier == null || targetIdentifier == null) {
            throw new EntityNotFoundException();
        }

        // extract the necessary variables needed in query execution
        String sourceIdName = sourceIdentifier.getPropertyName();
        String targetIdName = targetIdentifier.getPropertyName();
        String sourceId = (String)edgeEntity.getPropertyValue(sourceIdName).getValue();
        String targetId = (String)edgeEntity.getPropertyValue(targetIdName).getValue();

        String sourceClass = edgeEntity.getSourceEntity().getEntityClass().getClassName();
        String targetClass = edgeEntity.getTargetEntity().getEntityClass().getClassName();
        String edgeClass = edgeEntity.getEntityClass().getClassName();

        // prepare the statement we need to execute
        String stmt = "SELECT transition FROM (MATCH {class: " + sourceClass + ", as: source, where: (" + sourceIdName + " = :" + sourceIdName + ")}" +
                ".outE('" + edgeClass + "') {as: transition}.outV('" + targetClass + "') {as: target, where: (" + targetIdName + " = :" + targetIdName + ")} RETURN action)";
        // provide a map with the values
        Map<String, Object> params = new HashMap<>();
        params.put(sourceIdName, sourceId);
        params.put(targetIdName, targetId);
        OResultSet rs = db.query(stmt, params);
        return rs;
    }


    /**
     * This method will attempt to create a new class if it is not already present in the database
     * @param entityClass
     */
    public void createClass(EntityClass entityClass) {
        try (ODatabaseSession db = connection.getDatabaseSession()) {
            // check if the class already exists
            OClass oClass = db.getClass(entityClass.getClassName());
            if (oClass != null) return;

            // no class yet, let's create it
            String entitySuperClass = entityClass.isVertex() ? "V" : entityClass.isEdge() ? "E" : "";
            String superClassName = entityClass.getSuperClassName() != null ? entityClass.getSuperClassName() : entitySuperClass;
            oClass = db.createClass(entityClass.getClassName(), superClassName);

            Set<String> propertyBlackList = new HashSet<>();
            // if the entityclass has a super class, we need to filter out the properties that have already been
            // created in the super class, as OrientDB will throw an error if we try to create it again in the child class
            if (entityClass.getSuperClassName() != null) {
                // fetch the superclass
                EntityClass superClass = EntityClassFactory.createEntityClass(EntityClassFactory.getEntityClassName(superClassName));
                if (superClass != null) {
                    propertyBlackList = superClass.getProperties().stream().map(Property::getPropertyName).collect(Collectors.toSet());
                }
            }

            // set the properties
            for (Property property : entityClass.getProperties()) {
                // for the auto-increment fields, we need to create a sequence
                if (property.isAutoIncrement()) {
                    String sequenceName = createSequenceId(entityClass, property);
                    System.out.println("creating sequence: " + sequenceName);
                    db.getMetadata().getSequenceLibrary().createSequence(sequenceName, OSequence.SEQUENCE_TYPE.ORDERED, null);
                }

                // we might need to skip adding certain properties
                // check if this property needs to be skipped
                if (propertyBlackList.contains(property.getPropertyName())) {
                    continue;
                }

                OProperty dbProperty = null;
                // binary types we do not create, as they will be stored as separate binary records
                if (property.getPropertyType() == OType.BINARY) {
                    continue;
                }
                // for linked and embedded type a childtype needs to be specified
                else if (property.getPropertyType().isEmbedded() || property.getPropertyType().isLink()) {
                    dbProperty = oClass.createProperty(property.getPropertyName(), property.getPropertyType(), property.getChildType());
                }
                else {
                    dbProperty = oClass.createProperty(property.getPropertyName(), property.getPropertyType());
                }

                dbProperty.setReadonly(property.isReadOnly());
                dbProperty.setMandatory(property.isMandatory());
                dbProperty.setNotNull(!property.isNullable());

                // we add an index for certain property fields
                if (property.isIndexAble()) {
                    String indexField = entityClass.getClassName() + "." + property.getPropertyName() + "Idx";
                    OClass.INDEX_TYPE indexType = property.isIdentifier() ? OClass.INDEX_TYPE.UNIQUE_HASH_INDEX : OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX;
                    oClass.createIndex(indexField, indexType,property.getPropertyName());
                }
            }
        }
    }

    /**
     * This method saves an entity to the data store.
     * @param entity
     */
    public void saveEntity(DocumentEntity entity) {
        try (ODatabaseSession db = connection.getDatabaseSession()) {
            if (entity.getEntityClass().isVertex()) {
                saveVertexEntity((VertexEntity) entity, db);
            }
            else if (entity.getEntityClass().isEdge()) {
                saveEdgeEntity((EdgeEntity) entity, db);
            }
        }
    }

    /**
     * This method saves a vertex entity to the data store.
     * @param entity
     * @param db
     */
    private void saveVertexEntity(VertexEntity entity, ODatabaseSession db) {
        OVertex oVertex;
        boolean newVertex = false;
        // check to see if the vertex already exists in the database
        try {
            oVertex = retrieveVertex(entity, db);
        }
        catch (EntityNotFoundException e) {
            // vertex doesn't exist yet. No problemo. We'll create one.
            oVertex = db.newVertex(entity.getEntityClass().getClassName());
            newVertex = true;
        }

        // check if we should process the properties
        if (!(newVertex || entity.updateEnabled())) return;

        // now we have to add or update properties!
        for (String propertyName : entity.getPropertyNames()) {
            setProperty(oVertex, propertyName, entity.getPropertyValue(propertyName).getValue(), db);
        }

        // check if one of the properties is an auto-increment field.
        // in that case we need to ask a sequence to provide a value
        for (Property property : entity.getEntityClass().getProperties()) {
            if (property.isAutoIncrement()) {
                // make sure the property does not have a value yet
                if (oVertex.getProperty(property.getPropertyName()) == null) {
                    // fetch the sequence
                    OSequence sequence = db.getMetadata().getSequenceLibrary().getSequence(createSequenceId(entity.getEntityClass(), property));
                    setProperty(oVertex, property.getPropertyName(), sequence.next(), db);
                }
            }
        }

        oVertex.save();
    }

    /**
     * This method saves an edge entity to the data store.
     * @param entity
     * @param db
     */
    private void saveEdgeEntity(EdgeEntity entity, ODatabaseSession db) {
        // an edge always needs both a source and a target vertex
        // an edge has a source and target vertex
        if (entity.getSourceEntity() == null || entity.getTargetEntity() == null) {
            return;
            //@todo we could at some point implement some error handling here, but for now we simply do not store the edge
        }

        // first we check if the edge already exists..this saves a lot of overhead.
        OEdge edge;
        try {
            edge = retrieveEdge(entity, db);
            // the edge exists. Check if we have to update properties
            if (entity.updateEnabled()) {
                for (String propertyName : entity.getPropertyNames()) {
                    setProperty(edge, propertyName, entity.getPropertyValue(propertyName).getValue(), db);
                }
            }
            // that's all we need to do. An edge has a unique identifier, no need to continue processing.
            return;

        } catch (EntityNotFoundException e) {
            // we don't do anything here. If the edge does not exist, we just want to continue with method execution
        }

        // retrieve and/or update/save the source vertex
        OVertex sourceVertex;
        boolean newSourceVertex = false;
        try {
            sourceVertex = retrieveVertex(entity.getSourceEntity(), db);
        }
        catch (EntityNotFoundException e) {
            sourceVertex = db.newVertex(entity.getSourceEntity().getEntityClass().getClassName());
            newSourceVertex = true;
        }

        if (newSourceVertex || entity.getSourceEntity().updateEnabled()) {
            for (String propertyName : entity.getSourceEntity().getPropertyNames()) {
                setProperty(sourceVertex, propertyName, entity.getSourceEntity().getPropertyValue(propertyName).getValue(), db);
            }

            // check if one of the properties is an auto-increment field.
            // in that case we need to ask a sequence to provide a value
            for (Property property : entity.getSourceEntity().getEntityClass().getProperties()) {
                if (property.isAutoIncrement()) {
                    // make sure the property does not have a value yet
                    if (sourceVertex.getProperty(property.getPropertyName()) == null) {
                        // fetch the sequence
                        OSequence sequence = db.getMetadata().getSequenceLibrary().getSequence(createSequenceId(entity.getSourceEntity().getEntityClass(), property));
                        setProperty(sourceVertex, property.getPropertyName(), sequence.next(), db);
                    }
                }
            }

            sourceVertex.save();
        }

        // retrieve and/or update/save the target vertex
        OVertex targetVertex;
        boolean newTargetVertex = false;
        try {
            targetVertex = retrieveVertex(entity.getTargetEntity(), db);
        }
        catch (EntityNotFoundException e) {
            targetVertex = db.newVertex(entity.getTargetEntity().getEntityClass().getClassName());
            newTargetVertex = true;
        }

        if (newTargetVertex || entity.getTargetEntity().updateEnabled()) {
            for (String propertyName : entity.getTargetEntity().getPropertyNames()) {
                setProperty(targetVertex, propertyName, entity.getTargetEntity().getPropertyValue(propertyName).getValue(), db);
            }

            // check if one of the properties is an auto-increment field.
            // in that case we need to ask a sequence to provide a value
            for (Property property : entity.getTargetEntity().getEntityClass().getProperties()) {
                if (property.isAutoIncrement()) {
                    // make sure the property does not have a value yet
                    if (targetVertex.getProperty(property.getPropertyName()) == null) {
                        // fetch the sequence
                        OSequence sequence = db.getMetadata().getSequenceLibrary().getSequence(createSequenceId(entity.getTargetEntity().getEntityClass(), property));
                        setProperty(targetVertex, property.getPropertyName(), sequence.next(), db);
                    }
                }
            }

            targetVertex.save();
        }

        // now create the new edge, set the properties and save
        edge = sourceVertex.addEdge(targetVertex, entity.getEntityClass().getClassName());
        for (String propertyName : entity.getPropertyNames()) {
            setProperty(edge, propertyName, entity.getPropertyValue(propertyName).getValue(), db);
        }

        // check if one of the properties is an auto-increment field.
        // in that case we need to ask a sequence to provide a value
        for (Property property : entity.getEntityClass().getProperties()) {
            if (property.isAutoIncrement()) {
                // make sure the property does not have a value yet
                if (edge.getProperty(property.getPropertyName()) == null) {
                    // fetch the sequence
                    OSequence sequence = db.getMetadata().getSequenceLibrary().getSequence(createSequenceId(entity.getEntityClass(), property));
                    setProperty(edge, property.getPropertyName(), sequence.next(), db);
                }
            }
        }

        edge.save();
    }

    public void deleteEntity(DocumentEntity entity) {
            // we delete an entity based on its class and its id
            EntityClass entityClass = entity.getEntityClass();

            Property identifier = entityClass.getIdentifier();
            if (identifier == null) {
                // cannot delete without an id value
                return;
            }
            Set<Object> idValues = new HashSet<>();
            idValues.add(entity.getPropertyValue(identifier.getPropertyName()).getValue());
            deleteEntities(entityClass, idValues);
    }

    /**
     * Delete entities in a given entity class, based on a provided set of id values.
     * @param entityClass
     * @param idValues
     */
    public void deleteEntities(EntityClass entityClass, Set<Object> idValues) {
        try (ODatabaseSession db = connection.getDatabaseSession()) {
            String typeName;
            if (entityClass.getEntityType() == EntityClass.EntityType.Vertex) {
                typeName = "VERTEX";
            }
            else if (entityClass.getEntityType() == EntityClass.EntityType.Edge) {
                typeName = "EDGE";
            }
            else {
                // should not happen
                return;
            }

            Property identifier = entityClass.getIdentifier();
            if (identifier == null) {
                // cannot delete without an id value
                return;
            }

            String stmt = "DELETE " + typeName + " " + entityClass.getClassName() + " WHERE " + identifier.getPropertyName() + " IN :" + identifier.getPropertyName();
            Map<String, Object> params = new HashMap<>();
            params.put(identifier.getPropertyName(), idValues);
            db.command(stmt, params);
        }
    }

    /**
     * Helper method to format a property before adding it to a map.
     * This is a bit of a dirty solution to prevent OrientDB from protesting to certain values that are
     * stored. Will need to investigate further at some point in time.
     * @param element
     * @param propertyName
     * @param propertyValue
     */
    private void setProperty(OElement element, String propertyName, Object propertyValue, ODatabaseSession db) {
        if (propertyValue instanceof Boolean)
            element.setProperty(propertyName, ((Boolean) propertyValue).booleanValue());
        else if (propertyValue instanceof Byte)
            element.setProperty(propertyName, ((Byte) propertyValue).byteValue());
        else if (propertyValue instanceof Character)
            element.setProperty(propertyName, ((Character) propertyValue).charValue());
        else if (propertyValue instanceof Double)
            element.setProperty(propertyName, ((Double) propertyValue).doubleValue());
        else if (propertyValue instanceof Float)
            element.setProperty(propertyName, ((Float) propertyValue).floatValue());
        else if (propertyValue instanceof Integer)
            element.setProperty(propertyName, ((Integer) propertyValue).intValue());
        else if (propertyValue instanceof Long)
            element.setProperty(propertyName, ((Long) propertyValue).longValue());
        else if (propertyValue instanceof Short)
            element.setProperty(propertyName, ((Short) propertyValue).shortValue());
        else if (propertyValue instanceof Visualizer) {
            //skip Don't put visualizer in the graph since it has no meaning for graph.
            //It will get a meaning when we want to use the data for reply.
        }
        else if (propertyValue instanceof byte[]) {
            // for binary data we add a separate record and connect it to the element
            OBlob record = db.newBlob((byte[]) propertyValue);
            element.setProperty(propertyName, record);
        }
        else if (propertyValue instanceof Set) {
            element.setProperty(propertyName, propertyValue);
        }
        else if (propertyValue instanceof Date) {
            element.setProperty(propertyName, propertyValue);
        }
        else
            element.setProperty(propertyName, propertyValue.toString());
        //@todo need to make the linked and set types right here
    }

    /**
     * Helper method to set all the properties collected from the datastore onto an entity instance.
     * @param entity
     * @param oElement
     */
    private void setProperties(DocumentEntity entity, OElement oElement) {
        for (String propertyName : oElement.getPropertyNames()) {
            Object property = oElement.getProperty(propertyName);
            OType oType = OType.getTypeByValue(property);
            Object convertedProperty = getConvertedValue(oType, property);
            if (convertedProperty != null) {
                entity.addPropertyValue(propertyName, new PropertyValue(oType, convertedProperty));
            }
        }
    }

    /**
     * Helper method that converts an object value based on a specified OrientDB data type.
     * @param oType
     * @param valueToConvert
     * @return
     */
    private Object getConvertedValue(OType oType, Object valueToConvert) {
        Object convertedValue = null;
        switch (oType) {
            case BOOLEAN:
                convertedValue = OType.convert(valueToConvert, Boolean.class);
                break;

            case STRING:
                convertedValue = OType.convert(valueToConvert, String.class);
                break;

            case LINKBAG:
                // we don't process these as a separate attribute
                break;

            case EMBEDDEDSET:
                convertedValue = OType.convert(valueToConvert, Set.class);
                break;
        }
        return  convertedValue;
    }

    /**
     * This method retrieves all stored instances of a given class.
     * @param entityClass
     * @param entityProperties a map containing property values to use in selection, with the property name used as a key
     * @return
     */
    public Set<DocumentEntity> retrieveAllOfClass(EntityClass entityClass, Map<String, PropertyValue> entityProperties) {
        HashSet<DocumentEntity> documents = new HashSet<>();
        try (ODatabaseSession db = connection.getDatabaseSession()) {
            String stmt = "SELECT FROM " + entityClass.getClassName();

            OResultSet rs;
            // check if there are properties that we need
            if (entityProperties != null && !entityProperties.isEmpty()) {
                Map<String, Object> params = new HashMap<>();
                StringJoiner stringJoiner = new StringJoiner(" AND ");
                stmt += " WHERE ";
                for (String propertyName : entityProperties.keySet()) {
                    stringJoiner.add(propertyName + " = :" + propertyName);
                    params.put(propertyName, getConvertedValue(entityProperties.get(propertyName).getType(), entityProperties.get(propertyName).getValue()));
                }
                stmt += stringJoiner.toString();
                rs = db.query(stmt, params);
            }
            else {
                rs = db.query(stmt);
            }

            while (rs.hasNext()) {
                OResult result = rs.next();
                if (result.isVertex()) {
                    documents.add(extractVertexEntity(result, entityClass));
                }
                else if (result.isEdge()) {
                    documents.add(extractEdgeEntity(result));
                }
                // should not happen, but we just ignore the result
            }
        }
        return documents;
    }

    /**
     * THis method retrieves an entity of a given entity class from the data store, for a given id value.
     * @param entityClass
     * @param idValue
     * @return
     */
    public DocumentEntity retrieveEntity(EntityClass entityClass, Object idValue) {
        try (ODatabaseSession db = connection.getDatabaseSession()) {
            // first we have to retrieve the identifying field
            Property identifier = entityClass.getIdentifier();
            if (identifier == null) return null; // cannot search without an id field

            String idField = identifier.getPropertyName();
            // convert the id value to the correct type to use in the database query
            idValue = getConvertedValue(identifier.getPropertyType(), idValue);

            // prepare a statement and execute it
            String stmt = "SELECT FROM " + entityClass.getClassName() + " WHERE " + idField + " = :" + idField;
            // get the id parameter ready
            Map<String, Object> params = new HashMap<>();
            params.put(idField, idValue);
            //execute the query using statement and parameters
            OResultSet rs = db.query(stmt, params);

            // process the results
            if (!rs.hasNext()) {
                return null;
            }

            OResult oResult = rs.next();
            if (oResult.isVertex()) {
                return extractVertexEntity(oResult, entityClass);
            }
            else if (oResult.isEdge()) {
                return extractEdgeEntity(oResult);
            }
            else {
                return null;
            }

        }
    }

    /**
     * Helper method that will extract a retrieved data store object into a vertex entity instance.
     * @param result
     * @param entityClass
     * @return
     */
    private VertexEntity extractVertexEntity(OResult result, EntityClass entityClass) {
        Optional<OVertex> op = result.getVertex();
        if (!op.isPresent()) return null;
        OVertex oVertex = op.get();
        // first we set the attributes
        VertexEntity vertexEntity = new VertexEntity(entityClass);
        setProperties(vertexEntity, oVertex);

        // next, we want to set the incoming and outgoing edges
        for (OEdge edge : oVertex.getEdges(ODirection.OUT)) {
            // look up the entity class for the edge entity
            EdgeEntity edgeEntity = processEdgeEntity(vertexEntity, edge, true);
            if (edgeEntity == null) continue;
            vertexEntity.addOutgoingEdge(edgeEntity);
        }

        for (OEdge edge : oVertex.getEdges(ODirection.IN)) {
            // look up the entity class for the edge entity
            EdgeEntity edgeEntity = processEdgeEntity(vertexEntity, edge, false);
            if (edgeEntity == null) continue;
            vertexEntity.addIncomingEdge(edgeEntity);
        }

        return vertexEntity;
    }

    /**
     * Helper method that will create an edge entity instance for a retrieved edge object from the data store.
     * @param vertexEntity
     * @param edge
     * @param vertexIsSource
     * @return
     */
    private EdgeEntity processEdgeEntity(VertexEntity vertexEntity, OEdge edge, boolean vertexIsSource) {
        Optional<OClass> opClass = edge.getSchemaType();
        // if the edge does not have a class for some reason, we cannot process it
        if (!opClass.isPresent()) return null;
        OClass oEdgeClass = opClass.get();
        EntityClassFactory.EntityClassName edgeClassName = EntityClassFactory.getEntityClassName(oEdgeClass.getName());
        EntityClass edgeEntityClass = EntityClassFactory.createEntityClass(edgeClassName);
        if (edgeEntityClass == null) return null;

        //@todo ideally the target entity would have a lazy loading implementation
        // get the vertex endpoint that we do not have yet
        OVertex targetVertex = vertexIsSource ? edge.getTo() : edge.getFrom();
        //get the class of the target
        if (!targetVertex.getSchemaType().isPresent()) return null;
        OClass oTargetClass = targetVertex.getSchemaType().get();
        EntityClassFactory.EntityClassName targetClassName = EntityClassFactory.getEntityClassName(oTargetClass.getName());
        EntityClass targetEntityClass = EntityClassFactory.createEntityClass(targetClassName);
        VertexEntity targetEntity = new VertexEntity(targetEntityClass);
        // set the attributes on the target vertex
        setProperties(targetEntity, targetVertex);

        EdgeEntity edgeEntity;
        if (vertexIsSource) {
            edgeEntity = new EdgeEntity(edgeEntityClass, vertexEntity, targetEntity);
        }
        else {
            edgeEntity = new EdgeEntity(edgeEntityClass, targetEntity, vertexEntity);
        }
        // set the attributes on the edge
        setProperties(edgeEntity, edge);
        return edgeEntity;
    }


    /**
     * Helper method that will extract a retrieved data store object into a vertex entity instance.
     * @param result
     * @return
     */
    private EdgeEntity extractEdgeEntity(OResult result) {
        // check if the result contains an edge
        if (!result.getEdge().isPresent()) return null;
        OEdge oEdge = result.getEdge().get();

        // get the source vertex
        OVertex sourceVertex = oEdge.getFrom();
        // check for the presence of a class
        if (!sourceVertex.getSchemaType().isPresent()) return null;

        OClass sourceVertexClass = sourceVertex.getSchemaType().get();
        EntityClassFactory.EntityClassName sourceClassName = EntityClassFactory.getEntityClassName(sourceVertexClass.getName());
        EntityClass sourceClass = EntityClassFactory.createEntityClass(sourceClassName);
        if (sourceClass == null) return null;

        VertexEntity sourceVertexEntity = new VertexEntity(sourceClass);
        // set the attributes
        setProperties(sourceVertexEntity, sourceVertex);

        // get the rest of the edge
        EdgeEntity edgeEntity = processEdgeEntity(sourceVertexEntity, oEdge, true);
        return edgeEntity;
    }

    // returns a sequence id for an entity property
    private String createSequenceId(EntityClass entityClass, Property property) {
        return entityClass.getClassName() + "-" + property.getPropertyName() + "-seq";
    }

    /**
     * Returns the connection object currently used by the entity manager
     * @return Connection
     */
    public Connection getConnection() {
        return connection;
    }
}
