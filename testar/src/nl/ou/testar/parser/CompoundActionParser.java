package nl.ou.testar.parser;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.actions.CompoundAction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompoundActionParser implements IActionParser {

    private final static String TEMPLATE = "Compound\\s+Action\\s+=\\s*(.*)";

    protected MultipleActionParser itemParser = new MultipleActionParser() {

        @Override
        protected IActionParser[] getParsers() {
            final IActionParser parsers[] = {
                    new KeyDownParser(),
                    new KeyUpParser(),
                    new MouseDownParser(),
                    new MouseUpParser(),
                    new MouseMoveParser(),
                    new TypeParser(),
                    new PasteTextParser(),
                    new WaitParser(),
                    new NOPParser()
            };
            return parsers;
        }
    };

    @Override
    public Pair<Action, String> parse(String src) throws ActionParseException {
        final Pattern pattern = Pattern.compile(TEMPLATE, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(src);
        final CompoundAction.Builder builder = new CompoundAction.Builder();
        if (matcher.matches() && matcher.groupCount() == 1) {
            String rest = matcher.group(1).trim();
            while (!rest.equals("")) {
                final Pair<Action, String> result = itemParser.parse(rest);
                if (result == null) {
                    throw new ActionParseException("Cannot parse action: " + rest);
                }
                builder.add(result.left(), 1);
                rest = result.right().trim();
            }
            return new Pair<>(builder.build(), rest);
        }
        return null;
    }
}
