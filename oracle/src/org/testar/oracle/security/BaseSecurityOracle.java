/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.security;

import org.openqa.selenium.devtools.DevTools;
import org.testar.core.verdict.Verdict;

public class BaseSecurityOracle {
    protected SecurityResultWriter securityResultWriter;

    public BaseSecurityOracle(SecurityResultWriter securityResultWriter)
    {
        this.securityResultWriter = securityResultWriter;
    }

    public void addListener(DevTools devTools)
    {
        /** Add listeners **/
    }

    public Verdict getVerdict()
    {
        /** ORACLE **/
        return Verdict.OK;
    }
}
