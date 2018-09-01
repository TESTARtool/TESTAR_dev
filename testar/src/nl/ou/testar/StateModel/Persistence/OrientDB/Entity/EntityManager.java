package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
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
    public Vertex getVertexWithFilter(String filterField, String filterValue) throws EntityNotFoundException {
        OrientGraph graph = graphFactory.getTx();
        Iterable<Vertex> vertices = graph.getVertices(filterField, filterValue);
        if (vertices instanceof Collection) {
            int size = ((Collection<?>) vertices).size();
            if (size == 0 || size > 1) {
                //@todo should split this into different exceptions at some point
                throw new EntityNotFoundException();
            }
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

    private void createVertexClass(EntityClass entityClass) {
        OrientGraphNoTx graph = graphFactory.getNoTx();
        OrientVertexType vertexType = graph.getVertexType(entityClass.getClassName());
        if (vertexType == null) {
            // no vertex class with this name exists yet. Let's make one!
            vertexType = graph.createVertexType(entityClass.getClassName());
            for (Property property : entityClass.getProperties()) {
                OrientVertexType.OrientVertexProperty vertexProperty = vertexType.createProperty(property.getPropertyName(), property.getPropertyType());
                vertexProperty.setReadonly(property.isReadOnly());
                vertexProperty.setMandatory(property.isMandatory());
                vertexProperty.setNotNull(!property.isNullable());
            }
        }
    }

    private void createEdgeClass(EntityClass entityClass) {
        OrientGraphNoTx graph = graphFactory.getNoTx();
        OrientEdgeType edgeType = graph.getEdgeType(entityClass.getClassName());
        if (edgeType == null) {
            // no edge class with this name exists yet. Let's make one!
            OrientEdgeType orientEdgeType = graph.createEdgeType(entityClass.getClassName());
            for (Property property : entityClass.getProperties()) {
                OProperty edgeProperty = orientEdgeType.createProperty(property.getPropertyName(), property.getPropertyType());
                edgeProperty.setReadonly(property.isReadOnly());
                edgeProperty.setMandatory(property.isMandatory());
                edgeProperty.setNotNull(!property.isNullable());
            }
        }
    }



}
