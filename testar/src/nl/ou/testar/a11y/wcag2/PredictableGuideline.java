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

import org.fruit.alayer.Tags;

import nl.ou.testar.GraphDB;
import nl.ou.testar.GremlinStart;
import nl.ou.testar.a11y.reporting.EvaluationResults;
import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;

/**
 * A WCAG 2.0 guideline
 * @author Davy Kager
 *
 */
public final class PredictableGuideline extends AbstractGuideline {

  private static final long serialVersionUID = 7865244281809536826L;

  PredictableGuideline(AbstractPrinciple parent) {
    super(2, "Predictable", parent);
    List<SuccessCriterion> criteria = getCriteria();
    criteria.add(new SuccessCriterion(1, "On Focus",
        this, Level.A, "consistent-behavior-receive-focus"));
    criteria.add(new SuccessCriterion(2, "On Input",
        this, Level.A, "consistent-behavior-unpredictable-change"));
    criteria.add(new SuccessCriterion(3, "Consistent Navigation",
        this, Level.AA, "consistent-behavior-consistent-locations"));
    criteria.add(new SuccessCriterion(4, "Consistent Identification",
        this, Level.AA, "consistent-behavior-consistent-functionality"));
    setCriteria(criteria);
  }

  @Override
  public EvaluationResults query(GraphDB graphDB) {
    EvaluationResults results = new EvaluationResults();
    SuccessCriterion sc = getSuccessCriterionByName("On Focus");
    String tagConcreteID = Tags.ConcreteID.name();
    String gremlinStateChange =
        // find actions ...
        "_().has('@class','Action')" +
        // ... that navigate within the same window ...
        ".has('" + WCAG2Tags.WCAG2IsInWindowNavigation.name() + "',true)" +
        // ... where the ID of the new state is different from the ID of the old state
        ".filter{it.inE('targetedBy').outV." +  tagConcreteID + "!=" +
        "it.outE('resultsIn').outV." + tagConcreteID + "}" +
        // go to the resulting state,
        // then through the 'has' edge to the widgets of the state ...
        ".outE('resultsIn').outV.outE('has').inV" +
        // ... where the widget is a main window ...
        ".has('" + WCAG2Tags.WCAG2IsWindow.name() + "',true)" +
        // ... and return the title
        "." + Tags.Title.name();
    List<Object> stateChanges = graphDB.getObjectsFromGremlinPipe(gremlinStateChange,
        GremlinStart.VERTICES);
    // the list contains the titles of the new states
    for (Object title: stateChanges) {
      results.add(new WCAG2EvaluationResult(sc, WCAG2EvaluationResult.Type.ERROR,
          "Unexpected state change to \"" + title + "\""));
    }
    if (stateChanges.isEmpty()) {
      results.add(evaluationPassed(sc));
    }
    return results;
  }

}
