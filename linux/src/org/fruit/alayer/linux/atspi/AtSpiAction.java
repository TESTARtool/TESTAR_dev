/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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

package org.fruit.alayer.linux.atspi;

import org.fruit.alayer.linux.util.BridJHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Java implementation of an AtSpiAction object.
 */
public class AtSpiAction {

    //region Properties

    private long _actionPtr;
    public long actionPtr() {
        return _actionPtr;
    }

    public int actionCount() {
        return LibAtSpi.atspi_action_get_n_actions(_actionPtr, 0);
    }

    private List<AtSpiActionInfo> _actions;
    public List<AtSpiActionInfo> actions() {
      return _actions;
    }

    //endregion

    //region Constructors

    /**
     * Default empty constructor.
     */
    private AtSpiAction() {

    }

    /**
     * Creates a new instance of an AtSpiAction object from a pointer.
     * @param actionPtr Pointer to the AtSpiAction object.
     * @return A Java instance of an AtSpiAction object.
     */
    public static AtSpiAction CreateInstance(long actionPtr) {

        if (actionPtr == 0) {
            return null;
        }

        // Create a new instance.
        AtSpiAction aObj = new AtSpiAction();

        // Fill the instance's properties.
        aObj._actionPtr = actionPtr;

        ArrayList<AtSpiActionInfo> actions = new ArrayList<>();

        for (int i = 0; i < aObj.actionCount(); i++) {

            AtSpiActionInfo ai = new AtSpiActionInfo();

            ai.name = BridJHelper.convertToString(LibAtSpi.atspi_action_get_action_name(actionPtr, i, 0));
            ai.index = i;
            ai.description = BridJHelper.convertToString(LibAtSpi.atspi_action_get_action_description(actionPtr, i, 0));
            ai.keyBinding = BridJHelper.convertToString(LibAtSpi.atspi_action_get_key_binding(actionPtr, i, 0));

            actions.add(ai);
        }

        aObj._actions = actions;

        return aObj;

    }

    //endregion

    /**
     * Performs an action on the AtSpiAccessible object this action belongs to. The action run is specified by
     * an index attainable from the AtSpiActionInfo.
     * @param actionIndex The index of the action to run.
     * @return True if action performed successfully; False otherwise.
     */
    public boolean invoke(int actionIndex) {
        return LibAtSpi.atspi_action_do_action(_actionPtr, actionIndex, 0);
    }

    //region Object overrides

    /**
     * Returns a string representation of an AtSpiAction object.
     * @return Returns a string representation of an AtSpiAction object.
     */
    @Override
    public String toString() {
        return "Action count: " + actionCount();
    }
    //endregion
}
