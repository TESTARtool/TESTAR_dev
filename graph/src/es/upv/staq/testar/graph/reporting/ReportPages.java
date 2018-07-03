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


package es.upv.staq.testar.graph.reporting;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.graph.GraphEdge;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.graph.TESTARGraph;
import org.fruit.alayer.actions.BriefActionRolesMap;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * Reporting pages.
 *
 * @author Urko Rueda Molina (alias: urueda)
 */
public class ReportPages {

  /**
   * @param env
   * @return report on clusters page
   */
  public static String getClustersPageReport (IEnvironment env) {

    StringBuffer report = new StringBuffer();

    final int CLUSTER_MEMBERS_PERLINE = 8;

    report.append("STATES CLUSTERS:\n");
    HashMap<String, Set<String>> absStateGroups = env.getGraphStateClusters();
    int gi = 1; 
    int i;
    int nlc;
    for (String g : absStateGroups.keySet()) {
      report.append("[" + gi++ + "] " + g + " contains:\n\t");
      i = 1;
      nlc = 0;
      for (String s : absStateGroups.get(g)) {
        nlc++;
        report.append("(" + i++ + ") " + s + (nlc % CLUSTER_MEMBERS_PERLINE == 0 ? "\n\t" : " "));
      }
      report.append(nlc % CLUSTER_MEMBERS_PERLINE != 0 ? "\n" : "");
    }

    report.append("\nACTIONS CLUSTERS:\n");
    absStateGroups = env.getGraphActionClusters();
    gi = 1;
    for (String g : absStateGroups.keySet()) {
      report.append("[" + gi++ + "] " + g + " contains:\n\t");
      i = 1;
      nlc = 0;
      for (String s : absStateGroups.get(g)) {
        nlc++;
        report.append("(" + i++ + ") " + s + (nlc % CLUSTER_MEMBERS_PERLINE == 0 ? "\n\t" : " "));
      }
      report.append(nlc % CLUSTER_MEMBERS_PERLINE != 0 ? "\n" : "");
    }

    return report.toString();
  }

  /**
   * @param env
   * @param tGraph
   * @param orderedActions
   * @param SEQUENCE_LENGTH
   * @return report on test result
   */
  public static String getTestTablePageReport (IEnvironment env, TESTARGraph tGraph,
                                               GraphEdge[] orderedActions, int SEQUENCE_LENGTH,
                                               int firstSequenceActionNumber) {

    StringBuffer report = new StringBuffer();

    // Ordered test sequence actions list
    report.append("ACTION_TYPES:\n");
    for (String actionRole : BriefActionRolesMap.map.keySet()) {
      report.append("\t" + BriefActionRolesMap.map.get(actionRole) + " = " + actionRole + "\n");
    }
    report.append("\n");

    int ID_LENGTH = CodingManager.ID_LENTGH;
    String tableHead = String.format(
        "%1$" + SEQUENCE_LENGTH 
            + "s %2$7"  // RAM (KB)
            + "s %3$11"  // CPU (user)
            + "s %4$11"  // CPU (system)
            + "s %5$6"  // CPU %
            + "s %6$" + ID_LENGTH 
            + "s %7$" + SEQUENCE_LENGTH
            + "s %8$" + ID_LENGTH
            + "s %9$" + SEQUENCE_LENGTH
            + "s %10$" + ID_LENGTH 
            + "s %11$" + SEQUENCE_LENGTH
            + "s %12$s",
        "#",
        "RAM(KB)", //"Sync",
        "CPUser(ms)",
        "CPUsys(ms)",
        "CPU(%)",
        "FROM",
        "x",
        "TO",
        "x",
        "ACTION",
        "x",
        "ACTION_TYPE ( (WIDGET,ROLE,TITLE)[parameter*] )+ ");

    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < tableHead.length(); i++) {
      sb.append("-");
    }
    sb.append("\n" + tableHead + "\n");
    for (int i = 0; i < tableHead.length(); i++) {
      sb.append("-");
    }
    final String TABLE_HEADING = sb.toString() + "\n";

    report.append(TABLE_HEADING);

    int i = firstSequenceActionNumber; //int i=1;
    long[] cpu;
    IGraphState from;
    IGraphState to;
    IGraphAction ga;
    //List<Integer> movesSync = Grapher.getMovementsSync();
    for (GraphEdge edge : orderedActions) {
      ga = env.getAction(edge.getActionID());
      if (ga.knowledge()) {
        continue;
      }
      cpu = ga.getCPUsage();
      from = env.getSourceState(ga);
      to = env.getState(edge.getTargetStateID());
      String actionList = String.format("%1$" + SEQUENCE_LENGTH 
    		  + "d %2$7" // RAM (KB)
              + "d %3$11" // CPU (user)
              + "d %4$11" // CPU (system)
              + "d %5$6" // CPU %
              + "s %6$" + ID_LENGTH
              + "s %7$" + SEQUENCE_LENGTH
              + "d %8$" + ID_LENGTH
              + "s %9$" + SEQUENCE_LENGTH
              + "s %10$" + ID_LENGTH
              + "s %11$" + SEQUENCE_LENGTH
              + "d %12$s",
          i,
          ga.getMemUsage(), //movesSync.get(i-1),
          cpu[0], // user
          cpu[1], // system
          String.format("%.2f", (cpu[0] + cpu[1]) / (double) cpu[2] * 100), // cpu %
          from.getConcreteID(),
          from.getCount(),
          to.getConcreteID(),
          to.getCount(),
          ga.getConcreteID(),
          ga.getCount(),
          ga.getDetailedName());
      report.append(actionList + "\n");
      i++;
      if (i % 50 == 0) {
        report.append(TABLE_HEADING); // repeat heading
      }
    }

    return report.toString();
  }

  /**
   * @param env
   * @param tGraph
   * @param SEQUENCE_LENGTH
   * @return report on exploration of curve for page
   */
  public static String getExplorationCurvePageReport (IEnvironment env, TESTARGraph tGraph, int SEQUENCE_LENGTH, int firstSequenceActionNumber) {

    StringBuffer report = new StringBuffer();

    StringBuffer sb = new StringBuffer();

    // Exploration curve
    String headerS = String.format("%1$22s %2$16s %3$16s",
        "________________UNIQUE",
        "________ABSTRACT",
        "___________TOTAL");
    String explorationCurveS = String.format("%1$" + SEQUENCE_LENGTH + "s, %2$6s, %3$7s, %4$6s, %5$7s, %6$6s, %7$8s %8$18s %9$7s %10$6s %11$6s %12$7s",
        "#",
        "states",
        "actions",
        "states",
        "actions",
        "unique",
        "abstract",
        "unexplored-actions",
        "maxpath",
        "minCvg",
        "maxCvg",
        "KCVG");
    for (int i = 0; i < explorationCurveS.length(); i++) {
      sb.append("-");
    }
    sb.append("\n" + headerS + "\n");
    sb.append(explorationCurveS + "\n");
    for (int i = 0; i < explorationCurveS.length(); i++) {
      sb.append("-");
    }
    final String TABLE_HEADING = sb.toString() + "\n";

    report.append(TABLE_HEADING);

    int idx = firstSequenceActionNumber - 1; //int idx = 1;
    String sampleS;
    List<IEnvironment.ResumingMetrics> explorationCurve = env.getExplorationCurve();
    for (IEnvironment.ResumingMetrics sample : explorationCurve) {
      sampleS = String.format("%1$" + SEQUENCE_LENGTH + "s, " 
    	+ "%2$6d, %3$7d, %4$6d, %5$7d, %6$6d, %7$8d %8$18d %9$7d %10$6s %11$6s %12$7s",
          idx++,
          sample.getStateNumber(),
          sample.getActionNumber(),
          sample.getAbstractStateNumber(),
          sample.getAbstractActionNumber(),
          sample.getStateNumber() + sample.getActionNumber(),
          sample.getAbstractStateNumber() + sample.getAbstractActionNumber(),
          sample.getUnexploredActionNumber(),
          sample.getLongesthPath(),
          sample.getMinCvg() + "%",
          sample.getMaxCvg() + "%",
          sample.getCoverageUIspace() + "@" + env.convertKCVG(sample.getSpaceScale()));
      report.append(sampleS + "\n");
      if (idx % 50 == 0) {
        report.append(TABLE_HEADING); // repeat heading
      }
    }

    return report.toString();
  }

  /**
   * @param env
   * @param tGraph
   * @param orderedActions
   * @param SEQUENCE_LENGTH
   * @return report on stats for page
   */
  public static String getStatsPageReport (IEnvironment env, TESTARGraph tGraph,
                                           GraphEdge[] orderedActions, int SEQUENCE_LENGTH) {

    StringBuffer report = new StringBuffer();

    int unxStates = 0;
    int unxActions = 0;
    int totalStates = -1; // discard start node
    String verdict = null;
    for (IGraphState vertex : tGraph.vertexStates()) {
      if (vertex.getUnexploredActionsSize() > 0) {
        unxStates++;
      }
      unxActions += vertex.getUnexploredActionsSize();
      if (vertex.toString().equals(Grapher.GRAPH_NODE_FAIL)) {
        verdict = Grapher.GRAPH_NODE_FAIL;
      }
      else if (vertex.toString().equals(Grapher.GRAPH_NODE_PASS)) {
        verdict = Grapher.GRAPH_NODE_PASS;
      }
      else {
        totalStates += vertex.getCount();
      }
    }

    report.append("=== TEST SUMMARY ===\n");
    String statsMetahead = String.format("%1$27s %2$27s %3$17s",
        "______________________________STATES",
        "_____________________________ACTIONS",
        "____________TOTAL");
    report.append(statsMetahead + "\n");
    //String statsHead = String.format("%1$5s, %2$6s, %3$8s, %4$10s, %5$5s, %6$6s, %7$8s, %8$10s, %9$6s, %10$8s, ... %11$7s",
    String statsHead = String.format("%1$5s, %2$6s, %3$8s, %4$10s, %5$5s, %6$6s, %7$8s, %8$10s, %9$6s, %10$8s, %11$7s, %12$6s, %13$6s %14$7s", // by fraalpe2

        "total",
        "unique",
        "abstract",
        "unexplored",
        "total",
        "unique",
        "abstract",
        "unexplored",
        "unique",
        "abstract",
        // begin by fraalpe2
        "maxpath",
        "minCvg",
        "maxCvg",
        // end by fraalpe2
        "VERDICT");
    report.append(statsHead + "\n");
    int uniqueStates = tGraph.vertexSet().size() - 2;
    int uniqueActions = tGraph.edgeSet().size() - 2;
    int abstractStates = env.getGraphStateClusters().size();
    int longestPath = env.getLongestPathLength();
    int abstractActions = env.getGraphActionClusters().size();

    if (Grapher.testGenerator.equals(Grapher.EVOLUTIONARY_GENERATOR) 
    	&& verdict.equals(Grapher.GRAPH_NODE_PASS)) { // temporal fitness.txt patch for evolutionary algorithm
      try {
        java.io.Writer fitnessWriter = new java.io.FileWriter("output/fitness.txt");
        //fitnessWriter.write(new Double(abstractStates == 0 ? 2.0 : 1.0/(double)abstractStates).toString()); // fitness = 0.0 .. 1.0 (0 is best)
        fitnessWriter.write(new Double(abstractStates == 0 ? 0.0 : (double) abstractStates).toString()); // fitness = numero estados abstractos (higher is better)
        fitnessWriter.close();
      }
      catch (Exception e) {
      }
    }

    IEnvironment.CoverageMetrics cvgMetrics = env.getCoverageMetrics();
    String minCvg = String.format("%.2f", cvgMetrics.getMinCoverage());
    String maxCvg = String.format("%.2f", cvgMetrics.getMaxCoverage());
    String stats = String.format("%1$5s, %2$6s, %3$8s, %4$10s, %5$5s, %6$6s, %7$8s, %8$10s, %9$6s, %10$8s, %11$7s, %12$6s, %13$6s %14$7s", // by fraalpe2
        totalStates,
        uniqueStates, // without start/end states
        abstractStates, // clusters
        unxStates, // states with unexplored actions (unexplored actions may not discover new states)
        orderedActions.length,
        uniqueActions, // without start/end edges
        abstractActions, // clusters
        unxActions,
        uniqueStates + uniqueActions,
        abstractStates + abstractActions,
        // begin by fraalpe2
        longestPath,
        minCvg + "%", // min coverage
        maxCvg + "%", // max coverage
        // end by fraalpe2
        verdict == null ? "????" : verdict);
    report.append(stats + "\n");

    int[] grm = env.getGraphResumingMetrics();
    report.append("\n=== GRAPH RESUMING ===\n");
    report.append("Known states: " + grm[0] + "\n");
    report.append("Revisited states: " + grm[1] + "\n");
    report.append("New states: " + grm[2] + "\n");

    report.append("\n=== TEST GENERATOR ===\n");
    report.append("Name: " + Grapher.testGenerator + "\n");
    if (Grapher.testGenerator.equals(Grapher.QLEARNING_GENERATOR)) {
      report.append("DISCOUNT: " + Grapher.QLEARNING_DISCOUNT_PARAM + "\n");
      report.append("MAXREWARD: " + Grapher.QLEARNING_MAXREWARD_PARAM + "\n");
      String calibS = String.format("%1$8.2f , %2$10.2f , %3$10d , %4$10d , %5$11d , %6$11d , %7$11d , %8$20d , %9$20d , %10$11s , %11$6s , %12$6s",
          Grapher.QLEARNING_DISCOUNT_PARAM * 100,
          Grapher.QLEARNING_MAXREWARD_PARAM,
          uniqueStates,
          abstractStates,
          uniqueActions,
          abstractActions,
          orderedActions.length,
          uniqueStates + uniqueActions,
          abstractStates + abstractActions,
          // begin by fraalpe2
          longestPath,
          minCvg + "%", // min coverage
          maxCvg + "%"); // max coverage
      // end by fraalpe2
      report.append("CALIB. : " + calibS + "\n");
      report.append("format : discount x max_reward x unq_states x abs_states x unq_actions x abs_actions x exc_actions = unq_states_n_actions x abs_states_n_actions x longestPath x minCvg x maxCvg");
    }
		
		/*report.append("\n=== Stats report ===\n");

		double sequenceLength = orderedActions.length;
		int count;
		double frac, idealFrac = 1.0 / tGraph.edgeSet().size();
		double dist = 0;
		IGraphAction ga;
		for (String[] edge : tGraph.edgeSet()) {
			ga = env.getAction(edge[0]);
			count = ga.getCount();
			frac = count / sequenceLength;
			if (frac < idealFrac)
				dist += (count > 0 ? idealFrac / frac : 100 * (idealFrac / (1.0 / sequenceLength))) - 1.0; 
		}
		dist = Math.sqrt(dist);
		
		double edgeRepetition = sequenceLength / tGraph.edgeSet().size();				
		report.append("Edge repetition mean: " + edgeRepetition + "\n");
		report.append("Distance: " + dist +"\n");*/

    return report.toString();
  }
}
