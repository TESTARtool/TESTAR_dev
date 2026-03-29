/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.event;

public interface StateModelEventListener {

    /**
     * This method handles the reception of a state model event.
     * @param event
     */
    void eventReceived(StateModelEvent event);

    /**
     * This method sets the event listener to active or deactive
     * @param listening
     */
    void setListening(boolean listening);

}
