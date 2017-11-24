package nl.ou.testar;
/*************************************************************************************
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


import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

import com.tinkerpop.blueprints.Vertex;

/**
 * Wrapper for interaction with the Graph Database
 * Created by floren on 5-6-2017.
 */
public class GraphDB implements GraphDBRepository {


    private final boolean enabled;
    private GraphDBRepository repository;

    public GraphDB(final boolean enabled, final String url, final String userName, final String password) {
        this.enabled = enabled;
        if(enabled) {
            repository = new OrientDBRepository(url, userName, password);
        }
    }

    /**
     * Store the State in the graph database.
     * @param state state to store.
     */
    @Override
    public void addState(final State state) {
        if(enabled) {
            repository.addState(state);
        }
    }


    /**
     * Add Widget to the graph database. A widget will be identified by it's concrete id.
     * @param statedID State to which the widget belongs
     * @param widget The widget to add
     */
    @Override
    public void addWidget(final String statedID, final Widget widget) {
        if(enabled) {
            repository.addWidget(statedID,widget);
        }
    }

    /**
     * Store an action in the graph database.
     * @param action the action performed
     * @param toStateId the new state.
     */
    @Override
    public void addAction(final Action action, final String toStateId) {
        if(enabled) {
            repository.addAction(action, toStateId);
        }
    }

    /**
     * Store an action without an targetID (widget). It's assumed that the action operates on the State it was
     * fired from.
     * @param fromSateID id of the original state
     * @param action The action performed
     * @param toStateID the resulting stateId
     */
    @Override
    public void addActionOnState(final String fromSateID, final Action action, final String toStateID) {
        if(enabled) {
            repository.addActionOnState(fromSateID, action, toStateID);
        }
    }
    
    public Iterable<Vertex> getStateVertices() {
        if(enabled) {
            return repository.getStateVertices();
        }
        return null;
    }

    /**
     * Setter only used in test.
     * @param repo Mock repository
     */
    void setRepository(GraphDBRepository repo) {
        repository = repo;
    }

}
