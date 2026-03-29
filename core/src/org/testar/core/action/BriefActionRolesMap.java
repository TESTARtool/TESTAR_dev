/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */


package org.testar.core.action;

import java.util.HashMap;

/**
 * ActionRoles brief map.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class BriefActionRolesMap {

    public static final HashMap<String, String> map = new HashMap<String, String>() {
        private static final long serialVersionUID = 4653256125166285524L;

        {
            put("LeftClickAt", "LC"); // ActionRoles.LeftClickAt
            put("RightClickAt", "RC"); // ActionRoles.RightClickAt
            put("ClickTypeInto", "T"); // ActionRoles.ClickTypeInto
        }
    };

}
