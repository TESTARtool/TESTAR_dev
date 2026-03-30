/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2021-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2021-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.core.Assert;
import org.testar.core.Pair;
import org.testar.core.action.*;
import org.testar.core.alayer.*;
import org.testar.core.state.*;
import org.testar.core.tag.*;
import org.testar.core.visualizers.Visualizer;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.exceptions.PositionException;
import org.testar.webdriver.state.WdWidget;
import org.testar.webdriver.tag.WdTags;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.remote.RemoteWebElement;

public class WdRemoteTypeAction extends TaggableBase implements Action {
	private static final long serialVersionUID = -7167990248634363307L;

	protected WdWidget widget;
	protected CharSequence keys;
	protected static final Logger logger = LogManager.getLogger();

	private static final Pen TypePen = Pen.newPen().setColor(Color.Blue)
			.setFillPattern(FillPattern.None).setStrokeWidth(3).build();

	public static class TypeVisualizer implements Visualizer {

		private static final long serialVersionUID = 856304220974950751L;

		final String text;
		final Pen pen;
		final Rect rect;

		public TypeVisualizer(Rect rect, String text, Pen pen){
			Assert.notNull(rect, text, pen);
			this.text = text;
			this.pen = pen;
			this.rect = rect;
		}

		public void run(State state, Canvas cv, Pen pen) {
			Assert.notNull(state, cv, pen);
			pen = Pen.merge(pen, this.pen);
			try { 
				Pair<Double, Double> m = cv.textMetrics(pen, text);
				double mx = rect.x() + (rect.width() / 2.0);
				double my = rect.y() + (rect.height() / 2.0);

				cv.text(pen, mx - (m.left() / 2.0), my - (m.right() / 2.0), 0, text);
			} catch (PositionException pe) {}
		}
	}

	public WdRemoteTypeAction(WdWidget widget, CharSequence keys) {
		this.widget = widget;
		this.mapOriginWidget(widget);
		this.set(Tags.Role, WdActionRoles.RemoteType);
		setKeys(keys);
	}

	public void setKeys(CharSequence keys) {
		String keyText = keys == null ? "" : keys.toString();
		this.keys = keyText;
		this.set(Tags.InputText, keyText);
		this.set(Tags.Desc, "Remote type " + keyText + " to widget " + widget.get(Tags.Desc, widget.element.getElementDescription()));
		this.set(Tags.Visualizer, new TypeVisualizer(widget.get(WdTags.WebBoundingRectangle), keyText, TypePen));
	}

	@Override
	public void run(SUT system, State state, double duration) throws ActionFailedException {
		try {
			RemoteWebElement remoteElement = widget.element.remoteWebElement;
			remoteElement.clear();
			org.testar.core.util.Util.pause(0.1);
			remoteElement.sendKeys(keys);
		}
		catch (ElementClickInterceptedException ee) {
			// This happens when other element obscure the desired element to interact with
			logger.warn(String.format("%s : %s", this.get(Tags.Desc, ""), ee.getMessage()));
		}
		catch (StaleElementReferenceException se) {
			// This happens when the state changes between obtaining the widget and executing the action
			logger.warn(String.format("%s : %s", this.get(Tags.Desc, ""), se.getMessage()));
		}
		catch (InvalidElementStateException ie) {
		    // This happens when trying to execute a type action into a non-editable element
		    logger.warn(String.format("%s : %s", this.get(Tags.Desc, ""), ie.getMessage()));
		}
		catch (Exception e) {
			logger.warn("Remote type action failed", e);
		}
	}

	@Override
	public String toShortString() {
		return "Remote type " + keys + " " + widget.element.getElementDescription();
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

	public CharSequence getKeys() {
		return keys;
	}
}
