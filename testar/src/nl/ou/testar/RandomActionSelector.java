package nl.ou.testar;

import org.fruit.alayer.Action;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class RandomActionSelector {

    public static Action selectAction(Set<Action> actions) {
        Random rnd = new Random(System.currentTimeMillis());
        return new ArrayList<>(actions).get(rnd.nextInt(actions.size()));
    }
}
