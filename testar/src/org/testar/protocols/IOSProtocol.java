/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2023 Open Universiteit - www.ou.nl
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

package org.testar.protocols;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.ios.enums.IOSTags;
import org.testar.plugin.NativeLinker;
import org.testar.settings.Settings;

import java.util.Set;

public class IOSProtocol extends GenericUtilsProtocol {
    //Attributes for adding slide actions
    protected static double SCROLL_ARROW_SIZE = 36; // sliding arrows
    protected static double SCROLL_THICK = 16; //scroll thickness

    /**
     * Called once during the life time of TESTAR
     * This method can be used to perform initial setup work
     * @param   settings  the current TESTAR settings as specified by the user.
     */
    @Override
    protected void initialize(Settings settings){
        NativeLinker.addIOS();
        super.initialize(settings);
    }

    /**
     * This method is called when the TESTAR requests the state of the SUT.
     * Here you can add additional information to the SUT's state or write your
     * own state fetching routine. The state should have attached an oracle
     * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
     * state is erroneous and if so why.
     * @return  the current state of the SUT with attached oracle.
     */
    @Override
    protected State getState(SUT system) throws StateBuildException {
    	return super.getState(system);
    }

    /**
     * This method is used by TESTAR to determine the set of currently available actions.
     * You can use the SUT's current state, analyze the widgets and their properties to create
     * a set of sensible actions, such as: "Click every Button which is enabled" etc.
     * The return value is supposed to be non-null. If the returned set is empty, TESTAR
     * will stop generation of the current action and continue with the next one.
     * @param system the SUT
     * @param state the SUT's current state
     * @return  a set of actions
     */
    @Override
    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {

        //The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
        //the foreground. You should add all other actions here yourself.
        // These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
        return super.deriveActions(system,state);
    }

    @Override
    protected boolean isClickable(Widget w) {
        return (w.get(IOSTags.iosClassName, "").equals("XCUIElementTypeButton")
                || w.get(IOSTags.iosClassName, "").equals("XCUIElementTypeCell"));
    }

    @Override
    protected boolean isTypeable(Widget w) {
        return (w.get(IOSTags.iosClassName, "").equals("XCUIElementTypeTextField"));
    }
    
    /**
     * Select one of the available actions (e.g. at random)
     * @param state the SUT's current state
     * @param actions the set of derived actions
     * @return  the selected action (non-null!)
     */
    @Override
    protected Action selectAction(State state, Set<Action> actions){
        return super.selectAction(state, actions);
    }

    @Override
    protected void closeTestSession() {
        super.closeTestSession();
        NativeLinker.cleanIOS();
    }
}
