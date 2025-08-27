package org.testar.oracles;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TagsBase;

public class DSLTags extends TagsBase {

    protected static final Set<Tag<?>> dslTags = new HashSet<>();

    private DSLTags() {
    }

    public static final Tag<String> DSLTableHeaderText = from("DSLTableHeaderText", String.class);

    public static Set<Tag<?>> getAllTags() {
        return tagSet;
    }

    protected static <T> Tag<T> from(String name, Class<T> valueType) {
        Tag<T> tag = TagsBase.from(name, valueType);
        dslTags.add(tag);
        return tag;
    }

    public static Set<Tag<?>> getDSLTags() {
        return Collections.unmodifiableSet(dslTags);
    }
}
