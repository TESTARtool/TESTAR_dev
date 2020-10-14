/*
***************************************************************************************************
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

/*
   @author Sebastian Bauersfeld
 */

package nl.ou.testar.temporal.util.foundation;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.fruit.Util;

import java.io.Serializable;

public class PairBean<L, R> implements Serializable {
	private static final long serialVersionUID = 6777608823096421544L;

	public PairBean() { //for JSON handler
	}
	@JsonGetter("type")
	public L getLeft() {
		return left;
	}
	@JsonGetter("value")
	public R getRight() {
		return right;
	}
	@JsonSetter("type")
	public void setLeft(L left) {
		this.left = left;
	}
	@JsonSetter("value")
	public void setRight(R right) {
		this.right = right;
	}

	private  L left;
	private  R right;

	public static <L, R> PairBean<L, R> from(L left, R right){
		return new PairBean<>(left, right);
	}

	public PairBean(L left, R right){
		this.left = left;
		this.right = right;
	}
	
	public boolean equals(Object o){
		if(this == o)
			return true;
		
		if(o instanceof PairBean){
			PairBean<?, ?> po = (PairBean<?, ?>) o;
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
