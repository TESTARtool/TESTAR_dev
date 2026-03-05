/**
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.webdriver.stub;

import org.openqa.selenium.remote.RemoteWebElement;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdState;
import org.testar.monkey.alayer.webdriver.WdWidget;

public class WdWidgetStub extends WdWidget {
    private static final long serialVersionUID = 2469259954952714389L;

    public WdWidgetStub() {
        this(createState(), "", "", Roles.Widget, "div", "text", Rect.fromCoordinates(0, 0, 1, 1), createRemoteWebElement(""));
    }

    public WdWidgetStub(String description, String remoteId, Role role, String tagName) {
        this(createState(), description, remoteId, role, tagName, "text", Rect.fromCoordinates(0, 0, 1, 1));
    }

    public WdWidgetStub(WdState state, String description, String remoteId, Role role, String tagName) {
        this(state, description, remoteId, role, tagName, "text", Rect.fromCoordinates(0, 0, 1, 1));
    }

    public WdWidgetStub(WdState state, String description, String remoteId, Role role, String tagName, String type, Rect rect) {
        this(state, description, remoteId, role, tagName, type, rect, createRemoteWebElement(remoteId));
    }

    public WdWidgetStub(WdState state, String description, String remoteId, Role role, String tagName, String type, Rect rect, RemoteWebElement remoteWebElement) {
        super(state, state, createElement(state, description, remoteId, tagName, type, rect, remoteWebElement));
        setDescription(description);
        setRemoteId(remoteId);
        setRole(role);
        setTagName(tagName);
        setType(type);
        setRect(rect);
    }

    public static WdState createState() {
        return new WdState(new WdElement(null, null));
    }

    public WdWidgetStub setDescription(String description) {
        this.element.name = description;
        this.set(Tags.Desc, description);
        return this;
    }

    public WdWidgetStub setRemoteId(String remoteId) {
        this.element.id = remoteId;
        if (this.element.remoteWebElement == null) {
            this.element.remoteWebElement = createRemoteWebElement(remoteId);
        } else {
            this.element.remoteWebElement.setId(remoteId);
        }
        return this;
    }

    public WdWidgetStub setRole(Role role) {
        this.set(Tags.Role, role);
        return this;
    }

    public WdWidgetStub setTagName(String tagName) {
        this.element.tagName = tagName;
        return this;
    }

    public WdWidgetStub setType(String type) {
        this.element.type = type;
        return this;
    }

    public WdWidgetStub setRect(Rect rect) {
        this.element.rect = rect;
        return this;
    }

    public WdWidgetStub setRemoteWebElement(RemoteWebElement remoteWebElement) {
        this.element.remoteWebElement = remoteWebElement;
        return this;
    }

    private static WdElement createElement(WdState state, String description, String remoteId, String tagName, String type, Rect rect, RemoteWebElement remoteWebElement) {
        WdElement element = new WdElement(null, state.element);
        element.name = description;
        element.id = remoteId;
        element.tagName = tagName;
        element.type = type;
        element.rect = rect;
        element.remoteWebElement = remoteWebElement;

        return element;
    }

    private static RemoteWebElement createRemoteWebElement(String remoteId) {
        RemoteWebElement remoteWebElement = new RemoteWebElement();
        remoteWebElement.setId(remoteId);
        return remoteWebElement;
    }
}
