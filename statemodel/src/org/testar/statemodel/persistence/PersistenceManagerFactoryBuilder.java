/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence;

import org.testar.statemodel.persistence.orientdb.OrientDBManagerFactory;

/**
 * This class creates concrete factory classes following the abstract factory pattern.
 */
public abstract class PersistenceManagerFactoryBuilder {

    public enum ManagerType { ORIENTDB, DUMMY }

    /**
     * This method returns a concrete implementation of the abstract PersistManagerFactory class/interface.
     * @param managerType
     * @return
     */
    public static PersistenceManagerFactory createPersistenceManagerFactory(ManagerType managerType) {
        switch (managerType) {
            case ORIENTDB:
                return new OrientDBManagerFactory();

            case DUMMY:
            default:
                return new DummyManagerFactory();
        }
    }

}
