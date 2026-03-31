/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.action;

import org.openqa.selenium.WebElement;
import org.testar.android.AndroidAppiumFramework;
import org.testar.android.alayer.AndroidRoles;
import org.testar.android.tag.AndroidTags;
import org.testar.core.alayer.*;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;
import org.testar.core.visualizers.TextVisualizer;

public class AndroidActionType extends TaggableBase implements Action {

	private static final long serialVersionUID = 6685918140970666660L;

	private final String accessibilityId;
	private final Widget widget;
	private final String widgetClass;
	private final String xpath;

	private static final Pen TypePen = Pen.newPen().setColor(Color.Blue)
			.setFillPattern(FillPattern.None).setStrokeWidth(3).build(); // use default font size
	private final int DISPLAY_TEXT_MAX_LENGTH = 16;

	public AndroidActionType(State state, Widget w, String typeText) {
		this.set(Tags.Role, ActionRoles.ClickTypeInto);
		this.mapOriginWidget(w);
		this.set(Tags.InputText, typeText);
		this.accessibilityId = w.get(AndroidTags.AndroidAccessibilityId, "");
		this.widget = w;
		this.widgetClass = w.get(AndroidTags.AndroidClassName, "");
		this.xpath = w.get(AndroidTags.AndroidXpath);
		double relX = w.get(Tags.Shape).x() + w.get(Tags.Shape).width()/2;
		double relY = w.get(Tags.Shape).y() + w.get(Tags.Shape).height()/2;
		Position position = new AbsolutePosition(relX, relY);
		this.set(Tags.Visualizer, new TextVisualizer(position, Util.abbreviate(typeText, DISPLAY_TEXT_MAX_LENGTH, "..."), TypePen));
		this.set(Tags.Desc, toShortString());
	}

	@Override
	public void run(SUT system, State state, double duration) throws ActionFailedException {
		String typeText = this.get(Tags.InputText, "");
		try {
			WebElement element = AndroidAppiumFramework.resolveElementByIdOrXPath(this.accessibilityId, this.widget);
			element.clear();
			element.sendKeys(typeText);
		} catch(Exception e) {
			System.err.println("Exception trying to Type " + typeText + " in the Element with Id : " + this.accessibilityId);
			System.err.println(e.getMessage());
			throw new ActionFailedException(toShortString());
		}
	}

	@Override
	public String toShortString() {
		String typeText = this.get(Tags.InputText, "");
		return "Execute Android type on Widget of type: '" + this.widgetClass + "', with Id: '" + this.accessibilityId + "', with xPath: '" + this.xpath + "', typing text: " + typeText;
	}

	@Override
	public String toParametersString() {
		return "";
	}

	@Override
	public String toString(Role... discardParameters) {
		return "";
	}

	public Widget getWidget(){
		return widget;
	}

}
