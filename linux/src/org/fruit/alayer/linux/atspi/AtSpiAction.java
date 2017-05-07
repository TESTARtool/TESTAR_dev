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
    public List<AtSpiActionInfo> actions() { return _actions; }


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