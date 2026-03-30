/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import org.testar.core.state.*;
import org.testar.core.tag.*;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.webdriver.state.WdWidget;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

public class WdRemoteScrollTypeAction extends WdRemoteTypeAction {
	private static final long serialVersionUID = 5967321502092097275L;

	public WdRemoteScrollTypeAction(WdWidget widget, CharSequence keys) {
		super(widget, keys);
		this.set(Tags.Desc, "Remote scroll and type " + keys + " to widget " + widget.get(Tags.Desc, widget.element.getElementDescription()));
		this.set(Tags.Role, WdActionRoles.RemoteScrollType);
	}

	@Override
	public void run(SUT system, State state, double duration) throws ActionFailedException {
		try {
			RemoteWebElement remoteElement = widget.element.remoteWebElement;
			RemoteWebDriver d = (RemoteWebDriver)remoteElement.getWrappedDriver();
			// Scroll the element to the middle of the screen
			// Because scrolling the element to the top may provoke an obscuration by some web headers
			d.executeScript("arguments[0].scrollIntoView({block: 'center'})", remoteElement);
			remoteElement.clear();
			org.testar.core.util.Util.pause(0.1);
			remoteElement.sendKeys(keys);
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
			logger.warn("Remote scroll and type action failed", e);
		}
	}

	@Override
	public String toShortString() {
		return "Remote scroll and type " + keys + " " + widget.element.getElementDescription();
	}

}
