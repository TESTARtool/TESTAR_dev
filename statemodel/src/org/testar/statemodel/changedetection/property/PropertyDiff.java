/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.statemodel.changedetection.property;

import java.util.Objects;

import org.testar.statemodel.changedetection.delta.DiffType;

public class PropertyDiff {

    private final String propertyName;
    private final String oldValue;
    private final String newValue;
    private final DiffType diffType;

    public PropertyDiff(String propertyName, String oldValue, String newValue, DiffType diffType) {
        this.propertyName = Objects.requireNonNull(propertyName, "propertyName cannot be null");
        this.diffType = Objects.requireNonNull(diffType, "diffType cannot be null");
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public DiffType getDiffType() {
        return diffType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PropertyDiff)) {
            return false;
        }
        PropertyDiff other = (PropertyDiff) obj;
        return diffType == other.diffType &&
                propertyName.equals(other.propertyName) &&
                Objects.equals(oldValue, other.oldValue) &&
                Objects.equals(newValue, other.newValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyName, oldValue, newValue, diffType);
    }

}
