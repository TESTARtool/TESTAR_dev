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

        // Video player
        scriptJoiner.add("// video");
        scriptJoiner.add("window.onload = async function () {");
        scriptJoiner.add("	const stateBlocks = document.querySelectorAll('.stateBlock');");
        scriptJoiner.add("	const actionBlocks = document.querySelectorAll('.selectedBlock');");
        scriptJoiner.add("");
        scriptJoiner.add("	const stateFrames = [];");
        scriptJoiner.add("	const actionFrames = [];");
        scriptJoiner.add("	const stateTexts = [];");
        scriptJoiner.add("	const actionTexts = [];");
        scriptJoiner.add("");
        scriptJoiner.add("	function createPlaceholderImage(width, height, text = 'Image not found') {");
        scriptJoiner.add("	  const canvas = document.createElement('canvas');");
        scriptJoiner.add("	  canvas.width = width;");
        scriptJoiner.add("	  canvas.height = height;");
        scriptJoiner.add("	  const ctx = canvas.getContext('2d');");
        scriptJoiner.add("	  ctx.fillStyle = '#ddd';");
        scriptJoiner.add("	  ctx.fillRect(0, 0, width, height);");
        scriptJoiner.add("	  ctx.fillStyle = '#333';");
        scriptJoiner.add("	  ctx.font = '20px sans-serif';");
        scriptJoiner.add("	  ctx.textAlign = 'center';");
        scriptJoiner.add("	  ctx.textBaseline = 'middle';");
        scriptJoiner.add("	  ctx.fillText(text, width / 2, height / 2);");
        scriptJoiner.add("	  const img = new Image();");
        scriptJoiner.add("	  img.src = canvas.toDataURL('image/png');");
        scriptJoiner.add("	  return img;");
        scriptJoiner.add("	}");
        scriptJoiner.add("");
        scriptJoiner.add("	async function loadImage(src) {");
        scriptJoiner.add("	  return new Promise(resolve => {");
        scriptJoiner.add("		const img = new Image();");
        scriptJoiner.add("		img.src = src;");
        scriptJoiner.add("		img.onload = () => resolve(img);");
        scriptJoiner.add("		img.onerror = () => resolve(createPlaceholderImage(800, 600));");
        scriptJoiner.add("	  });");
        scriptJoiner.add("	}");
        scriptJoiner.add("");
        scriptJoiner.add("	for (const block of stateBlocks) {");
        scriptJoiner.add("	  const img = block.querySelector('img');");
        scriptJoiner.add("	  const h2 = block.querySelector('h2')?.textContent || '';");
        scriptJoiner.add("	  stateFrames.push(await loadImage(img.src));");
        scriptJoiner.add("	  stateTexts.push(h2);");
        scriptJoiner.add("	}");
        scriptJoiner.add("");
        scriptJoiner.add("	for (const block of actionBlocks) {");
        scriptJoiner.add("	  const img = block.querySelector('img');");
        scriptJoiner.add("	  const h4 = block.querySelector('h4')?.textContent || '';");
        scriptJoiner.add("	  actionFrames.push(await loadImage(img.src));");
        scriptJoiner.add("	  actionTexts.push(h4);");
        scriptJoiner.add("	}");
        scriptJoiner.add("");
        scriptJoiner.add("	const totalFrames = stateFrames.length;");
        scriptJoiner.add("	document.getElementById('frameSlider').max = totalFrames - 1;");
        scriptJoiner.add("");
        scriptJoiner.add("	const prevActionCanvas = document.getElementById('prevActionCanvas');");
        scriptJoiner.add("	const stateCanvas = document.getElementById('stateCanvas');");
        scriptJoiner.add("");
        scriptJoiner.add("	const prevActionCtx = prevActionCanvas.getContext('2d');");
        scriptJoiner.add("	const stateCtx = stateCanvas.getContext('2d');");
        scriptJoiner.add("");
        scriptJoiner.add("	const prevActionTextDiv = document.getElementById('prevActionText');");
        scriptJoiner.add("	const stateTextDiv = document.getElementById('stateText');");
        scriptJoiner.add("");
        scriptJoiner.add("	let frame = 0;");
        scriptJoiner.add("	let playing = true;");
        scriptJoiner.add("	let frameDelay = parseInt(document.getElementById('speedControl').value);");
        scriptJoiner.add("	let intervalId;");
        scriptJoiner.add("");
        scriptJoiner.add("	function drawFrame() {");
        scriptJoiner.add("		prevActionCtx.clearRect(0, 0, prevActionCanvas.width, prevActionCanvas.height);");
        scriptJoiner.add("		stateCtx.clearRect(0, 0, stateCanvas.width, stateCanvas.height);");
        scriptJoiner.add("");
        scriptJoiner.add("		const stateImg = stateFrames[frame];");
        scriptJoiner.add("");
        scriptJoiner.add("		if (frame > 0)");
        scriptJoiner.add("		{");
        scriptJoiner.add("			prevActionImg = actionFrames[frame-1];");
        scriptJoiner.add("			prevActionTextDiv.style = '';");
        scriptJoiner.add("			prevActionCanvas.style = '';");
        scriptJoiner.add("			");
        scriptJoiner.add("			prevActionCanvas.width = prevActionImg.naturalWidth;");
        scriptJoiner.add("			prevActionCanvas.height = prevActionImg.naturalHeight;");
        scriptJoiner.add("			prevActionCtx.drawImage(prevActionImg, 0, 0);");
        scriptJoiner.add("			prevActionTextDiv.textContent = actionTexts[frame-1];");
        scriptJoiner.add("		}");
        scriptJoiner.add("		else");
        scriptJoiner.add("		{");
        scriptJoiner.add("			prevActionTextDiv.style = 'display: none;';");
        scriptJoiner.add("			prevActionCanvas.style = 'display: none;';");
        scriptJoiner.add("		}");
        scriptJoiner.add("		");
        scriptJoiner.add("		stateCanvas.width = stateImg.naturalWidth;");
        scriptJoiner.add("		stateCanvas.height = stateImg.naturalHeight;");
        scriptJoiner.add("		stateCtx.drawImage(stateImg, 0, 0);");
        scriptJoiner.add("		stateTextDiv.textContent = stateTexts[frame];");
        scriptJoiner.add("");
        scriptJoiner.add("	document.getElementById('frameSlider').value = frame;");
        scriptJoiner.add("	}");
        scriptJoiner.add("");
        scriptJoiner.add("	function nextFrame() {");
        scriptJoiner.add("	  if (frame < totalFrames - 1)");
        scriptJoiner.add("		{");
        scriptJoiner.add("			frame = frame + 1;");
        scriptJoiner.add("		}");
        scriptJoiner.add("		drawFrame();");
        scriptJoiner.add("	}");
        scriptJoiner.add("");
        scriptJoiner.add("	function prevFrame() {");
        scriptJoiner.add("	  if (frame > 0)");
        scriptJoiner.add("		{");
        scriptJoiner.add("			frame = frame - 1;");
        scriptJoiner.add("		}");
        scriptJoiner.add("		drawFrame();");
        scriptJoiner.add("	}");
        scriptJoiner.add("");
        scriptJoiner.add("	function togglePlayPause() {");
        scriptJoiner.add("	  playing = !playing;");
        scriptJoiner.add("	  document.getElementById('playPauseBtn').textContent = playing ? 'Pause' : 'Play';");
        scriptJoiner.add("	  if (playing) {");
        scriptJoiner.add("		intervalId = setInterval(nextFrame, frameDelay);");
        scriptJoiner.add("	  } else {");
        scriptJoiner.add("		clearInterval(intervalId);");
        scriptJoiner.add("	  }");
        scriptJoiner.add("	}");
        scriptJoiner.add("");
        scriptJoiner.add("	document.getElementById('playPauseBtn').addEventListener('click', togglePlayPause);");
        scriptJoiner.add("	document.getElementById('nextFrameBtn').addEventListener('click', () => {");
        scriptJoiner.add("	  if (playing) togglePlayPause();");
        scriptJoiner.add("	  nextFrame();");
        scriptJoiner.add("	});");
        scriptJoiner.add("	document.getElementById('prevFrameBtn').addEventListener('click', () => {");
        scriptJoiner.add("	  if (playing) togglePlayPause();");
        scriptJoiner.add("	  prevFrame();");
        scriptJoiner.add("	});");
        scriptJoiner.add("");
        scriptJoiner.add("	document.getElementById('frameSlider').addEventListener('input', (e) => {");
        scriptJoiner.add("	  if (playing) togglePlayPause();");
        scriptJoiner.add("		frame = parseInt(e.target.value);");
        scriptJoiner.add("	  drawFrame();");
        scriptJoiner.add("	});");
        scriptJoiner.add("");
        scriptJoiner.add("	document.getElementById('speedControl').addEventListener('change', (e) => {");
        scriptJoiner.add("	  frameDelay = parseInt(e.target.value);");
        scriptJoiner.add("	  if (playing) {");
        scriptJoiner.add("		clearInterval(intervalId);");
        scriptJoiner.add("		intervalId = setInterval(nextFrame, frameDelay);");
        scriptJoiner.add("	  }");
        scriptJoiner.add("	});");
        scriptJoiner.add("");
        scriptJoiner.add("	drawFrame();");
        scriptJoiner.add("	intervalId = setInterval(nextFrame, frameDelay);");
        scriptJoiner.add("};");

        scriptJoiner.add("// Clone verdict block to top, so user does not have to scroll to end of page");
        scriptJoiner.add("window.addEventListener('DOMContentLoaded', () => {");
        scriptJoiner.add("  const verdict = document.querySelector('.verdictBlock');");
        scriptJoiner.add("  const topVerdict = document.querySelector('.topVerdictBlock');");
        scriptJoiner.add("  if (verdict) {");
        scriptJoiner.add("    const clone = verdict.cloneNode(true);");
        scriptJoiner.add("	topVerdict.appendChild(clone);");
        scriptJoiner.add("  }");
        scriptJoiner.add("});");        

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
        scriptJoiner.add("let stateBlock = visualizerElement.closest('.stateBlock');");
        scriptJoiner.add("if (!stateBlock) {");
        scriptJoiner.add("let sibling = visualizerElement.parentElement?.previousElementSibling;");
        scriptJoiner.add("while (sibling && !sibling.classList.contains('stateBlock')) {");
        scriptJoiner.add("sibling = sibling.previousElementSibling;");
        scriptJoiner.add("}");
        scriptJoiner.add("stateBlock = sibling;");
        scriptJoiner.add("}");
        scriptJoiner.add("const imgContainer = stateBlock.querySelector('.background');");
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

        // Video player
        styleJoiner.add("canvas {");
        styleJoiner.add("  border: 1px solid #ccc;");
        styleJoiner.add("  margin-bottom: 5px;");
        styleJoiner.add("}");
        styleJoiner.add(".video-block {");
        styleJoiner.add("  display: inline-block;");
        styleJoiner.add("  margin: 20px;");
        styleJoiner.add("  vertical-align: top;");
        styleJoiner.add("}");
        styleJoiner.add(".controls {");
        styleJoiner.add("  margin-top: 10px;");
        styleJoiner.add("}");
        styleJoiner.add(".controls > button {");
        styleJoiner.add("  width: 100px;");
        styleJoiner.add("}");

        return styleJoiner.toString();
    }
}
