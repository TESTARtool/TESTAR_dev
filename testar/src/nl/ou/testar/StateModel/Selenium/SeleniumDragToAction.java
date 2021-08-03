package nl.ou.testar.StateModel.Selenium;

import org.fruit.alayer.Point;
import org.fruit.alayer.Position;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.actions.ActionRoles;

public class SeleniumDragToAction extends SeleniumAction {

    private final State state;
    private final Position from;
    private final Position to;

    public SeleniumDragToAction(String target, State state, Position from, Position to) {
        super(target);
        this.state = state;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        final int offset[] = getOffset(from, to, state);
        return "Drag And Drop By Offset\t" + offset[0] + "\t" + offset[1];
    }

    @Override
    public String toParametersString() {
        final int offset[] = getOffset(from, to, state);
        return "(" + offset[0] + "," + offset[1] + ")";
    }

    @Override
    public String getType() {
        return SeleniumActionFactory.DRAG_TO;
    }

    @Override
    protected void performAction(State state) {
        final int offset[] = getOffset(from, to, state);
        actions.dragAndDropBy(element, offset[0], offset[1]);
    }

    @Override
    protected Role getDefaultRole() {
        return ActionRoles.SeleniumDrag;
    }

    @Override
    protected String getDescription() {
        final int offset[] = getOffset(from, to, state);
        return "Dragged to (" + offset[0] + ", " + offset[1] + ")";
    }

    private int[] getOffset(Position from, Position to, State state) {
        final Point fromPoint = from.apply(state);
        final Point toPoint = to.apply(state);
        return new int[]{(int) (toPoint.x() - fromPoint.x()), (int) (toPoint.y() - fromPoint.y())};
    };
}
