/***************************************************************************************************
 *
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.monkey;

import org.testar.CodingManager;
import org.testar.ProtocolUtil;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.plugin.NativeLinker;

import java.util.List;

public class WindowsWindowTitleSutConnector implements SutConnector {

    private String windowTitle;
    private double maxEngangeTime;
    private StateBuilder builder;
    private boolean forceToForeground;

    public WindowsWindowTitleSutConnector(String windowTitle, double maxEngangeTime, StateBuilder builder, boolean forceToForeground) {
        this.windowTitle = windowTitle;
        this.maxEngangeTime = maxEngangeTime;
        this.builder = builder;
        this.forceToForeground = forceToForeground;
    }

    @Override
    public SUT startOrConnectSut() throws SystemStartException {
        List<SUT> suts = null;
        State state; Role role; String title;
        long now = System.currentTimeMillis();
        do{
            Util.pauseMs(100);
            suts = NativeLinker.getNativeProcesses();
            if (suts != null){
                for (SUT theSUT : suts){
                    state = getStateByWindowTitle(theSUT);
                    // If the system is in the foreground or if the option to force to the foreground is enabled
                    if (state.get(Tags.Foreground) || forceToForeground){
                        for (Widget w : state){
                            role = w.get(Tags.Role, null);
                            if (role != null && Role.isOneOf(role, NativeLinker.getNativeRole_Window())){
                                title = w.get(Tags.Title, null);
                                if (title != null && title.contains(windowTitle)){
                                    System.out.println("SUT with Window Title -" + windowTitle + "- DETECTED!");
                                    return theSUT;
                                }
                            }
                        }
                    }
                }
            }
        } while (System.currentTimeMillis() - now < maxEngangeTime);
        throw new SystemStartException("SUT Window Title not found!: -" + windowTitle + "-");
    }

    protected State getStateByWindowTitle(SUT system) throws StateBuildException {
        Assert.notNull(system);
        State state = builder.apply(system);

        CodingManager.buildIDs(state);

        Shape viewPort = state.get(Tags.Shape, null);
        if(viewPort != null){
            state.set(Tags.ScreenshotPath, ProtocolUtil.getStateshot(state));
            //Don't include WdProtocolUtil option because TESTAR doesn't connect to webdrivers through the windows title
        }

        state = ProtocolUtil.calculateZIndices(state);

        return state;
    }

}
