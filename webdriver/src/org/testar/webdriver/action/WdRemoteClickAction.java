/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.core.Assert;
import org.testar.core.action.*;
import org.testar.core.alayer.*;
import org.testar.core.state.*;
import org.testar.core.tag.*;
import org.testar.core.visualizers.Visualizer;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.exceptions.PositionException;
import org.testar.webdriver.state.WdWidget;
import org.testar.webdriver.alayer.WdRoles;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.remote.RemoteWebElement;

public class WdRemoteClickAction extends TaggableBase implements Action {
	private static final long serialVersionUID = -965497351267324232L;

	protected WdWidget widget;
	protected static final Logger logger = LogManager.getLogger();

	private static final Pen LClickPen = Pen.newPen().setColor(Color.Green)
			.setFillPattern(FillPattern.Solid).setStrokeWidth(3).build();

	public static class ClickVisualizer implements Visualizer {
		private static final long serialVersionUID = -2006402344810634504L;

		private final double width, height;
		private final Pen pen;
		private final Rect rec;

		public ClickVisualizer(Rect rec, Pen pen, double width, double height){
			Assert.notNull(rec, pen);
			this.width = width;
			this.height = height;
			this.pen = pen;
			this.rec = rec;
		}

		public void run(State state, Canvas canvas, Pen pen) {
			Assert.notNull(state, canvas, pen);
			pen = Pen.merge(pen, this.pen);
			try {
				double mx = rec.x() + (rec.width() / 2.0);
				double my = rec.y() + (rec.height() / 2.0);
				canvas.ellipse(pen, mx - width * .5, my - height * .5, width, height);
			} catch (PositionException pe) {}
		}
	}

	public WdRemoteClickAction(WdWidget widget) {
		this.widget = widget;
		this.mapOriginWidget(widget);
		this.set(Tags.Desc, "Remote click " + widget.get(Tags.Desc, widget.element.getElementDescription()));
		this.set(Tags.Role, WdActionRoles.RemoteClick);
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (!role.equals(WdRoles.WdOPTION)) {
			this.set(Tags.Visualizer, new ClickVisualizer(widget.element.rect, LClickPen, 10, 10));
		}
	}

	@Override
	public void run(SUT system, State state, double duration) throws ActionFailedException {
		try {
			RemoteWebElement remoteElement = widget.element.remoteWebElement;
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
			logger.warn("Remote click action failed", e);
		}
	}

	@Override
	public String toShortString() {
		return "Remote click " + widget.element.getElementDescription();
	}

	@Override
	public String toParametersString() {
		return toShortString();
	}

	@Override
	public String toString(Role... discardParameters) {
		return toShortString();
	}

	@Override
	public String toString() {
		return toShortString();
	}
}
