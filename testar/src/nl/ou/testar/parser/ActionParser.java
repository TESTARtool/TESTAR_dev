package nl.ou.testar.parser;

public class ActionParser extends MultipleActionParser {
    @Override
    protected IActionParser[] getParsers() {
        final IActionParser parsers[] = {
                new CompoundActionParser(),
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
}
