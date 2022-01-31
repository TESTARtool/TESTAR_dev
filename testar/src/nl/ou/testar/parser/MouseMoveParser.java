package nl.ou.testar.parser;

import org.fruit.Pair;
import org.fruit.alayer.Action;
import org.fruit.alayer.actions.MouseMove;
import org.fruit.alayer.actions.MouseUp;
import org.fruit.alayer.devices.MouseButtons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MouseMoveParser implements IActionParser {
    private final static String TEMPLATE = "Move\\s+mouse\\s+to\\s+\\(([^\\(\\),]+),\\s+([^\\(\\),]+)\\)\\.\\s*(.*)";

    @Override
    public Pair<Action, String> parse(String src) {
        final Pattern pattern = Pattern.compile(TEMPLATE, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(src);
        if (matcher.groupCount() > 3) {
            final String moveXString = matcher.group(1);
            final String moveYString = matcher.group(2);
            final String rest = matcher.group(3);
            final MouseMove mouseMove = new MouseMove(Double.parseDouble(moveXString), Double.parseDouble(moveYString));
            return new Pair<>(mouseMove, rest);
        }
        return null;
    }
}
