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
        scriptJoiner.add("// Feature to reverse state and action div nodes");
        scriptJoiner.add("function reverse(){");
        scriptJoiner.add("let direction = document.getElementById('main').style.flexDirection;");
        scriptJoiner.add("if(direction === 'column') document.getElementById('main').style.flexDirection = " + "'column-reverse';");
        scriptJoiner.add("else document.getElementById('main').style.flexDirection = 'column';}");
        scriptJoiner.add("");

        // Collapsible function
        scriptJoiner.add("// Feature to enable derived action nodes to be shown or hidden");
        scriptJoiner.add("function toggleCollapsible(){");
        scriptJoiner.add("document.querySelectorAll('.collapsible').forEach(btn => {");
        scriptJoiner.add("btn.addEventListener('click', function() {"); // Click event listener
        scriptJoiner.add("this.classList.toggle('active');"); // Toogle the triangle dropdown icon
        scriptJoiner.add("let content = this.nextElementSibling;");
        scriptJoiner.add("if (content.style.maxHeight) {");
        scriptJoiner.add("content.style.maxHeight = null;");
        scriptJoiner.add("} else {");
        scriptJoiner.add("content.style.maxHeight = content.scrollHeight + 'px';");
        scriptJoiner.add("}");  
        scriptJoiner.add("}); }); }");
        scriptJoiner.add("document.addEventListener('DOMContentLoaded', toggleCollapsible);");
        scriptJoiner.add("");

        // Visualize rectangle script
        scriptJoiner.add("// Feature to highlight the visualizer of the verdict widget");
        scriptJoiner.add("document.addEventListener('DOMContentLoaded', function () {");
        scriptJoiner.add("const visualizerText = document.getElementById('visualizer-rect')?.innerText;");
        scriptJoiner.add("if (!visualizerText) return;");
        scriptJoiner.add("const rectPattern = /Rect\\s*\\[x:(\\d+\\.\\d+)\\s*y:(\\d+\\.\\d+)\\s*w:(\\d+\\.\\d+)\\s*h:(\\d+\\.\\d+)\\]/g;");
        scriptJoiner.add("const matches = [...visualizerText.matchAll(rectPattern)];");
        scriptJoiner.add("if (matches.length === 0) return;");
        scriptJoiner.add("const visualizerElement = document.getElementById('visualizer-rect');");
        scriptJoiner.add("const block = visualizerElement?.closest('.block');");
        scriptJoiner.add("if (!block) return;");
        scriptJoiner.add("const prevBlock = block.previousElementSibling;");
        scriptJoiner.add("if (!prevBlock) return;");
        scriptJoiner.add("const imgContainer = prevBlock.querySelector('.background');");
        scriptJoiner.add("const img = imgContainer?.querySelector('img');");
        scriptJoiner.add("if (!img) return;");
        scriptJoiner.add("function updateRectangles() {");
        scriptJoiner.add("const imgRect = img.getBoundingClientRect();");
        scriptJoiner.add("const containerRect = imgContainer.getBoundingClientRect();");
        scriptJoiner.add("const offsetX = imgRect.left - containerRect.left;");
        scriptJoiner.add("const offsetY = imgRect.top - containerRect.top;");
        scriptJoiner.add("const scaleX = imgRect.width / img.naturalWidth;");
        scriptJoiner.add("const scaleY = imgRect.height / img.naturalHeight;");
        scriptJoiner.add("// Clear all existing rectangles");
        scriptJoiner.add("const existingRectangles = imgContainer.querySelectorAll('.rectangle');");
        scriptJoiner.add("existingRectangles.forEach(rect => rect.remove());");
        scriptJoiner.add("// Loop through all matches to create rectangle elements");
        scriptJoiner.add("matches.forEach(match => {");
        scriptJoiner.add("const [x, y, width, height] = match.slice(1, 5).map(parseFloat);");
        scriptJoiner.add("// Prevent to highlight elements outside screen boundaries");
        scriptJoiner.add("if (x > img.naturalWidth || y > img.naturalHeight || (x + width) < 0 || (y + height) < 0) return;");
        scriptJoiner.add("if (width === 0 && height === 0) return;");
        scriptJoiner.add("const rectangleDiv = document.createElement('div');");
        scriptJoiner.add("rectangleDiv.classList.add('rectangle');");
        scriptJoiner.add("Object.assign(rectangleDiv.style, {");
        scriptJoiner.add("position: 'absolute',");
        scriptJoiner.add("left: (x * scaleX + offsetX) - 3 + 'px',");
        scriptJoiner.add("top: (y * scaleY + offsetY) - 3 + 'px',");
        scriptJoiner.add("width: (width * scaleX) - 6 + 'px',");
        scriptJoiner.add("height: (height * scaleY) - 6 + 'px',");
        scriptJoiner.add("border: '2px solid red'");
        scriptJoiner.add("});");
        scriptJoiner.add("imgContainer.appendChild(rectangleDiv);");
        scriptJoiner.add("});");
        scriptJoiner.add("}");
        scriptJoiner.add("img.onload = updateRectangles;");
        scriptJoiner.add("window.addEventListener('resize', updateRectangles);");
        scriptJoiner.add("});");

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
        styleJoiner.add("position: relative;"); // Position for the visualizer rectangle
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

        styleJoiner.add(".collapsible:after {");
        styleJoiner.add("content: '\\25B6';");
        styleJoiner.add("color: black;");
        styleJoiner.add("font-weight: bold;");
        styleJoiner.add("float: left;");
        styleJoiner.add("margin-left: 2px;");
        styleJoiner.add("margin-right: 10px;");
        styleJoiner.add("}");

        styleJoiner.add(".collapsible.active:after {");
        styleJoiner.add("content: '\\25BC';");
        styleJoiner.add("}");

        styleJoiner.add(".collapsibleContent {");
        styleJoiner.add("padding: 0 15px;");
        styleJoiner.add("max-height: 0;");
        styleJoiner.add("overflow: hidden;");
        styleJoiner.add("border-radius: 4px;");
        styleJoiner.add("transition: max-height 0.3s ease-out;");
        styleJoiner.add("}");

        return styleJoiner.toString();
    }
}
