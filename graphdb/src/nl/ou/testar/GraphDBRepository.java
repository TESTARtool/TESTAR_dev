package nl.ou.testar;
/************************************************************************************
 *
 * COPYRIGHT (2017):
 *
 * Open Universiteit
 * www.ou.nl<http://www.ou.nl>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of mosquitto nor the names of its
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
 ************************************************************************************/

import java.util.List;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;


/**
 * Repository API
 * Created by floren on 9-6-2017.
 */
public interface GraphDBRepository {

    /**
     * Store State in Graph database.
     *
     * @param state State of the SUT for this step.
     * @param isInitial indicate if the state is initial.
     */
    void addState(final State state, final boolean isInitial);

    /**
     * Add Action on a widget to the graph database as Edge
     *
     * @param action      The performed action
     * @param toStateID   ConcreteID of the new State
     */
    void addAction( final Action action, final String toStateID);

    /**
     * Add Action on a State to the graph database as Edge
     *
     * @param stateId id of the state on which the action is performed.
     * @param action the action.
     * @param toStateID the resulting state
     */
    void addActionOnState(final String stateId, final Action action, final String toStateID);


    /**
     * Add a widget to the the graph database as Wiget.
     * @param stateID State to which the widget belongs
     * @param w The widget object
     */
    void addWidget(final String stateID, Widget w);

    /**
     * Store a custom type in the graph database.
     * @param sourceID the ID of the artifact to which the custom type relates.
     * @param relation The name of the relation
     * @param instance the custom object.
     */
    void addCustomType(final String sourceID, final String relation, final CustomType instance);
    
    /**
     * Get all objects from a pipe specified by a Gremlin-Groovy expression
     * @param gremlin The Gremlin-Groovy expression.
     * @param start The starting point for the expression.
     * @return A list of all objects in the pipe.
     */
    List<Object> getObjectsFromGremlinPipe(String gremlin, GremlinStart start);
}
