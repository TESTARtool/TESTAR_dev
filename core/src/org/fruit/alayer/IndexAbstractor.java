/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.fruit.Util;
import org.fruit.alayer.exceptions.AbstractionException;

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