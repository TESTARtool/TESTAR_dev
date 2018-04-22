/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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
 * Unit test to demonstrate the operation of the TestarEnvironment.
 * Created by florendegier on 27/05/2017.
 */
public class TESTAREnvironmentTest {
  private TESTAREnvironment environment;

  @Before
  public void createEnviroment () {
    environment = new TESTAREnvironment("test");
  }

  @Test
  public void validateEmptyEnvironment () {
    assertEquals("In a clean environment the path length shall be ",
        0, environment.getLongestPathLength());

    assertArrayEquals(new int[]{0, 0, -2}, environment.getGraphResumingMetrics());
  }

  @Test
  public void testAddPopulateWithFirstAction () {
    environment.populateEnvironment(new GraphState(Grapher.GRAPH_NODE_ENTRY),
        new GraphAction("a1"),
        new GraphState("v2"));

    assertEquals("Longest path shall be ", 2, environment.getLongestPathLength());
    assertTrue("v2 shall be inserted", environment.stateAtGraph(new GraphState("v2")));
  }

  @Test
  public void testPopulateWithTwoActions () {

    environment.populateEnvironment(new GraphState(Grapher.GRAPH_NODE_ENTRY),
        new GraphAction("a1"),
        new GraphState("v2"));
    environment.populateEnvironment(new GraphState("v2"),
        new GraphAction("a2"),
        new GraphState("v3"));
    assertEquals("Longest path shall be ", 3, environment.getLongestPathLength());
    assertTrue("v1 shall be inserted", environment.stateAtGraph(new GraphState("v3")));
  }
}
