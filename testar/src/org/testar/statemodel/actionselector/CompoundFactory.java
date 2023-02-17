package org.testar.statemodel.actionselector;

import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;

import java.util.ArrayList;
import java.util.List;

public class CompoundFactory {

    public static CompoundActionSelector getCompoundActionSelector(Settings settings) {
        // this is hardcoded for now, but can be configurable in the future.
        List<ActionSelector> selectors = new ArrayList<>();
        if (settings.get(ConfigTags.ActionSelectionAlgorithm).equals("unvisited")) {
            selectors.add(new ImprovedUnvisitedActionSelector());
        }
        selectors.add(new RandomActionSelector());
        return new CompoundActionSelector(selectors);
    }

}
