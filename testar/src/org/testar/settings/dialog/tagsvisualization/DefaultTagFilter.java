package org.testar.settings.dialog.tagsvisualization;

import org.testar.StateManagementTags;
import org.testar.settings.dialog.TagsExtractor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DefaultTagFilter {

    private static final Set<String> allAvailableTags;
    static{
        // Fill the list with all the available tags.
        Set<String> tmpList = new HashSet<>();
        TagsExtractor.getAllUIATags().forEach(t -> tmpList.add(t.name()));
        TagsExtractor.getAllTags().forEach(t -> tmpList.add(t.name()));
        StateManagementTags.getAllTags().forEach(t -> tmpList.add(t.name()));
        allAvailableTags = Collections.unmodifiableSet(tmpList);
    }
    public static Set<String> getList() {
        return allAvailableTags;
    }
}
