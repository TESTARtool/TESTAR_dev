/***************************************************************************************************
 *
 * Copyright (c) 2016 - 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 - 2026 Open Universiteit - www.ou.nl
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

package org.testar.util;

import java.util.Objects;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;

public class IndexUtil {

    private IndexUtil() {
    }

    /**
     * Calculate the max and the min ZIndex of all the widgets in a state
     * 
     * @param state
     */
    public static State calculateZIndices(State state) {
        Objects.requireNonNull(state, "State cannot be null");
        double minZIndex = Double.MAX_VALUE;
        double maxZIndex = Double.MIN_VALUE;
        for (Widget w : state) {
            double zindex = w.get(Tags.ZIndex, 0.0).doubleValue();
            if (zindex < minZIndex)
                minZIndex = zindex;
            if (zindex > maxZIndex)
                maxZIndex = zindex;
        }
        state.set(Tags.MinZIndex, minZIndex);
        state.set(Tags.MaxZIndex, maxZIndex);
        return state;
    }

}
