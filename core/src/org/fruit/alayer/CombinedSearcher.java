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
package org.fruit.alayer;

import org.fruit.Assert;
import org.fruit.UnFunc;

public final class CombinedSearcher implements Searcher {

	private static final class MyVisitor implements UnFunc<Widget, SearchFlag>{
		Searcher nextSearcher;
		UnFunc<Widget, SearchFlag> nextVisitor;
		public SearchFlag apply(Widget widget) {
			return nextSearcher.apply(widget, nextVisitor);
		}
	}
	
	private static final long serialVersionUID = -7319307314114534139L;
	final Searcher searcher1, searcher2;
	transient MyVisitor myVisitor;
	
	public CombinedSearcher(Searcher searcher1, Searcher searcher2){
		Assert.notNull(searcher1, searcher2);
		this.searcher1 = searcher1;
		this.searcher2 = searcher2;
	}
	
	public SearchFlag apply(Widget widget, UnFunc<Widget, SearchFlag> visitor) {
		if(myVisitor == null){
			myVisitor = new MyVisitor();
			myVisitor.nextSearcher = searcher2;
		}
		myVisitor.nextVisitor = visitor;
		return searcher1.apply(widget, myVisitor);
	}
}
