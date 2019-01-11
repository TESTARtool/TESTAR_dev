/***************************************************************************************************
*
* Copyright (c) 2017 Universitat Politecnica de Valencia - www.upv.es
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

package es.upv.staq.testar.graph;

/**
 * A graph edge representation as a pair: action concrete ID x target state concrete ID
 *
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class GraphEdge {

  private String actionID, // concrete ID
           targetStateID; // concrete ID

  private GraphEdge() {}

  public GraphEdge(String actionID, String targetStateID) {
    this();
    this.actionID = actionID;
    this.targetStateID = targetStateID;
  }

  public String getActionID() {
    return this.actionID;
  }

  public String getTargetStateID() {
    return this.targetStateID;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o == null) return false;
    if (!(o instanceof GraphEdge)) return false;
    GraphEdge edge = (GraphEdge) o;
    return edge.getActionID().equals(this.getActionID()) &&
         edge.getTargetStateID().equals(this.getTargetStateID());
  }

  @Override
  public int hashCode() {
    return (this.actionID + this.targetStateID).hashCode();
  }

}
