/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.monkey.alayer.android.actions;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.monkey.alayer.android.enums.AndroidRoles;

public class AndroidBackAction extends TaggableBase implements Action {

    private static final long serialVersionUID = 6808185819403073531L;

    public final Widget widget;

    public AndroidBackAction(State state, Widget widget) {
        this.widget = widget;
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.mapOriginWidget(widget);
        this.set(Tags.Desc, toShortString());
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        try {
            AndroidAppiumFramework.clickBackButton();
        } catch(Exception e) {
            System.out.println("Exception trying to click Android back button: ");
            System.out.println(e.getMessage());
            throw new ActionFailedException(toShortString());
        }
    }

    @Override
    public String toShortString() {
        return "Execute click Android back button";
    }

    @Override
    public String toParametersString() {
        return "";
    }

    @Override
    public String toString(Role... discardParameters) {
        return "";
    }
}
