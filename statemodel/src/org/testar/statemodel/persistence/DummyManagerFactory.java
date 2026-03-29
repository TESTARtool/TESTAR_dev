/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence;

import org.testar.core.tag.TaggableBase;

public class DummyManagerFactory implements PersistenceManagerFactory {

    @Override
    public PersistenceManager getPersistenceManager(TaggableBase settings) {
        return new DummyManager();
    }
}
