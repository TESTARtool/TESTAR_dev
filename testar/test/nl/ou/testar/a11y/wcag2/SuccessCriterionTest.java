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

import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;

public class SuccessCriterionTest {
	
	private final double PRIO_A = 1.0; // high
	private final double PRIO_AA = 0.666; // medium
	private final double PRIO_AAA = 0.333; // low
	private final double DELTA = 0.1;
	
	@Test
	public void testGetNr() {
		AbstractPrinciple p = new PerceivablePrinciple();
		AbstractGuideline g = new TimeBasedMediaGuideline(p);
		SuccessCriterion s = new SuccessCriterion(3, "Foo", g, Level.A, "foo");
		assertEquals("1.2.3", s.getNr());
	}
	
	@Test
	public void testToString() {
		AbstractPrinciple p = new PerceivablePrinciple();
		AbstractGuideline g = new TimeBasedMediaGuideline(p);
		SuccessCriterion s = new SuccessCriterion(3, "Foo", g, Level.A, "foo");
		assertEquals("1.2.3 Foo", s.toString());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testGetVerdictPriority() {
		if (PRIO_AAA < Verdict.SEVERITY_MIN || PRIO_A > Verdict.SEVERITY_MAX) {
			fail("The Verdict priority range has changed, update this test");
		}
		AbstractPrinciple p = new PerceivablePrinciple();
		AbstractGuideline g = new TimeBasedMediaGuideline(p);
		SuccessCriterion s1 = new SuccessCriterion(3, "Foo", g, Level.A, "foo"),
				s2 = new SuccessCriterion(3, "Bar", g, Level.AA, "bar"),
				s3 = new SuccessCriterion(3, "Baz", g, Level.AAA, "baz");
		assertEquals("Level A shall have a high priority",
				PRIO_A, s1.getVerdictSeverity(), DELTA);
		assertEquals("Level AA shall have a medium priority",
				PRIO_AA, s2.getVerdictSeverity(), DELTA);
		assertEquals("Level AAA shall have a low priority",
				PRIO_AAA, s3.getVerdictSeverity(), DELTA);
	}

}
