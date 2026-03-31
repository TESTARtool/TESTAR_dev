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

public class AndroidSystemActionOrientation extends TaggableBase implements Action {

    private static final long serialVersionUID = 138171508205790215L;

    public AndroidSystemActionOrientation(State state) {
        this.set(Tags.Role, AndroidRoles.AndroidWidget);
        this.mapOriginWidget(state);
        this.set(Tags.Desc, toShortString());
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        try {
            AndroidAppiumFramework.changeOrientation();
        } catch(Exception e) {
            System.out.println("Exception trying to change orientation SUT: ");
            System.out.println(e.getMessage());
            throw new ActionFailedException(toShortString());
        }
    }

    @Override
    public String toShortString() {
        return "Execute Android orientation change (portrait vs landscape)";
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
