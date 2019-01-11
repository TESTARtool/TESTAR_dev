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

import org.junit.Test;

public class GuidelineTest {

  @Test
  public void testGetNr() {
    AbstractPrinciple p = new PerceivablePrinciple();
    AbstractGuideline g = new TimeBasedMediaGuideline(p);
    assertEquals("1.2", g.getNr());
  }

  @Test
  public void testGetSuccessCriterionByName() {
    AbstractPrinciple p = new PerceivablePrinciple();
    AbstractGuideline g = new TimeBasedMediaGuideline(p);
    // first
    assertEquals(g.getSuccessCriteria().get(0),
        g.getSuccessCriterionByName("Audio-only and Video-only (Prerecorded)"));
    // middle
    assertEquals(g.getSuccessCriteria().get(2),
        g.getSuccessCriterionByName("Audio Description or Media Alternative (Prerecorded)"));
    // last
    assertEquals(g.getSuccessCriteria().get(4),
        g.getSuccessCriterionByName("Audio Description (Prerecorded)"));
    // case-insensitive
    assertEquals(g.getSuccessCriteria().get(4),
        g.getSuccessCriterionByName("audio description (prerecorded)"));
    // invalid
    assertNull(g.getSuccessCriterionByName("No such criterion"));
    assertNull(g.getSuccessCriterionByName(""));
    assertNull(g.getSuccessCriterionByName(""));
  }

}
