package org.testar.webstudio.workspace;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testar.config.ConfigTags;
import org.testar.config.settings.SettingsDefaults;
import org.testar.core.Pair;
import org.testar.core.tag.Tag;
import org.testar.plugin.tagsvisualization.DefaultTagFilter;

// Implements WS-FUNC-TEST-SETTINGS-004: catalogs Spy attributes and their configured defaults.
public final class SpyTagCatalog {

    private SpyTagCatalog() {
    }

    public record TagOption(String key, String group, boolean defaultSelected) {
    }

    public static List<TagOption> options() {
        Map<String, String> groupsByTag = new LinkedHashMap<>();
        for (Tag<?> tag : DefaultTagFilter.getSet()) {
            groupsByTag.putIfAbsent(tag.name(), groupFor(tag.name()));
        }

        Set<String> defaults = defaultTagNames();
        return groupsByTag.entrySet().stream()
                .map(entry -> new TagOption(entry.getKey(), entry.getValue(), defaults.contains(entry.getKey())))
                .sorted(Comparator.comparing(TagOption::group).thenComparing(TagOption::key))
                .toList();
    }

    private static String groupFor(String tagName) {
        if (tagName.startsWith("UIA")) {
            return "Windows";
        }
        if (tagName.startsWith("Web")) {
            return "WebDriver";
        }
        if (tagName.startsWith("Android")) {
            return "Android";
        }
        return "Common";
    }

    private static Set<String> defaultTagNames() {
        for (Pair<?, ?> pair : SettingsDefaults.getSettingsDefaults()) {
            if (pair.left().equals(ConfigTags.SpyTagAttributes)) {
                Set<String> names = new HashSet<>();
                for (Object name : (List<?>) pair.right()) {
                    names.add(String.valueOf(name));
                }
                return names;
            }
        }
        return Set.of();
    }
}
