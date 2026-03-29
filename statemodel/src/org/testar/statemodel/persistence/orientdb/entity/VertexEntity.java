/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class VertexEntity extends DocumentEntity {

    // set of this vertex's outgoing edges
    private final Set<EdgeEntity> outgoingEdges;

    // set of this vertex's incoming edges
    private final Set<EdgeEntity> incomingEdges;

    /**
     * Constructor
     * @param entityClass
     */
    public VertexEntity(EntityClass entityClass) {
        super(Objects.requireNonNull(entityClass, "EntityClass cannot be null"));
        outgoingEdges = new HashSet<>();
        incomingEdges = new HashSet<>();
    }

    /**
     * Retrieves the outgoing edges for this vertex.
     * @return
     */
    public Set<EdgeEntity> getOutgoingEdges() {
        return outgoingEdges;
    }

    /**
     * Retrieves the incoming edges for this vertex.
     * @return
     */
    public Set<EdgeEntity> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * Adds an outgoing edge to this vertex.
     * @param edge
     */
    public void addOutgoingEdge(EdgeEntity edge) {
        outgoingEdges.add(Objects.requireNonNull(edge, "OutgoingEdge cannot be null"));
    }

    /**
     * Adds an incoming edge to this vertex.
     * @param edge
     */
    public void addIncomingEdge(EdgeEntity edge) {
        incomingEdges.add(Objects.requireNonNull(edge, "IncomingEdge cannot be null"));
    }
}
