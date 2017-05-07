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

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.exceptions.AbstractionException;
import org.fruit.alayer.exceptions.WidgetNotFoundException;

public final class StdAbstractor implements Abstractor {
	
	private static final Tag<?>[] tags = {Tags.Role, Tags.Title, Tags.ToolTipText, Tags.Enabled, Tags.Blocked}; 
	private static final double[] weights = {2.0, 2.0, 2.0, 1.0 , 1.0}; 
	private static final double bestScore;
	
	static{
		double sum = 0.0;
		for(double w : weights)
			sum += w;
		bestScore = sum;
	}
	
	private final static class StdFinder implements Finder{
		private static final long serialVersionUID = 5934623634863977010L;
		transient private final Widget cachedWidget;
		private final Tag<?>[] tags;
		private final Object[] values;
		private final int[] indexPath;
		private final double[] weights;
		private final double minSimilarity;
		
		public StdFinder(Tag<?>[] tags, Object[] values, double[] weights, int[] indexPath, double minSimilarity, Widget cachedWidget){
			assert(tags.length == values.length && values.length == weights.length);
			this.cachedWidget = cachedWidget;
			this.tags = tags;
			this.values = values;
			this.indexPath = indexPath;
			this.weights = weights;
			this.minSimilarity = minSimilarity;
		}
		
		public Widget apply(Widget start) throws WidgetNotFoundException {
			Assert.notNull(start);
			if(cachedWidget != null && cachedWidget.root() == start)
				return cachedWidget;
			
			List<Widget> candidates = Util.newArrayList();
			double maxScore = 0.0;
			
			// go through all widgets, compare their tag values against the ones
			// we are looking for and calculate a similarity score
			for(Widget w : Util.makeIterable(start)){
				double score = widgetSimilarity(w);
				if(score > maxScore){
					candidates.clear();
					maxScore = score;
					candidates.add(w);
				}else if(score == maxScore){
					candidates.add(w);
				}
			}
			
			// if none of the examined widgets is similar enough, we give up
			if(maxScore / bestScore < minSimilarity)
				throw new WidgetNotFoundException();
			
			assert(candidates.size() != 0);  // that should not happen, since we have at least start
			
			if(candidates.size() == 1)
				return candidates.get(0);
			
			// since we now have several equally scored candidates, we will choose the one
			// which is closest to the position of the widget we are looking for
			Widget bestCandidate = null;
			maxScore = -1.0;
			for(Widget candidate : candidates){
				double score = indexPathSimilarity(indexPath, Util.indexPath(candidate));
				if(score > maxScore){
					bestCandidate = candidate;
					maxScore = score;
				}
			}
			
			return bestCandidate;
		}
		
		private double widgetSimilarity(Widget other){
			double score = 0.0;
			for(int i = 0; i < tags.length; i++)
				score += (Util.equals(other.get(tags[i], null), values[i]) ? 1.0 : 0.0) * weights[i];
			return score;
		}
		
		private double indexPathSimilarity(int[] path1, int[] path2){
			double score = 0.0;
			for(int i = 0; i < Math.min(path1.length, path2.length); i++)
				score += 1.0 / (Math.abs(path1[path1.length - 1 - i] - path2[path2.length - 1 - i]) + 1.0);
			return score;
		}

		@Override
		public Widget getCachedWidget() { // by urueda
			return cachedWidget;
		}
		
	}
	
	private final boolean cache;
	private final double minSimilarity;
	
	//public StdAbstractor(){	this(false, 0.5); }
	public StdAbstractor(){	this(true, 0.5); } // by urueda
	
	public StdAbstractor(boolean cacheWidgetInFinder, double minSimilarity){
		this.cache = cacheWidgetInFinder;
		this.minSimilarity = minSimilarity;
	}
	
	public Finder apply(Widget widget) throws AbstractionException {		
		Object[] values = new Object[tags.length];
		for(int i = 0; i < values.length; i++)
			values[i] = widget.get(tags[i], null);
		return new StdFinder(tags, values, weights, Util.indexPath(widget), minSimilarity, cache ? widget : null);
	}
}