/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android;

import org.w3c.dom.Document;

public final class AndroidPageSourceResult {

    private final Document document;
    private final String feedback;

    public AndroidPageSourceResult(Document document, String feedback) {
        this.document = document;
        this.feedback = feedback;
    }

    public Document getDocument() {
        return document;
    }

    public String getFeedback() {
        return feedback;
    }
}
