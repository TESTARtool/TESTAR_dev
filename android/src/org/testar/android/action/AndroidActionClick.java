/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.action;

import org.openqa.selenium.WebElement;
import org.testar.android.AndroidAppiumFramework;
import org.testar.core.alayer.*;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;

public class AndroidActionClick extends TaggableBase implements Action {

	private static final long serialVersionUID = 6663144395605910140L;

	private final Widget widget;

	public AndroidActionClick(State state, Widget w) {
		this.set(Tags.Role, ActionRoles.LeftClickAt);
		this.mapOriginWidget(w);
		this.widget = w;
		this.set(Tags.Desc, toShortString());
	}

	@Override
	public void run(SUT system, State state, double duration) throws ActionFailedException {
		try {
			WebElement element = AndroidAppiumFramework.resolveElementByIdOrXPath(this.widget);
			element.click();
		} catch(Exception e) {
			System.err.println("Exception trying to execute : " + toShortString());
			System.err.println(e.getMessage());
			throw new ActionFailedException(toShortString());
		}
	}

	@Override
	public String toShortString() {
		return AndroidActionDescriptions.describeWidgetAction("click", this.widget);
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
