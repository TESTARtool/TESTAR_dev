/**
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
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
 *
 */

package org.testar.webdriver.utils;

import java.awt.Color;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtils {

    private ColorUtils() {}

    /**
     * Converts an "rgb(r, g, b)" string to a Color object.
     */
    public static Color rgbToColor(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        Pattern pattern = Pattern.compile(
                "rgba?\\s*\\(\\s*(\\d{1,3})\\s*,\\s*(\\d{1,3})\\s*,\\s*(\\d{1,3})" +
                        "(?:\\s*,\\s*(\\d*(?:\\.\\d+)?))?\\s*\\)",
                        Pattern.CASE_INSENSITIVE
                );
        Matcher matcher = pattern.matcher(input.trim());

        if (!matcher.matches()) {
            return null;
        }

        try {
            int r = Integer.parseInt(matcher.group(1));
            int g = Integer.parseInt(matcher.group(2));
            int b = Integer.parseInt(matcher.group(3));

            if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                return null;
            }

            String alphaGroup = matcher.group(4);
            int a = 255; // default opaque

            if (alphaGroup != null && !alphaGroup.isEmpty()) {
                float alphaFloat = Float.parseFloat(alphaGroup);
                if (alphaFloat < 0f || alphaFloat > 1f) {
                    return null;
                }
                a = Math.round(alphaFloat * 255);
            }

            return new Color(r, g, b, a);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Returns a human-friendly color name for a Color. */
    public static String colorToName(Color c) {
        if (c == null) return null;
        if (c.getAlpha() == 0) return "transparent";

        // Drop alpha for naming; compare on RGB only
        int r = c.getRed(), g = c.getGreen(), b = c.getBlue();

        // 1) Exact match first
        String exact = EXACT_LOOKUP.get((r << 16) | (g << 8) | b);
        if (exact != null) return exact;

        // 2) Otherwise nearest neighbor in RGB space
        NamedColor nearest = null;
        int bestDistSq = Integer.MAX_VALUE;
        for (NamedColor nc : NAMED_COLORS) {
            int dr = r - nc.r, dg = g - nc.g, db = b - nc.b;
            int distSq = dr*dr + dg*dg + db*db;
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                nearest = nc;
            }
        }

        // Optional: only accept a "close enough" match. Tune threshold as needed.
        // Max possible dist^2 in RGB is 3*(255^2)=195075. Threshold here: (distance 30)^2.
        //int ACCEPT_DIST_SQ = 30 * 30;
        //if (nearest != null && bestDistSq <= ACCEPT_DIST_SQ) {
        //    return nearest.name;
        //}

        if (nearest != null) {
            return nearest.name;
        }

        // Fallback: give hex string
        return toHex(c);
    }

    /** Convenience: takes "rgb(...)" / "rgba(...)" and returns a name. */
    public static String cssRgbToName(String cssRgb) {
        Color c = rgbToColor(cssRgb);
        return c == null ? null : colorToName(c);
    }

    /** "#RRGGBB" (ignores alpha) */
    public static String toHex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static final class NamedColor {
        final String name;
        final int r, g, b;
        NamedColor(String name, int r, int g, int b) {
            this.name = name; this.r = r; this.g = g; this.b = b;
        }
        int key() { return (r << 16) | (g << 8) | b; }
    }

    // Curated set of common/CSS color names.
    // You can expand this list (e.g., full CSS3 140-color set) if you want finer matches.
    private static final List<NamedColor> NAMED_COLORS = List.of(
            new NamedColor("black",   0,   0,   0),
            new NamedColor("white",   255, 255, 255),
            new NamedColor("red",     255, 0,   0),
            new NamedColor("lime",    0,   255, 0),
            new NamedColor("blue",    0,   0,   255),
            new NamedColor("yellow",  255, 255, 0),
            new NamedColor("cyan",    0,   255, 255),
            new NamedColor("magenta", 255, 0,   255),
            new NamedColor("gray",    128, 128, 128),
            new NamedColor("silver",  192, 192, 192),
            new NamedColor("maroon",  128, 0,   0),
            new NamedColor("olive",   128, 128, 0),
            new NamedColor("green",   0,   128, 0),
            new NamedColor("purple",  128, 0,   128),
            new NamedColor("teal",    0,   128, 128),
            new NamedColor("navy",    0,   0,   128),
            new NamedColor("orange",  255, 165, 0),
            new NamedColor("pink",    255, 192, 203),
            new NamedColor("brown",   165, 42,  42),
            new NamedColor("gold",    255, 215, 0),
            new NamedColor("lightgray", 211, 211, 211),
            new NamedColor("darkgray",  169, 169, 169),
            new NamedColor("indigo",  75,  0,   130),
            new NamedColor("violet",  238, 130, 238),
            new NamedColor("beige",   245, 245, 220),
            new NamedColor("tan",     210, 180, 140),
            new NamedColor("chocolate", 210, 105, 30),
            new NamedColor("salmon",  250, 128, 114),
            new NamedColor("turquoise", 64,  224, 208),
            new NamedColor("skyblue", 135, 206, 235),
            new NamedColor("royalblue", 65, 105, 225),
            new NamedColor("deepskyblue", 0, 191, 255),
            new NamedColor("dodgerblue", 30, 144, 255),
            new NamedColor("crimson", 220, 20,  60),
            new NamedColor("tomato",  255, 99,  71),
            new NamedColor("forestgreen", 34,  139, 34),
            new NamedColor("seagreen", 46, 139, 87),
            new NamedColor("khaki",   240, 230, 140),
            new NamedColor("lavender", 230, 230, 250)
            );

    private static final Map<Integer, String> EXACT_LOOKUP;
    static {
        Map<Integer, String> m = new HashMap<>();
        for (NamedColor nc : NAMED_COLORS) {
            m.put(nc.key(), nc.name);
        }
        EXACT_LOOKUP = Collections.unmodifiableMap(m);
    }

}
