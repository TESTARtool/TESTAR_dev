package nl.ou.testar.visualvalidation.matcher;

/**
 * The match result of an individual character.
 */
enum CharacterMatchResult {
    MATCHED,
    CASE_MISMATCH,
    WHITESPACE_CORRECTED,
    NO_MATCH
}
