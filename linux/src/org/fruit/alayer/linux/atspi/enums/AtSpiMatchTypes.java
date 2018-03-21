package org.fruit.alayer.linux.atspi.enums;


/**
 * Enumeration used by AtspiMatchRule to specify how to interpret AtspiAccessible objects.
 */
public enum AtSpiMatchTypes {


    /**
     * Indicates an error condition or uninitialized value.
     */
    Invalid,


    /**
     * TRUE if all of the criteria are met.
     */
    All,


    /**
     * TRUE if any of the criteria are met.
     */
    Any,


    /**
     * TRUE if none of the criteria are met.
     */
    None,


    /**
     * Same as ATSPI_Collection_MATCH_ALL if the criteria is non-empty;
     * for empty criteria this rule requires returned value to also have empty set.
     */
    Empty,


    /**
     * Used only to determine the end of the enumeration.
     */
    LastDefined


}