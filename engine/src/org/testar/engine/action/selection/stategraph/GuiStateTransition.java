/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.selection.stategraph;

public class GuiStateTransition {
    private String sourceStateAbstractId;
    private String targetStateAbstractId;
    private String actionAbstractId;

    public GuiStateTransition(String sourceStateAbstractId, String targetStateAbstractId, String actionAbstractId) {
        this.sourceStateAbstractId = sourceStateAbstractId;
        this.targetStateAbstractId = targetStateAbstractId;
        this.actionAbstractId = actionAbstractId;
    }

    public String getSourceStateAbstractId() {
        return sourceStateAbstractId;
    }

    public String getTargetStateAbstractId() {
        return targetStateAbstractId;
    }

    public String getActionAbstractId() {
        return actionAbstractId;
    }
}
