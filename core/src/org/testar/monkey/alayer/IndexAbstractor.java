/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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


/**
 *  @author Sebastian Bauersfeld
 */
package org.testar.monkey.alayer;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.testar.monkey.Util;
import org.testar.monkey.alayer.exceptions.AbstractionException;

public final class IndexAbstractor implements Abstractor {

	private static final class IndexNode{
		int idx;
		IndexNode parent;
		public IndexNode(int idx, IndexNode parent){
			this.idx = idx;
			this.parent = parent;
		}
	}
	
	final boolean caching;
	Map<Widget, IndexNode> wtoi;
	
	public IndexAbstractor(){ this(true); }
	
	public IndexAbstractor(boolean enableCaching){
		caching = enableCaching;
		wtoi = new WeakHashMap<Widget, IndexNode>();
	}
		
	public void clearCache(){ wtoi.clear(); }
	public void cache(Widget root){ cache(root, null); }
	
	private void cache(Widget widget, IndexNode parentNode){		
		for(int i = 0; i < widget.childCount(); i++){
			Widget c = widget.child(i);
			IndexNode childNode = new IndexNode(i, parentNode);
			wtoi.put(c, childNode);
			cache(c, childNode);
		}			
	}
	
	private int[] getIndexPath(Widget widget){

		// caching
		if(caching){
			IndexNode node = wtoi.get(widget);
			if(node == null){
				cache(widget.root());
				node = wtoi.get(widget);
			}
			return indexArrayFromNode(node);
		}
		
		// no caching
		return Util.indexPath(widget);
	}
		
	private int[] indexArrayFromNode(IndexNode node){
		List<Integer> list = Util.newArrayList();
		
		while(node != null){
			list.add(node.idx);
			node = node.parent;
		}
		int size = list.size();
		int[] ret = new int[size];
		for(int i = 0; i < size; i++)
			ret[i] = list.get(size - i - 1);
		return ret;
	}
	
	public Finder apply(Widget widget) throws AbstractionException {
		if(widget.parent() == null)
			return new IndexFinder(new int[0]);
		return new IndexFinder(getIndexPath(widget));
	}
}
