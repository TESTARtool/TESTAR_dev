/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2026 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.testar.monkey.alayer.visualizers.ShapeVisualizer;

public class VerdictTest {

	private final Visualizer failVisualizer = new ShapeVisualizer(
					Pen.PEN_RED, 
					Rect.from(0, 0, 10, 10), 
					"Fail Visualizer", 
					0.5, 0.5);

	@Test
	public void testToString() {
		Verdict v = new Verdict(Verdict.Severity.OK, "This is a test verdict");
		assertEquals("The string representation of a Verdict shall include its severity and its info",
				"severity: 0.0 info: This is a test verdict", v.toString());
	}

	@Test
	public void testVerdictHelperAreOK() {
		Verdict ok = Verdict.OK;
		Verdict warn = new Verdict(Verdict.Severity.SUSPICIOUS_TAG, "Issue", failVisualizer);

		assertTrue(Verdict.helperAreAllVerdictsOK(Arrays.asList(ok)));
		assertFalse(Verdict.helperAreAllVerdictsOK(Arrays.asList(warn)));
	}

	@Test
	public void testEquals() {
		Verdict v1 = Verdict.OK;
		Verdict v2 = Verdict.FAIL;
		Verdict v3 = new Verdict(Verdict.Severity.FAIL, "Failure");
		Verdict v4 = new Verdict(Verdict.Severity.FAIL, "Different failure");

		assertTrue(Verdict.OK.equals(Verdict.OK));
		assertTrue(v1.equals(Verdict.OK));
		assertTrue(Verdict.OK.equals(v1));
		assertTrue(v2.equals(Verdict.FAIL));
		assertTrue(Verdict.FAIL.equals(v2));
		assertFalse(v1.equals(Verdict.FAIL));
		assertFalse(v3.equals(Verdict.FAIL));
		assertFalse(v3.equals(v4));
		assertFalse(v4.equals(v3));
	}

}
