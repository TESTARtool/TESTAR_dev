/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.commons.text.StringEscapeUtils;
import org.testar.core.Assert;
import org.testar.core.alayer.Role;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.exceptions.ActionFailedException;

public final class PasteText extends TaggableBase implements Action {

    private static final long serialVersionUID = 1033277478070938602L;
    private static final CharsetEncoder CharEncoder = Charset.forName("UTF-32").newEncoder();
    private final String text;

    public PasteText(String text) {
        Assert.hasText(text);
        checkEncoder(text);
        this.text = text;
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        Assert.isTrue(duration >= 0);
        Assert.notNull(system);

        // Tag used to change the final text of PasteText actions
        // This is necessary for LLMs decisions but may alter the actions abstraction
        String inputText = this.get(Tags.InputText, this.text);

        StringSelection selection = new StringSelection(inputText);

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        try {
            clipboard.setContents(selection, null);
        } catch (IllegalStateException ise) {
            throw new ActionFailedException("PasteText action execution failed due to currently unavailable clipboard!", ise);
        }

        system.get(Tags.StandardKeyboard).paste();
    }

    public static void checkEncoder(String text) {
        if (!CharEncoder.canEncode(text)) {
            throw new IllegalArgumentException("This string is not an UTF-32 string!");
        }
    }

    public String toString() {
        return "Pasted text '" + StringEscapeUtils.escapeHtml4(this.get(Tags.InputText, this.text)) + "'";
    }

    @Override
    public String toString(Role... discardParameters) {
        for (Role r : discardParameters) {
            if (r.name().equals(ActionRoles.Type.name())) {
                return "Text pasted";
            }
        }
        return toString();
    }

    @Override
    public String toShortString() {
        Role r = get(Tags.Role, null);
        if (r != null) {
            return r.toString();
        } else {
            return toString();
        }
    }

    @Override
    public String toParametersString() {
        return "(" + this.get(Tags.InputText, this.text) + ")";
    }

}
