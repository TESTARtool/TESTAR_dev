/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import org.testar.core.exceptions.ActionFailedException;
import org.testar.webdriver.state.WdDriver;
import org.testar.core.action.*;
import org.testar.core.alayer.*;
import org.testar.core.state.*;
import org.testar.core.tag.*;

public class WdSubmitAction extends TaggableBase implements Action {
    private static final long serialVersionUID = 9102753249877445289L;

    private String formId;

    public WdSubmitAction(String formId) {
        this.formId = formId;
        this.set(Tags.Role, WdActionRoles.SubmitScript);
        this.set(Tags.Desc, "Execute Webdriver script to submit an action into " + formId);
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        String form = String.format("document.getElementById('%s')", formId);
        try {
            WdDriver.executeScript(String.format("%s.submit();", form));
        } catch (Exception wde) {
            String message = "";
            if(wde.getMessage() != null) {
                message = wde.getMessage();
            }
            // The form can not be found by id, let's try by name
            if (message.contains("Cannot read property 'submit' of null") || message.contains("Cannot read properties of null (reading 'submit')")) {
                form = String.format("document.getElementsByName('%s')[0]", formId);
                WdDriver.executeScript(String.format("%s.submit();", form));
            }
            // Let's try by clicking on the submit button
            else if (message.contains("submit is not a function")) {
                WdDriver.executeScript(String.format( "%s.querySelector('input[type=\"submit\"]').click();", form));
            }
        }
    }

    @Override
    public String toShortString() {
        return "Submit form : '" + formId + "'";
    }

    @Override
    public String toParametersString() {
        return toShortString();
    }

    @Override
    public String toString(Role... discardParameters) {
        return toShortString();
    }
}