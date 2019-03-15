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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;

import nl.ou.testar.GraphDB;
import nl.ou.testar.a11y.protocols.Evaluator;
import nl.ou.testar.a11y.reporting.EvaluationResult;
import nl.ou.testar.a11y.reporting.EvaluationResults;

/**
 * A WCAG principle
 * @author Davy Kager
 *
 */
public abstract class AbstractPrinciple extends ItemBase implements Evaluator {

  private static final long serialVersionUID = 7735450322487421780L;

  /**
   * The list of all the guidelines in this principle
   */
  protected final List<AbstractGuideline> guidelines = new ArrayList<>();

  /**
   * Constructs a new principle
   * @param nr The number of the principle.
   * @param name The name of the principle.
   */
  protected AbstractPrinciple(int nr, String name) {
    super(nr, name);
  }

  /**
   * Gets all guidelines in this principle
   * @return The list of guidelines.
   */
  public List<AbstractGuideline> getGuidelines() {
    return Collections.unmodifiableList(guidelines);
  }

  /**
   * Evaluates the accessibility of the given state
   * This will collect evaluation results from all guidelines in this principle.
   * This method executes oracles in on-the-fly evaluation.
   * @param widgets The widgets to consider.
   * @return The results of the evaluation.
   */
  @Override
  public EvaluationResults evaluate(List<Widget> widgets) {
    EvaluationResults results = new EvaluationResults();
    for (AbstractGuideline g: guidelines) {
      for (EvaluationResult result: g.evaluate(widgets).getResults()) {
        results.add(result);
      }
    }
    return results;
  }

  /**
   * Derives the possible actions from the given state
   * This will collect actions from all guidelines in this principle.
   * The actions are specific to accessibility.
   * This method derives actions in on-the-fly evaluation.
   * @param widgets The widgets to consider.
   * @return The set of actions.
   */
  @Override
  public Set<Action> deriveActions(List<Widget> widgets) {
    Set<Action> actions = new HashSet<>();
    for (AbstractGuideline g: guidelines) {
      actions.addAll(g.deriveActions(widgets));
    }
    return actions;
  }

  /**
   * Evaluates the overall accessibility of the SUT by querying the given graph database
   * This will collect evaluation results from all guidelines in this principle.
   * This method executes oracles in offline evaluation.
   * @param graphDB The graph database.
   * @return The results of the evaluation.
   */
  @Override
  public EvaluationResults query(GraphDB graphDB) {
    EvaluationResults results = new EvaluationResults();
    for (AbstractGuideline g: guidelines) {
      for (EvaluationResult result: g.query(graphDB).getResults()) {
        results.add(result);
      }
    }
    return results;
  }

  @Override
  public String getImplementationVersion() {
    return "WCAG2ICT-principle-" + WCAG2ICT.VERSION;
  }

}
