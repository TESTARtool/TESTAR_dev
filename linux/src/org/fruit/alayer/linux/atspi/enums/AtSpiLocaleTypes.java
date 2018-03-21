package org.fruit.alayer.linux.atspi.enums;


/**
 * Used by interfaces AtspiText and AtspiDocument, this enumeration corresponds to the POSIX 'setlocale' enum values.
 */
public enum AtSpiLocaleTypes {


    /**
     * For localizable natural-language messages.
     */
    Messages,


    /**
     * For regular expression matching and string collation.
     */
    Collate,


    /**
     * For regular expression matching, character classification, conversion,
     * case-sensitive comparison, and wide character functions.
     */
    CType,


    /**
     * For monetary formatting.
     */
    Monetary,


    /**
     * For number formatting (such as the decimal point and the thousands separator).
     */
    Numeric,


    /**
     * For time and date formatting.
     */
    Time


}