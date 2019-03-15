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

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;

import java.io.IOException;

/**
 * AT-SPI implementation.
 */
@Library("libatspi")
public class LibAtSpi {

    static{
        try {
            BridJ.getNativeLibrary("libatspi");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BridJ.register();
    }

    //***           Registry            ***\\

    /**
     * Gets the number of virtual desktops. NOTE: multiple virtual desktops are not implemented yet; as a consequence, this function always returns 1.
     * @return The number of active virtual desktops.
     */
    public static native int atspi_get_desktop_count();

    /**
     * Gets the virtual desktop indicated by index i. NOTE: currently multiple virtual desktops are not implemented; as
     * a consequence, any i value different from 0 will not return a virtual desktop - instead it will return NULL.
     * @param desktopIndex Index indicating which of the accessible desktops is to be returned.
     * @return Returns a pointer to the i -th virtual desktop's AtspiAccessible representation.
     */
    public static native long atspi_get_desktop(int desktopIndex);

    /**
     * Gets the list of virtual desktops. On return, list will point to a newly-created, NULL terminated array of virtual desktop pointers.
     * It is the responsibility of the caller to free this array when it is no longer needed. NOTE: currently multiple virtual desktops
     * are not implemented; this implementation always returns a Garray with a single AtspiAccessible desktop.
     * @return Returns a GArray of desktops (Type: AtspiAccessible*).
     */
    public static native long atspi_get_desktop_list();

    //***           AtspiAccessible            ***\\

    /**
     * Gets the name of an AtspiAccessible object.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a UTF-8 string indicating the name of the AtspiAccessible object or NULL on exception.
     */
    public static native Pointer<Byte> atspi_accessible_get_name(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets the description of an AtspiAccessible object.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a UTF-8 string describing the AtspiAccessible object or NULL on exception.
     */
    public static native Pointer<Byte> atspi_accessible_get_description(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets (a pointer to) an AtspiAccessible object's parent container.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a pointer to the AtspiAccessible object which contains the given AtspiAccessible instance,
     *         or NULL if the obj has no parent container.
     */
    public static native long atspi_accessible_get_parent(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets the number of children contained by an AtspiAccessible object.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a the number of AtspiAccessible children contained by an AtspiAccessible object or -1 on exception.
     */
    public static native int atspi_accessible_get_child_count(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets a UTF-8 string corresponding to the name of the role played by an object. This method will return useful values for
     * roles that fall outside the enumeration used in atspi_accessible_get_role().
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param childIndex The index of the child to get.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a UTF-8 string specifying the type of UI role played by an AtspiAccessible object.
     */
    public static native long atspi_accessible_get_child_at_index(long accessiblePtr, int childIndex, long errorPtrToPtr);

    /**
     * Gets the index of an AtspiAccessible object within its parent's AtspiAccessible children list.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns an int indicating the index of the AtspiAccessible object in its parent,
     *         or -1 if obj has no containing parent or on exception.
     */
    public static native int atspi_accessible_get_index_in_parent(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets the set of AtspiRelation objects which describes this AtspiAccessible object's relationships with other
     * AtspiAccessible objects.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a (pointer to a) GArray of AtspiRelation pointers or NULL on exception.
     */
    public static native long atspi_accessible_get_relation_set(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets the UI role played by an AtspiAccessible object. This role's name can be obtained via atspi_accessible_get_role_name().
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns the AtspiRole of an AtspiAccessible object.
     */
    public static native int atspi_accessible_get_role(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets a UTF-8 string corresponding to the name of the role played by an object. This method will return useful values for
     * roles that fall outside the enumeration used in atspi_accessible_get_role().
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a UTF-8 string specifying the type of UI role played by an AtspiAccessible object.
     */
    public static native Pointer<Byte> atspi_accessible_get_role_name(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets the states currently held by an object.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @return Returns a pointer to an AtspiStateSet representing an object's current state set.
     */
    public static native long atspi_accessible_get_state_set(long accessiblePtr);

    /**
     * Gets the AttributeSet representing any assigned name-value pair attributes or annotations for this object.
     * For typographic, textual, or textually-semantic attributes, see atspi_text_get_attributes instead.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns the name-value-pair attributes assigned to this object.
     */
    public static native long atspi_accessible_get_attributes(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets a GArray representing any assigned name-value pair attributes or annotations for this object.
     * For typographic, textual, or textually-semantic attributes, see atspi_text_get_attributes_as_array instead.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns the name-value-pair attributes assigned to this object.
     */
    public static native long atspi_accessible_get_attributes_as_array(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets the toolkit name for an AtspiAccessible object. Only works on application root objects.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a UTF-8 string indicating the toolkit name for the AtspiAccessible object or NULL on exception.
     */
    public static native Pointer<Byte> atspi_accessible_get_toolkit_name(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets the toolkit version for an AtspiAccessible object. Only works on application root objects.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a UTF-8 string indicating the toolkit version for the AtspiAccessible object or NULL on exception.
     */
    public static native Pointer<Byte> atspi_accessible_get_toolkit_version(long accessiblePtr, long errorPtrToPtr);

    /**
     * Gets the AtspiCollection interface for an AtspiAccessible.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @return Returns a pointer to an AtspiCollection interface instance, or NULL if obj does
     *         not implement AtspiCollection.
     */
    public static native long atspi_accessible_get_collection_iface(long accessiblePtr);

    /**
     * Gets the AtspiComponent interface for an AtspiAccessible.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @return a pointer to an AtspiComponent interface instance, or NULL if obj does not implement AtspiComponent.
     */
    public static native long atspi_accessible_get_component_iface(long accessiblePtr);

    /**
     * Gets the AtspiAction interface for an AtspiAccessible.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @return Returns a pointer to an AtspiAction interface instance, or NULL if obj does not implement AtspiAction.
     */
    public static native long atspi_accessible_get_action_iface(long accessiblePtr);

    /**
     * Gets the AtspiTable interface for an AtspiAccessible.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @return Returns a pointer to an AtspiValue interface instance, or NULL if obj does not implement AtspiValue.
     */
    public static native long atspi_accessible_get_value_iface(long accessiblePtr);

    /**
     * A set of pointers to strings describing all interfaces supported by an AtspiAccessible.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @return A GArray of strings describing the interfaces supported by the object.
     *         Interfaces are denoted in short-hand (i.e. "Component", "Text" etc.).
     */
    public static native long atspi_accessible_get_interfaces(long accessiblePtr);

    //***           AtSpi StateSet            ***\\

    /**
     * Determines whether a given AtspiStateSet includes a given state; that is, whether state is true
     * for the set in question.
     * @param setPtr A pointer to the AtspiStateSet object on which to operate.
     * @param state An AtspiStateType for which the specified AtspiStateSet will be queried.
     * @return TRUE if state is true/included in the given AtspiStateSet, otherwise FALSE.
     */
    public static native boolean atspi_state_set_contains(long setPtr, int state);

    /**
     * Returns the states in an AtspiStateSet as (a pointer to) an array.
     * @param setPtr A pointer to the AtspiStateSet object on which to operate.
     * @return A GArray of state types representing the current state.
     */
    public static native long atspi_state_set_get_states(long setPtr);

    //***           AtSpiAction            ***\\

    /**
     * Get the number of actions invokable on an AtspiAction implementor.
     * @param actionPtr A pointer to the AtSpiAction object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns an integer indicating the number of invocable actions.
     */
    public static native int atspi_action_get_n_actions(long actionPtr, long errorPtrToPtr);

    /**
     * Get the description of 'i -th' action invocable on an object implementing AtspiAction.
     * @param actionPtr A pointer to the AtSpiAction object on which to operate.
     * @param actionIndex An integer indicating which action to query.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a UTF-8 string describing the 'i -th' invocable action.
     */
    public static native Pointer<Byte> atspi_action_get_action_description(long actionPtr, int actionIndex, long errorPtrToPtr);

    /**
     * Get the description of 'i -th' action invocable on an object implementing AtspiAction.
     * @param actionPtr A pointer to the AtSpiAction object on which to operate.
     * @param actionIndex An integer indicating which action to query.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a UTF-8 string describing the 'i -th' invocable action.
     */
    public static native Pointer<Byte> atspi_action_get_key_binding(long actionPtr, int actionIndex, long errorPtrToPtr);

    /**
     * Get the keybindings for the i -th action invocable on an object implementing AtspiAction, if any are defined.
     * The keybindings string format is as follows: there are multiple parts to a keybinding string (typically 3). They
     * are delimited with ";". The first is the action's keybinding which is usable if the object implementing the action
     * is currently posted to the screen, e.g. if a menu is posted then these keybindings for the corresponding
     * menu-items are available. The second keybinding substring is the full key sequence necessary to post the
     * action's widget and activate it, e.g. for a menu item such as "File-&gt;Open" it would both post the menu and
     * activate the item. Thus the second keybinding string is available during the lifetime of the containing
     * toplevel window as a whole, whereas the first keybinding string only works while the object implementing
     * AtkAction is posted. The third (and optional) keybinding string is the "keyboard shortcut" which invokes
     * the action without posting any menus. Meta-keys are indicated by the conventional strings "&lt;Control&gt;",
     * "&lt;Alt&gt;", "&lt;hift&gt;", "&lt;Mod2&gt;", etc. (we use the same string as gtk_accelerator_name() in gtk+-2.X.
     * @param actionPtr A pointer to the AtSpiAction object on which to operate.
     * @param actionIndex An integer indicating which action to query.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a UTF-8 string which can be parsed to determine the i -th invocable action's keybindings.
     */
    public static native Pointer<Byte> atspi_action_get_action_name(long actionPtr, int actionIndex, long errorPtrToPtr);

    /**
     * Invoke the action indicated by index.
     * @param actionPtr A pointer to the AtSpiAction object on which to operate.
     * @param actionIndex An integer indicating which action to query.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns TRUE if the action is successfully invoked, otherwise FALSE.
     */
    public static native boolean atspi_action_do_action(long actionPtr, int actionIndex, long errorPtrToPtr);

    //***           AtSpiComponent            ***\\

    /**
     * Queries which layer the component is painted into, to help determine its visibility in terms of stacking order.
     * @param componentPtr A pointer to the AtSpiComponent object on which to operate.
     * @param coordType Type of the coordinate system to use: Screen or Window.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns the AtspiComponentLayer into which this component is painted.
     */
    public static native long atspi_component_get_extents(long componentPtr, int coordType, long errorPtrToPtr);

    /**
     * Gets the minimum x and y coordinates of the specified AtspiComponent.
     * @param componentPtr A pointer to the AtSpiComponent object on which to operate.
     * @param coordType Type of the coordinate system to use: Screen or Window.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns an AtspiPoint giving the obj's position.
     */
    public static native long atspi_component_get_position(long componentPtr, int coordType, long errorPtrToPtr);

    /**
     * Gets the size of the specified AtspiComponent.
     * @param componentPtr A pointer to the AtSpiComponent object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns an AtspiPoint giving the obj's size.
     */
    public static native long atspi_component_get_size(long componentPtr, long errorPtrToPtr);

    /**
     * Queries which layer the component is painted into, to help determine its visibility in terms of stacking order.
     * @param componentPtr A pointer to the AtSpiComponent object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns the AtspiComponentLayer into which this component is painted.
     */
    public static native int atspi_component_get_layer(long componentPtr, long errorPtrToPtr);

    /**
     * Queries the z stacking order of a component which is in the MDI or window layer
     * (Bigger z-order numbers mean nearer the top).
     * @param componentPtr A pointer to the AtSpiComponent object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns a short indicating the stacking order of the component in the MDI layer,
     *         or -1 if the component is not in the MDI layer.
     */
    public static native short atspi_component_get_mdi_z_order(long componentPtr, long errorPtrToPtr);

    /**
     * Attempts to set the keyboard input focus to the specified AtspiComponent.
     * @param componentPtr A pointer to the AtSpiComponent object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns TRUE if successful, FALSE otherwise.
     */
    public static native boolean atspi_component_grab_focus(long componentPtr, long errorPtrToPtr);

    //***           AtspiValue            ***\\

    /**
     * Gets the minimum allowed value for an AtspiValue.
     * @param valuePtr A pointer to the AtSpiValue object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns the minimum allowed value for this object.
     */
    public static native double atspi_value_get_minimum_value(long valuePtr, long errorPtrToPtr);

    /**
     * Gets the current value for an AtspiValue.
     * @param valuePtr A pointer to the AtSpiValue object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns the current value for this object.
     */
    public static native double atspi_value_get_current_value(long valuePtr, long errorPtrToPtr);

    /**
     * Gets the maximum allowed value for an AtspiValue.
     * @param valuePtr A pointer to the AtSpiValue object on which to operate.
     * @param errorPtrToPtr A pointer-to-pointer where an GError can be stored.
     * @return Returns the maximum allowed value for this object.
     */
    public static native double atspi_value_get_maximum_value(long valuePtr, long errorPtrToPtr);

    //***           AtspiRelation            ***\\

    /**
     * Gets the type of relationship represented by an AtspiRelation.
     * @param relationPtr A pointer to the AtSpiRelation object on which to operate.
     * @return Returns an AtspiRelationType indicating the type of relation encapsulated in this AtspiRelation object.
     */
    public static native int atspi_relation_get_relation_type(long relationPtr);

    /**
     * Gets the number of objects which this relationship has as its target objects (the subject is the
     * AtspiAccessible from which this AtspiRelation originated).
     * @param relationPtr A pointer to the AtSpiRelation object on which to operate.
     * @return Returns a int indicating how many target objects which the originating AtspiAccessible object
     *         has the AtspiRelation relationship with.
     */
    public static native int atspi_relation_get_n_targets(long relationPtr);

    /**
     * Gets the i -th target of a specified AtspiRelation relationship.
     * @param relationPtr A pointer to the AtSpiRelation object on which to operate.
     * @param relationIndex A (zero-index) int indicating which (of possibly several) target is requested.
     * @return Returns an AtspiAccessible which is the i -th object with which the originating AtspiAccessible has
     *         relationship specified in the AtspiRelation object.
     */
    public static native long atspi_relation_get_target(long relationPtr, int relationIndex);

    //***           gLib is a part of the libatspi library!!!               ***\\

    //***           GArray            ***\\

    /**
     * Gets the size of the elements in array.
     * @param arrayPtr A pointer to the GArray.
     * @return Size of each element, in bytes.
     */
    public static native int g_array_get_element_size(long arrayPtr);

    //***           GList            ***\\

    /**
     * Frees all of the memory used by a GList. The freed elements are returned to the slice allocator. If list
     * elements contain dynamically-allocated memory, you should either use g_list_free_full() or free them
     * manually first.
     * @param listPtr A pointer to the GList.
     */
    public static native void g_list_free(long listPtr);

    /**
     * Gets the number of elements in a GList. This function iterates over the whole list to count
     * its elements. Use a GQueue instead of a GList if you regularly need the number of items. To check whether
     * the list is non-empty, it is faster to check list against NULL.
     * @param listPtr A pointer to the GList.
     * @return Returns the number of elements in the GList
     */
    public static native int g_list_length(long listPtr);

    //***           GHashTable            ***\\

    /**
     * Returns the number of elements contained in the GHashTable.
     * @param hashTablePtr A pointer to the GHashTable.
     * @return Returns the number of key/value pairs in the GHashTable.
     */
    public static native int g_hash_table_size(long hashTablePtr);

    /**
     * Retrieves every key inside hash_table . The returned data is valid until changes to the hash release those keys.
     * This iterates over every entry in the hash table to build its return value. To iterate over the entries in a
     * GHashTable more efficiently, use a GHashTableIter.
     * @param hashTablePtr A pointer to the GHashTable.
     * @return Returns (a pointer to) a GList containing all the keys inside the hash table. The content of the list is
     *         owned by the hash table and should not be modified or freed. Use g_list_free() when done using the list.
     */
    public static native long g_hash_table_get_keys(long hashTablePtr);

}
