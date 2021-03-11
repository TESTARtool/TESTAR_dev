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

package org.fruit.monkey;

import es.upv.staq.testar.FlashFeedback;
import es.upv.staq.testar.NativeLinker;
import nl.ou.testar.SystemProcessHandling;
import org.fruit.Util;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.StateBuilder;
import org.fruit.alayer.Tags;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.windows.WinApiException;

public class WindowsCommandLineSutConnector implements SutConnector {

    private String SUTConnectorValue;
    private boolean processListenerEnabled;
    private double startupTime;
    private long maxEngageTime;
    private StateBuilder builder;
    private boolean tryToKillIfRunning = true; //set to false after 1st re-try

    public WindowsCommandLineSutConnector(String SUTConnectorValue, boolean processListenerEnabled, double startupTime, long maxEngageTime, StateBuilder builder) {
        this.SUTConnectorValue = SUTConnectorValue;
        this.processListenerEnabled = processListenerEnabled;
        this.startupTime = startupTime;
        this.maxEngageTime = maxEngageTime;
        this.builder = builder;
    }

    @Override
    public SUT startOrConnectSut() {

        SUT sut = NativeLinker.getNativeSUT(SUTConnectorValue, processListenerEnabled);
        //Print info to the user to know that TESTAR is NOT READY for its use :-(
        String printSutInfo = "Waiting for the SUT to be accessible ...";
        int timeFlash = (int)startupTime;

        //Refresh the flash information, to avoid that SUT hide the information
        int countTimeFlash = 0;
        while(countTimeFlash<timeFlash && !sut.isRunning()) {
            FlashFeedback.flash(printSutInfo, 2000);
            countTimeFlash += 2000;
        }

        final long now = System.currentTimeMillis();
        final long ENGAGE_TIME = tryToKillIfRunning ? Math.round(maxEngageTime / 2.0) : maxEngageTime; // half time is expected for the implementation
        State state;
        do{
            if (sut.isRunning()){
                //Print info to the user to know that TESTAR is READY for its use :-)
                printSutInfo = "SUT is READY";
                FlashFeedback.flash(printSutInfo,2000);
                System.out.println("SUT is running after <" + (System.currentTimeMillis() - now) + "> ms ... waiting UI to be accessible");
                state = builder.apply(sut);
                if (state != null && state.childCount() > 0) {
                    long extraTime = tryToKillIfRunning ? 0 : ENGAGE_TIME;
                    System.out.println("SUT accessible after <" + (extraTime + (System.currentTimeMillis() - now)) + "> ms");
                    return sut;
                } else {
                    // SUT process is running but GUI needs time to load
                    printSutInfo = "Waiting for the GUI to be accessible ...";
                    FlashFeedback.flash(printSutInfo, 2000);
                    System.out.println("SUT is running but: Waiting for the GUI to be accessible...");
                }
            } else {
                //Print info to the user to know that TESTAR is NOT READY for its use :-(
                printSutInfo = "Waiting for the SUT to be accessible ...";
                FlashFeedback.flash(printSutInfo, 1000);
            }
            Util.pauseMs(500);
        } while (System.currentTimeMillis() - now < maxEngageTime); //TODO runtime controls QUIT does not work now: mode() != RuntimeControlsProtocol.Modes.Quit &&

        if (sut.isRunning()) {
            sut.stop();
        }
        // If SUT didn't run and it is a java app, probably path exception
        else if(SUTConnectorValue.contains("java -jar")) {
            System.out.println("Unable to start the SUT of the java application: " + SUTConnectorValue);
            throw new WinApiException("JAVA SUT PATH EXCEPTION");
        }

        // issue starting the SUT
        if (tryToKillIfRunning){
            System.out.println("Unable to start the SUT after <" + maxEngageTime + "> ms");
            tryToKillIfRunning = false;
            killSutProcesses(sut, maxEngageTime);
            return startOrConnectSut();
        } else
            throw new SystemStartException("SUT not running after <" + Math.round(ENGAGE_TIME * 2.0) + "> ms!");
    }

    private void killSutProcesses(SUT sut, long pendingEngageTime) throws SystemStartException {
        // kill running SUT processes
        System.out.println("Trying to kill potential running SUT: <" + sut.get(Tags.Desc, "No SUT Desc available") + ">");
        if (SystemProcessHandling.killRunningProcesses(sut, Math.round(pendingEngageTime / 2.0))){ // All killed?
            System.out.println("Running SUT processes killed.");
        } else // unable to kill SUT
            throw new SystemStartException("Unable to kill SUT <" + sut.get(Tags.Desc, "No SUT Desc available") + "> while trying to rerun it after <" + pendingEngageTime + "> ms!");
    }
}
