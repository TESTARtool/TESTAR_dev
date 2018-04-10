/***************************************************************************************************
*
* Copyright (c) 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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


package es.upv.staq.testar.oracles.predicates;

import java.util.List;

import org.fruit.Assert;
import org.fruit.alayer.Verdict;

import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.prolog.PrologUtil;

/**
 * Predicates base implementation.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public abstract class PredicateBase implements IPredicate {
			
	protected String refuteQuery = null;
	protected Object[] predicateContext = null;

	@Override
	public Verdict getVerdict(JIPrologWrapper jipWrapper){
		Assert.notNull(this.refuteQuery); Assert.notNull(this.predicateContext);
		Grapher.syncMovements(); // synchronize graph movements consumption			
		jipWrapper.updatePrologFactsNrules();
		// check predicate
		List<List<String>> solutions = jipWrapper.setQuery(this.refuteQuery);
		if (solutions != null && !solutions.isEmpty()){
			String refuteMsg = ""; for (Object o : this.predicateContext) refuteMsg += o.toString() + " ";
			refuteMsg += "predicate refuted!";
			System.out.println("[" + getClass().getSimpleName() + "]  " +  refuteMsg); PrologUtil.printSolutions(solutions);
			return new Verdict(Verdict.SEVERITY_MAX, refuteMsg);
		}
		return Verdict.OK;  // cannot refute predicate
	}
	
}
