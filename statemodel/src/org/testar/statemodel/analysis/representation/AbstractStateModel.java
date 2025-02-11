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

package org.testar.statemodel.analysis.representation;

import java.util.List;
import java.util.Set;

public class AbstractStateModel {

    private String applicationVersion;

    private String applicationName;

    private String modelIdentifier;

    private Set abstractionAttributes;

    private List<TestSequence> sequences;

    public AbstractStateModel(String applicationName, String applicationVersion, String modelIdentifier, Set abstractionAttributes, List<TestSequence> sequences) {
        this.applicationVersion = applicationVersion;
        this.applicationName = applicationName;
        this.modelIdentifier = modelIdentifier;
        this.abstractionAttributes = abstractionAttributes;
        this.sequences = sequences;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getModelIdentifier() {
        return modelIdentifier;
    }

    public void setModelIdentifier(String modelIdentifier) {
        this.modelIdentifier = modelIdentifier;
    }

    public Set getAbstractionAttributes() {
        return abstractionAttributes;
    }

    public void setAbstractionAttributes(Set abstractionAttributes) {
        this.abstractionAttributes = abstractionAttributes;
    }

    public List<TestSequence> getSequences() {
        return sequences;
    }

    public void setSequences(List<TestSequence> sequences) {
        this.sequences = sequences;
    }

    /**
     * This method will return a string containing the abstraction attributes used in creating the model.
     * @return
     */
    public String getAbstractionAttributesAsString() {
        return (String)abstractionAttributes.stream().sorted().reduce("", (base, string) -> base.equals("") ? string : base + ", " + string);
    }
}
