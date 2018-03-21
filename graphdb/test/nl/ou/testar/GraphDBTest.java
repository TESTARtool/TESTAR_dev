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