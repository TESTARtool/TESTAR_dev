/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2015):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This graph project is distributed FREE of charge under the TESTAR license, as an open *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar.graph.reporting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fruit.alayer.Verdict;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.graph.GraphEdge;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.graph.TESTAREnvironment;
import es.upv.staq.testar.graph.TESTARGraph;

/**
 * Graoh reporting utility.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class GraphReporter {

	private static long usingGraphTime = -1;

	private static String testSequenceFolder = null;
	
	private static final String OUT_DIR = "output/graphs/";
	
	final static String THICK_PROPERTY = ", penwidth=3",
						CLUSTER_THICK_PROPERTY = ", penwidth=5";
	
	final static String WARNING_COLOR = "#FF7F00";
	
	private static String STYLE_NORMAL = ", style=solid",
						  STYLE_KNOWN = ", style=dotted",
						  STYLE_VERTEX_REVISIT = ", style=diagonals",
						  STYLE_EDGE_REVISIT = ", style=tapered, penwidth=3, arrowtail=none";

	private static HashMap<String,String> mapToAbstractStateIDs = new HashMap<String,String>(); // concrete -> abstract_R

	public static void useGraphData(long graphTime, String testSequencePath){		
		GraphReporter.testSequenceFolder = testSequencePath.replaceAll(".*(sequence[0-9]*)", "$1");		
		usingGraphTime = graphTime;
		(new File(OUT_DIR + testSequenceFolder)).mkdirs();
	}
		
	// [0] screenshots, [1] tiny, [2] minimal [3] abstract screenshoted, [4] abstract tiny, [5] abstract minimal
	private static String[] buildVertexGraph(IEnvironment env, TESTARGraph g){
		int unx = 1;
		String label;
		StringBuffer sb0 = new StringBuffer(), // screenshots
				     sb1 = new StringBuffer(), // tiny
				     sb2 = new StringBuffer(), // minimal
				     sb3 = new StringBuffer(), // abstract screenshoted
				     sb4 = new StringBuffer(), // abstract tiny
					 sb5 = new StringBuffer(), // abstract minimal
				     unxBuffer = new StringBuffer(),
				     unxBufferMinimal = new StringBuffer();
		IGraphState verdictVertex = null;
		Verdict verdict;
		int unxC;
		
		List<String> longestPath = g.getLongestPathArray();
		String thickS;
		
		HashMap<String,Set<String>> stateClusters = env.getGraphStateClusters();
		HashMap<String,Integer> statesCount = new HashMap<String,Integer>(g.vertexSet().size());
		HashMap<String,Integer> statesUnexploredCount = new HashMap<String,Integer>(g.vertexSet().size());
		HashMap<String,String> statesScreenshots = new HashMap<String,String>(g.vertexSet().size());
		
		boolean k, r;
		String color;
		
		for(IGraphState vertex : g.vertexStates()){
			if (vertex.toString().equals(Grapher.GRAPH_NODE_PASS) || vertex.toString().equals(Grapher.GRAPH_NODE_FAIL))
				verdictVertex = vertex;
			else if (vertex.toString().equals(Grapher.GRAPH_NODE_ENTRY))
				continue;
			else{

				statesCount.put(vertex.toString(),vertex.getCount());
				statesScreenshots.put(vertex.toString(),vertex.getStateshot());
				
				String nodeLabel = vertex.toString() + " (" + vertex.getCount() + ")";
				if (vertex.getStateshot() != null)
					label = "<<TABLE border=\"0\" cellborder=\"1\" color='#cccccc'><TR><TD><IMG SRC=\"" +
							vertex.getStateshot().replace("output","../..") +
							"\"/></TD></TR><TR><TD>" + nodeLabel +
							"</TD></TR></TABLE>>";
				else
					label = "\"" + nodeLabel + "\"";				
				
				if (longestPath.contains(vertex.getConcreteID()))
					thickS = THICK_PROPERTY;
				else
					thickS = "";
				
				k = vertex.knowledge(); r = vertex.revisited();
				
				color = getLineColor(vertex.getCount());
				
				sb0.append(vertex.toString() + " [label=" + label + thickS + (k ? (r ? STYLE_VERTEX_REVISIT : STYLE_KNOWN) : STYLE_NORMAL ) + ", color=" + color + "];\n");
				sb1.append(vertex.toString() + " [label=\"" + nodeLabel + "\"" + thickS + (k ? (r ? STYLE_VERTEX_REVISIT : STYLE_KNOWN) : STYLE_NORMAL ) + ", color=" + color +  "];\n");
				sb2.append(vertex.toString() + " [label=\"" + vertex.getCount() + "\", height=0.3" + thickS + (k ? (r ? STYLE_VERTEX_REVISIT : STYLE_KNOWN) : STYLE_NORMAL ) + ", color=" + color +  "];\n");

				verdict = vertex.getVerdict();
				if (verdict != null){
					String t1 = "verdict_" + vertex.toString() + " [color=\"" + WARNING_COLOR + "\", style=solid];\n",
						   t2 = vertex.toString() + " -> verdict_" + vertex.toString() + " [label=\"" + verdict.info() + "\", color=\"" + WARNING_COLOR + "\"];\n"; 
					sb0.append(t1);
					sb1.append(t1);
					sb2.append("verdict_" + vertex.toString() + " [color=\"" + WARNING_COLOR + "\", shape=point, height=0.3, style=solid, label=\"\"];\n");
					sb0.append(t2);
					sb1.append(t2);
					sb2.append(vertex.toString() + " -> verdict_" + vertex.toString() + " [label=\"\", color=\"" + WARNING_COLOR + "\"];\n");
				}
				
				unxC = vertex.getUnexploredActionsSize();
				if (unxC > 0){
					unxBuffer.append(vertex.toString() + " -> u" + unx + " [color=pink, fontcolor=red, label=\"unexplored(" + unxC + ")\", style=dashed];\n");
					unxBufferMinimal.append(vertex.toString() + " -> u" + unx + " [color=pink, fontcolor=red, label=\"" + unxC + "\", style=dashed];\n");
				}
				statesUnexploredCount.put(vertex.toString(), unxC);
			}
			unx++;
		}

		HashMap<String,Integer> unexploredClusterCount = new HashMap<String,Integer>(stateClusters.size());
		int clusterCount, unexploredCount;
		String scrshot;
		for (String cluster : stateClusters.keySet()){ // abstract_R
			clusterCount = 0;
			unexploredCount = 0;
			scrshot = null;
			k = false; r = false;
			for (String cs : stateClusters.get(cluster)){ // concrete
				mapToAbstractStateIDs.put(cs, cluster);
				clusterCount += statesCount.get(cs).intValue();
				unexploredCount += statesUnexploredCount.get(cs).intValue(); 
				if (statesScreenshots.get(cs) != null)
					scrshot = statesScreenshots.get(cs);
				statesScreenshots.remove(cs); // clean-up
				if (env.getState(cs).knowledge()) k = true; if (env.getState(cs).revisited()) r = true;
			}

			color = getLineColor(clusterCount);
			
			unexploredClusterCount.put(cluster,unexploredCount);
			if (clusterCount != 0){
				String toAppend = cluster + " [label=\"" + cluster + " (" + clusterCount + ")\"" +
						(k ? (r ? STYLE_VERTEX_REVISIT : STYLE_KNOWN) : STYLE_NORMAL) + ", color=" + color + CLUSTER_THICK_PROPERTY + "];\n";
				sb4.append(toAppend);
				sb5.append(cluster + " [label=\"" + clusterCount + "\"" + ", height=0.3" +
						(k ? (r ? STYLE_VERTEX_REVISIT : STYLE_KNOWN) : STYLE_NORMAL) + ", color=" + color + CLUSTER_THICK_PROPERTY + "];\n");
				if (scrshot == null)
					sb3.append(toAppend); // unable to retrieve any screenshot for the cluster
				else{
					String scrShotedLabel =
							"<<TABLE border=\"0\" cellborder=\"1\" color='#cccccc'><TR><TD><IMG SRC=\"" +
							scrshot.replace("output","../..") +
							"\"/></TD></TR><TR><TD>" + cluster + " (" + clusterCount + ")" +
							"</TD></TR></TABLE>>";
					sb3.append(cluster + " [label=" + scrShotedLabel + ", color=" + color + CLUSTER_THICK_PROPERTY + "];\n");
				}
			}
		}

		if (verdictVertex != null){
			String verdictColor = (verdictVertex.toString().equals(Grapher.GRAPH_NODE_PASS)) ? "green" : "red";
			String verdictFontColor = (verdictVertex.toString().equals(Grapher.GRAPH_NODE_PASS)) ? "black" : "white";
			String verdictNodeS = " [fixedsize=false, shape=doublecircle, style=filled, color=" + verdictColor + ", fontcolor=" + verdictFontColor + ", height=0.8];\n";
			sb0.append(verdictVertex + verdictNodeS);
			sb1.append(verdictVertex + verdictNodeS);
			sb2.append(verdictVertex + verdictNodeS);
			sb3.append(verdictVertex + verdictNodeS);
			sb4.append(verdictVertex + verdictNodeS);
			sb5.append(verdictVertex + verdictNodeS);
		}

		if (unxBuffer.length() > 0){
			String unxNodeS = "node [fixedsize=false, shape=ellipse, style=dashed, color=pink, fontcolor=pink, height=0.8];\n";
			String unxNodeMinimalS = "node [fixedsize=false, shape=point, style=solid, color=pink, fontcolor=pink, height=0.3];\n";
			sb0.append(unxNodeS);
			sb1.append(unxNodeS);
			sb2.append(unxNodeMinimalS);
			sb3.append(unxNodeS);
			sb4.append(unxNodeS);
			sb5.append(unxNodeMinimalS);
			sb0.append(unxBuffer.toString());
			sb1.append(unxBuffer.toString());
			sb2.append(unxBufferMinimal.toString());
			String unxLink; 
			for (String cluster : unexploredClusterCount.keySet()){
				unxLink = cluster + " -> u" + cluster + " [color=pink, fontcolor=red, label=\"unexplored(" + unexploredClusterCount.get(cluster).intValue() + ")\", style=dashed];\n";
				sb3.append(unxLink);
				sb4.append(unxLink);
				sb5.append(cluster + " -> u" + cluster + " [color=pink, fontcolor=red, label=\"" + unexploredClusterCount.get(cluster).intValue() + "\", style=dashed];\n");
			}
		}

		return new String[]{sb0.toString(),sb1.toString(),sb2.toString(),sb3.toString(),sb4.toString(),sb5.toString()};
	}
	
	private static String getLineColor(int count){
		if (count == 1)
			return "grey";
		else if (count < 5)
			return "black";
		else if (count < 10)
			return "blue";
		else if (count < 25)
			return "green";
		else if (count < 50)
			return "yellow";
		else
			return "red";
	}
	
	private static String breakInLines(String orderString){
		if (orderString.length() > CodingManager.ID_LENTGH * 5)
			return "[*]";
		StringBuffer brokenS = new StringBuffer();
		Pattern pattern = Pattern.compile("(\\[[0-9]+\\])");
		Matcher matcher = pattern.matcher(orderString);
		String group;
		final int LINE_LIMIT = 16;
		int lineC = CodingManager.ID_LENTGH + 1; // id + blank_space
		while (matcher.find()){
			group = matcher.group(1);
			brokenS.append(group);
			lineC += group.length();
			if (lineC >= LINE_LIMIT){
				brokenS.append("\n");
				lineC = 0;
			}
		}
		return brokenS.toString();
	}
	
	// [0] screenshots, [1] tiny, [2] minimal, [3] abstract screenshoted, [4] abstract tiny, [5] abstract minimal	
	private static String[] buildEdgeGraph(IEnvironment env, TESTARGraph g){
		String label, color, detailedS, linkLabel;
		StringBuffer sb0 = new StringBuffer(), sb1 = new StringBuffer(), sb2 = new StringBuffer(),
					 sb3 = new StringBuffer(), sb4 = new StringBuffer(), sb5 = new StringBuffer();
				
		HashMap<String,IGraphAction> actions = new HashMap<String,IGraphAction>(g.edgeSet().size());
		HashMap<String,Set<String>> actionClusters = env.getGraphActionClusters();

		boolean isStartAction, isStopAction;
		IGraphAction startAction = null, stopAction = null;
		
		boolean k, r;
		
		int edgeClusterCount;
		String edgeClusterOrder;
		boolean skipGraphDisplay;
		Set<String> itEdges = new HashSet<String>();

		Map<String,Set<IGraphAction>> actionsBySourceState = new HashMap<String,Set<IGraphAction>>(); // abstract source state ID x graph actions
		Map<String,Set<IGraphAction>> actionsByTargetState = new HashMap<String,Set<IGraphAction>>(); // abstract target state ID x graph actions
		String abstractSourceStateID, abstractTargetStateID;
		Set<IGraphAction> actionsSet, actionsSet2;

		for(IGraphAction graphAction : g.edgeActions()){
		/*IGraphAction graphAction;
		for (GraphEdge edge : g.edgeSet()){
			graphAction = env.getAction(edge.getActionID());*/

			if (graphAction.toString().equals(Grapher.GRAPH_ACTION_START)){
				startAction = graphAction;
				isStartAction = true;
				isStopAction = false;
			} else if (graphAction.toString().equals(Grapher.GRAPH_ACTION_STOP)){
				stopAction = graphAction;
				isStartAction = false;
				isStopAction = true;
			} else{
				isStartAction = false;
				isStopAction = false;
			}
			
			skipGraphDisplay = itEdges.contains(graphAction.getAbstractID());
			if (!skipGraphDisplay)
				itEdges.add(graphAction.getAbstractID());
			
			actions.put(graphAction.getConcreteID(), graphAction);
			
			if (skipGraphDisplay)
				continue;
			
			Set<String> targetStates = graphAction.getTargetStateIDs();
			for (String targetStateID : targetStates){
				
				String multiTargetVisuals = targetStates.size() == 1 ? "" : THICK_PROPERTY;
				
				String fromToS = g.getState(graphAction.getSourceStateID()) + " -> " + targetStateID;
				sb0.append(fromToS);
				sb1.append(fromToS);
				sb2.append(fromToS);
				//detailedS = ai.getDetailedName() == null ? "" : "\n{" + ai.getDetailedName() + "}";
				detailedS = "";
				edgeClusterCount = 0;
				edgeClusterOrder = "";
				if (actionClusters.containsKey(graphAction.getAbstractID())){
					for (String aid : actionClusters.get(graphAction.getAbstractID())){
						edgeClusterCount += g.getAction(aid).getCount();
						edgeClusterOrder += g.getAction(aid).getOrder(targetStateID);
					}
				}
				if (edgeClusterCount > 1){
					color = getLineColor(edgeClusterCount);
					linkLabel = "G_" + graphAction.getAbstractID() + breakInLines(edgeClusterOrder) + " (" + edgeClusterCount + ")";
				} else {
					color = getLineColor(graphAction.getCount());
					linkLabel = graphAction.getConcreteID() + " " + breakInLines(graphAction.getOrder(targetStateID)) + " (" + graphAction.getCount() + ")";
				}
				linkLabel += " " + detailedS;
				if (graphAction.getStateshot() != null)
					label = "<<TABLE border=\"0\" cellborder=\"1\" color='#cccccc'><TR><TD><IMG SRC=\"" +
							graphAction.getStateshot().replace("output","../..") +
						"\"/></TD></TR><TR><TD>" +
						linkLabel.replaceAll("\n","<br/>") +
						"</TD></TR></TABLE>>";
				else
					label = "\"" + linkLabel + "\"";						
	
				k = graphAction.knowledge(); r = graphAction.revisited();
				
				sb0.append(" [color=" + color + multiTargetVisuals + (k ? (r ? STYLE_EDGE_REVISIT : STYLE_KNOWN) : STYLE_NORMAL ) + ", label=" + label + "];\n");
				//label = label.replaceAll("(.*->.*)((?:\\n|<br/>)\\{(?s)[^\\}]*(?-s)\\})?(.*;)","$1$2"); // no detailed edges/actions
				sb1.append(" [color=" + color + multiTargetVisuals + (k ? (r ? STYLE_EDGE_REVISIT : STYLE_KNOWN) : STYLE_NORMAL ) + ", label=\"" + linkLabel + "\"];\n");
				sb2.append(" [color=" + color + multiTargetVisuals + (k ? (r ? STYLE_EDGE_REVISIT : STYLE_KNOWN) : STYLE_NORMAL ) + ", label=\"" +
						   (edgeClusterCount == 0 ? graphAction.getConcreteID() : edgeClusterCount) + "\"" + "];\n");
				
			}

			if (!isStartAction && !isStopAction){
				// actions clustering by abstract target state
				abstractSourceStateID = env.getState(graphAction.getSourceStateID()).getAbstract_R_ID();
				actionsSet = actionsBySourceState.get(abstractSourceStateID);
				if (actionsSet == null){
					actionsSet = new HashSet<IGraphAction>();
					actionsBySourceState.put(abstractSourceStateID, actionsSet);
				}
				actionsSet.add(graphAction);
				// actions clustering by abstract source state
				for (String tid : graphAction.getTargetStateIDs()){
					abstractTargetStateID = mapToAbstractStateIDs.get(tid);
					actionsSet = actionsByTargetState.get(abstractTargetStateID);
					if (actionsSet == null){
						actionsSet = new HashSet<IGraphAction>();
						actionsByTargetState.put(abstractTargetStateID, actionsSet);
					}
					actionsSet.add(graphAction);
				}
			}
			
		}
				
		int clusterCount;
		String clusterOrder;
		IGraphAction ga;
		Set<IGraphAction> actionsIntersect;
		// optimized: high population of cyclic actions in a cluster
		for (String abstractTID : actionsByTargetState.keySet()){
			actionsSet = actionsByTargetState.get(abstractTID);
			for (String abstractSID : actionsBySourceState.keySet()){
				actionsSet2 = actionsBySourceState.get(abstractSID);
				actionsIntersect = new HashSet<IGraphAction>(actionsSet);
				actionsIntersect.retainAll(actionsSet2); // source n target
				if (actionsIntersect.isEmpty()) continue;
				clusterCount = 0; clusterOrder = ""; k = false; r = false;
				for (IGraphAction gaBT : actionsIntersect){
					clusterCount += gaBT.getCount();
					clusterOrder += gaBT.getOrder(env.getGraphStateClusters().get(abstractTID));
					if (gaBT.knowledge()) k = true; if (gaBT.revisited()) r = true;
				}
				color = getLineColor(clusterCount);
				clusterOrder = breakInLines(clusterOrder);
				String toAppend = 
						abstractSID + // source state
						" -> " +
						abstractTID; // target state
				sb4.append(toAppend + " [color=" + color + ", label=" + "\"" + clusterOrder + " (" + clusterCount + ")\"" +
						(k ? (r ? STYLE_EDGE_REVISIT : STYLE_KNOWN) : STYLE_NORMAL) + CLUSTER_THICK_PROPERTY + "];\n");
				sb5.append(toAppend + " [color=" + color + ", label=" + "\"" + clusterOrder + " (" + clusterCount + ")\"" +
						(k ? (r ? STYLE_EDGE_REVISIT : STYLE_KNOWN) : STYLE_NORMAL) + CLUSTER_THICK_PROPERTY + "];\n");
				sb3.append(toAppend + " [color=" + color + ", label=" + "\"" + clusterOrder + " (" + clusterCount + ")\"" +
						(k ? (r ? STYLE_EDGE_REVISIT : STYLE_KNOWN) : STYLE_NORMAL) + CLUSTER_THICK_PROPERTY + "];\n");
				  // which screenshot for a cluster of actions?
				/*String scrshotLabel =
							"<<TABLE border=\"0\" cellborder=\"1\" color='#cccccc'><TR><TD><IMG SRC=\"" +
							scrshot.replace("output","../..") +
							"\"/></TD></TR><TR><TD>" +
							clusterCount + " " + clusterOrder.replaceAll("\n","<br/>") + " (" + clusterCount + ")" +
							"</TD></TR></TABLE>>";								
				sb3.append(toAppend + " [color=" + color + ", label=" + scrshotLabel + ", style=solid];\n");*/
			}
		}
		// clean up
		mapToAbstractStateIDs.clear();
		actionsBySourceState.clear();
		actionsByTargetState.clear();
		
		String toAppend;
		// START action
		if (startAction != null){
			for (String tid : startAction.getTargetStateIDs()){
				toAppend = 
						g.getState(startAction.getSourceStateID()).getAbstract_R_ID() + // source state
						" -> " +
						g.getState(tid).getAbstract_R_ID() + // target state							
						" [color=black, label=\"" + startAction.toString() + "\", style=solid];\n";
				sb3.append(toAppend);
				sb4.append(toAppend);
				sb5.append(toAppend);
			}
		}
		// STOP action
		if (stopAction != null){
			for (String tid : stopAction.getTargetStateIDs()){
				toAppend = 
						g.getState(stopAction.getSourceStateID()).getAbstract_R_ID() + // source state
						" -> " +
						g.getState(tid).getAbstract_R_ID() + // target state							
						" [color=black, label=\"" + stopAction.toString() + "\", style=solid];\n";
				sb3.append(toAppend);
				sb4.append(toAppend);
				sb5.append(toAppend);
			}
		}
		
		return new String[]{sb0.toString(),sb1.toString(),sb2.toString(),sb3.toString(),sb4.toString(),sb5.toString()};
	}
	
	// [0] = detailed/screenshoted, [1] = non screenshoted graph, [2] = abstracted, [3] = screenshoted and abstracted
	public static String[] buildGraph(IEnvironment env, TESTARGraph g){
		StringBuilder sb0 = new StringBuilder(), sb1 = new StringBuilder(), sb2 = new StringBuilder(),
					  sb3 = new StringBuilder(), sb4 = new StringBuilder(), sb5 = new StringBuilder();
		final String regularVertexStyle = "node [fixedsize=false, shape=rect, style=solid, color=black, fontcolor=black, height=0.8];\n";
		sb0.append("digraph TESTAR {\n");
		sb0.append("rankdir=LR;\n");
		sb0.append(Grapher.GRAPH_NODE_ENTRY + " [shape=point, height=0.3, style=solid, color=black];\n");
		sb0.append(regularVertexStyle);
		sb1.append(sb0.toString());
		sb2.append(sb0.toString());
		sb3.append(sb0.toString());
		sb4.append(sb0.toString());
		sb5.append(sb0.toString());

		String[] vertexGraph = buildVertexGraph(env,g);
		sb0.append(vertexGraph[0]);
		sb1.append(vertexGraph[1]);
		sb2.append(vertexGraph[2]);
		sb3.append(vertexGraph[3]);
		sb4.append(vertexGraph[4]);
		sb5.append(vertexGraph[5]);
		sb0.append(regularVertexStyle);
		sb1.append(regularVertexStyle);
		sb2.append(regularVertexStyle);
		sb3.append(regularVertexStyle);
		sb4.append(regularVertexStyle);
		sb5.append(regularVertexStyle);		
		String[] edgeGraph = buildEdgeGraph(env,g);
		sb0.append(edgeGraph[0]);
		sb1.append(edgeGraph[1]);
		sb2.append(edgeGraph[2]);
		sb3.append(edgeGraph[3]);
		sb4.append(edgeGraph[4]);
		sb5.append(edgeGraph[5]);
		
		sb0.append("}\n");
		sb1.append("}\n");
		sb2.append("}\n");
		sb3.append("}\n");
		sb4.append("}\n");
		sb5.append("}\n");
		
		return new String[]{sb0.toString(),sb1.toString(),sb2.toString(),sb3.toString(),sb4.toString(),sb5.toString()};
	}
	
	private static void dotConverter(String nullshotDotPath,
									 String scrshotedDotPath,
									 String scrshotedAbstractDotPath,
									 String minimalDotPath,
									 String abstractNullshotDotPath,
									 String minimalAbstractDotPath){
		String p1S = "dot.exe -Tsvg " + nullshotDotPath + " -o " + nullshotDotPath + ".svg",  // tiny
			   p2S = "dot.exe -Tsvg " + scrshotedDotPath + " -o " + scrshotedDotPath + ".svg", // screenshot
			   p3S = "dot.exe -Tsvg " + minimalDotPath + " -o " + minimalDotPath + ".svg", // minimal
			   p4S = "dot.exe -Tsvg " + abstractNullshotDotPath + " -o " + abstractNullshotDotPath + ".svg", // tiny abstract
			   p5S = "dot.exe -Tsvg " + minimalAbstractDotPath + " -o " + minimalAbstractDotPath + ".svg", // minimal abstract
			   p6S = "dot.exe -Tsvg " + scrshotedAbstractDotPath + " -o " + scrshotedAbstractDotPath + ".svg"; // screenshot abstract
		String outDirS = OUT_DIR + testSequenceFolder + "/";
		try {
			FileWriter w = new FileWriter(outDirS + "offline_graph_conversion.bat");
			w.write(p5S + "\n"); // minimal abstract
			w.write(p6S + "\n"); // screenshot abstract
			w.write(p2S + "\n"); // screenshot
			w.write(p3S + "\n"); // minimal
			w.write(p1S + "\n"); // tiny
			w.write(p4S + "\n"); // tiny abstract
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!Grapher.offlineGraphConversion){
			try {
				// save as SVG/PNG (tool: http://www.graphviz.org/)
				File outdirF = new File(outDirS);
				Process p1 = Runtime.getRuntime().exec(p1S, null, outdirF);
				Process p2 = Runtime.getRuntime().exec(p2S, null, outdirF);
				Process p3 = Runtime.getRuntime().exec(p3S, null, outdirF);
				Process p4 = Runtime.getRuntime().exec(p4S, null, outdirF);
				Process p5 = Runtime.getRuntime().exec(p5S, null, outdirF);
				Process p6 = Runtime.getRuntime().exec(p6S, null, outdirF);
				try {
					int p1Status = p1.waitFor();
					if (p1Status != 0)
						System.out.println("WARNING: dot2svg exit value = " + p1Status); // tiny
					int p2Status = p2.waitFor();
					if (p2Status != 0)
						System.out.println("WARNING: dot2svg (screenshoted) exit value = " + p2Status); // screenshot
					int p3Status = p3.waitFor();
					if (p3Status != 0)
						System.out.println("WARNING: dot2svg (minimal) exit value = " + p3Status); // minimal
					int p4Status = p4.waitFor();
					if (p4Status != 0)
						System.out.println("WARNING: dot2svg (abstract) exit value = " + p4Status); // tiny abstract	
					int p5Status = p5.waitFor();
					if (p5Status != 0)
						System.out.println("WARNING: dot2svg (abstract minimal) exit value = " + p5Status); // minimal abstract		
					int p6Status = p6.waitFor();
					if (p6Status != 0)
						System.out.println("WARNING: dot2svg (abstract screenshoted) exit value = " + p6Status); // screenshot abstract			
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// PNG version discarded (due to large explored spaces)
			} catch (IOException e) {
				System.out.println("Unable to convert graphs from .dot to .svg ... is tools\\graphviz-2.38\\release\\bin at PATH environment variable?");
			}
		}
	}
	
	/*private static String convertToMinimal(String nullshotStyleGraph){
		return nullshotStyleGraph.replaceAll("(.*)label=\".*[(](.*)[).*]\"(.*;)", "$1label=\"$2\"$3")				
				   				 .replaceAll("(.*)label=\"unexplored_.*\"(.*;)", "$1label=\"\"$2")
				   				 .replaceAll("shape=rect(.*)height=0.8];","shape=rect$1height=0.3];")
				   				 .replaceAll("shape=ellipse(.*)height=0.8];","shape=point$1height=0.3];");		
	}*/
			
	// TODO: RAM optimization for big graphs
	public static void saveGraph(IEnvironment env, TESTARGraph g){
		//System.out.print("\tExporting graph to xml ...");
		g.saveToXML(env, OUT_DIR + testSequenceFolder + "/" + "graph_" + usingGraphTime + ".xml");
		//System.out.println("\t... xml graph export finished!");
		
		//System.out.print("\tPopulating .dot contents ...");
		String[] graphString = buildGraph(env,g);
		//System.out.println("\t... .dot contents populated!");
		String scrshotedGraph = graphString[0],
			   scrshotedAbstractGraph = graphString[3];		
		String nullshotGraph = graphString[1];
		String abstractNullshotGraph = graphString[4];	
		//System.out.print("\tConverting .dot contents to minimal ...");
		String minimalGraph = graphString[2], //convertToMinimal(nullshotGraph),
			   minimalAbstractGraph = graphString[5]; //convertToMinimal(abstractNullshotGraph);
		//System.out.println("\t... minimal conversion done!");
						
		PrintWriter writer;
        final String scrshotedDotPath = "graph_" + usingGraphTime + "_scrshoted.dot",
        			 scrshotedAbstractDotPath = "graph_" + usingGraphTime + "_scrshoted_abstract.dot";
        final String nullshotDotPath = "graph_" + usingGraphTime + "_tiny.dot";
        final String abstractNullshotDotPath = "graph_" + usingGraphTime + "_tiny_abstract.dot";
        final String minimalDotPath = "graph_" + usingGraphTime + "_minimal.dot",
        		     minimalAbstractDotPath = "graph_" + usingGraphTime + "_minimal_abstract.dot";
        final String encoding = Charset.defaultCharset().name();
		try {
			writer = new PrintWriter(OUT_DIR + testSequenceFolder + "/" + scrshotedDotPath, encoding);
			writer.println(scrshotedGraph);
			writer.close();
			writer = new PrintWriter(OUT_DIR + testSequenceFolder + "/" + scrshotedAbstractDotPath, encoding);
			writer.println(scrshotedAbstractGraph);
			writer.close();
			writer = new PrintWriter(OUT_DIR + testSequenceFolder + "/" + nullshotDotPath, encoding);
			writer.println(nullshotGraph);
			writer.close();
			writer = new PrintWriter(OUT_DIR + testSequenceFolder + "/" + abstractNullshotDotPath, encoding);
			writer.println(abstractNullshotGraph);
			writer.close();			
			writer = new PrintWriter(OUT_DIR + testSequenceFolder + "/" + minimalDotPath, encoding);
			writer.println(minimalGraph);
			writer.close();
			writer = new PrintWriter(OUT_DIR + testSequenceFolder + "/" + minimalAbstractDotPath, encoding);
			writer.println(minimalAbstractGraph);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Thread t = new Thread(){
			public void run(){
				dotConverter(nullshotDotPath, scrshotedDotPath, scrshotedAbstractDotPath, minimalDotPath, abstractNullshotDotPath, minimalAbstractDotPath);
			}
		};
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}
	
	 // null or: [0] = clusters, [1] = test table, [2] = exploration curve, [3] = UI exploration data
	private static String[] reportGraphStats(IEnvironment env, TESTARGraph tGraph, int firstSequenceActionNumber){
		String[] report = new String[4];
		
		report[0] = ReportPages.getClustersPageReport(env);

		GraphEdge[] orderedActions = env.getSortedActionsByOrder(Integer.MIN_VALUE, Integer.MAX_VALUE);
		int SEQUENCE_LENGTH = (int)Math.log10((double)orderedActions.length) + 1;
		if (SEQUENCE_LENGTH < 4)
			SEQUENCE_LENGTH = 4; // minimum column width
		
		report[1] = ReportPages.getTestTablePageReport(env,tGraph,orderedActions,SEQUENCE_LENGTH,firstSequenceActionNumber);
		report[2] = ReportPages.getExplorationCurvePageReport(env,tGraph,SEQUENCE_LENGTH,firstSequenceActionNumber);
		report[3] = ReportPages.getStatsPageReport(env,tGraph,orderedActions,SEQUENCE_LENGTH);
		
		return report;
	}
	
	 // null or: [0] = clusters, [1] = test table, [2] = exploration curve, [3] = UI exploration data
	public static String[] getReport(TESTAREnvironment env, TESTARGraph g, int firstSequenceActionNumber){
		if (g.vertexSet().isEmpty() || g.edgeSet().isEmpty())
			return null; // empty graph
		else if (!mapToAbstractStateIDs.isEmpty()){
			System.out.println("WARNING - Last report not finished? Doing cleanup");
			mapToAbstractStateIDs.clear();
		}

		//System.out.println("\tWill save graphs ...");
		saveGraph(env,g);
		//System.out.println("\t... graphs saved!");
		
		//System.out.print("\tWill generate graph report ...");
		return reportGraphStats(env,g, firstSequenceActionNumber);
		//System.out.println("\t... graph report generated!");
					
	}
	
}
