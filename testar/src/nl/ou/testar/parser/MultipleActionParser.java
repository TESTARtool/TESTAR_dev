package nl.ou.testar.parser;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Action;

public abstract class MultipleActionParser implements IActionParser {

    protected abstract IActionParser[] getParsers();

    @Override
    public Pair<Action, String> parse(String src) throws ActionParseException {
        for (IActionParser parser: getParsers()) {
            final Pair<Action, String> result = parser.parse(src);
            if (result != null) {
                return  result;
            }
        }
        return null;
    }
}
