/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.action;

import org.testar.android.AndroidAppiumFramework;
import org.testar.android.alayer.AndroidRoles;
import org.testar.core.alayer.*;
import org.testar.core.action.Action;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;

public class AndroidSystemActionCall extends TaggableBase implements Action {

    private static final long serialVersionUID = -6556855399091167635L;

    public AndroidSystemActionCall(State state) {
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.mapOriginWidget(state);
        this.set(Tags.Desc, toShortString());
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        try {
            AndroidAppiumFramework.generatePhoneCall();
        } catch(Exception e) {
            System.out.println("Exception trying to generate phone call on SUT: ");
            System.out.println(e.getMessage());
            throw new ActionFailedException(toShortString());
        }
    }

    @Override
    public String toShortString() {
        return "Execute Android system event: Call";
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

