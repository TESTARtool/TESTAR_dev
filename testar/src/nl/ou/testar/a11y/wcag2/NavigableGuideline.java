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
import java.util.Map;
import java.util.Map.Entry;

import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import nl.ou.testar.GraphDB;
import nl.ou.testar.GremlinStart;
import nl.ou.testar.a11y.reporting.EvaluationResults;
import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;
import nl.ou.testar.a11y.windows.AccessibilityUtil;

/**
 * A WCAG 2.0 guideline
 * @author Davy Kager
 *
 */
public final class NavigableGuideline extends AbstractGuideline {

  private static final long serialVersionUID = 7746462844461205071L;

  private static final int SAME_TITLE_THRESHOLD = 3;

  NavigableGuideline(AbstractPrinciple parent) {
    super(4, "Navigable", parent);
    List<SuccessCriterion> criteria = getCriteria();
    criteria.add(new SuccessCriterion(1, "Bypass Blocks",
        this, Level.A, "navigation-mechanisms-skip"));
    criteria.add(new SuccessCriterion(2, "Page Titled",
        this, Level.A, "navigation-mechanisms-title"));
    criteria.add(new SuccessCriterion(3, "Focus Order",
        this, Level.A, "navigation-mechanisms-focus-order"));
    criteria.add(new SuccessCriterion(4, "Link Purpose (In Context)",
        this, Level.A, "navigation-mechanisms-refs"));
    criteria.add(new SuccessCriterion(5, "Multiple Ways",
        this, Level.AA, "navigation-mechanisms-mult-loc"));
    criteria.add(new SuccessCriterion(6, "Headings and Labels",
        this, Level.AA, "navigation-mechanisms-descriptive"));
    criteria.add(new SuccessCriterion(7, "Focus Visible",
        this, Level.AA, "navigation-mechanisms-focus-visible"));
    setCriteria(criteria);
  }

  @Override
  public EvaluationResults evaluate(List<Widget> widgets) {
    EvaluationResults results = new EvaluationResults();
    for (Widget w: widgets) {
      // used during offline evaluation
      w.set(WCAG2Tags.WCAG2IsWindow, AccessibilityUtil.isWindow(w));
    }
    return results;
  }

  @SuppressWarnings("unchecked")
  @Override
  public EvaluationResults query(GraphDB graphDB) {
    EvaluationResults results = new EvaluationResults();
    SuccessCriterion sc = getSuccessCriterionByName("Page Titled");
    String gremlinTitleCount = "_().has('@class','Widget')" +
        ".has('" + WCAG2Tags.WCAG2IsWindow.name() +"',true)" +
        ".groupCount{it." + Tags.Title.name() + "}.cap";
    List<Object> titleCounts = graphDB.getObjectsFromGremlinPipe(gremlinTitleCount,
        GremlinStart.VERTICES);
    // the list contains one map with title counts
    Map<String, Long> titleCount = (Map<String, Long>)titleCounts.get(0);
    boolean hasViolations = false;
    for (Entry<String, Long> entry: titleCount.entrySet()) {
      if (entry.getValue() > SAME_TITLE_THRESHOLD) {
        hasViolations = true;
        results.add(new WCAG2EvaluationResult(sc, WCAG2EvaluationResult.Type.WARNING,
            "Possible ambiguous title \"" + entry.getKey() +
            "\" appeared " + entry.getValue() + " times"));
      }
    }
    if (!hasViolations) {
      results.add(evaluationPassed(sc));
    }
    return results;
  }

}
