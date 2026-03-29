/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import org.testar.core.Assert;
import org.testar.core.alayer.Role;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.exceptions.NoSuchTagException;
import org.testar.core.util.Util;

public class ActivateSystem extends TaggableBase implements Action {

    private static final long serialVersionUID = 4023460564018645348L;

    public void run(SUT system, State state, double duration) throws ActionFailedException {
        Assert.notNull(system);
        Assert.isTrue(duration >= 0);

        try {
            double start = Util.time();
            Runnable activator = system.get(Tags.SystemActivator);
            activator.run();
            Util.pause(duration - (Util.time() - start));
        } catch (NoSuchTagException nste) {
            throw new ActionFailedException(nste);
        }
    }

    public String toString() {
        return "Bring the system to the foreground.";
    }

    @Override
    public String toString(Role... discardParameters) {
        return toString();
    }

    @Override
    public String toShortString() {
        Role r = get(Tags.Role, null);
        if (r != null) {
            return r.toString();
        } else {
            return toString();
        }
    }

    @Override
    public String toParametersString() {
        return "";
    }

}
