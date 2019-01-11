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

import java.util.List;
import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;

/**
 * A WCAG 2.0 guideline
 * @author Davy Kager
 *
 */
public final class DistinguishableGuideline extends AbstractGuideline {

  private static final long serialVersionUID = 4930832964961139602L;

  DistinguishableGuideline(AbstractPrinciple parent) {
    super(4, "Distinguishable", parent);
    List<SuccessCriterion> criteria = getCriteria();
    criteria.add(new SuccessCriterion(1, "Use of Color",
        this, Level.A, "visual-audio-contrast-without-color"));
    criteria.add(new SuccessCriterion(2, "Audio Control",
        this, Level.A, "visual-audio-contrast-dis-audio"));
    criteria.add(new SuccessCriterion(3, "Contrast (Minimum)",
        this, Level.AA, "visual-audio-contrast-contrast"));
    criteria.add(new SuccessCriterion(4, "Resize Text",
        this, Level.AA, "visual-audio-contrast-scale"));
    criteria.add(new SuccessCriterion(5, "Images of Text",
        this, Level.AA, "visual-audio-contrast-text-presentation"));
    setCriteria(criteria);
  }

}
