package nl.ou.testar.parser;

import org.fruit.Pair;
import org.fruit.alayer.Action;
import org.fruit.alayer.actions.CompoundAction;

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
                    new WaitParser()
            };
            return new IActionParser[0];
        }
    };

    @Override
    public Pair<Action, String> parse(String src) throws ActionParseException {
        final Pattern pattern = Pattern.compile(TEMPLATE, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(src);
        int groupCount = matcher.groupCount();

        final CompoundAction.Builder builder = new CompoundAction.Builder();
        if (groupCount > 1) {
            String rest = matcher.group(1).trim();
            while (rest != "") {
                final Pair<Action, String> result = itemParser.parse(rest);
                if (result == null) {
                    throw new ActionParseException("Cannot parse action: " + rest);
                }
                builder.add(result.left(), 1);
                rest = result.right().trim();
            }
        }
        return null;
    }
}
