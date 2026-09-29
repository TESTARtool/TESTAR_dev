package org.testar.plugin.tagsvisualization;

import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;

// Implements WS-FUNC-TEST-SETTINGS-004: applies the saved Spy attribute selection at runtime.
public final class SpyTagSelection {

    private final List<String> includedNames;
    private final Set<String> includedNameSet;
    private final Map<String, Tag<?>> knownTags;

    public SpyTagSelection(List<String> includedNames) {
        this.includedNames = List.copyOf(includedNames);
        this.includedNameSet = Set.copyOf(includedNames);
        this.knownTags = new LinkedHashMap<>();
        for (Tag<?> tag : DefaultTagFilter.getSet()) {
            if (includedNameSet.contains(tag.name())) {
                knownTags.putIfAbsent(tag.name(), tag);
            }
        }
    }

    public boolean includes(Tag<?> tag) {
        return includedNameSet.contains(tag.name());
    }

    public Map<String, String> selectWidgetProperties(Widget widget) {
        Map<String, String> selected = new LinkedHashMap<>();
        Set<String> unresolvedNames = new HashSet<>(includedNameSet);

        for (String name : includedNames) {
            Tag<?> tag = knownTags.get(name);
            if (tag != null) {
                addProperty(selected, name, widget.get(tag, null));
                unresolvedNames.remove(name);
            }
        }

        if (!unresolvedNames.isEmpty()) {
            for (Tag<?> tag : widget.tags()) {
                if (unresolvedNames.contains(tag.name())) {
                    addProperty(selected, tag.name(), widget.get(tag, null));
                }
            }
        }

        return selected;
    }

    private void addProperty(Map<String, String> properties, String name, Object value) {
        if (value != null && !String.valueOf(value).isBlank()) {
            properties.put(name, String.valueOf(value));
        }
    }
}
