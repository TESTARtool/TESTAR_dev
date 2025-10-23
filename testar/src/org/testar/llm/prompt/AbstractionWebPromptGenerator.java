/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.llm.prompt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.statemodel.StateModelManager;

public class AbstractionWebPromptGenerator implements IPromptAbstractionGenerator {

    protected static final Logger logger = LogManager.getLogger();

    /**
     * Creates a new state abstraction identifier prompt generator that uses state images.
     */
    public AbstractionWebPromptGenerator() {}

    @Override
    public String generateAbstractionPrompt(State state, String appName) {

        String webUrl = state.get(WdTags.WebHref, "");
        String pageTitle = state.get(WdTags.WebTitle, "");

        StringBuilder builder = new StringBuilder();
        builder.append("You are a web test agent that outputs ONE short GUI state identifier (30 characters aprox). ");
        builder.append("Respond in JSON only as: {\"identifier\":\"State Identifier\"}. ");
        builder.append(String.format("App: %s ", appName));
        builder.append(String.format("URL: %s ", webUrl));
        builder.append(String.format("Page title: %s ", pageTitle));
        builder.append("A screenshot of the current state is attached. ");

        return builder.toString();
    }

    /**
     * Creates a new state abstraction identifier prompt generator. 
     * It uses the state model transitions information, together with current state images. 
     */
    @Override
    public String generateAbstractionPrompt(State currentState,
            State previousState,
            Action currentAction, 
            StateModelManager stateModelManager, 
            String appName) {

        String modelIdentifier = stateModelManager.getModelIdentifier();

        /*
            SELECT
              actionId,
              {"identifier": fromId} AS fromState,
              {"identifier": toId}   AS toState
            FROM (
              SELECT DISTINCT
                actionId,
                out.stateId AS fromId,
                in.stateId  AS toId
              FROM AbstractAction
              WHERE modelIdentifier LIKE '1vbls211b996618654%'
            )
            ORDER BY actionId, fromId, toId
         */

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ");
        queryBuilder.append("actionId, ");
        queryBuilder.append("{'identifier': fromId} AS fromState, ");
        queryBuilder.append("{'identifier': toId}   AS toState ");
        queryBuilder.append("FROM ( ");
        queryBuilder.append("SELECT DISTINCT ");
        queryBuilder.append("actionId, ");
        queryBuilder.append("out.stateId AS fromId, ");
        queryBuilder.append("in.stateId  AS toId ");
        queryBuilder.append("FROM AbstractAction ");
        queryBuilder.append("WHERE modelIdentifier LIKE '" + modelIdentifier + "%' ");
        queryBuilder.append(") ");
        queryBuilder.append("ORDER BY actionId, fromId, toId ");

        String query = queryBuilder.toString();
        String modelTransitions = stateModelManager.queryStateModel(query);

        String previousId = previousState.get(Tags.AbstractID);

        String webUrl = currentState.get(WdTags.WebHref, "");
        String pageTitle = currentState.get(WdTags.WebTitle, "");

        String actionId = currentAction.get(Tags.AbstractID);

        StringBuilder builder = new StringBuilder();
        builder.append("You are a web test agent that outputs GUI state identifiers. ");
        builder.append("You have context of existing GUI model transitions, previous state, executed action, and the current State. ");
        builder.append("You have to output ONE short GUI state identifier (30 characters aprox) of the current State. ");
        builder.append("Using the existing context you have to determine if creating a new identifier or using an existing one. ");
        builder.append("Respond in JSON only as: {\"identifier\":\"State Identifier\"}. ");

        builder.append(String.format("CONTEXT of existing State Model Transitions: %s . ", modelTransitions));

        builder.append(String.format("CONTEXT of previous State: %s . ", previousId));

        builder.append(String.format("CONTEXT of executed Action: %s . ", actionId));

        builder.append(String.format("CONTEXT of current State information: "));
        builder.append(String.format("App: %s ", appName));
        builder.append(String.format("URL: %s ", webUrl));
        builder.append(String.format("Page title: %s ", pageTitle));
        builder.append("A screenshot of the current state is attached. ");

        return builder.toString();
    }

}