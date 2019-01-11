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

package nl.ou.testar;

import org.fruit.alayer.*;
import org.fruit.alayer.actions.NOP;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests the GraphDB Wrapper. Actions should only be performed when the enabled is true.
 * Created by floren on 9-6-2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class GraphDBTest {

    @Mock
    private GraphDBRepository repository;

    @Test
    public void addStateOnEnabledRepository() {
        GraphDB sut =create(true);
        State state = new StdState();
        sut.addState(state);
        sut.addState(state,true);
        Mockito.verify(repository,Mockito.times(1)).addState(state,false);
       Mockito.verify(repository,Mockito.times(1)).addState(state,true);
    }

    @Test
    public void addStateOnDisabledRepository() {
        GraphDB sut =create(false);
        State state = new StdState();
        sut.addState(state);
        Mockito.verify(repository,Mockito.times(0)).addState(state,false);
    }

    @Test
    public void addActionOnEnabledRepository()  {
        GraphDB sut =create(true);
        Action action = new NOP();
        sut.addAction(action,"");
        sut.addActionOnState("",action,"");
        Mockito.verify(repository,Mockito.times(1)).addAction(action,"");
        Mockito.verify(repository,Mockito.times(1)).addActionOnState("",
                action,"");
    }

    @Test
    public void addActionOnDisabledRepository()  {
        GraphDB sut =create(false);
        Action action = new NOP();
        sut.addAction(action,"");
        sut.addActionOnState("",action,"");
        Mockito.verify(repository,Mockito.times(0)).addAction(action,"");
        Mockito.verify(repository,Mockito.times(0)).addActionOnState("",
                action,"");
    }

    @Test
    public void addWidgetOnEnabledRepository()  {
        GraphDB sut =create(true);
        Widget widget = new StdWidget();
        sut.addWidget("",widget);
        Mockito.verify(repository,Mockito.times(1)).addWidget("",
                widget);
    }

    @Test
    public void addWidgetOnDisabledRepository()  {
        GraphDB sut =create(false);
        Widget widget = new StdWidget();
        sut.addWidget("",widget);
        Mockito.verify(repository,Mockito.times(0)).addWidget("",
                widget);
    }

    private GraphDB create(boolean isEnabled) {
        GraphDB sut = new GraphDB(isEnabled,"memory:demo","admin","admin");
        sut.setRepository(repository);
        return sut;
    }

}
