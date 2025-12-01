/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2025 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.android.actions;

import org.openqa.selenium.WebElement;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.visualizers.TextVisualizer;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.monkey.alayer.android.enums.AndroidTags;

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
