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

import org.fruit.alayer.Widget;

import nl.ou.testar.a11y.reporting.EvaluationResult;

/**
 * The result of evaluating a WCAG2ICT success criterion
 * @author Davy Kager
 *
 */
public final class WCAG2EvaluationResult extends EvaluationResult {

  private static final long serialVersionUID = -3449985990033944575L;

  private final SuccessCriterion criterion;

  /**
   * Constructs a new evaluation result that does not apply to a single widget
   * @param criterion The success criterion associated with this evaluation result.
   * @param type The problem type.
   * @param message The problem description.
   */
  WCAG2EvaluationResult(SuccessCriterion criterion, Type type, String message) {
    this(criterion, type, message, null);
  }

  /**
   * Constructs a new evaluation result that applies to a single widget
   * @param criterion The success criterion associated with this evaluation result.
   * @param type The problem type.
   * @param message The problem description.
   * @param widget The widget this evaluation result applies to.
   */
  WCAG2EvaluationResult(SuccessCriterion criterion, Type type, String message, Widget widget) {
    super(type, message, widget);
    this.criterion = criterion;
  }

  /**
   * Gets the success criterion associated with this evaluation result
   * @return The success criterion.
   */
  public SuccessCriterion getSuccessCriterion() {
    return criterion;
  }

  @Override
  public double getVerdictSeverity() {
    return criterion.getVerdictSeverity();
  }

}
