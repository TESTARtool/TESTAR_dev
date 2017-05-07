package org.fruit.alayer.linux.atspi;


import org.fruit.alayer.linux.atspi.enums.AtSpiStateTypes;
import org.fruit.alayer.linux.glib.GArray;

import java.util.ArrayList;
import java.util.List;


/**
 *  Implements wrappers around a bitmap of accessible states for an AtSpiAccessible object.
 */
public class AtSpiStateSet {


    //region Properties


    private long _stateSetPtr;
    public long stateSetPtr() {
        return _stateSetPtr;
    }


    private boolean _invalid;
    public boolean isInvalid() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Invalid.ordinal());
    }


    private boolean _active;
    public boolean isActive() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Active.ordinal());
    }


    private boolean _armed;
    public boolean isArmed() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Armed.ordinal());
    }


    private boolean _busy;
    public boolean isBusy() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Busy.ordinal());
    }


    private boolean _checked;
    public boolean isChecked() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Checked.ordinal());
    }


    private boolean _collapsed;
    public boolean isCollapsed() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Collapsed.ordinal());
    }


    private boolean _defunct;
    public boolean isDefunct() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Defunct.ordinal());
    }


    private boolean _editable;
    public boolean isEditable() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Editable.ordinal());
    }


    private boolean _enabled;
    public boolean isEnabled() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Enabled.ordinal());
    }


    private boolean _expandable;
    public boolean isExpandable() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Expandable.ordinal());
    }


    private boolean _expanded;
    public boolean isExpanded() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Expanded.ordinal());
    }


    private boolean _focusable;
    public boolean isFocusable() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Focusable.ordinal());
    }


    private boolean _focused;
    public boolean isFocused() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Focused.ordinal());
    }


    private boolean _hasTooltip;
    public boolean hasTooltip() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.HasTooltip.ordinal());
    }


    private boolean _horizontal;
    public boolean isHorizontal() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Horizontal.ordinal());
    }


    private boolean _iconified;
    public boolean isIconified() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Iconified.ordinal());
    }


    private boolean _modal;
    public boolean isModal() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Modal.ordinal());
    }


    private boolean _multiLine;
    public boolean isMultiLine() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.MultiLine.ordinal());
    }


    private boolean _multiSelectable;
    public boolean isMultiSelectable() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.MultiSelectable.ordinal());
    }


    private boolean _opaque;
    public boolean isOpaque() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Opaque.ordinal());
    }


    private boolean _pressed;
    public boolean isPressed() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Pressed.ordinal());
    }


    private boolean _resizable;
    public boolean isResizable() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Resizable.ordinal());
    }


    private boolean _selectable;
    public boolean isSelectable() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Selectable.ordinal());
    }


    private boolean _selected;
    public boolean isSelected() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Selected.ordinal());
    }


    private boolean _sensitive;
    public boolean isSensitive() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Sensitive.ordinal());
    }


    private boolean _showing;
    public boolean isShowing() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Showing.ordinal());
    }


    private boolean _singleLine;
    public boolean isSingleLine() {return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.SingleLine.ordinal()); }

    private boolean _stale;
    public boolean isStale() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Stale.ordinal());
    }


    private boolean _transient;
    public boolean isTransient() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Transient.ordinal());
    }


    private boolean _vertical;
    public boolean isVertical() {return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Vertical.ordinal()); }

    private boolean _visible;
    public boolean isVisible() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Visible.ordinal());
    }


    private boolean _managesDescendants;
    public boolean isManagesDescendants() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.ManagesDescendants.ordinal());
    }


    private boolean _indeterminate;
    public boolean isIndeterminate() { return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Indeterminate.ordinal()); }


    private boolean _truncated;
    public boolean isTruncated() {return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Truncated.ordinal()); }


    private boolean _required;
    public boolean isRequired() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Required.ordinal());
    }


    private boolean _invalidEntry;
    public boolean isInvalidEntry() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.InvalidEntry.ordinal());
    }


    private boolean _supportsAutoCompletion;
    public boolean isSupportsAutoCompletion() { return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.SupportsAutoCompletion.ordinal()); }


    private boolean _selectableText;
    public boolean isSelectableText() { return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.SelectableText.ordinal()); }

    private boolean _default;
    public boolean isDefault() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.IsDefault.ordinal());
    }


    private boolean _visited;
    public boolean isVisited() {
        return LibAtSpi.atspi_state_set_contains(_stateSetPtr, AtSpiStateTypes.Visited.ordinal());
    }


    /**
     * Gets all the states as a list - only the states that are True are returned.
     * @return A list of all True states.
     */
    public List<AtSpiStateTypes> getStates() {


        GArray<Integer> states = GArray.CreateInstance(LibAtSpi.atspi_state_set_get_states(_stateSetPtr), Integer.class);


        ArrayList<AtSpiStateTypes> statesList = new ArrayList<>();


        if (states != null) {
            for (int e : states.elements()) {
                statesList.add(AtSpiStateTypes.values()[e]);
            }
        }

        return statesList;

    }


    //endregion


    //region Constructors


    /**
     * Default empty constructor.
     */
    private AtSpiStateSet() {

    }


    /**
     * Creates a new instance of an AtSpiStateSet object from a pointer.
     * @param stateSetPtr Pointer to the AtSpiStateSet object.
     * @return A Java instance of an AtSpiStateSet object.
     */
    public static AtSpiStateSet CreateInstance(long stateSetPtr) {


        if (stateSetPtr == 0) {
            return null;
        }


        // Create a new instance.
        AtSpiStateSet ssObj = new AtSpiStateSet();


        // Fill the instance's properties - don't do this as it makes retrieving elements slower.
        //fillInstance(stateSetPtr, ssObj);


        ssObj._stateSetPtr = stateSetPtr;


        return ssObj;

    }


    /**
     * Fills an AtSpiStateSet object's information.
     * @param stateSetPtr Pointer to the AtSpiStateSet object.
     * @param ssObj The Java instance of an AtSpiStateSet object.
     */
    private static void fillInstance(long stateSetPtr, AtSpiStateSet ssObj) {


        // Fill the properties with the information.
        ssObj._stateSetPtr = stateSetPtr;


        ssObj._invalid = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Invalid.ordinal());
        ssObj._active = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Active.ordinal());
        ssObj._armed = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Armed.ordinal());
        ssObj._busy = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Busy.ordinal());
        ssObj._checked = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Checked.ordinal());
        ssObj._collapsed = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Collapsed.ordinal());
        ssObj._defunct = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Defunct.ordinal());
        ssObj._editable = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Editable.ordinal());
        ssObj._enabled = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Enabled.ordinal());
        ssObj._expandable = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Expandable.ordinal());
        ssObj._expanded = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Expanded.ordinal());
        ssObj._focusable = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Focusable.ordinal());
        ssObj._focused = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Focused.ordinal());
        ssObj._hasTooltip = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.HasTooltip.ordinal());
        ssObj._horizontal = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Horizontal.ordinal());
        ssObj._iconified = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Iconified.ordinal());
        ssObj._modal = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Modal.ordinal());
        ssObj._multiLine = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.MultiLine.ordinal());
        ssObj._multiSelectable = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.MultiSelectable.ordinal());
        ssObj._opaque = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Opaque.ordinal());
        ssObj._pressed = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Pressed.ordinal());
        ssObj._resizable = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Resizable.ordinal());
        ssObj._selectable = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Selectable.ordinal());
        ssObj._selected = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Selected.ordinal());
        ssObj._sensitive = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Sensitive.ordinal());
        ssObj._showing = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Showing.ordinal());
        ssObj._singleLine = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.SingleLine.ordinal());
        ssObj._stale = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Stale.ordinal());
        ssObj._transient = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Transient.ordinal());
        ssObj._vertical = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Vertical.ordinal());
        ssObj._visible = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Visible.ordinal());
        ssObj._managesDescendants = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.ManagesDescendants.ordinal());
        ssObj._indeterminate = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Indeterminate.ordinal());
        ssObj._truncated = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Truncated.ordinal());
        ssObj._required = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Required.ordinal());
        ssObj._invalidEntry = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.InvalidEntry.ordinal());
        ssObj._supportsAutoCompletion = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.SupportsAutoCompletion.ordinal());
        ssObj._selectableText = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.SelectableText.ordinal());
        ssObj._default = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.IsDefault.ordinal());
        ssObj._visited = LibAtSpi.atspi_state_set_contains(stateSetPtr, AtSpiStateTypes.Visited.ordinal());


    }


    //endregion


    /**
     * Fills this object with full state information -- intended for testing purposes: complete view of the object's state.
     */
    public void retrieveStates() {
        fillInstance(_stateSetPtr, this);
    }



}
