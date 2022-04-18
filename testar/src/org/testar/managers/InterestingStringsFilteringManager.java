package org.testar.managers;
/**
 * This class implements a FilteringManager that works with the InterestingsStringsDatamanager,
 * which generates text output for widgets, based on interesting strings (prefixes, suffixes,
 * full strings, fragments) that would have typically been captured by SUT instrumentation.
 */

import java.util.Set;

import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;

import org.testar.managers.InterestingStringsDataManager;

public class InterestingStringsFilteringManager extends FilteringManager {

    InterestingStringsDataManager dataManager;

    public InterestingStringsFilteringManager (InterestingStringsDataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }

    /**
     * This method has been adapted to call
     */
    @Override
    public String getRandomText (Widget w)
    {
      System.out.println("getRandomText");
      return dataManager.generateTextInput();

    }


}