/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.process;

import org.testar.core.process.ProcessHandle;
import org.testar.windows.exceptions.WinApiException;
import org.testar.windows.state.WinProcess;

public final class WinProcHandle implements ProcessHandle {
    private final long pid;
    public WinProcHandle(long pid){ this.pid = pid;    }
    public void kill() { WinProcess.killProcess(pid); }
    public boolean isRunning() { return WinProcess.isRunning(pid); }
    public String name() {
        try{
            return WinProcess.procName(pid);
        }catch(WinApiException wae){
            return null;
        }
    }
    public long pid() { return pid; }
}
