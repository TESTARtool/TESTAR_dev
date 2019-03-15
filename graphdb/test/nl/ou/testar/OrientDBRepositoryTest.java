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

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.NOP;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test to validate the storage of State objects
 * A State will be stored ones. Every time the same state is stored a flag visited is updated.
 * Created by florendegier on 03/06/2017.
 */
public class OrientDBRepositoryTest {

   private OrientGraphFactory factory;

   private OrientDBRepository sut;


   @Before
   public void setup() {
      factory = new OrientGraphFactory("memory:demo");
      factory.setMaxRetries(10);
      factory.setupPool(1, 2);
      factory.setRequireTransaction(false);

      sut = new OrientDBRepository("memory:demo", "admin", "admin");
   }

   @After
   public void cleanup() {
      OrientGraph graph = factory.getTx();
      graph.drop();
      graph.shutdown();
      factory.close();
   }

   @Test
   public void testAddState() {

      StdState state = new StdState();
      state.set(Tags.ConcreteID, "0xCAFE");
      state.set(Tags.Abstract_R_ID,"demo");

      sut.addState(state,false);

      OrientGraph graph = factory.getTx();
      long data = graph.getRawGraph().countClass("State");
      assertEquals("The database shall contain one element", 1, data);
      graph.shutdown();

      sut.addState(state,false);

      graph = factory.getTx();
      data = graph.getRawGraph().countClass("State");
      assertEquals("The database shall contain one element", 1, data);

      state.set(Tags.ConcreteID, "0xDEAD");

      sut.addState(state,false);

      graph = factory.getTx();
      data = graph.getRawGraph().countClass("State");
      assertEquals("The database shall contain two elements", 2, data);
      data = graph.getRawGraph().countClass("AbsState");
      assertEquals("The database shall contain one abstract State",1,data);

      graph.shutdown();

   }

   @Test
   public void testAddWidgetOnce() {
      Widget widget = new StdWidget();
      widget.set(Tags.ConcreteID, "0xDADA");
      widget.set(Tags.Desc, "Demo");
      widget.set(Tags.Abstract_R_ID, "TestRole");
      widget.set(Tags.Abstract_R_T_ID, "RolAndTitle");
      widget.set(Tags.Abstract_R_T_P_ID, "RoleTitleAndPath");

      State from = new StdState();
      from.set(Tags.ConcreteID, "0xDEAD");
      from.set(Tags.Abstract_R_ID,"demo");

      sut.addState(from,false);
      sut.addWidget("0xDEAD", widget);
      sut.addWidget("0xDEAD", widget);

      OrientGraph graph = factory.getTx();
      graph = factory.getTx();
      long data = graph.getRawGraph().countClass("Widget");
      assertEquals("The database shall contain one element", 1, data);

   }


   @Test
   public void testAddction() {

      Action action = new NOP();
      action.set(Tags.ConcreteID, "0xDAAD");
      action.set(Tags.TargetID, "0xDADA");
      action.set(Tags.AbstractID, "demo");
      action.set(Tags.Desc, "test");
      State from = new StdState();
      from.set(Tags.ConcreteID, "0xDEAD");
      from.set(Tags.Abstract_R_ID,"demo");
      State to = new StdState();
      to.set(Tags.ConcreteID, "0xCAFE");
      to.set(Tags.Abstract_R_ID,"demo");

      Widget widget = new StdWidget();
      widget.set(Tags.ConcreteID, "0xDADA");
      widget.set(Tags.Abstract_R_ID, "TestRole");
      widget.set(Tags.Abstract_R_T_ID, "RolAndTitle");
      widget.set(Tags.Abstract_R_T_P_ID, "RoleTitleAndPath");
      widget.set(Tags.Role, Role.from("test"));
      widget.set(Tags.Title, "dummy");
      widget.set(Tags.Desc, "Demo");

      sut.addState(from,false);
      sut.addWidget("0xDEAD", widget);
      sut.addState(to,false);

      sut.addAction(action, "0xCAFE");

   }

   @Test
   public void testAddactionMissingWidget() {

      Action action = new NOP();
      action.set(Tags.ConcreteID, "0xDAAD");
      action.set(Tags.TargetID, "0xDADA");
      action.set(Tags.Desc, "test");
      State from = new StdState();
      from.set(Tags.ConcreteID, "0xDEAD");
      from.set(Tags.Abstract_R_ID,"demo");
      State to = new StdState();
      to.set(Tags.ConcreteID, "0xCAFE");
      to.set(Tags.Abstract_R_ID,"demo");

      sut.addState(from,false);
      sut.addState(to,false);
      try {
         sut.addAction(action, "0xCAFE");
         fail("addAction should throw an exception");
      } catch (Exception e) {
         assertTrue("Expect a GraphDBException", e instanceof GraphDBException);
      }
   }

   @Test
   public void testAddctionMissingToState() {

      Action action = new NOP();
      action.set(Tags.ConcreteID, "0xDAAD");
      action.set(Tags.TargetID, "0xDADA");
      action.set(Tags.Desc, "test");
      State from = new StdState();
      from.set(Tags.ConcreteID, "0xDEAD");
      from.set(Tags.Abstract_R_ID,"demo");

      Widget widget = new StdWidget();
      widget.set(Tags.ConcreteID, "0xDADA");
      widget.set(Tags.Abstract_R_ID,"TestRole");
      widget.set(Tags.Abstract_R_T_ID,"RolAndTitle");
      widget.set(Tags.Abstract_R_T_P_ID,"RoleTitleAndPath");
      widget.set(Tags.Desc, "Demo");

      sut.addState(from,false);
      sut.addWidget("0xDEAD", widget);

      try {
         sut.addAction(action, "0xCAFE");
         fail("addAction should throw an exception");
      } catch (Exception e) {
         assertTrue("Expect a GraphDBException", e instanceof GraphDBException);
      }

   }


   @Test
   public void testAddActionOnState() {

      Action action = new NOP();
      action.set(Tags.ConcreteID, "0xDAAD");
      action.set(Tags.TargetID, "0xDADA");
      action.set(Tags.AbstractID, "demo");
      action.set(Tags.Desc, "test");
      State from = new StdState();
      from.set(Tags.ConcreteID, "0xDEAD");
      from.set(Tags.Abstract_R_ID,"demo");
      State to = new StdState();
      to.set(Tags.ConcreteID, "0xCAFE");
      to.set(Tags.Abstract_R_ID,"demo");

      sut.addState(from,false);
      sut.addState(to,false);

      sut.addActionOnState("0xDEAD", action, "0xCAFE");

   }

   @Test
   public void testAddActionOnStateWithMissingFromState() {
      try {
         Action action = new NOP();
         action.set(Tags.Desc, "test");
         action.set(Tags.ConcreteID, "0xDAAD");
         action.set(Tags.TargetID, "0xCAFE");
         action.set(Tags.AbstractID,"demo");
         State to = new StdState();
         to.set(Tags.ConcreteID, "0xCAFE");
         to.set(Tags.Abstract_R_ID,"demo");

         sut.addState(to,false);

         sut.addActionOnState("0xDEAD", action, "0xCAFE");
         fail("addAction should throw an exception");
      } catch (Exception e) {
         assertTrue("Expect a GraphDBException", e instanceof GraphDBException);
      }
   }


   @Test
   public void testAddWidgetOnUnknownState() {
      try {
         Widget widget = new StdWidget();
         widget.set(Tags.ConcreteID, "0xDADA");
         widget.set(Tags.Desc, "Demo");

         sut.addWidget("unknown", widget);
         fail("addWidget should throw an exception");
      } catch (Exception e) {
         assertTrue("Expect a GraphDBException", e instanceof GraphDBException);
      }
   }


   @Test
   public void testAddActionOnStateWithMissingToState() {
      try {
         Action action = new NOP();
         action.set(Tags.Desc, "test");
         action.set(Tags.ConcreteID, "0xDAAD");
         action.set(Tags.AbstractID,"demo");
         action.set(Tags.TargetID, "0xCAFE");
         State from = new StdState();
         from.set(Tags.ConcreteID, "0xCAFE");
         from.set(Tags.Abstract_R_ID,"demo");

         sut.addState(from,false);

         sut.addActionOnState("0xCAFE", action, "0xDEAD");
         fail("addAction should throw an exception");
      } catch (Exception e) {
         assertTrue("Expect a GraphDBException " + e.getClass(), e instanceof GraphDBException);
      }
   }

}
