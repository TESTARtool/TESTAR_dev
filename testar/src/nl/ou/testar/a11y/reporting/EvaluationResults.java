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

package nl.ou.testar.a11y.reporting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fruit.Assert;
import org.fruit.alayer.Verdict;

/**
 * The results of evaluating muliple success criteria
 * @author Davy Kager
 *
 */
public final class EvaluationResults implements Serializable {

  private static final long serialVersionUID = 4338993838674375390L;

  /**
   * The severity of a warning
   */
  public static final double SEVERITY_WARNING =
      Verdict.SEVERITY_MIN + ((Verdict.SEVERITY_MAX - Verdict.SEVERITY_MIN) / 10.0);
  // The severity of an error is computed, see getOverallVerdict().

  private final List<EvaluationResult> results = new ArrayList<>();

  private int passCount = 0, warningCount = 0, errorCount = 0;

  /**
   * Constructs a new container for evaluation results
   */
  public EvaluationResults() {}

  /**
   * Add an evaluation result to the list of results
   * @param result The result.
   */
  public void add(EvaluationResult result) {
    results.add(Assert.notNull(result));
    switch (result.getType()) {
      case WARNING:
        warningCount++;
        return;
      case ERROR:
        errorCount++;
        return;
      case OK:
        passCount++;
        return;
      default:
        return;
    }
  }

  /**
   * Get a list of all evaluation results
   * @return The list of results.
   */
  public List<EvaluationResult> getResults() {
    return Collections.unmodifiableList(results);
  }

  /**
   * Get the total number of evaluation results
   * @return The result count.
   */
  public int getResultCount() {
    return results.size();
  }

  /**
   * Get the total number of evaluation results that are passes
   * @return The pass count.
   */
  public int getPassCount() {
    return passCount;
  }

  /**
   * Get the total number of evaluation results that are warnings
   * @return The warning count.
   */
  public int getWarningCount() {
    return warningCount;
  }

  /**
   * Get the total number of evaluation results that are errors
   * @return The error count.
   */
  public int getErrorCount() {
    return errorCount;
  }

  /**
   * Returns if at least one evaluation result is a violation (warning or error)
   * @return Whether or not the results contain any violations.
   */
  public boolean hasViolations() {
    return getResultCount() - passCount > 0;
  }

  /**
   * Computes an overall Verdict from all evaluation results
   * The severity will match that of the highest-level problem that was found.
   * It will be the minimum severity if no problems were found.
   * @return A Verdict.
   */
  public Verdict getOverallVerdict() {
    double severity = Verdict.OK.severity();
    for (EvaluationResult result: results) {
      switch (result.getType()) {
      case WARNING:
        severity = Math.max(severity, SEVERITY_WARNING);
        continue;
      case ERROR:
        severity = Math.max(severity, result.getVerdictSeverity());
        continue;
      default:
        continue;
      }
    }
    return new Verdict(severity, "Accessibility evaluation");
  }

  @Override
  public String toString() {
    return "Warnings: " + getWarningCount() +
        ", Errors: " + getErrorCount() +
        ", Total: " + getResultCount();
  }

}
