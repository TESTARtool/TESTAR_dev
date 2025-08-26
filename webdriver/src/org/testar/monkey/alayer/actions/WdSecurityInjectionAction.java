/***************************************************************************************************
 *
 * Copyright (c) 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.monkey.alayer.actions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

/** Enables TESTAR to use special characters in input fields **/
public class WdSecurityInjectionAction extends TaggableBase implements Action {
    private static final long serialVersionUID = 754070773402916305L;

    private WebElement element;
    private String text;

    public WdSecurityInjectionAction(RemoteWebDriver webDriver, Widget widget, String text)
    {
    	//TODO: This only works if the web element contains the id property
        element = webDriver.findElement(new By.ById(widget.get(WdTags.WebId, "")));
        this.set(Tags.Role, WdActionRoles.FormFillingAction);
        this.set(Tags.Desc, "Inject text that contains special characters");
        this.text = text;
        this.mapOriginWidget(widget);
    }

    public String getText() {
    	return text;
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        element.clear();
        element.sendKeys(this.text);
    }

    @Override
    public String toShortString() {
        return "Inject text";
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
