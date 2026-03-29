/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb.hydrator;

import org.testar.statemodel.exceptions.HydrationException;
import org.testar.statemodel.persistence.orientdb.entity.DocumentEntity;

public interface EntityHydrator<T extends DocumentEntity> {

    /**
     * This method will populate the target object with data extracted from the source object.
     * @param target
     * @param source
     * @return
     */
    public void hydrate(T target, Object source) throws HydrationException;

}
