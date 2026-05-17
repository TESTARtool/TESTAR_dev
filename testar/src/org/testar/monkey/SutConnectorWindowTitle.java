/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 - 2026 Universitat Politecnica de Valencia - www.upv.es
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
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.plugin.NativeLinker;
import org.testar.screenshot.ScreenshotProviderFactory;
import org.testar.serialisation.ScreenshotSerialiser;
import org.testar.util.IndexUtil;

import java.awt.GraphicsEnvironment;
import java.util.List;

public class SutConnectorWindowTitle implements SutConnector {

    private String windowTitle;
    private double maxEngangeTime;
    private StateBuilder builder;
    private boolean forceToForeground;

    public SutConnectorWindowTitle(String windowTitle, double maxEngangeTime, StateBuilder builder, boolean forceToForeground) {
        this.windowTitle = windowTitle;
        this.maxEngangeTime = maxEngangeTime;
        this.builder = builder;
        this.forceToForeground = forceToForeground;
    }

    @Override
    public SUT startOrConnectSut() throws SystemStartException {
        long now = System.currentTimeMillis();
        do{
            Util.pauseMs(100);
            List<SUT> suts = NativeLinker.getNativeProcesses();
            if (suts != null){
                for (SUT theSUT : suts){
                    State state = getStateByWindowTitle(theSUT);
                    // If the system is in the foreground or if the option to force to the foreground is enabled
                    if (state.get(Tags.Foreground) || forceToForeground){
                        for (Widget w : state){
                            Role role = w.get(Tags.Role, null);
                            if (role != null && Role.isOneOf(role, NativeLinker.getNativeRole_Window())){
                                String title = w.get(Tags.Title, null);
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
        if(!GraphicsEnvironment.isHeadless() && viewPort != null){
			AWTCanvas screenshot = ScreenshotProviderFactory.current().getStateshotBinary(state);

			if(screenshot != null) {
				String screenshotPath = ScreenshotSerialiser.saveStateshot(state.get(Tags.ConcreteID, "NoConcreteIdAvailable"), screenshot);
				state.set(Tags.ScreenshotPath, screenshotPath);
			}
        }

        state = IndexUtil.calculateZIndices(state);

        return state;
    }

}
