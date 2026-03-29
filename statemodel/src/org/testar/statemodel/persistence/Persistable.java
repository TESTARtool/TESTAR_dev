/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence;

public interface Persistable {

    /**
     * This method should return true if it is possible to delay persistence to after a test sequence has finished.
     * @return
     */
    boolean canBeDelayed();

}
