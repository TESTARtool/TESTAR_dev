/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability.android;

import org.testar.config.ConfigTags;
import org.testar.core.Assert;
import org.testar.oracle.Oracle;
import org.testar.oracle.android.log.AndroidLogcatOracle;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.capability.TestSequenceCapability;

public class AndroidTestSequenceCapability extends TestSequenceCapability {

    private final TestSequenceCapability delegate;

    public AndroidTestSequenceCapability(TestSequenceCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public void startTestSequence(RuntimeContext runtimeContext) {
        delegate.startTestSequence(runtimeContext);

        if (runtimeContext.settings().get(ConfigTags.LogOracleEnabled, false)) {
            Oracle logOracle = new AndroidLogcatOracle(runtimeContext.settings());
            logOracle.initialize();
            runtimeContext.setLogOracle(logOracle);
        }
    }
}
