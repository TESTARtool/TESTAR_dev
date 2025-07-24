/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

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
