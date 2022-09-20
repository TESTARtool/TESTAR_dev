/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.ios;

public class IOSRootElement extends IOSElement {
	private static final long serialVersionUID = 7333122749170300870L;
	
	public long pid;
	public long windowsHandle;
	public long timeStamp;
	public boolean isRunning;
	public boolean isForeground;
	IOSElementMap elementMap;

	public IOSRootElement() {
		super(null);
		root = this;
		parent = this;
		isForeground = false;
		blocked = false;
		elementMap = IOSElementMap.newBuilder().build();
	}

	public boolean visibleAt(IOSElement el, double x, double y){		
		if(el.rect == null || !el.rect.contains(x, y) || !this.rect.contains(x, y)) {
			return false;
		}

		IOSElement topLevelContainer = elementMap.at(x, y);
		return (topLevelContainer == null || topLevelContainer.zindex <= el.zindex) && !obscuredByChildren(el, x, y);
	}

	public boolean visibleAt(IOSElement el, double x, double y, boolean obscuredByChildFeature){		
		if(el.rect == null || !el.rect.contains(x, y) || !this.rect.contains(x, y)) {
			return false;
		}

		IOSElement topLevelContainer = elementMap.at(x, y);
		return (topLevelContainer == null || topLevelContainer.zindex <= el.zindex ||
				!obscuredByChildFeature || !obscuredByChildren(el, x, y));
	}

	boolean obscuredByChildren(IOSElement el, double x, double y){		
		for(int i = 0; i < el.children.size(); i++){
			IOSElement child = el.children.get(i);
			if(child.rect != null && child.rect.contains(x, y) && child.zindex >= el.zindex) {
				return true;
			}
		}
		return false;
	}

	/*public boolean visibleAt(double x, double y) {
		if(root == null || root.rect == null) {
			return false;
		}

		double rootX = root.rect.x(); // 0
		double rootY = root.rect.y(); // 0
		return rect.contains(x - rootX, y - rootY);

		//return true;
	}

	public boolean visibleAt(double x, double y, boolean obscuredByChildFeature) {
		return visibleAt(x, y);
	}*/
}
