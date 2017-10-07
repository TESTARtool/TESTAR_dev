package nl.ou.testar;

import org.fruit.alayer.StdState;
import org.fruit.alayer.Tags;

public class Util {

   static StdState createState(String tag) {
        StdState state = new StdState();
        state.set(Tags.ConcreteID, tag);
        return state;
    }
}
