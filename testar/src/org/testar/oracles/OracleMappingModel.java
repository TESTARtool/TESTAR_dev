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

import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

public class OracleMappingModel {

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

    private static final Map<String, List<Role>> element2Role = Map.ofEntries(
            Map.entry("button", List.of(WdRoles.WdBUTTON, WdRoles.WdINPUT)),
            Map.entry("input_text", List.of(WdRoles.WdINPUT, Roles.Text)),
            Map.entry("input_numeric", List.of(WdRoles.WdINPUT, Roles.Text)),
            Map.entry("static_text", List.of(WdRoles.WdLABEL, WdRoles.WdP, WdRoles.WdSPAN,
                    WdRoles.WdPRE, WdRoles.WdCODE, WdRoles.WdBLOCKQUOTE, WdRoles.WdSTRONG, WdRoles.WdEM,
                    WdRoles.WdH1, WdRoles.WdH2, WdRoles.WdH3, WdRoles.WdH4, WdRoles.WdH5, WdRoles.WdH6,
                    WdRoles.WdDIV, WdRoles.WdSECTION, WdRoles.WdARTICLE, WdRoles.WdFIELDSET, 
                    WdRoles.WdASIDE, WdRoles.WdMAIN ,WdRoles.WdHEADER, WdRoles.WdFOOTER, WdRoles.WdFIGCAPTION)),
            Map.entry("alert", List.of(Roles.Widget)), // This is not an element itself
            Map.entry("dropdown", List.of(WdRoles.WdSELECT, WdRoles.WdOPTION, WdRoles.WdOPTGROUP)),
            Map.entry("checkbox", List.of(WdRoles.WdINPUT)),
            Map.entry("radiogroup", List.of(WdRoles.WdINPUT)),
            Map.entry("radio", List.of(WdRoles.WdINPUT)),
            Map.entry("image", List.of(WdRoles.WdIMG)),
            Map.entry("link", List.of(WdRoles.WdA)),
            Map.entry("label", List.of(WdRoles.WdLABEL)),
            Map.entry("panel", List.of(WdRoles.WdDIV, WdRoles.WdSECTION, WdRoles.WdARTICLE, WdRoles.WdFIELDSET, 
                    WdRoles.WdASIDE, WdRoles.WdMAIN ,WdRoles.WdHEADER, WdRoles.WdFOOTER)),
            Map.entry("table_data", List.of(WdRoles.WdTH, WdRoles.WdTD)),
            Map.entry("menu", List.of(WdRoles.WdMENU, WdRoles.WdUL, WdRoles.WdNAV)),
            Map.entry("menu_item", List.of(WdRoles.WdLI)),
            Map.entry("form", List.of(WdRoles.WdFORM)),
            Map.entry("element", List.of(Roles.Widget))
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

    private OracleMappingModel() { }

    public static Set<String> getValidStatusPerElement(String elementType) {
        return validStatusPerElement.getOrDefault(elementType, Set.of());
    }

    public static List<Role> getElementRoles(String elementType) {
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

}
