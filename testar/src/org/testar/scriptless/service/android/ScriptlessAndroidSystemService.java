/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.android;

import org.testar.core.service.SystemService;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.service.ScriptlessSystemService;

public class ScriptlessAndroidSystemService extends ScriptlessSystemService {

    public ScriptlessAndroidSystemService(SystemService delegate, RuntimeContext runtimeContext) {
        super(delegate, runtimeContext);
    }
}
