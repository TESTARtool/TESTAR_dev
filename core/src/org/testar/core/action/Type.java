/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.testar.core.Assert;
import org.testar.core.alayer.Role;
import org.testar.core.devices.KBKeys;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;

/**
 * An action that types a given text on the StandardKeyboard of the SUT.
 */
public final class Type extends TaggableBase implements Action {

    private static final long serialVersionUID = 2555715152455716781L;
    private static final CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
    private final String text;

    public Type(String text) {
        Assert.hasText(text);
        checkAscii(text);
        this.text = text;
    }

    public void run(SUT system, State state, double duration) throws ActionFailedException {
        Assert.isTrue(duration >= 0);
        Assert.notNull(system);

        // Tag used to change the final text of Type actions
        // This is necessary for LLMs decisions but may alter the actions abstraction
        String inputText = this.get(Tags.InputText, this.text);

        double d = duration / inputText.length();
        Action shiftDown = new KeyDown(KBKeys.VK_SHIFT);
        Action shiftUp = new KeyUp(KBKeys.VK_SHIFT);

        for (int i = 0; i < inputText.length(); i++) {

            try {

                char c = inputText.charAt(i);
                boolean shift = false;

                if (Character.isLetter(c)) {
                    if (Character.isLowerCase(c)) {
                        c = Character.toUpperCase(c);
                    } else {
                        shift = true;
                    }
                }

                KBKeys key = getKey(c);

                if (shift) {
                    shiftDown.run(system, state, .0);
                }
                new KeyDown(key).run(system, state, .0);
                new KeyUp(key).run(system, state, .0);
                if (shift) {
                    shiftUp.run(system, state, .0);
                }
                Util.pause(d);

            } catch (IllegalArgumentException e) {
                System.out.println("TESTAR support for better character encodings is being developed");
                System.out.println(e.getMessage());
            }
        }
    }

    public static void checkAscii(String text) {
        if (!asciiEncoder.canEncode(text)) {
            throw new IllegalArgumentException("This string is not an ascii string!");
        }
    }

    private KBKeys getKey(char c) {
        for (KBKeys key : KBKeys.values()) {
            if (key.code() == (int) c) {
                return key;
            }
        }

        throw new IllegalArgumentException("Unable to find the corresponding keycode for character '" + c + "(" + ((int)c) +  ")'!");
    }
    
    public String toString() {
        return "Type text '" + this.get(Tags.InputText, this.text) + "'";
    }

    @Override
    public String toString(Role... discardParameters) {
        for (Role r : discardParameters) {
            if (r.name().equals(ActionRoles.Type.name())) {
                return "Text typed";
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
