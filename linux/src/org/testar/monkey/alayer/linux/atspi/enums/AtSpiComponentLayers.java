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
