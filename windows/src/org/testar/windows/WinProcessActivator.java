/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows;

import org.testar.core.exceptions.ActionFailedException;
import org.testar.windows.exceptions.WinApiException;
import org.testar.windows.state.WinProcess;

public final class WinProcessActivator implements Runnable {
    private final long pid;
    
    public WinProcessActivator(long pid){ this.pid = pid; }
    
    public void run() {
        try{
            WinProcess.toForeground(pid);
        }catch(WinApiException wae){
            throw new ActionFailedException(wae);
        }        
    }
}
