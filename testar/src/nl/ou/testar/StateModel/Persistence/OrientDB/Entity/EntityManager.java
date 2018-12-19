package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.tinkerpop.blueprints.impls.orient.*;
import nl.ou.testar.StateModel.Exception.EntityNotFoundException;
import org.fruit.alayer.Visualizer;

import java.util.*;

public class EntityManager {

    // factory that will create graphs
    private final OrientGraphFactory graphFactory;

    // orient db instance that will create database sessions
    private OrientDB orientDB;

    /**
     * Constructor
     * @param connectionString
     * @param username
     * @param password
     */
    public EntityManager(final String connectionString, final String username, final String password) {
        graphFactory = new OrientGraphFactory(connectionString, username, password);
        orientDB = new OrientDB("remote:/localhost/", OrientDBConfig.defaultConfig());
    }


    private boolean hasVertex(VertexEntity vertexEntity, ODatabaseSession db) {
        boolean hasVertex = true;
        try {
            retrieveVertex(vertexEntity, db);
        }
        catch (EntityNotFoundException e) {
            hasVertex = false;
        }
        return hasVertex;
    }

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
        params.put(idField, vertexEntity.getPropertyValue(idField).right());
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
        params.put(idField, edgeEntity.getPropertyValue(idField).right());
        //execute the query using statement and parameters
        OResultSet rs = db.query(stmt, params);
        return rs;
    }

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
        String sourceId = (String)edgeEntity.getPropertyValue(sourceIdName).right();
        String targetId = (String)edgeEntity.getPropertyValue(targetIdName).right();

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
        System.out.println("classname: " + entityClass.getClassName());
        try (ODatabaseSession db = orientDB.open("testar", "testar", "testar")) {
            // check if the class already exists
            OClass oClass = db.getClass(entityClass.getClassName());
            if (oClass != null) return;

            // no class yet, let's create it
            String entitySuperClass = entityClass.isVertex() ? "V" : entityClass.isEdge() ? "E" : "";
            String superClassName = entityClass.getSuperClassName() != null ? entityClass.getSuperClassName() : entitySuperClass;
            oClass = db.createClass(entityClass.getClassName(), superClassName);

            // set the properties
            for (Property property : entityClass.getProperties()) {
                OProperty dbProperty = null;
                // for linked and embedded type a childtype needs to be specified
                if (property.getPropertyType().isEmbedded() || property.getPropertyType().isLink()) {
                    dbProperty = oClass.createProperty(property.getPropertyName(), property.getPropertyType(), property.getChildType());
                }
                else {
                    dbProperty = oClass.createProperty(property.getPropertyName(), property.getPropertyType());
                }

                dbProperty.setReadonly(property.isReadOnly());
                dbProperty.setMandatory(property.isMandatory());
                dbProperty.setNotNull(!property.isNullable());
            }

            // we add an index for the identifier fields for fast lookup
            Property identifier = entityClass.getIdentifier();
            if (identifier != null) {
                String indexField = entityClass.getClassName() + "." + identifier.getPropertyName() + "Idx";
                oClass.createIndex(indexField, OClass.INDEX_TYPE.UNIQUE,identifier.getPropertyName());
            }
        }
    }

    public void saveEntity(DocumentEntity entity) {
        try (ODatabaseSession db = orientDB.open("testar", "testar", "testar")) {
            if (entity instanceof VertexEntity) {
                saveVertexEntity((VertexEntity) entity, db);
            }
            else if (entity instanceof EdgeEntity) {
                saveEdgeEntity((EdgeEntity) entity, db);
            }
        }
    }

    private void saveVertexEntity(VertexEntity entity, ODatabaseSession db) {
        OVertex oVertex;
        // check to see if the vertex already exists in the database
        try {
            oVertex = retrieveVertex(entity, db);
        }
        catch (EntityNotFoundException e) {
            // vertex doesn't exist yet. No problemo. We'll create one.
            oVertex = db.newVertex(entity.getEntityClass().getClassName());
        }

        // now we have to add or update properties!
        for (String propertyName : entity.getPropertyNames()) {
            setProperty(oVertex, propertyName, entity.getPropertyValue(propertyName).right());
        }
        oVertex.save();
    }

    private void saveEdgeEntity(EdgeEntity entity, ODatabaseSession db) {
        // an edge always needs both a source and a target vertex
        // an edge has a source and target vertex
        if (entity.getSourceEntity() == null || entity.getTargetEntity() == null) {
            return;
            //@todo we could at some point implement some error handling here, but for now we simply do not store the edge
        }

        // make sure the edge's endpoints exist in the database
        if (!hasVertex(entity.getSourceEntity(), db)) {
            saveVertexEntity(entity.getSourceEntity(), db);
        }
        if (!hasVertex(entity.getTargetEntity(), db)) {
            saveVertexEntity(entity.getTargetEntity(), db);
        }

        // check if the edge entity already exists
        OEdge edge;
        try {
            edge = retrieveEdge(entity, db);
        } catch (EntityNotFoundException e) {
            // edge doesn't exist yet. Let's create a new one
            try {
                OVertex sourceVertex = retrieveVertex(entity.getSourceEntity(), db);
                OVertex targetVertex = retrieveVertex(entity.getTargetEntity(), db);
                edge = sourceVertex.addEdge(targetVertex, entity.getEntityClass().getClassName());
            } catch (EntityNotFoundException e1) {
                // should not happen at this point
                e1.printStackTrace();
                return;
            }
        }

        // now we have to add or update properties!
        for (String propertyName : entity.getPropertyNames()) {
            setProperty(edge, propertyName, entity.getPropertyValue(propertyName).right());
        }
        edge.save();
    }

    /**
     * Helper method to format a property before adding it to a map.
     * This is a bit of a dirty solution to prevent OrientDB from protesting to certain values that are
     * stored. Will need to investigate further at some point in time.
     * @param element
     * @param propertyName
     * @param propertyValue
     */
    private void setProperty(OElement element, String propertyName, Object propertyValue) {
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
        } else
            element.setProperty(propertyName, propertyValue.toString());
    }
}
