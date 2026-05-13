/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.reporting.sequence.record;

import java.util.Locale;
import org.testar.core.action.Action;
import org.testar.core.alayer.Role;
import org.testar.core.tag.Tags;

public final class DefaultSemanticActionExtractor implements SemanticActionExtractor {

    @Override
    public SemanticActionRecord extract(Action action) {
        if (action == null) {
            return new SemanticActionRecord("", "", "");
        }

        return new SemanticActionRecord(
                roleName(action),
                action.get(Tags.Desc, ""),
                action.get(Tags.InputText, "")
        );
    }

    private static String roleName(Action action) {
        Role role = action.get(Tags.Role, null);
        if (role == null) {
            return "";
        }
        return role.toString().toLowerCase(Locale.ROOT);
    }
}
