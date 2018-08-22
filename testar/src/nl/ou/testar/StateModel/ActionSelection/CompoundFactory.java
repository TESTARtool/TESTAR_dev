package nl.ou.testar.StateModel.ActionSelection;

import java.util.ArrayList;
import java.util.List;

public class CompoundFactory {

    public static CompoundActionSelector getCompoundActionSelector() {
        List<ActionSelector> selectors = new ArrayList<>();
        selectors.add(new UnvisitedActionsSelector());
        selectors.add(new RandomActionSelector());
        return new CompoundActionSelector(selectors);
    }

}
