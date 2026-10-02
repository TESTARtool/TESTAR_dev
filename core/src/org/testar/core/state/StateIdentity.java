/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.state;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

import org.testar.core.Assert;
import org.testar.core.StateManagementTags;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.util.IdentityEncoding;

/** Immutable identification configuration for an ordered widget tree, including its root. */
public final class StateIdentity {

    private static final List<Tag<String>> ID_TAGS = Arrays.asList(
            Tags.ConcreteID,
            Tags.AbstractID,
            Tags.Abstract_R_ID,
            Tags.Abstract_R_T_ID,
            Tags.Abstract_R_T_P_ID
    );
    private static final String[] ID_PREFIXES = {"C", "A", "R", "T", "P"};

    private final Tag<?>[] abstractTags;
    private final Tag<?>[] concreteTags;

    public StateIdentity(Tag<?>[] abstractTags, Tag<?>[] concreteTags) {
        this.abstractTags = sortedCopy(abstractTags);
        this.concreteTags = sortedCopy(concreteTags);
    }

    public static StateIdentity fromAttributes(List<String> abstractStateAttributes) {
        Assert.notNull(abstractStateAttributes);
        Tag<?>[] abstractTags = abstractStateAttributes.stream()
                .map(StateManagementTags::getTagFromSettingsString).filter(Objects::nonNull)
                .distinct().toArray(Tag<?>[]::new);
        if (abstractTags.length == 0) {
            abstractTags = new Tag<?>[] {StateManagementTags.WidgetControlType};
        }
        return new StateIdentity(abstractTags, StateManagementTags.getAllTags().toArray(new Tag<?>[0]));
    }

    public Tag<?>[] abstractTags() {
        return abstractTags.clone();
    }

    public Tag<?>[] concreteTags() {
        return concreteTags.clone();
    }

    public void buildIDs(Widget widget) {
        Assert.notNull(widget);
        Tag<?>[][] selections = {concreteTags, abstractTags, {Tags.Role},
            {Tags.Role, Tags.Title}, {Tags.Role, Tags.Title, Tags.Path}};
        if (widget.parent() != null) {
            assignWidgetIds(widget, selections);
        } else if (widget instanceof State) {
            buildStateIds((State) widget, selections);
        }
    }

    public String modelHash(String applicationName, String applicationVersion) {
        StringBuilder identity = new StringBuilder(IdentityEncoding.encode(applicationName, applicationVersion));
        for (Tag<?> tag : abstractTags) {
            identity.append(IdentityEncoding.encode(tag.name(), tag.type().getName()));
        }
        return IdentityEncoding.hash(identity.toString());
    }

    private void buildStateIds(State state, Tag<?>[][] selections) {
        StringBuilder[] identities = new StringBuilder[ID_TAGS.size()];
        for (int index = 0; index < identities.length; index++) {
            identities[index] = new StringBuilder();
        }

        // Preorder plus child counts preserves hierarchy and sibling order without using the state's iterator.
        Deque<Widget> pending = new ArrayDeque<>();
        pending.push(state);
        while (!pending.isEmpty()) {
            Widget widget = pending.pop();
            String[] widgetIds = assignWidgetIds(widget, selections);
            String childCount = Integer.toString(widget.childCount());
            for (int index = 0; index < identities.length; index++) {
                identities[index].append(IdentityEncoding.encode(widgetIds[index], childCount));
            }
            for (int index = widget.childCount() - 1; index >= 0; index--) {
                pending.push(widget.child(index));
            }
        }
        for (int index = 0; index < identities.length; index++) {
            state.set(ID_TAGS.get(index), "S" + ID_PREFIXES[index] + IdentityEncoding.hash(identities[index].toString()));
        }
    }

    private String[] assignWidgetIds(Widget widget, Tag<?>[][] selections) {
        String[] ids = new String[ID_TAGS.size()];
        for (int index = 0; index < ids.length; index++) {
            ids[index] = "W" + ID_PREFIXES[index] + IdentityEncoding.hash(attributeIdentity(widget, selections[index]));
            if (!(widget instanceof State) || widget.parent() != null) {
                widget.set(ID_TAGS.get(index), ids[index]);
            }
        }
        return ids;
    }

    private String attributeIdentity(Widget widget, Tag<?>[] tags) {
        StringBuilder identity = new StringBuilder();
        for (Tag<?> tag : tags) {
            appendAttribute(identity, widget, tag);
            if (StateManagementTags.isStateManagementTag(tag)
                    && StateManagementTags.getTagGroup(tag) == StateManagementTags.Group.ControlPattern) {
                StateManagementTags.getChildTags(tag).stream().sorted(Comparator.comparing(Tag::name))
                        .forEach(childTag -> appendAttribute(identity, widget, childTag));
            }
        }
        return identity.toString();
    }

    private void appendAttribute(StringBuilder identity, Widget widget, Tag<?> tag) {
        Object value = widget.get(tag, null);
        identity.append(IdentityEncoding.encode(tag.name(), tag.type().getName(), value == null ? null : value.toString()));
    }

    private static Tag<?>[] sortedCopy(Tag<?>[] tags) {
        Tag<?>[] copy = Assert.notNull(tags).clone();
        for (Tag<?> tag : copy) {
            Assert.notNull(tag);
        }
        Arrays.sort(copy, Comparator.comparing((Tag<?> tag) -> tag.name()).thenComparing(tag -> tag.type().getName()));
        return copy;
    }
}
