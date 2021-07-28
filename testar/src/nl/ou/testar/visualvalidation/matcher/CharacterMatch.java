package nl.ou.testar.visualvalidation.matcher;

/**
 * Stores the outcome of the matcher result for the given character.
 */
public class CharacterMatch {
    CharacterMatchResult result;
    final CharacterMatchEntry character;

    /**
     * Constructor.
     * @param character The character which we try to match.
     */
    public CharacterMatch(char character){
        result = CharacterMatchResult.NO_MATCH;
        this.character = new CharacterMatchEntry(character);
    }

    /**
     * @return Get the result of the character match.
     */
    public CharacterMatchResult getMatchResult(){
        return result;
    }

    /**
     * @return Get the character match.
     */
    public CharacterMatchEntry getCharacterMatch(){
        return character;
    }

    @Override
    public String toString() {
        return "CharacterMatch{" +
                "result=" + result +
                ", ch=" + character +
                '}';
    }
}
