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
package org.fruit;

import java.io.Serializable;

public class Pair<L, R> implements Serializable {
	private static final long serialVersionUID = 6777608823096421544L;
	private final L left;
	private final R right;
	
	public static <L, R> Pair<L, R> from(L left, R right){ 
		return new Pair<L, R>(left, right); 
	}

	public Pair(L left, R right){
		this.left = left;
		this.right = right;
	}
	
	public boolean equals(Object o){
		if(this == o)
			return true;
		
		if(o instanceof Pair){
			Pair<?, ?> po = (Pair<?, ?>) o;
			return Util.equals(left, po.left()) && 
					Util.equals(right, po.right());
		}
		
		return false;
	}
	
	public int hashCode(){
		return Util.hashCode(left) + Util.hashCode(right);
	}
	
	public String toString(){
		return "(" + Util.toString(left) + ", " + Util.toString(right) + ")";
	}
	
	public L left(){ return left; }
	public R right(){ return right; }	
}