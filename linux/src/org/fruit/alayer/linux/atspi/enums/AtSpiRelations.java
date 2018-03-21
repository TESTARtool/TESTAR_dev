package org.fruit.alayer.linux.atspi.enums;


/**
 * AtspiRelationType specifies a relationship between objects (possibly one-to-many or many-to-one) outside of the
 * normal parent/child hierarchical relationship. It allows better semantic identification of how objects are
 * associated with one another. For instance the ATSPI_RELATION_LABELLED_BY relationship may be used to identify
 * labelling information that should accompany the accessible name property when presenting an object's content or
 * identity to the end user. Similarly, ATSPI_RELATION_CONTROLLER_FOR can be used to further specify the context in
 * which a valuator is useful, and/or the other UI components which are directly effected by user interactions with
 * the valuator. Common examples include association of scrollbars with the viewport or panel which they control.
 *
 * Enumeration used to specify the type of relation encapsulated in an AtspiRelation object
 */
public enum AtSpiRelations {


    /**
     * Not a meaningful relationship; clients should not normally encounter this AtspiRelationType value.
     */
    Null,


    /**
     * Object is a label for one or more other objects.
     */
    LabelFor,


    /**
     * Object is labelled by one or more other objects.
     */
    LabelledBy,


    /**
     * Object is an interactive object which modifies the state, onscreen location, or other attributes
     * of one or more target objects.
     */
    ControllerFor,


    /**
     * Object state, position, etc. is modified/controlled by user interaction with one or more other objects.
     * For instance a viewport or scroll pane may be ATSPI_RELATION_CONTROLLED_BY scrollbars.
     */
    ControlledBy,


    /**
     * Object has a grouping relationship (e.g. 'same group as') to one or more other objects.
     */
    MemberOf,


    /**
     * Object is a tooltip associated with another object.
     */
    ToolTipFor,


    /**
     * Object is a child of the target.
     */
    NodeChildOf,


    /**
     * Object is a parent of the target.
     */
    NodeParentOf,


    /**
     * Used to indicate that a relationship exists, but its type is not specified in the enumeration.
     */
    Extended,


    /**
     * Object renders content which flows logically to another object. For instance, text in a paragraph may flow to
     * another object which is not the 'next sibling' in the accessibility hierarchy.
     */
    FlowsTo,


    /**
     * Reciprocal of ATSPI_RELATION_FLOWS_TO.
     */
    FlowsFrom,


    /**
     * Object is visually and semantically considered a subwindow of another object, even though it is not the
     * object's child. Useful when dealing with embedded applications and other cases where the widget
     * hierarchy does not map cleanly to the onscreen presentation.
     */
    SubWindowOf,


    /**
     * Similar to ATSPI_RELATION_SUBWINDOW_OF, but specifically used for cross-process embedding.
     */
    Embeds,


    /**
     * Reciprocal of ATSPI_RELATION_EMBEDS. Used to denote content rendered by embedded
     * renderers that live in a separate process space from the embedding context.
     */
    EmbeddedBy,


    /**
     * Denotes that the object is a transient window or frame associated with another onscreen object. Similar
     * to ATSPI_TOOLTIP_FOR , but more general. Useful for windows which are technically toplevels but which,
     * for one or more reasons, do not explicitly cause their associated window to lose 'window focus'. Creation
     * of an ATSPI_ROLE_WINDOW object with the ATSPI_RELATION_POPUP_FOR relation usually requires some presentation
     * action on the part of assistive technology clients, even though the previous toplevel ATSPI_ROLE_FRAME object
     * may still be the active window.
     */
    PopupFor,


    /**
     * This is the reciprocal relation to ATSPI_RELATION_POPUP_FOR .
     */
    ParentWindowOf,


    /**
     * Indicates that an object provides descriptive information about another object; more verbose than
     * ATSPI_RELATION_LABEL_FOR .
     */
    DescriptionFor,


    /**
     * Indicates that another object provides descriptive information about this object;
     * more verbose than ATSPI_RELATION_LABELLED_BY .
     */
    DescribedBy,


    /**
     * Do not use as a parameter value, used to determine the size of the enumeration.
     */
    LastDefined



}