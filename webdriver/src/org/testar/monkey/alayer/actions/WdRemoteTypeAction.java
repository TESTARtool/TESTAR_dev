/**
 * Copyright (c) 2021 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2021 - 2025 Universitat Politecnica de Valencia - www.upv.es
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
import org.testar.monkey.Assert;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.exceptions.PositionException;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.openqa.selenium.ElementClickInterceptedException;
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
		this.keys = keys;

		this.mapActionToWidget(widget);
		this.set(Tags.Desc, "Remote type " + keys + " to widget " 
				+ widget.element.getElementDescription() + " : " + widget.element.remoteWebElement.getId());
		this.set(Tags.Role, WdActionRoles.RemoteType);
		this.set(Tags.Visualizer, new TypeVisualizer(widget.get(WdTags.WebBoundingRectangle), keys.toString(), TypePen));
	}

	@Override
	public void run(SUT system, State state, double duration) throws ActionFailedException {
		try {
			RemoteWebElement remoteElement = widget.element.remoteWebElement;
			remoteElement.clear();
			org.testar.monkey.Util.pause(0.1);
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
