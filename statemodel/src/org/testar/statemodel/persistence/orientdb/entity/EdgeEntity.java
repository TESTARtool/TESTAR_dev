/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb.entity;

import java.util.Objects;

public class EdgeEntity extends DocumentEntity {

    private final VertexEntity sourceEntity;
    private final VertexEntity targetEntity;

    public EdgeEntity(EntityClass entityClass, VertexEntity sourceEntity, VertexEntity targetEntity) {
        super(Objects.requireNonNull(entityClass, "EntityClass cannot be null"));
        this.sourceEntity = Objects.requireNonNull(sourceEntity, "VertexEntity sourceEntity cannot be null");
        this.targetEntity = Objects.requireNonNull(targetEntity, "VertexEntity targetEntity cannot be null");
    }

    /**
     * Returns this edge's source vertex entity
     * @return
     */
    public VertexEntity getSourceEntity() {
        return sourceEntity;
    }

    /**
     * Returns this edge's target vertex entity
     * @return
     */
    public VertexEntity getTargetEntity() {
        return targetEntity;
    }
}
