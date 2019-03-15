/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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


package nl.ou.testar.a11y.wcag2;

import static org.junit.Assert.*;

import org.fruit.alayer.Verdict;
import org.junit.Test;

import nl.ou.testar.a11y.reporting.EvaluationResult.Type;
import nl.ou.testar.a11y.reporting.EvaluationResults;
import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;

public class EvaluationResultsTest {
	
	private final double DELTA = 0.01;

	@Test
	public void testGetOverallVerdict() {
		AbstractPrinciple p = new PerceivablePrinciple();
		AbstractGuideline g = new TextAlternativesGuideline(p);
		SuccessCriterion s1 = new SuccessCriterion(1, "Foo", g, Level.A, "foo"),
				s2 = new SuccessCriterion(2, "Bar", g, Level.AA, "bar"),
				s3 = new SuccessCriterion(3, "Baz", g, Level.AAA, "baz");
		WCAG2EvaluationResult r1 = new WCAG2EvaluationResult(s1, Type.OK, "Pass"),
				r2 = new WCAG2EvaluationResult(s2, Type.WARNING, "Warning"),
				r3 = new WCAG2EvaluationResult(s1, Type.WARNING, "Warning"),
				r4 = new WCAG2EvaluationResult(s3, Type.ERROR, "Error"),
				r5 = new WCAG2EvaluationResult(s2, Type.ERROR, "Error"),
				r6 = new WCAG2EvaluationResult(s1, Type.ERROR, "Error");
		double os1, os2, os3;
		EvaluationResults results = new EvaluationResults();
		
		// no results
		assertEquals(Verdict.OK.severity(), results.getOverallVerdict().severity(), DELTA);
		// one result that is OK
		results.add(r1);
		os1 = results.getOverallVerdict().severity();
		assertEquals(Verdict.OK.severity(), os1, DELTA);
		// a warning, should be less severe than the lowest error but more severe than no problem
		results.add(r2);
		os2 = results.getOverallVerdict().severity();
		assertTrue(s3.getVerdictSeverity() > os2 + DELTA);
		assertTrue(os1 < os2);
		// a warning from a higher-level success criterion, should not affect the severity
		results.add(r3);
		os3 = results.getOverallVerdict().severity();
		assertEquals(os2, os3, DELTA);
		// a level 3 problem
		results.add(r4);
		assertEquals(s3.getVerdictSeverity(), results.getOverallVerdict().severity(), DELTA);
		// a level 2 problem
		results.add(r5);
		assertEquals(s2.getVerdictSeverity(), results.getOverallVerdict().severity(), DELTA);
		// a level 1 error
		results.add(r6);
		assertEquals(s1.getVerdictSeverity(), results.getOverallVerdict().severity(), DELTA);
	}

}
