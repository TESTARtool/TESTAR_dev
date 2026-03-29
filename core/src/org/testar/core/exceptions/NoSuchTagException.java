/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.exceptions;

import org.testar.core.tag.Tag;

public class NoSuchTagException extends FruitException {

    private static final long serialVersionUID = 4053535487924085555L;

    public NoSuchTagException(Tag<?> tag) {
        super("Tag '" + tag.name() + "' unavailable!");
    }
}
