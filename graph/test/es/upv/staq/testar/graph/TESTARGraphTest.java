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


package es.upv.staq.testar.graph;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by florendegier on 27/05/2017.
 */
public class TESTARGraphTest {

   private TESTARGraph graph;

   private TESTAREnvironment env = new TESTAREnvironment( "output/graphs", "junit");

   @Before
   public void createSUT() {
      graph = TESTARGraph.buildEmptyGraph();
   }

   @Test
   public void buildEmptyGraph() {
      assertNotNull("Graph object shall be created",graph);
   }

   @Test
   public void testEmptyLongestPath() {
      assertEquals("Longest path (0) = ",graph.getLongestPath());
   }

   @Test
   public void testAddNewVertex() {

      graph.addVertex(env, new GraphState("v1"));
      IGraphState gs = graph.getState("v1");
      assertTrue("The vertex has been added", gs != null);
      assertEquals("There should be one vertext", 1,graph.vertexSet().size());

   }

   @Test
   public void testAddExistingVertex() {
      graph.addVertex(env, new GraphState("v1"));
      graph.addVertex(env, new GraphState("v1"));
      //assertFalse("A vertex can be added once using it's concrete ID", result);
      assertEquals("There should be one vertext", 1,graph.vertexSet().size());
   }

}
