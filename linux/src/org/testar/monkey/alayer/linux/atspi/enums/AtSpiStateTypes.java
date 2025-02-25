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


package org.testar.monkey.alayer.linux.atspi.enums;

/**
 * Enumeration used by various interfaces indicating every possible state an AtspiAccesible object can assume.
 */
public enum AtSpiStateTypes {


    /**
     * Indicates an invalid state - probably an error condition.
     */
    Invalid,


    /**
     * Indicates a window is currently the active window, or an object is the active subelement within a container or
     * table. ATSPI_STATE_ACTIVE should not be used for objects which have ATSPI_STATE_FOCUSABLE or ATSPI_STATE_SELECTABLE:
     * Those objects should use ATSPI_STATE_FOCUSED and ATSPI_STATE_SELECTED respectively. ATSPI_STATE_ACTIVE is a means to
     * indicate that an object which is not focusable and not selectable is the currently-active item within its parent container.
     */
    Active,


    /**
     * Indicates that the object is armed.
     */
    Armed,


    /**
     * Indicates the current object is busy, i.e. onscreen representation is in the process of changing,
     * or the object is temporarily unavailable for interaction due to activity already in progress.
     */
    Busy,


    /**
     * Indicates this object is currently checked.
     */
    Checked,


    /**
     * Indicates this object is collapsed.
     */
    Collapsed,


    /**
     * Indicates that this object no longer has a valid backing widget (for instance, if its peer object has been destroyed).
     */
    Defunct,


    /**
     * Indicates the user can change the contents of this object.
     */
    Editable,


    /**
     * Indicates that this object is enabled, i.e. that it currently reflects some application state. Objects that are
     * "greyed out" may lack this state, and may lack the ATSPI_STATE_SENSITIVE if direct user interaction cannot
     * cause them to acquire ATSPI_STATE_ENABLED . See ATSPI_STATE_SENSITIVE .
     */
    Enabled,


    /**
     * Indicates this object allows progressive disclosure of its children.
     */
    Expandable,


    /**
     * Indicates this object is expanded.
     */
    Expanded,


    /**
     * Indicates this object can accept keyboard focus, which means all events resulting from typing on the keyboard
     * will normally be passed to it when it has focus.
     */
    Focusable,


    /**
     * Indicates this object currently has the keyboard focus.
     */
    Focused,


    /**
     * Indicates that the object has an associated tooltip.
     */
    HasTooltip,


    /**
     * Indicates the orientation of this object is horizontal.
     */
    Horizontal,


    /**
     * Indicates this object is minimized and is represented only by an icon.
     */
    Iconified,


    /**
     * Indicates something must be done with this object before the user can interact with an object in a different window.
     */
    Modal,


    /**
     * Indicates this (text) object can contain multiple lines of text.
     */
    MultiLine,


    /**
     * Indicates this object allows more than one of its children to be selected at the same time, or in the case of
     * text objects, that the object supports non-contiguous text selections.
     */
    MultiSelectable,


    /**
     * Indicates this object paints every pixel within its rectangular region. It also indicates an alpha value of unity,
     * if it supports alpha blending.
     */
    Opaque,


    /**
     * Indicates this object is currently pressed.
     */
    Pressed,


    /**
     * Indicates the size of this object's size is not fixed.
     */
    Resizable,


    /**
     * Indicates this object is the child of an object that allows its children to be selected and that this
     * child is one of those children that can be selected.
     */
    Selectable,


    /**
     * Indicates this object is the child of an object that allows its children to
     * be selected and that this child is one of those children that has been selected.
     */
    Selected,


    /**
     * Indicates this object is sensitive, e.g. to user interaction. ATSPI_STATE_SENSITIVE usually accompanies.
     * ATSPI_STATE_ENABLED for user-actionable controls, but may be found in the absence of ATSPI_STATE_ENABLED
     * if the current visible state of the control is "disconnected" from the application state. In such cases,
     * direct user interaction can often result in the object gaining ATSPI_STATE_SENSITIVE , for instance if a user
     * makes an explicit selection using an object whose current state is ambiguous or undefined.
     * See ATSPI_STATE_ENABLED , ATSPI_STATE_INDETERMINATE .
     */
    Sensitive,


    /**
     * Indicates this object, the object's parent, the object's parent's parent, and so on, are all 'shown' to the
     * end-user, i.e. subject to "exposure" if blocking or obscuring objects do not interpose between this object
     * and the top of the window stack.
     */
    Showing,


    /**
     * Indicates this (text) object can contain only a single line of text.
     */
    SingleLine,


    /**
     * Indicates that the information returned for this object may no longer be synchronized with the application
     * state. This can occur if the object has ATSPI_STATE_TRANSIENT , and can also occur towards
     * the end of the object peer's lifecycle.
     */
    Stale,


    /**
     * Indicates this object is transient.
     */
    Transient,


    /**
     * Indicates the orientation of this object is vertical; for example this state may appear on such objects
     * as scrollbars, text objects (with vertical text flow), separators, etc.
     */
    Vertical,


    /**
     * Indicates this object is visible, e.g. has been explicitly marked for exposure to the user.
     * ATSPI_STATE_VISIBLE is no guarantee that the object is actually unobscured on the screen, only
     * that it is 'potentially' visible, barring obstruction, being scrolled or clipped out of the field of view,
     * or having an ancestor container that has not yet made visible. A widget is potentially onscreen if it has both
     * ATSPI_STATE_VISIBLE and ATSPI_STATE_SHOWING . The absence of ATSPI_STATE_VISIBLE and ATSPI_STATE_SHOWING is
     * semantically equivalent to saying that an object is 'hidden'.
     */
    Visible,


    /**
     * Indicates that "active-descendant-changed" event is sent when children become 'active' (i.e. are selected
     * or navigated to onscreen). Used to prevent need to enumerate all children in very large containers,
     * like tables. The presence of ATSPI_STATE_MANAGES_DESCENDANTS is an indication to the client that the
     * children should not, and need not, be enumerated by the client. Objects implementing this state are expected
     * to provide relevant state notifications to listening clients, for instance notifications of visibility changes
     * and activation of their contained child objects, without the client having previously requested references
     * to those children.
     */
    ManagesDescendants,


    /**
     * Indicates that a check box or other boolean indicator is in a state other than checked or not checked.
     * This usually means that the boolean value reflected or controlled by the object does not apply consistently
     * to the entire current context. For example, a checkbox for the "Bold" attribute of text may have
     * ATSPI_STATE_INDETERMINATE if the currently selected text contains a mixture of weight attributes.
     * In many cases interacting with a ATSPI_STATE_INDETERMINATE object will cause the context's corresponding
     * boolean attribute to be homogenized, whereupon the object will lose ATSPI_STATE_INDETERMINATE and a
     * corresponding state-changed event will be fired.
     */
    Indeterminate,


    /**
     * Indicates that user interaction with this object is 'required' from the user, for instance before
     * completing the processing of a form.
     */
    Required,


    /**
     * Indicates that an object's onscreen content is truncated, e.g. a text value in a spreadsheet cell.
     */
    Truncated,


    /**
     * Indicates this object's visual representation is dynamic, not static. This state may be applied to an object
     * during an animated 'effect' and be removed from the object once its visual representation becomes static.
     * Some applications, notably content viewers, may not be able to detect all kinds of animated content.
     * Therefore the absence of this state should not be taken as definitive evidence that the object's visual
     * representation is static; this state is advisory.
     */
    Animated,


    /**
     * This object has indicated an error condition due to failure of input validation. For instance,
     * a form control may acquire this state in response to invalid or malformed user input.
     */
    InvalidEntry,


    /**
     * This state indicates that the object in question implements some form of typeahead or pre-selection behavior
     * whereby entering the first character of one or more sub-elements causes those elements to scroll into view
     * or become selected. Subsequent character input may narrow the selection further as long as one or more
     * sub-elements match the string. This state is normally only useful and encountered on objects that
     * implement AtspiSelection. In some cases the typeahead behavior may result in full or partial completion
     * of the data in the input field, in which case these input events may trigger text-changed events from the source.
     */
    SupportsAutoCompletion,


    /**
     * This state indicates that the object in question supports text selection. It should only be
     * exposed on objects which implement the AtspiText interface, in order to distinguish
     * this state from ATSPI_STATE_SELECTABLE , which infers that the object in question is
     * a selectable child of an object which implements AtspiSelection. While similar,
     * text selection and subelement selection are distinct operations.
     */
    SelectableText,


    /**
     * This state indicates that the object in question is the 'default' interaction object in a dialog,
     * i.e. the one that gets activated if the user presses "Enter" when the dialog is initially posted.
     */
    IsDefault,


    /**
     * This state indicates that the object (typically a hyperlink) has already been activated or
     * invoked, with the result that some backing data has been downloaded or rendered.
     */
    Visited,


    /**
     * Indicates this object has the potential to be checked, such as a checkbox or toggle-able table cell. Since : 2.12
     */
    Checkable,


    /**
     * Indicates that the object has a popup context menu or sub-level menu which may or may not be showing.
     * This means that activation renders conditional content. Note that ordinary tooltips are not
     * considered popups in this context. Since : 2.12
     */
    HasPopup,


    /**
     * Indicates that an object which is ENABLED and SENSITIVE has a value which can be read,
     * but not modified, by the user. Since : 2.16
     */
    ReadOnly,


    /**
     * This value of the enumeration should not be used as a parameter,
     * it indicates the number of items in the AtspiStateType enumeration
     */
    LastDefined


}
