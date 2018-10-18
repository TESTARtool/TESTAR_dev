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

import es.upv.staq.testar.oracles.UIOperations;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.prolog.PrologUtil;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.LogSerialiser.LogLevel;

/**
 * A widget TITLE property UPDATE predicate.
 * 
 * status (alpha dev.):
 *   - target-app: windows calculator
 *   - procedure: hit a number button =&gt; set calculator display as verification point
 *   - predicate: display must/should change (previous text + last hit number button)
 *   - sandbox: a calculator that randomly shows '5' text or nothing in the display when hitting the '5' number button
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class TitleUpdatePredicate extends PredicateBase {
				
	private TitleUpdatePredicate(String refuteQuery){
		this.refuteQuery = refuteQuery;
		this.predicateContext = new Object[]{ UIOperations.W_PROPERTIES.TITLE, UIOperations.OP_TYPES.UPDATE };
	}
	
	public static IPredicate infer(JIPrologWrapper jipWrapper){
		// can we infer a predicate?
		List<List<String>> solutions = jipWrapper.setQuery(
			"verify(Sv,Wv),title(Wv,Tv),role(Wv,Rv),path(Wv,Pv)," + // info about the verification point
			"action(A,Sa,Wa,T,O),title(Wa,Ta),\\=(Sv,Sa)," + // info about an action that might affect the verification point
			"widget(Wva,Sa),title(Wva,Tva),role(Wva,Rv),path(Wva,Pv)," + // info about the verification point in the action state
			"\\=(Tv,Tva)." // the action produced a change to the verification point? (hence, a state change)
		);
		if (solutions == null || solutions.isEmpty())
			return null;
		else{
			String Wa = PrologUtil.getSolutions("Wa", solutions).get(0);
			// predicate exists => build a refuting predicate to use as verdict
			String refuteQuery = "action(A,Sa,Wa,T,O),Wa='" + Wa + "',source(Ss,A),target(St,A),==(Ss,St)."; // the predicate action did not change state?
			LogSerialiser.log("Title-Update oracle inferred:\n" + refuteQuery + "\n",LogLevel.Info);
			return new TitleUpdatePredicate(refuteQuery);
		}
	}
	
}
