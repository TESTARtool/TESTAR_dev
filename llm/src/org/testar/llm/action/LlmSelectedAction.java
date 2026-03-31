/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.action;

/**
 * This is the syntax we want the LLM to respond with.
 * actionId refers to the action to take.
 * input refers to the characters to enter into the widget if applicable.
 */
public class LlmSelectedAction {
    private final String actionId;
    private final String input;

    public LlmSelectedAction(String actionId, String input) {
        this.actionId = actionId;
        this.input = input;
    }

    public String getActionId() {
        return actionId;
    }

    public String getInput() {
        if(input == null) {
            return "";
        }
        return input;
    }
}
