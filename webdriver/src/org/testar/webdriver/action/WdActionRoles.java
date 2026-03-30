/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import org.testar.core.alayer.Role;
import org.testar.core.action.ActionRoles;

public class WdActionRoles {
	private WdActionRoles(){}

	public static final Role

	ExecuteScript = Role.from("ExecuteScript", ActionRoles.Action), 
	CloseTabScript = Role.from("CloseTabScript", ExecuteScript),
	HistoryBackScript = Role.from("HistoryBackScript", ExecuteScript),
	SubmitScript = Role.from("SubmitScript", ExecuteScript),
	SetAttributeScript = Role.from("SetAttributeScript", ExecuteScript),
	FormFillingAction = Role.from("FormFillingAction", ActionRoles.CompoundAction),
	SelectListAction = Role.from("SelectListAction", ExecuteScript),
	RemoteAction = Role.from("RemoteAction", ActionRoles.Action),
	RemoteClick = Role.from("RemoteClick", WdActionRoles.RemoteAction),
	RemoteScrollClick = Role.from("RemoteScrollClick", WdActionRoles.RemoteClick),
	RemoteType = Role.from("RemoteType", WdActionRoles.RemoteAction),
	RemoteScrollType = Role.from("RemoteScrollType", WdActionRoles.RemoteType);

}
