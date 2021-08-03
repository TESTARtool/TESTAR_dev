package org.fruit.alayer.actions;

import org.fruit.alayer.Action;
import org.fruit.alayer.Position;
import org.fruit.alayer.Widget;

public interface WidgetActionCompiler<W extends Widget> {
    Action leftClickAt(W w);
    Action leftDoubleClickAt(W w);
    Action leftTripleClickAt(W w);
    Action dragFromTo(W from, W to);
    Action slideFromTo(Position from, Position to, W widget);
    Action clickTypeInto(W w, String text, boolean replaceText);
    Action pasteTextInto(W w, String text, boolean replaceText);
}
