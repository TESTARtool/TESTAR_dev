package nl.ou.testar.ReinforcementLearning.RewardFunctions;


import org.apache.commons.lang.NotImplementedException;
import org.fruit.Drag;
import org.fruit.Util;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.exceptions.NoSuchTagException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Stub for unit tests
 */
public class WidgetStub implements Widget {
    Map<Tag<?>, Object> tags = Util.newHashMap();

    private final List<Widget> widgets = new ArrayList<>();

    private Widget parent = null;

    @Override
    public State root() {
        return (State)this;
    }

    @Override
    public Widget parent() {
        return parent;
    }
    public void setParent(Widget parent) {
        this.parent = parent;
    }

    @Override
    public Widget child(int i) {
        return widgets.get(i);
    }

    @Override
    public int childCount() {
        return widgets.size();
    }

    @Override
    public void remove() {
        throw new NotImplementedException();
    }

    @Override
    public void moveTo(Widget p, int idx) {
        throw new NotImplementedException();
    }

    @Override
    public Widget addChild() {
        throw new NotImplementedException();
    }

    public Widget addChild(Widget widget) {
        widgets.add(widget);
        return this;
    }

    @Override
    public Drag[] scrollDrags(double scrollArrowSize, double scrollThick) {
        throw new NotImplementedException();
    }

    @Override
    public String getRepresentation(String tab){
        StringBuffer repr = new StringBuffer();
        repr.append(tab + "WIDGET = " + this.get(Tags.ConcreteID) + ", " +
                this.get(Tags.Abstract_R_ID) + ", " +
                this.get(Tags.Abstract_R_T_ID) + ", " +
                this.get(Tags.Abstract_R_T_P_ID) + "\n");
        return repr.toString();
    }

    @Override
    public String toString(Tag<?>... tags){
        return Util.treeDesc(this, 2, tags);
    }

    @Override
    public <T> T get(Tag<T> tag) throws NoSuchTagException {
        Object returnObject = tags.get(tag);
        return (T) returnObject;
    }

    @Override
    public final <T> T get(Tag<T> tag, T defaultValue) {
        Object returnObject = tags.get(tag);
        return (T) returnObject;
    }

    @Override
    public Iterable<Tag<?>> tags() {
        throw new NotImplementedException();
    }

    @Override
    public final <T> void set(Tag<T> tag, T value) { /*check;*/ tags.put(tag, value); }

    @Override
    public void remove(Tag<?> tag) {
        throw new NotImplementedException();
    }
}
