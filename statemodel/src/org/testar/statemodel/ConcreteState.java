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

import org.testar.statemodel.persistence.Persistable;
import org.testar.monkey.alayer.Tag;

import java.util.Set;

public class ConcreteState extends Widget implements Persistable {

    // a set of tags that was used in creating the concrete state id
    private Set<Tag<?>> tags;

    /**
     * The abstract state that is abstracted from this concrete state.
     */
    private AbstractState abstractState;

    // a byte array holding the screenshot for this state
    private byte[] screenshot;

    public ConcreteState(String id, Set<Tag<?>> tags, AbstractState abstractState) {
        super(id);
        this.tags = tags;
        this.setRootWidget(this);
        this.abstractState = abstractState;
    }

    /**
     * Retrieves the screenshot data for this state.
     * @return
     */
    public byte[] getScreenshot() {
        return screenshot;
    }

    /**
     * Sets the screenshot data for this state.
     * @param screenshot
     */
    public void setScreenshot(byte[] screenshot) {
        this.screenshot = screenshot;
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
