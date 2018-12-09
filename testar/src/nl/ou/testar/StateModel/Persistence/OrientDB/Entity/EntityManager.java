package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.orient.*;
import nl.ou.testar.StateModel.Exception.EntityNotFoundException;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Visualizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class EntityManager {

    // factory that will create graphs
    private final OrientGraphFactory graphFactory;

    /**
     * Constructor
     * @param connectionString
     * @param username
     * @param password
     */
    public EntityManager(final String connectionString, final String username, final String password) {
        graphFactory = new OrientGraphFactory(connectionString, username, password);
    }

    /**
     * This method checks if the database already contains a given vertex.
     * @param vertexEntity
     * @param graph
     * @return
     */
    private boolean hasVertex (VertexEntity vertexEntity, OrientGraph graph) {
        boolean hasVertex = true;
        try {
            retrieveVertex(vertexEntity, graph);
        }
        catch (EntityNotFoundException ex) {
            hasVertex = false;
        }
        return hasVertex;
    }

    /**
     * This method attempts to retrieve a vertex from the graph database
     * @param vertexEntity
     * @param graph
     * @return
     * @throws EntityNotFoundException
     */
    private Vertex retrieveVertex(VertexEntity vertexEntity, OrientGraph graph) throws EntityNotFoundException {
        Property identifier = vertexEntity.getEntityClass().getIdentifier();
        if (identifier == null) {
            // cannot retrieve a vertex without identifier
            throw new EntityNotFoundException();
        }

        String searchKey = vertexEntity.getEntityClass().getClassName() + "." + identifier.getPropertyName();
        Object searchValue = vertexEntity.getPropertyValue(identifier.getPropertyName()).right();

        // query the graph database for the vertex based on these identifier values
        Iterable<Vertex> vertices = graph.getVertices(searchKey, searchValue);
        if (!vertices.iterator().hasNext()) {
            throw new EntityNotFoundException();
        }
        return vertices.iterator().next();
    }

    /**
     * This method will attempt to create a new class if it is not already present in the database
     * @param entityClass
     */
    public void createClass(EntityClass entityClass) {
        if (entityClass.getEntityType() == EntityClass.EntityType.Vertex) {
            createVertexClass(entityClass);
        }
        else if (entityClass.getEntityType() == EntityClass.EntityType.Edge) {
            createEdgeClass(entityClass);
        }
    }

    /**
     * This private method attempts to create a new vertex class in the orient database
     * @param entityClass
     */
    private void createVertexClass(EntityClass entityClass) {
        System.out.println("classname: " + entityClass.getClassName());
        OrientGraphNoTx graph = graphFactory.getNoTx();
        try {
            OrientVertexType vertexType = graph.getVertexType(entityClass.getClassName());
            if (vertexType == null) {
                // no vertex class with this name exists yet. Let's make one!
                vertexType = graph.createVertexType(entityClass.getClassName(), entityClass.getSuperClassName());
                // add the classes properties
                for (Property property : entityClass.getProperties()) {
                    OrientVertexType.OrientVertexProperty vertexProperty = null;
                    if (property.getPropertyType().isEmbedded() || property.getPropertyType().isLink()) {
                        vertexProperty = vertexType.createProperty(property.getPropertyName(), property.getPropertyType(), property.getChildType());
                    }
                    else {
                        vertexProperty = vertexType.createProperty(property.getPropertyName(), property.getPropertyType());
                    }
                    vertexProperty.setReadonly(property.isReadOnly());
                    vertexProperty.setMandatory(property.isMandatory());
                    vertexProperty.setNotNull(!property.isNullable());
                }
                // we add an index for the identifier fields for fast lookup
                //@todo ideally, this should be a composite key with a unique restraint
                // however, this is complex to do with the java api, so we'll look at it later
                Property identifier = entityClass.getIdentifier();
                if (identifier != null) {
                    String indexFieldName = identifier.getPropertyName();
                    graph.createKeyIndex(indexFieldName, Vertex.class, new Parameter("class", entityClass.getClassName()), new Parameter("type", "UNIQUE"), new Parameter("collate", "ci"));
                }
            }
        } finally {
            graph.shutdown();
        }
    }

    /**
     * This private method attempts to create a new edge class in the orient database
     * @param entityClass
     */
    private void createEdgeClass(EntityClass entityClass) {
        OrientGraphNoTx graph = graphFactory.getNoTx();
        try {
            OrientEdgeType edgeType = graph.getEdgeType(entityClass.getClassName());
            if (edgeType == null) {
                // no edge class with this name exists yet. Let's make one!
                edgeType = graph.createEdgeType(entityClass.getClassName(), entityClass.getSuperClassName());
                // add the classes properties
                for (Property property : entityClass.getProperties()) {
                    OProperty edgeProperty = null;
                    if (property.getPropertyType().isEmbedded() || property.getPropertyType().isLink()) {
                        edgeProperty = edgeType.createProperty(property.getPropertyName(), property.getPropertyType(), property.getChildType());
                    }
                    else {
                        edgeProperty = edgeType.createProperty(property.getPropertyName(), property.getPropertyType());
                    }
                    edgeProperty.setReadonly(property.isReadOnly());
                    edgeProperty.setMandatory(property.isMandatory());
                    edgeProperty.setNotNull(!property.isNullable());
                }
                // we add an index for the identifier fields for fast lookup
                Property identifier = entityClass.getIdentifier();
                if (identifier != null) {
                    String indexFieldName = identifier.getPropertyName();
                    graph.createKeyIndex(indexFieldName, Edge.class, new Parameter("class", entityClass.getClassName()), new Parameter("type", "UNIQUE"), new Parameter("collate", "ci"));
                }
            }
        } finally {
            graph.shutdown();
        }
    }

    /**
     * This method will save a new or existing entity to the orient database
     * @param entity
     */
    public void saveEntity(DocumentEntity entity) {
        OrientGraph graph = graphFactory.getTx();
        // not a fan of an if/else if structure like this, as it can get out of hand quickly as the application grows
        try {
            if (entity instanceof VertexEntity) {
                saveVertexEntity((VertexEntity) entity, graph);
            }
            else if (entity instanceof EdgeEntity){
                saveEdgeEntity((EdgeEntity) entity, graph);
            }
        } finally {
            if (entity instanceof VertexEntity) {
                for (String propertyName : entity.getPropertyNames()) {
                    System.out.println(propertyName + ":" + entity.getPropertyValue(propertyName).right());
                }
            }
            graph.shutdown();
        }
    }

    /**
     * This method will save a vertex entity to the database
     * @param entity
     */
    private void saveVertexEntity(VertexEntity entity, OrientGraph graph) {
        Vertex vertex;
        // check to see if the entity already exists in the database
        try {
            vertex = retrieveVertex(entity, graph);
            // add the properties
//            for (String propertyName : entity.getPropertyNames()) {
//                vertex.setProperty(propertyName, entity.getPropertyValue(propertyName).right());
//            }
            HashMap<String, Object> props = new HashMap<>();
            for (String propertyName : entity.getPropertyNames()) {
                setProperty(props, propertyName, entity.getPropertyValue(propertyName).right());
            }
            ((OrientVertex)vertex).setProperties(props);
        }
        catch (EntityNotFoundException ex) {
            // we have to create a map containing the properties and pass it to the vertex
            // creation method.
            HashMap<String, Object> props = new HashMap<>();
            for (String propertyName : entity.getPropertyNames()) {
                setProperty(props, propertyName, entity.getPropertyValue(propertyName).right());
            }
            graph.addVertex("class:" + entity.getEntityClass().getClassName(), props);
        }

    }

    /**
     * This method will save an edge entity to the database.
     * @param entity
     */
    private void saveEdgeEntity(EdgeEntity entity, OrientGraph graph) {
        // an edge has a source and target vertex
        if (entity.getSourceEntity() == null || entity.getTargetEntity() == null) {
            return;
            //@todo we could at some point implement some error handling here, but for now we simply do not store the edge
        }
        Vertex sourceVertex;
        Vertex targetVertex;

        if (!hasVertex(entity.getSourceEntity(), graph)) {
            saveVertexEntity(entity.getSourceEntity(), graph);
        }
        if (!hasVertex(entity.getTargetEntity(), graph)) {
            saveVertexEntity(entity.getTargetEntity(), graph);
        }

        try {
            sourceVertex = retrieveVertex(entity.getSourceEntity(), graph);
            targetVertex = retrieveVertex(entity.getTargetEntity(), graph);
        } catch (EntityNotFoundException e) {
            // should not happen at this point
            e.printStackTrace();
            return;
        }

        // we want to add an edge only if there is not yet a similar edge with the same id between the same two states
        Edge newEdge = null;
        for (Edge edge: sourceVertex.getEdges(Direction.OUT, entity.getEntityClass().getClassName())) {

            Property identifier = entity.getEntityClass().getIdentifier();
            String edgeId = edge.getProperty(identifier.getPropertyName());
            String entityId = (String)entity.getPropertyValue(identifier.getPropertyName()).right();

            if (!edgeId.equals(entityId)) {
                continue;
            }

            // so this edge is already in the database based on its identifiers.
            // however, it could be that it is connected to a different target state
            // In that case we want to store the new edge
            Vertex edgeTargetVertex = edge.getVertex(Direction.OUT);
            Property targetIdentifier = entity.getTargetEntity().getEntityClass().getIdentifier();
            String edgeTargetId = edgeTargetVertex.getProperty(targetIdentifier.getPropertyName());
            String newTargetId = targetVertex.getProperty(targetIdentifier.getPropertyName());
            if (edgeTargetId == null || newTargetId == null) {
                continue;
            }
            if (edgeTargetId.equals(newTargetId)) {
                newEdge = edge;
                break;
            }
        }

        // check if we need to create a new edge
        if (newEdge == null) {
            // we have to create a map containing the properties and pass it to the edge
            // creation method.
            HashMap<String, Object> props = new HashMap<>();
            for (String propertyName : entity.getPropertyNames()) {
                props.put(propertyName, entity.getPropertyValue(propertyName).right());
            }
            ((OrientVertex)sourceVertex).addEdge(entity.getEntityClass().getClassName(), (OrientVertex)targetVertex,
                    entity.getEntityClass().getClassName(), null, props);
        }
        else {
            // add the properties
            for (String propertyName : entity.getPropertyNames()) {
                newEdge.setProperty(propertyName, entity.getPropertyValue(propertyName).right());
            }
        }
    }

    /**
     * Helper method to format a property before adding it to a map.
     * @param map
     * @param propertyName
     * @param propertyValue
     */
    private void setProperty(HashMap<String, Object> map, String propertyName, Object propertyValue) {
        if (propertyValue instanceof Boolean)
            map.put(propertyName, ((Boolean) propertyValue).booleanValue());
        else if (propertyValue instanceof Byte)
            map.put(propertyName, ((Byte) propertyValue).byteValue());
        else if (propertyValue instanceof Character)
            map.put(propertyName, ((Character) propertyValue).charValue());
        else if (propertyValue instanceof Double)
            map.put(propertyName, ((Double) propertyValue).doubleValue());
        else if (propertyValue instanceof Float)
            map.put(propertyName, ((Float) propertyValue).floatValue());
        else if (propertyValue instanceof Integer)
            map.put(propertyName, ((Integer) propertyValue).intValue());
        else if (propertyValue instanceof Long)
            map.put(propertyName, ((Long) propertyValue).longValue());
        else if (propertyValue instanceof Short)
            map.put(propertyName, ((Short) propertyValue).shortValue());
        else if (propertyValue instanceof Visualizer) {
            //skip Don't put visualizer in the graph since it has no meaning for graph.
            //It will get a meaning when we want to use the data for reply.
        } else
            map.put(propertyName, propertyValue.toString());
    }
}
