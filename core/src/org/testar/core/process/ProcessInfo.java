/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.process;

import org.testar.core.state.SUT;

public class ProcessInfo {
    public SUT sut;
    public long pid;
    public long handle;
    public String Desc;

    public ProcessInfo(SUT sut, long pid, long handle, String desc){
        this.sut = sut;
        this.pid = pid;
        this.handle = handle;
        this.Desc = desc;
    }

    public String toString(){
        return "PID <" + this.pid + "> HANDLE <" + this.handle + "> DESC <" + this.Desc + ">";
    }
}
