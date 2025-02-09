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

package org.testar.reporting;

import java.util.StringJoiner;

public class HtmlHelper {

    public static String getHtmlScript()
    {
        StringJoiner scriptJoiner = new StringJoiner("\n");

        // Reverse function
        scriptJoiner.add("function reverse(){");
        scriptJoiner.add("let direction = document.getElementById('main').style.flexDirection;");
        scriptJoiner.add("if(direction === 'column') document.getElementById('main').style.flexDirection = " + "'column-reverse';");
        scriptJoiner.add("else document.getElementById('main').style.flexDirection = 'column';}");

        // Collapsible function
        scriptJoiner.add("function toggleCollapsible(){");
        scriptJoiner.add("document.querySelectorAll('.collapsible').forEach(btn => {");
        scriptJoiner.add("btn.addEventListener('click', function() {");
        scriptJoiner.add("let content = this.nextElementSibling;");
        scriptJoiner.add("content.style.display = (content.style.display === 'block') ? 'none' : 'block';");
        scriptJoiner.add("}); }); }");
        scriptJoiner.add("document.addEventListener('DOMContentLoaded', toggleCollapsible);");

        return scriptJoiner.toString();
    }

    public static String getHtmlStyle()
    {
        StringJoiner styleJoiner = new StringJoiner("\n");

        // Div style
        styleJoiner.add("div {");
        styleJoiner.add("border: 1px solid black;");
        styleJoiner.add("margin: 2px;");
        styleJoiner.add("padding: 2px;");
        styleJoiner.add("}");

        // Background style
        styleJoiner.add(".background {");
        styleJoiner.add("padding: 15px;");
        styleJoiner.add("width: 100%;");
        styleJoiner.add("border: none;");
        styleJoiner.add("text-align: left;");
        styleJoiner.add("outline: none;");
        styleJoiner.add("border-radius: 4px;");
        styleJoiner.add("}");

        // Background img style
        styleJoiner.add(".background img {");
        styleJoiner.add("max-width: 98%;");
        styleJoiner.add("max-height: 98%;");
        styleJoiner.add("object-fit: contain;");
        styleJoiner.add("}");

        // Collapsible style
        styleJoiner.add(".collapsible {");
        styleJoiner.add("color: black;");
        styleJoiner.add("font-weight: bold;");
        styleJoiner.add("cursor: pointer;");
        styleJoiner.add("padding: 15px;");
        styleJoiner.add("width: 100%;");
        styleJoiner.add("border: none;");
        styleJoiner.add("text-align: left;");
        styleJoiner.add("outline: none;");
        styleJoiner.add("font-size: 18px;");
        styleJoiner.add("border-radius: 4px;");
        styleJoiner.add("}");

        styleJoiner.add(".active, .collapsible:hover {}");

        styleJoiner.add(".collapsibleContent {");
        styleJoiner.add("padding: 0 15px;");
        styleJoiner.add("display: none;");
        styleJoiner.add("overflow: hidden;");
        styleJoiner.add("border-radius: 4px;");
        styleJoiner.add("}");

        return styleJoiner.toString();
    }
}
