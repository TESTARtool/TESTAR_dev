/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
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

import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.TaggableBase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IOSElement extends TaggableBase implements Serializable {
	private static final long serialVersionUID = -2910535746470588590L;
	
	List<IOSElement> children = new ArrayList<>();
	IOSElement parent;
	IOSRootElement root;
	IOSWidget backRef;
	
	boolean enabled;
	boolean ignore;
	boolean blocked;
	boolean isTopLevelContainer;

	double zindex;
	
	Rect rect;
	Rect bounds;
	
	int nodeIndex;
	int x;
	int y;
	int width;
	int height;

	String text;
	String className;
	String accessibilityId;
	String xPath;
	String label;

	boolean visible;
	
	public IOSElement(){ this(null); }

	public IOSElement(IOSElement parent){
		this.parent = parent;
		if(parent != null) {
			root = parent.root;
		}
		enabled = true;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException{
		oos.defaultWriteObject();
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
	}
}
