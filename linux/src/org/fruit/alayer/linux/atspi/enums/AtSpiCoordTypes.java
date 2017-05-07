package org.fruit.alayer.linux.atspi.enums;

/**
 * Enumeration used by AtspiComponent, AtspiImage, and AtspiText interfaces to specify whether coordinates are
 * relative to the window or the screen.
 */
public enum AtSpiCoordTypes {


    /**
     * Specifies xy coordinates relative to the screen.
     */
    Screen,


    /**
     * Specifies xy coordinates relative to the widget's top-level window.
     */
    Window


}