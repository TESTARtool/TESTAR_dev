package nl.ou.testar.parser;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Action;

public interface IActionParser {
    Pair<Action, String> parse(String src) throws ActionParseException;
}
