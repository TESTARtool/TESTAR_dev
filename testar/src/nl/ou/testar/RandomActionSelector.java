package nl.ou.testar;

import org.fruit.alayer.Action;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class RandomActionSelector {

    public static Action selectAction(Set<Action> actions) {
        long graphTime = System.currentTimeMillis();
        Random rnd = new Random(graphTime);
        return new ArrayList<Action>(actions).get(rnd.nextInt(actions.size()));
    }
}
