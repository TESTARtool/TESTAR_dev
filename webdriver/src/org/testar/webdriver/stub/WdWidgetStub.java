/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.stub;

import org.openqa.selenium.remote.RemoteWebElement;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Role;
import org.testar.core.alayer.Roles;
import org.testar.core.tag.Tags;
import org.testar.webdriver.state.WdElement;
import org.testar.webdriver.state.WdState;
import org.testar.webdriver.state.WdWidget;

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
