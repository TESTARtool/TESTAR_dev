package org.fruit.alayer.linux.atspi.enums;


/**
 * The AtspiComponentLayer of an AtspiComponent instance indicates its relative stacking order with respect
 * to the onscreen visual representation of the UI. AtspiComponentLayer, in combination with AtspiComponent
 * bounds information, can be used to compute the visibility of all or part of a component. This is important
 * in programmatic determination of region-of-interest for magnification, and in flat screen review models of
 * the screen, as well as for other uses. Objects residing in two of the AtspiComponentLayer categories support
 * further z-ordering information, with respect to their peers in the same layer: namely, ATSPI_LAYER_WINDOW and
 * ATSPI_LAYER_MDI . Relative stacking order for other objects within the same layer is not available; the
 * recommended heuristic is first child paints first. In other words, assume that the first siblings in the
 * child list are subject to being overpainted by later siblings if their bounds intersect. The order of
 * layers, from bottom to top, is: ATSPI_LAYER_BACKGROUND , ATSPI_LAYER_WINDOW , ATSPI_LAYER_MDI ,
 * ATSPI_LAYER_CANVAS , ATSPI_LAYER_WIDGET , ATSPI_LAYER_POPUP , and ATSPI_LAYER_OVERLAY .
 */
public enum AtSpiComponentLayers {


    /**
     * Indicates an error condition or uninitialized value.
     */
    Invalid,


    /**
     * The bottom-most layer, over which everything else is painted. The 'desktop background'
     * is generally in this layer.
     */
    Background,


    /**
     * The 'background' layer for most content renderers and UI AtspiComponent containers.
     */
    Canvas,


    /**
     * The layer in which the majority of ordinary 'foreground' widgets reside.
     */
    Widget,


    /**
     * A special layer between ATSPI_LAYER_CANVAS and ATSPI_LAYER_WIDGET ,
     * in which the 'pseudo windows' (e.g. the MDI frames) reside. See atspi_component_get_mdi_z_order.
     */
    Mdi,


    /**
     * A layer for popup window content, above ATSPI_LAYER_WIDGET .
     */
    Popup,


    /**
     * The topmost layer.
     */
    Overlay,


    /**
     * The layer in which a toplevel window background usually resides.
     */
    Window,


    /**
     * Used only to determine the end of the enumeration.
     */
    LastDefined


}