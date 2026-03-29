/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import java.util.Arrays;
import java.util.Objects;

import org.testar.statemodel.persistence.Persistable;

public class ConcreteState extends ModelWidget implements Persistable {

    /**
     * The abstract state that is abstracted from this concrete state.
     */
    private final AbstractState abstractState;

    // a byte array holding the screenshot for this state
    private byte[] screenshot;

    public ConcreteState(String id, AbstractState abstractState) {
        super(Objects.requireNonNull(id, "ConcreteState ID cannot be null"));
        if (id.trim().isEmpty()) {
            throw new IllegalArgumentException("ConcreteState ID cannot be empty or blank");
        }
        this.setRootWidget(this);
        this.abstractState = Objects.requireNonNull(abstractState, "AbstractState cannot be null");
    }

    /**
     * Retrieves the screenshot data for this state.
     * @return
     */
    public byte[] getScreenshot() {
        return screenshot != null ? Arrays.copyOf(screenshot, screenshot.length) : new byte[0];
    }

    /**
     * Sets the screenshot data for this state.
     * @param screenshot
     */
    public void setScreenshot(byte[] screenshot) {
        this.screenshot = screenshot != null ? Arrays.copyOf(screenshot, screenshot.length) : null;
    }

    /**
     * Returns the abstract state attached to this concrete state
     * @return
     */
    public AbstractState getAbstractState() {
        return abstractState;
    }

    @Override
    public boolean canBeDelayed() {
        return true;
    }
}
