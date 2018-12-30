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


package nl.ou.testar.a11y.protocols;

import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;

import nl.ou.testar.GraphDB;
import nl.ou.testar.a11y.reporting.EvaluationResults;

/**
 * Specifies the requirements for an object to be plugged into an AbstractProtocol to evaluate accessibility
 * Such objects need to be able to evaluate a given state and derive actions to move to a new state.
 * @author Davy Kager
 *
 */
public interface Evaluator {

  /**
   * Evaluates the accessibility of the given state
   * This method executes oracles in on-the-fly evaluation.
   * @param widgets The widgets to consider.
   * @return The results of the evaluation.
   */
  public EvaluationResults evaluate(List<Widget> widgets);

  /**
   * Derives the possible actions from the given state
   * The actions are specific to accessibility.
   * This method derives actions in on-the-fly evaluation.
   * @param widgets The widgets to consider.
   * @return The set of actions.
   */
  public Set<Action> deriveActions(List<Widget> widgets);

  /**
   * Evaluates the overall accessibility of the SUT by querying the given graph database
   * This method executes oracles in offline evaluation.
   * @param graphDB The graph database.
   * @return The results of the evaluation.
   */
  public EvaluationResults query(GraphDB graphDB);

  /**
   * Gets the unique version of the implementation for the guidelines being used
   * @return The version as a String.
   */
  public String getImplementationVersion();

}
