package nl.ou.testar.parser;

import org.fruit.Pair;
import org.fruit.alayer.Action;

public interface IActionParser {
    Pair<Action, String> parse(String src) throws ActionParseException;
}
