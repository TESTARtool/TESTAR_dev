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
package org.fruit.alayer.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;

/**
 * An action that is composed of several other actions.
 */
public final class CompoundAction extends TaggableBase implements Action {

	private static final long serialVersionUID = -5836624942752268573L;
	private final List<Action> actions;
	private final List<Double> relativeDurations;
	
	public static final class Builder{
		private List<Double> relativeDurations = Util.newArrayList();
		private List<Action> actions = Util.newArrayList();
		double durationSum = 0.0;
		
		public Builder add(Action a, double relativeDuration){
			Assert.notNull(a);
			Assert.isTrue(relativeDuration >= 0);
			relativeDurations.add(relativeDuration);
			actions.add(a);
			durationSum += relativeDuration;
			return this;
		}
				
		public CompoundAction build(){
			Assert.isTrue(durationSum > 0.0, "Sum of durations needs to be larger than 0!");

			// normalize
			for(int i = 0; i < relativeDurations.size(); i++)
				relativeDurations.set(i, relativeDurations.get(i) / durationSum);
			return new CompoundAction(this);
		}
	}
	
	private CompoundAction(Builder b){
		this.actions = b.actions;
		this.relativeDurations = b.relativeDurations;
	}
	
	public CompoundAction(Action...actions){
		Assert.notNull((Object)actions);
		this.actions = Arrays.asList(actions);
		this.relativeDurations = Util.newArrayList();
		
		if(actions.length > 0){
			for(int i = 0; i < actions.length; i++)
				relativeDurations.add(1.0 / actions.length);
		}
	}
			
	public void run(SUT system, State state, double duration) {		
		for(int i = 0; i < actions.size(); i++)
			actions.get(i).run(system, state, relativeDurations.get(i) * duration);
	}
		
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Compound Action =");
		for(Action a : actions)
			sb.append(Util.lineSep()).append(a.toString());
		return sb.toString();
	}
	
	// by urueda
	@Override
	public String toString(Role... discardParameters) {
		StringBuilder sb = new StringBuilder();
		sb.append("Compound Action =");
		for(Action a : actions)
			sb.append(Util.lineSep()).append(a.toString(discardParameters));
		return sb.toString();
	}	

	// by urueda
	@Override
	public String toShortString() {
		StringBuilder sb = new StringBuilder();
		Role r = get(Tags.Role, null);
		if (r != null)
			sb.append(r.toString());
		else
			sb.append("UNDEF");
		HashSet<String> parameters = new HashSet<String>();
		for (Action a : actions)
			parameters.add(a.toParametersString());
		for (String p : parameters)
			sb.append(p);
		return sb.toString();
	}

	// by urueda
	@Override
	public String toParametersString() {
		String params = "";
		for (Action a : actions)
			params += a.toParametersString();
		return params;
	}
}