package nl.ou.testar.StateModel.ActionSelection;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.util.ArrayList;
import java.util.List;

public class CompoundFactory {

    public static CompoundActionSelector getCompoundActionSelector(Settings settings) {
        // this is hardcoded for now, but can be configurable in the future.
        List<ActionSelector> selectors = new ArrayList<>();
        System.out.println("CompoundActionSelector: "+settings.get(ConfigTags.ActionSelectionAlgorithm));
        if (settings.get(ConfigTags.ActionSelectionAlgorithm).equals("unvisited")) {
            selectors.add(new ImprovedUnvisitedActionSelector());
        } 
        if (settings.get(ConfigTags.ActionSelectionAlgorithm).equals("db"))
        {
            selectors.add(new UnvisitedActionsSelectorFromDb());
        }
        selectors.add(new RandomActionSelector());
        return new CompoundActionSelector(selectors);
    }

}
