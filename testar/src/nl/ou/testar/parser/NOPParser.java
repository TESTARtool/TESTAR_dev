package nl.ou.testar.parser;

import org.fruit.Pair;
import org.fruit.alayer.Action;
import org.fruit.alayer.actions.KeyUp;
import org.fruit.alayer.actions.NOP;
import org.fruit.alayer.devices.KBKeys;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NOPParser implements IActionParser{
    private final static String TEMPLATE = "No\\s+Operation\\s*(.*)";
    public Pair<Action, String> parse(String src) {
        final Pattern pattern = Pattern.compile(TEMPLATE, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(src);
        if (matcher.groupCount() > 1) {
            final String rest = matcher.group(1);
            return new Pair<>(new NOP(), rest);
        }
        return null;
    }
}
