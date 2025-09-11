/**
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2025 Universitat Politecnica de Valencia - www.upv.es
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
 *
 */

package org.testar.monkey.alayer.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.TaggableBase;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.WdDriver;

public class WdSelectListAction extends TaggableBase implements Action {
    private static final long serialVersionUID = -5522966388178892530L;
    protected static final Logger logger = LogManager.getLogger();

    private String target;
    private String value;
    private JsTargetMethod targetMethod;

    public enum JsTargetMethod {
        ID,
        NAME,
        CSS
    }

    public WdSelectListAction(String target, String value, Widget widget, JsTargetMethod targetMethod) {
        this.target = target;
        this.value = value;
        this.targetMethod = targetMethod;
        this.set(Tags.Role, WdActionRoles.SelectListAction);
        this.set(Tags.Desc, "Set Webdriver select list script to set into " + targetMethod.toString() + " " + target + " : " + value);
        this.mapOriginWidget(widget);
    }

    @Override
    public void run(SUT system, State state, double duration) {
        String selectScript =
                "(() => {" +
                "  const field = %s;" +
                "  if (!field) return false;" +
                "  const wanted = '%s';" +
                "  const opts = Array.from(field.options);" +
                "  const norm = s => (s == null ? '' : ('' + s).trim());" +
                "  const match = opts.find(o => norm(o.value) === wanted || norm(o.label) === wanted || norm(o.textContent) === wanted);" +
                "  if (match) {" +
                "    field.value = match.value;" +
                "    match.selected = true;" +
                "  } else {" +
                "    field.value = wanted;" +
                "  }" +
                "  field.dispatchEvent(new Event('input',  { bubbles: true }));" +
                "  field.dispatchEvent(new Event('change', { bubbles: true }));" +
                "})();";
        
        switch(targetMethod) {
        case ID:
            WdDriver.executeScript(String.format(
                    selectScript,
                    String.format("document.getElementById('%s')", target),
                    value
                    ));
            break;

        case NAME:
            WdDriver.executeScript(String.format(
                    selectScript,
                    String.format("document.getElementsByName('%s')[0]", target),
                    value
                    ));
            break;

        case CSS:
            WdDriver.executeScript(String.format(
                    selectScript,
                    String.format("document.querySelector('%s')", target),
                    value
                    ));
            break;

        default:
            logger.warn("WdSelectListAction targetMethod is null!");
        }
    }

    @Override
    public String toShortString() {
        return "Set select list on '" + target + "' to '" + value + "'";
    }

    @Override
    public String toParametersString() {
        return toShortString();
    }

    @Override
    public String toString(Role... discardParameters) {
        return toShortString();
    }

    public String getValue() {
        return value;
    }

    public String getTarget() {
        return target;
    }
}
