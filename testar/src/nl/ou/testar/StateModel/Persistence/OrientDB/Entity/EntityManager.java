package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.*;
import nl.ou.testar.StateModel.Exception.EntityNotFoundException;

import java.util.Collection;

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
     * Return a single Vertex value based on a filter value
     * @param filterField
     * @param filterValue
     * @return
     * @throws EntityNotFoundException
     */
    public Vertex getVertexWithFilter(String filterField, Object filterValue) throws EntityNotFoundException {
        OrientGraphNoTx graph = graphFactory.getNoTx();
        Iterable<Vertex> vertices = graph.getVertices(filterField, filterValue);
        if (!vertices.iterator().hasNext()) {
            throw new EntityNotFoundException();
        }
        return vertices.iterator().next();
    }

    /**
     * This method checks if the database contains a vertex with the given filter value
     * @param filterfield
     * @param filterValue
     * @return
     */
    public boolean hasVertex (String filterfield, Object filterValue) {
        boolean hasVertex = true;
        try {
            getVertexWithFilter(filterfield, filterValue);
        }
        catch (EntityNotFoundException ex) {
            hasVertex = false;
        }
        return hasVertex;
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
        OrientGraphNoTx graph = graphFactory.getNoTx();
        OrientVertexType vertexType = graph.getVertexType(entityClass.getClassName());
        if (vertexType == null) {
            // no vertex class with this name exists yet. Let's make one!
            vertexType = graph.createVertexType(entityClass.getClassName(), entityClass.getSuperClassName());
            // add the classes properties
            for (Property property : entityClass.getProperties()) {
                OrientVertexType.OrientVertexProperty vertexProperty = vertexType.createProperty(property.getPropertyName(), property.getPropertyType());
                vertexProperty.setReadonly(property.isReadOnly());
                vertexProperty.setMandatory(property.isMandatory());
                vertexProperty.setNotNull(!property.isNullable());
            }
        }
    }

    /**
     * This private method attempts to create a new edge class in the orient database
     * @param entityClass
     */
    private void createEdgeClass(EntityClass entityClass) {
        OrientGraphNoTx graph = graphFactory.getNoTx();
        OrientEdgeType edgeType = graph.getEdgeType(entityClass.getClassName());
        if (edgeType == null) {
            // no edge class with this name exists yet. Let's make one!
            edgeType = graph.createEdgeType(entityClass.getClassName(), entityClass.getSuperClassName());
            // add the classes properties
            for (Property property : entityClass.getProperties()) {
                OProperty edgeProperty = edgeType.createProperty(property.getPropertyName(), property.getPropertyType());
                edgeProperty.setReadonly(property.isReadOnly());
                edgeProperty.setMandatory(property.isMandatory());
                edgeProperty.setNotNull(!property.isNullable());
            }
        }
    }

    /**
     * This method will save a new or exisiting entity to the orient database
     * @param entity
     */
    public void saveEntity(DocumentEntity entity) {
        // not a fan of an if/else if structure like this, as it can get out of hand quickly as the application grows
        if (entity instanceof VertexEntity) {
            saveVertexEntity((VertexEntity) entity);
        }
        else if (entity instanceof EdgeEntity){
            saveEdgeEntity((EdgeEntity) entity);
        }
    }

    /**
     * This method will save a vertex entity to the database
     * @param entity
     */
    private void saveVertexEntity(VertexEntity entity) {
        Vertex vertex;
        // check to see if the entity already exists in the database
        String idField = entity.getEntityClass().getClassName() + "." + entity.getEntityClass().getIdentifier().getPropertyName();
        Object idValue = entity.getPropertyValue(entity.getEntityClass().getIdentifier().getPropertyName()).right();
        try {
            vertex = getVertexWithFilter(idField, idValue);
        }
        catch (EntityNotFoundException ex) {
            OrientGraph graph = graphFactory.getTx();
            vertex = graph.addVertex("class:" + entity.getEntityClass().getClassName());
        }
        // add the properties
        for (String propertyName : entity.getPropertyNames()) {
            vertex.setProperty(propertyName, entity.getPropertyValue(propertyName).right());
        }
    }

    /**
     * This method will save an edge entity to the database.
     * @param entity
     */
    private void saveEdgeEntity(EdgeEntity entity) {
        // an edge has a source and target vertex
        if (entity.getSourceEntity() == null || entity.getTargetEntity() == null) {
            return;
            //@todo we could at some point implement some error handling here, but for now we simply do not store the edge
        }
        // we will assume that vertices have already been created or updated. As a backup to this assumption,
        // we will create states if for some reason they do not yet exist in the database, but we will not update them.
        // this way, if for some reason at a later point in time we move to async saving of entities into the database,
        // it is still fine to create them, but updating would be dangerous. This keeps things somewhat future proof.
        String sourceIdField = entity.getSourceEntity().getEntityClass().getClassName() + "." + entity.getSourceEntity().getEntityClass().getIdentifier().getPropertyName();
        Object sourceIdValue = entity.getSourceEntity().getPropertyValue(entity.getSourceEntity().getEntityClass().getIdentifier().getPropertyName()).right();

        String targetIdField = entity.getTargetEntity().getEntityClass().getClassName() + "." + entity.getTargetEntity().getEntityClass().getIdentifier().getPropertyName();
        Object targetIdValue = entity.getTargetEntity().getPropertyValue(entity.getTargetEntity().getEntityClass().getIdentifier().getPropertyName()).right();
        Vertex sourceVertex;
        Vertex targetVertex;

        if (!hasVertex(sourceIdField, sourceIdValue)) {
            saveVertexEntity(entity.getSourceEntity());
        }
        if (!hasVertex(targetIdField, targetIdValue)) {
            saveVertexEntity(entity.getTargetEntity());
        }

        try {
            sourceVertex = getVertexWithFilter(sourceIdField, sourceIdValue);
            targetVertex = getVertexWithFilter(targetIdField, targetIdValue);
        } catch (EntityNotFoundException e) {
            // should happen at this point
            e.printStackTrace();
            return;
        }

        // we want to add an edge only if there is not yet a similar edge with the same id between the same two states
        Edge newEdge = null;
        for (Edge edge: sourceVertex.getEdges(Direction.OUT, entity.getEntityClass().getClassName())) {
            Property identifier = entity.getEntityClass().getIdentifier();
            // check if the id of the actions matches
            String edgeId = edge.getProperty(identifier.getPropertyName());
            String entityId = (String)entity.getPropertyValue(identifier.getPropertyName()).right();
            if (edgeId.equals(entityId)) {
                // check if the id of the target vertices matches
                Property targetIdentifier = entity.getTargetEntity().getEntityClass().getIdentifier();
                String targetVertexId = edge.getVertex(Direction.OUT).getProperty(targetIdentifier.getPropertyName());
                String targetEntityId = (String)entity.getTargetEntity().getPropertyValue(targetIdentifier.getPropertyName()).right();
                if (targetVertexId.equals(targetEntityId)) {
                    newEdge = edge;
                    break;
                }
            }
        }

        // check if we need to create a new edge
        if (newEdge == null) {
            newEdge = sourceVertex.addEdge(entity.getEntityClass().getClassName(), targetVertex);
        }
        // add the properties
        for (String propertyName : entity.getPropertyNames()) {
            newEdge.setProperty(propertyName, entity.getPropertyValue(propertyName).right());
        }
    }
}
