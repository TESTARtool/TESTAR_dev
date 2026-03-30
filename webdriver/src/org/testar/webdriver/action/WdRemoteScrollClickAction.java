/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2021-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2021-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.webdriver.state.WdWidget;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

public class WdRemoteScrollClickAction extends WdRemoteClickAction {
	private static final long serialVersionUID = -4804242088760009256L;

	public WdRemoteScrollClickAction(WdWidget widget) {
		super(widget);
		this.set(Tags.Desc, "Remote scroll and click " + widget.get(Tags.Desc, widget.element.getElementDescription()));
		this.set(Tags.Role, WdActionRoles.RemoteScrollClick);
	}

	@Override
	public void run(SUT system, State state, double duration) throws ActionFailedException {
		try {
			RemoteWebElement remoteElement = widget.element.remoteWebElement;
			RemoteWebDriver d = (RemoteWebDriver)remoteElement.getWrappedDriver();
			// Scroll the element to the middle of the screen
			// Because scrolling the element to the top may provoke an obscuration by some web headers
			d.executeScript("arguments[0].scrollIntoView({block: 'center'})", remoteElement);
			org.testar.core.util.Util.pause(0.1);
			remoteElement.click();
		}
		catch (ElementClickInterceptedException ie) {
			// This happens when other element obscure the desired element to interact with
			logger.warn(String.format("%s : %s", this.get(Tags.Desc, ""), ie.getMessage()));
		}
		catch (StaleElementReferenceException se) {
			// This happens when the state changes between obtaining the widget and executing the action
			logger.warn(String.format("%s : %s", this.get(Tags.Desc, ""), se.getMessage()));
		}
		catch (Exception e) {
			logger.warn("Remote scroll and click action failed", e);
		}
	}

	@Override
	public String toShortString() {
		return "Remote scroll and click " + widget.element.getElementDescription();
	}

}
