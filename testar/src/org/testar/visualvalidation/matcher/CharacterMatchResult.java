package org.testar.visualvalidation.matcher;

/**
 * The match result of an individual character.
 */
public enum CharacterMatchResult {
    MATCHED,
    CASE_MISMATCH,
    WHITESPACE_CORRECTED,
    NO_MATCH;

    @Override
    public String toString() {
        switch (this) {
            case MATCHED:
                return "V";
            case CASE_MISMATCH:
                return "C";
            case WHITESPACE_CORRECTED:
                return "W";
            case NO_MATCH:
                return "X";
            default:
                return "";
        }
    }
}
