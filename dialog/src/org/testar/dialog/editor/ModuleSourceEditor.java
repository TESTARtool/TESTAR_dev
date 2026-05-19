/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.dialog.editor;

public final class ModuleSourceEditor extends AbstractTextFileEditor {

    private static final long serialVersionUID = 7608356901520255946L;

    public ModuleSourceEditor(String sourceFilePath, String titleSuffix, String defaultTemplate) {
        super("Module Source Editor - " + titleSuffix, sourceFilePath, defaultTemplate);
    }
}
