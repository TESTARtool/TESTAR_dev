package nl.ou.testar.visualvalidation.matcher;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Container to store the matched counter part of the expected character.
 */
public class CharacterMatchEntry {
    final Character character;
    CharacterMatchEntry match;

    /**
     * Constructor.
     *
     * @param character The character which we try to match.
     */
    CharacterMatchEntry(char character) {
        this.character = character;
        match = null;
    }

    /**
     * Mark this entry as matched.
     *
     * @param matchedCharacter The matched character.
     */
    public void Match(@NonNull CharacterMatchEntry matchedCharacter) {
        match = matchedCharacter;
        matchedCharacter.match = this;
    }

    /**
     * Check if the character has not matched.
     *
     * @return True when not matched.
     */
    public boolean isNotMatched() {
        return match == null;
    }

    /**
     * Check if the character is matched.
     *
     * @return True when matched.
     */
    public boolean isMatched() {
        return !isNotMatched();
    }

    @Override
    public String toString() {
        return "CharacterMatchEntry{" +
                "match=" + Integer.toHexString(System.identityHashCode(match)) +
                ", character=" + character +
                '}';
    }
}
