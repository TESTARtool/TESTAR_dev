/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core;

import java.util.List;
import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.action.ActionIdentity;
import org.testar.core.state.State;
import org.testar.core.state.StateIdentity;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.util.IdentityEncoding;

/**
 * Core coding manager.
 */
public class CodingManager {

    public static final int ID_LENTGH = 24; // 2 (prefixes) + 7 (MAX_RADIX) + 5 (max expected text length) + 10 (CRC32)

    // Concrete and Abstract identifiers used for widgets, states, and actions
    public static final String CONCRETE_ID = "ConcreteID";
    public static final String ABSTRACT_ID = "AbstractID";

    public static final String ABSTRACT_R_ID = "Abs(R)ID"; // ROLE
    public static final String ABSTRACT_R_T_ID = "Abs(R,T)ID"; // ROLE, TITLE
    public static final String ABSTRACT_R_T_P_ID = "Abs(R,T,P)ID"; // ROLE, TITLE, PATH

    public static final String ID_PREFIX_CONCRETE = "C";
    public static final String ID_PREFIX_ABSTRACT = "A";
    public static final String ID_PREFIX_ABSTRACT_R = "R";
    public static final String ID_PREFIX_ABSTRACT_R_T = "T";
    public static final String ID_PREFIX_ABSTRACT_R_T_P = "P";

    public static final String ID_PREFIX_STATE = "S";
    public static final String ID_PREFIX_WIDGET = "W";
    public static final String ID_PREFIX_ACTION = "A";

    // Runtime services own their configuration; these defaults support direct CodingManager callers.
    private static StateIdentity stateIdentity = StateIdentity.fromAttributes(List.of("WidgetControlType"));

    /**
     * This method initializes the coding manager with custom tags to use for constructing
     * concrete and abstract state ids.
     */
    public static synchronized void initCodingManager(List<String> abstractStateAttributes) {
        stateIdentity = StateIdentity.fromAttributes(abstractStateAttributes);
    }

    /**
     * Set the array of tags that should be used in constructing the concrete state id's.
     *
     * @param tags array
     */
    public static synchronized void setCustomTagsForConcreteId(Tag<?>[] tags) {
        stateIdentity = new StateIdentity(stateIdentity.abstractTags(), tags);
    }

    /**
     * Set the array of tags that should be used in constructing the abstract state id's.
     *
     * @param tags
     */
    public static synchronized void setCustomTagsForAbstractId(Tag<?>[] tags) {
        stateIdentity = new StateIdentity(tags, stateIdentity.concreteTags());
    }

    /**
     * Returns the tags that are currently being used to create a custom abstract state id
     * @return
     */
    public static synchronized Tag<?>[] getCustomTagsForAbstractId() {
        return stateIdentity.abstractTags();
    }

    /**
     * Returns the tags that are currently being used to create a custom concrete state id
     * @return
     */
    public static synchronized Tag<?>[] getCustomTagsForConcreteId() {
        return stateIdentity.concreteTags();
    }

    /**
     * Returns the default tags for use in creating the abstract state id
     * @return
     */
    public static Tag<?>[] getDefaultAbstractStateTags() {
        return new Tag<?>[] {StateManagementTags.WidgetControlType};
    }

    public static synchronized StateIdentity getStateIdentity() {
        return stateIdentity;
    }

    // ###########################################
    //  Widgets/States and Actions IDs management
    // ###########################################

    /**
     * Builds IDs for a widget or state.
     * @param widget A widget or a State (widget-tree, or widget with children)
     *
     * Widget IDs encode selected attribute names, types, and values with explicit boundaries.
     * State IDs also encode root attributes and the ordered parent/child structure.
     */
    public static synchronized void buildIDs(Widget widget) {
        stateIdentity.buildIDs(widget);
    }

    /**
     * Builds IDs (abstract, concrete) for a set of actions.
     * @param state Current State of the SUT
     * @param actions The actions.
     */
    public static synchronized void buildIDs(State state, Set<Action> actions) {
        Assert.notNull(state, actions);
        for (Action action : actions) {
            buildActionIDs(state, Assert.notNull(action));
        }
    }

    /**
     * Builds IDs (abstract, concrete) for an environment action using the same identity contract.
     * @param action An action.
     */
    public static synchronized void buildEnvironmentActionIDs(State state, Action action) {
        buildActionIDs(Assert.notNull(state), Assert.notNull(action));
    }

    private static void buildActionIDs(State state, Action action) {
        String abstractIdentity = ActionIdentity.encode(state.get(Tags.AbstractID), ActionIdentity.describe(state, action, true));
        String concreteIdentity = ActionIdentity.encode(state.get(Tags.ConcreteID), ActionIdentity.describe(state, action, false));
        action.set(Tags.AbstractID, ID_PREFIX_ACTION + ID_PREFIX_ABSTRACT + IdentityEncoding.hash(abstractIdentity));
        action.set(Tags.ConcreteID, ID_PREFIX_ACTION + ID_PREFIX_CONCRETE + IdentityEncoding.hash(concreteIdentity));
    }

    // #####################################
    // ## New abstract state model coding ##
    // #####################################

    /**
     * This method will return the unique hash to identify the abstract state model
     * @return String A unique hash
     */
    public static synchronized String getAbstractStateModelHash(String applicationName, String applicationVersion) {
        return stateIdentity.modelHash(applicationName, applicationVersion);
    }

}
