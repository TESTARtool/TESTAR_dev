/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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
 *******************************************************************************************************/

package org.testar.oracles;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

public class OracleMappingModel {

    private OracleMappingModel() { }

    public static Set<String> getValidStatusPerElement(String elementType) {
        return validStatusPerElement.getOrDefault(elementType, Set.of());
    }

    public static List<RoleMatcher> getElementRoles(String elementType) {
        return element2Role.getOrDefault(elementType, List.of());
    }

    public static List<Tag<?>> getSelectorTags(String selectorType) {
        return selectorString2Tags.getOrDefault(selectorType, List.of());
    }

    public static List<Tag<?>> getStatusTags(String status) {
        return statusTags.getOrDefault(status, List.of());
    }

    public static List<Tag<?>> getAttributeTags(String attribute) {
        return attributeTags .getOrDefault(attribute, List.of());
    }

    private static final Map<String, Set<String>> validStatusPerElement = Map.ofEntries(
            Map.entry("button", Set.of("visible", "enabled", "focused", "clickable", "onscreen", "offscreen")),
            Map.entry("input_text", Set.of("visible", "enabled", "focused", "readonly", "empty", "filled", "onscreen", "offscreen")),
            Map.entry("input_numeric", Set.of("visible", "enabled", "focused", "readonly", "empty", "filled", "onscreen", "offscreen")),
            Map.entry("static_text", Set.of("visible", "enabled", "focused", "readonly", "empty", "onscreen", "offscreen")),
            Map.entry("checkbox", Set.of("visible", "enabled", "focused", "checked", "clickable", "onscreen", "offscreen")),
            Map.entry("radiogroup", Set.of("onscreen", "offscreen")),
            Map.entry("radio", Set.of("visible", "enabled", "focused", "checked", "clickable", "onscreen", "offscreen")),
            Map.entry("dropdown", Set.of("visible", "enabled", "focused", "empty", "selected", "clickable", "onscreen", "offscreen")),
            Map.entry("label", Set.of("visible", "focused", "onscreen", "offscreen")),
            Map.entry("image", Set.of("visible", "focused", "onscreen", "offscreen")),
            Map.entry("link", Set.of("visible", "clickable", "focused", "onscreen", "offscreen")),
            Map.entry("alert", Set.of("visible", "focused", "onscreen", "offscreen")),
            Map.entry("panel", Set.of("visible", "focused", "empty", "onscreen", "offscreen")),
            Map.entry("table_data", Set.of("visible", "focused", "empty", "onscreen", "offscreen")),
            Map.entry("menu", Set.of("visible", "enabled", "focused", "clickable", "empty", "onscreen", "offscreen")),
            Map.entry("menu_item", Set.of("visible", "enabled", "focused", "clickable", "empty", "onscreen", "offscreen")),
            Map.entry("form", Set.of("visible", "focused", "empty", "onscreen", "offscreen")),
            Map.entry("element", Set.of("visible", "enabled", "focused", "empty", "onscreen", "offscreen", "disabled"))
            );

    private static final Map<String, List<RoleMatcher>> element2Role = Map.ofEntries(
            Map.entry("button", List.of(
                    rm(WdRoles.WdBUTTON),
                    rm(WdRoles.WdINPUT, w -> {
                        String type = w.get(WdTags.WebType, "");
                        return Set.of("button", "submit", "reset").contains(type);
                    })
                    )),
            Map.entry("input_text", List.of(
                    rm(WdRoles.WdTEXTAREA),
                    rm(WdRoles.WdINPUT, w -> {
                        String type = w.get(WdTags.WebType, "");
                        return Set.of("text", "password", "email", "tel", "search", "url").contains(type);
                    }),
                    rm(Roles.Text)
                    )),
            Map.entry("input_numeric", List.of(
                    rm(WdRoles.WdINPUT, w -> {
                        String type = w.get(WdTags.WebType, "");
                        return Set.of("number", "range").contains(type);
                    }),
                    rm(Roles.Text)
                    )),
            Map.entry("static_text", List.of(
                    rm(WdRoles.WdLABEL), rm(WdRoles.WdP), rm(WdRoles.WdSPAN),
                    rm(WdRoles.WdPRE), rm(WdRoles.WdCODE), rm(WdRoles.WdBLOCKQUOTE),
                    rm(WdRoles.WdSTRONG), rm(WdRoles.WdEM), rm(WdRoles.WdH1),
                    rm(WdRoles.WdH2), rm(WdRoles.WdH3), rm(WdRoles.WdH4),
                    rm(WdRoles.WdH5), rm(WdRoles.WdH6), rm(WdRoles.WdDIV),
                    rm(WdRoles.WdSECTION), rm(WdRoles.WdARTICLE), rm(WdRoles.WdFIELDSET),
                    rm(WdRoles.WdASIDE), rm(WdRoles.WdMAIN), rm(WdRoles.WdHEADER),
                    rm(WdRoles.WdFOOTER), rm(WdRoles.WdFIGCAPTION)
                    )),
            Map.entry("alert", List.of(
                    rm(Roles.Widget)
                    )),
            Map.entry("dropdown", List.of(
                    rm(WdRoles.WdSELECT), rm(WdRoles.WdOPTION), rm(WdRoles.WdOPTGROUP)
                    )),
            Map.entry("checkbox", List.of(
                    rm(WdRoles.WdINPUT, w -> "checkbox".equals(w.get(WdTags.WebType, "")))
                    )),
            Map.entry("radiogroup", List.of(
                    rm(WdRoles.WdINPUT, w -> "radio".equals(w.get(WdTags.WebType, "")))
                    )),
            Map.entry("radio", List.of(
                    rm(WdRoles.WdINPUT, w -> "radio".equals(w.get(WdTags.WebType, "")))
                    )),
            Map.entry("image", List.of(
                    rm(WdRoles.WdIMG)
                    )),
            Map.entry("link", List.of(
                    rm(WdRoles.WdA)
                    )),
            Map.entry("label", List.of(
                    rm(WdRoles.WdLABEL)
                    )),
            Map.entry("panel", List.of(
                    rm(WdRoles.WdDIV), rm(WdRoles.WdSECTION), rm(WdRoles.WdARTICLE),
                    rm(WdRoles.WdFIELDSET), rm(WdRoles.WdASIDE), rm(WdRoles.WdMAIN),
                    rm(WdRoles.WdHEADER), rm(WdRoles.WdFOOTER)
                    )),
            Map.entry("table_data", List.of(
                    rm(WdRoles.WdTH), rm(WdRoles.WdTD)
                    )),
            Map.entry("menu", List.of(
                    rm(WdRoles.WdMENU), rm(WdRoles.WdUL), rm(WdRoles.WdNAV)
                    )),
            Map.entry("menu_item", List.of(
                    rm(WdRoles.WdLI)
                    )),
            Map.entry("form", List.of(
                    rm(WdRoles.WdFORM)
                    )),
            Map.entry("element", List.of(
                    rm(Roles.Widget)
                    ))
            );

    private static final Map<String, List<Tag<?>>> selectorString2Tags = Map.ofEntries(
            Map.entry("button", List.of(Tags.Title, WdTags.WebTextContent, WdTags.WebValue)),
            Map.entry("input_text", List.of(Tags.Title, WdTags.WebName, WdTags.WebPlaceholder)),
            Map.entry("input_numeric", List.of(Tags.Title, WdTags.WebName, WdTags.WebPlaceholder)),
            Map.entry("static_text", List.of(Tags.Title, WdTags.WebTextContent)),
            Map.entry("alert", List.of(Tags.Title)),
            Map.entry("dropdown", List.of(Tags.Title, WdTags.WebTextContent)),
            Map.entry("checkbox", List.of(Tags.Title)),
            Map.entry("radiogroup", List.of(Tags.Title, WdTags.WebName)),
            Map.entry("radio", List.of(Tags.Title)),
            Map.entry("image", List.of(WdTags.WebAlt, WdTags.WebSrc, Tags.Desc, WdTags.WebTitle)),
            Map.entry("link", List.of(Tags.Title, WdTags.WebHref, WdTags.WebTextContent)),
            Map.entry("label", List.of(Tags.Title, WdTags.WebTextContent)),
            Map.entry("table_data", List.of(Tags.Title, WdTags.WebTextContent)),
            Map.entry("panel", List.of(Tags.Title, WdTags.WebId, WdTags.WebTextContent)),
            Map.entry("menu", List.of(Tags.Title, WdTags.WebTextContent)),
            Map.entry("menu_item", List.of(Tags.Title, WdTags.WebTextContent)),
            Map.entry("form", List.of(Tags.Title, WdTags.WebAriaLabel, WdTags.WebAriaLabelledBy)),
            Map.entry("element", List.of(Tags.Title))
            );

    private static final Map<String, List<Tag<?>>> statusTags = Map.ofEntries(
            Map.entry("visible", List.of(WdTags.WebIsDisplayed)),
            Map.entry("onscreen", List.of(WdTags.WebIsFullOnScreen)),
            Map.entry("offscreen", List.of(WdTags.WebIsOffScreen)),

            Map.entry("enabled", List.of(WdTags.WebIsEnabled)),
            Map.entry("disabled", List.of(WdTags.WebIsDisabled)),
            Map.entry("clickable", List.of(WdTags.WebIsClickable)),
            Map.entry("focused", List.of(WdTags.WebHasKeyboardFocus)),
            Map.entry("readonly", List.of(WdTags.WebIsKeyboardFocusable)),

            Map.entry("selected", List.of(WdTags.WebIsSelected)),
            Map.entry("checked", List.of(WdTags.WebIsChecked)),

            // TODO: Create WdTag with this labeled boolean logic
            // Map.entry("labeled", List.of(WdTags))

            Map.entry("empty", List.of(WdTags.WebLength, WdTags.WebValue)),
            Map.entry("filled", List.of(WdTags.WebValue))
            );

    private static final Map<String, List<Tag<?>>> attributeTags = Map.ofEntries(
            Map.entry("alttext", List.of(WdTags.WebAlt)),
            Map.entry("role", List.of(Tags.Role)),
            Map.entry("placeholder", List.of(WdTags.WebPlaceholder)),
            Map.entry("text", List.of(WdTags.WebTextContent, WdTags.WebValue, Tags.Title)),
            Map.entry("tooltip", List.of(Tags.Desc, WdTags.WebTextContent)),
            Map.entry("fontsize", List.of(WdTags.WebComputedFontSize)),
            Map.entry("color", List.of(WdTags.WebComputedColor)),
            Map.entry("backgroundColor", List.of(WdTags.WebComputedBackgroundColor)),
            Map.entry("length", List.of(WdTags.WebLength)),
            Map.entry("title", List.of(WdTags.WebAriaLabel, WdTags.WebAriaLabelledBy, Tags.Title)),
            Map.entry("children", List.of(Tags.WidgetChildren)) // This Tag is null but used for DSL mapping
            );

    public static RoleMatcher rm(Role role) {
        return new RoleMatcher(role, null);
    }

    public static RoleMatcher rm(Role role, Predicate<Widget> filter) {
        return new RoleMatcher(role, filter);
    }
}

class RoleMatcher {
    private final Role role;
    private final Predicate<Widget> extraFilter;

    public RoleMatcher(Role role, Predicate<Widget> extraFilter) {
        this.role = role;
        this.extraFilter = extraFilter != null ? extraFilter : w -> true;
    }

    public boolean matches(Widget widget) {
        return role.equals(widget.get(Tags.Role, Roles.Invalid)) && extraFilter.test(widget);
    }
}
