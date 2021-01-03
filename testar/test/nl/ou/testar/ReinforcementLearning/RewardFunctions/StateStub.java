package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.fruit.alayer.WidgetIterator;

import java.util.Iterator;

public class StateStub extends WidgetStub implements State {

    @Override
    public Iterator<Widget> iterator() {
       return new WidgetIterator(this);
    }
}
