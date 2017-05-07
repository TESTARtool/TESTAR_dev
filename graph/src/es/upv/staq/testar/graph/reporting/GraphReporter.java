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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fruit.alayer.Verdict;
import org.fruit.alayer.actions.BriefActionRolesMap;

import es.upv.staq.testar.CodingManager;
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
			
	public static void useGraphData(long graphTime, String testSequencePath){		
		GraphReporter.testSequenceFolder = testSequencePath.replaceAll(".*(sequence[0-9]*)", "$1");		
		usingGraphTime = graphTime;
		(new File(OUT_DIR + testSequenceFolder)).mkdirs();
	}
	
	private static HashMap<String,String> mapToAbstractStateIDs = new HashMap<String,String>();
	
	private static String dashedS = ", style=dashed",
						  dottedS = ", style=dotted";
	
	// [0] screenshots, [1] tiny, [2] minimal [3] abstract screenshoted, [4] abstract tiny, [5] abstract minimal
	private static String[] buildVertexGraph(IEnvironment env, TESTARGraph g){
		//mapToAbstractStateIDs.clear();
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
		
		final String THICK_PROPERTY = ", penwidth=3";
		List<IGraphState> longestPath = g.getLongestPathArray();
		String thickS;
		
		HashMap<String,Set<String>> stateClusters = env.getGraphStateClusters();
		HashMap<String,Integer> statesCount = new HashMap<String,Integer>(g.vertexSet().size());
		HashMap<String,Integer> statesUnexploredCount = new HashMap<String,Integer>(g.vertexSet().size());
		HashMap<String,String> statesScreenshots = new HashMap<String,String>(g.vertexSet().size());
		
		boolean k, r;
		
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
				
				if (longestPath.contains(vertex))
					thickS = THICK_PROPERTY;
				else
					thickS = "";
				
				k = vertex.knowledge(); r = vertex.revisited();
				
				sb0.append(vertex.toString() + " [label=" + label + thickS + "];\n");
				sb1.append(vertex.toString() + " [label=\"" + nodeLabel + "\"" + thickS + "];\n");
				sb2.append(vertex.toString() + " [label=\"" + vertex.getCount() + "\", height=0.3" + thickS + (k ? (r ? dottedS : dashedS) : "" ) + "];\n");

				verdict = vertex.getVerdict();
				if (verdict != null){
					String t1 = "verdict_" + vertex.toString() + " [color=\"#ffff00\", style=solid];\n",
						   t2 = vertex.toString() + " -> verdict_" + vertex.toString() + " [label=\"" + verdict.info() + "\", color=\"#ffff00\"];\n"; 
					sb0.append(t1);
					sb1.append(t1);
					sb2.append("verdict_" + vertex.toString() + " [color=\"#ffff00\", shape=point, height=0.3, style=solid, label=\"\"];\n");
					sb0.append(t2);
					sb1.append(t2);
					sb2.append(vertex.toString() + " -> verdict_" + vertex.toString() + " [label=\"\", color=\"#ffff00\"];\n");
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
		for (String cluster : stateClusters.keySet()){
			clusterCount = 0;
			unexploredCount = 0;
			scrshot = null;
			for (String cs : stateClusters.get(cluster)){
				mapToAbstractStateIDs.put(cs, cluster);
				clusterCount += statesCount.get(cs).intValue();
				unexploredCount += statesUnexploredCount.get(cs).intValue(); 
				if (statesScreenshots.get(cs) != null)
					scrshot = statesScreenshots.get(cs);
				statesScreenshots.remove(cs); // clean-up
			}
			unexploredClusterCount.put(cluster,unexploredCount);
			if (clusterCount != 0){
				String toAppend = cluster + " [label=\"" + cluster + " (" + clusterCount + ")\"" + "];\n";
				sb4.append(toAppend);
				sb5.append(cluster + " [label=\"" + clusterCount + "\"" + ", height=0.3];\n");
				if (scrshot == null)
					sb3.append(toAppend); // unable to retrieve any screenshot for the cluster
				else{
					String scrShotedLabel =
							"<<TABLE border=\"0\" cellborder=\"1\" color='#cccccc'><TR><TD><IMG SRC=\"" +
							scrshot.replace("output","../..") +
							"\"/></TD></TR><TR><TD>" + cluster + " (" + clusterCount + ")" +
							"</TD></TR></TABLE>>";
					sb3.append(cluster + " [label=" + scrShotedLabel + "];\n");
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
		HashMap<IGraphAction,Integer> actionsCount = new HashMap<IGraphAction,Integer>();
		HashMap<IGraphAction,String> actionsOrder = new HashMap<IGraphAction,String>(); 
		HashMap<String,String> actionsScreenshots = new HashMap<String,String>(g.edgeSet().size());
		IGraphAction startAction = null, stopAction = null;
		
		boolean k, r;
		
		int edgeClusterCount;
		String edgeClusterOrder;
		boolean skipGraphDisplay;
		Set<String> itEdges = new HashSet<String>();
		
		for(IGraphAction edge : g.edgeActions()){

			if (edge.toString().equals(Grapher.GRAPH_ACTION_START))
				startAction = edge;
			else if (edge.toString().equals(Grapher.GRAPH_ACTION_STOP))
				stopAction = edge;

			skipGraphDisplay = itEdges.contains(edge.getAbstractID());
			if (!skipGraphDisplay)
				itEdges.add(edge.getAbstractID());
			
			actions.put(edge.getConcreteID(), edge);
			actionsCount.put(edge, edge.getCount());
			actionsOrder.put(edge, edge.getOrder());
			actionsScreenshots.put(edge.toString(), edge.getStateshot());
			
			if (skipGraphDisplay)
				continue;
			
			sb0.append(g.getState(g.getEdgeSource(edge.getConcreteID())) + " -> " + g.getState(g.getEdgeTarget(edge.getConcreteID())));
			sb1.append(g.getState(g.getEdgeSource(edge.getConcreteID())) + " -> " + g.getState(g.getEdgeTarget(edge.getConcreteID())));
			sb2.append(g.getState(g.getEdgeSource(edge.getConcreteID())) + " -> " + g.getState(g.getEdgeTarget(edge.getConcreteID())));
			//detailedS = ai.getDetailedName() == null ? "" : "\n{" + ai.getDetailedName() + "}";
			detailedS = "";
			edgeClusterCount = 0;
			edgeClusterOrder = "";
			if (actionClusters.containsKey(edge.getAbstractID())){
				for (String aid : actionClusters.get(edge.getAbstractID())){
					edgeClusterCount += g.getAction(aid).getCount();
					edgeClusterOrder += g.getAction(aid).getOrder();
				}
			}
			if (edgeClusterCount > 1){
				color = getLineColor(edgeClusterCount);
				linkLabel = "G_" + edge.getAbstractID() + breakInLines(edgeClusterOrder) + " (" + edgeClusterCount + ")";
			} else {
				color = getLineColor(edge.getCount());
				linkLabel = edge.getConcreteID() + " " + breakInLines(edge.getOrder()) + " (" + edge.getCount() + ")";
			}
			linkLabel += " " + detailedS;
			if (edge.getStateshot() != null)
				label = "<<TABLE border=\"0\" cellborder=\"1\" color='#cccccc'><TR><TD><IMG SRC=\"" +
						edge.getStateshot().replace("output","../..") +
					"\"/></TD></TR><TR><TD>" +
					linkLabel.replaceAll("\n","<br/>") +
					"</TD></TR></TABLE>>";
			else
				label = "\"" + linkLabel + "\"";						
			sb0.append(" [color=" + color + ", label=" + label + ", style=solid];\n");

			k = edge.knowledge(); r = edge.revisited();
			
			//label = label.replaceAll("(.*->.*)((?:\\n|<br/>)\\{(?s)[^\\}]*(?-s)\\})?(.*;)","$1$2"); // no detailed edges/actions
			sb1.append(" [color=" + color + ", label=\"" + linkLabel + "\", style=solid];\n");
			sb2.append(" [color=" + color + ", label=\"" +
					   (edgeClusterCount == 0 ? edge.getConcreteID() : edgeClusterCount) +
					   "\", style=solid" + (k ? (r ? dottedS : dashedS) : "") + "];\n");

		}
				
		int clusterCount;
		String clusterOrder, targetStateID;
		IGraphAction ga;
		Set<IGraphAction> actionsBT;
		HashMap<String,Set<IGraphAction>> actionsByTarget = new HashMap<String,Set<IGraphAction>>();
		String scrshot;
		for (String cluster : actionClusters.keySet()){ // pending optimization: high population of cyclic actions in a cluster
			actionsByTarget.clear();
			scrshot = null;
			for (String ca : actionClusters.get(cluster)){
				ga = actions.get(ca);
				targetStateID = mapToAbstractStateIDs.get(g.getState(g.getEdgeTarget(ga.getConcreteID())).toString());
				actionsBT = actionsByTarget.get(targetStateID);
				if (actionsBT == null){
					actionsBT = new HashSet<IGraphAction>();
					actionsByTarget.put(targetStateID, actionsBT);
				}
				actionsBT.add(ga);				
				if (actionsScreenshots.get(ca.toString()) != null)
					scrshot = actionsScreenshots.get(ca.toString());
				actionsScreenshots.remove(ca.toString()); // clean-up
			}
			for (String gsID : actionsByTarget.keySet()){
				clusterCount = 0;
				clusterOrder = "";
				actionsBT = actionsByTarget.get(gsID);
				for (IGraphAction graphAction : actionsBT){
					clusterCount += actionsCount.get(graphAction).intValue();
					clusterOrder += actionsOrder.get(graphAction);
				}
				clusterOrder = breakInLines(clusterOrder);
				ga = actionsBT.iterator().next(); // any action from the set
				color = getLineColor(clusterCount);
				String toAppend = 
						g.getState(g.getEdgeSource(ga.getConcreteID())).getAbstract_R_ID() + // source state
						" -> " +
						g.getState(g.getEdgeTarget(ga.getConcreteID())).getAbstract_R_ID() + // target state
						" [color=" + color + ", label=" + "\"" + cluster + " " + clusterOrder + " (" + clusterCount + ")\", style=solid];\n";
				sb4.append(toAppend);
				sb5.append(g.getState(g.getEdgeSource(ga.getConcreteID())).getAbstract_R_ID() + // source state
						   " -> " +
						   g.getState(g.getEdgeTarget(ga.getConcreteID())).getAbstract_R_ID() + // target state
						   " [color=" + color + ", label=" + "\"" + clusterCount + "\", style=solid];\n");
				if (scrshot == null)
					sb3.append(toAppend); // unable to retrieve any screenshot from cluster actions
				else {
					String scrshotLabel =
								"<<TABLE border=\"0\" cellborder=\"1\" color='#cccccc'><TR><TD><IMG SRC=\"" +
								scrshot.replace("output","../..") +
								"\"/></TD></TR><TR><TD>" +
								cluster + " " + clusterOrder.replaceAll("\n","<br/>") + " (" + clusterCount + ")" +
								"</TD></TR></TABLE>>";
							
					sb3.append(
							g.getState(g.getEdgeSource(ga.getConcreteID())).getAbstract_R_ID() + // source state
							" -> " +
							g.getState(g.getEdgeTarget(ga.getConcreteID())).getAbstract_R_ID() + // target state
							" [color=" + color + ", label=" + scrshotLabel + ", style=solid];\n"
					);
				}
			}
		}
		mapToAbstractStateIDs.clear(); // clean-up
		String toAppend;
		// START action
		if (startAction != null){
			toAppend = 
					g.getState(g.getEdgeSource(startAction.getConcreteID())).getAbstract_R_ID() + // source state
					" -> " +
					g.getState(g.getEdgeTarget(startAction.getConcreteID())).getAbstract_R_ID() + // target state							
					" [color=black, label=\"" + startAction.toString() + "\", style=solid];\n";
			sb3.append(toAppend);
			sb4.append(toAppend);
			sb5.append(toAppend);
		}
		// STOP action
		if (stopAction != null){
			toAppend = 
					g.getState(g.getEdgeSource(stopAction.getConcreteID())).getAbstract_R_ID() + // source state
					" -> " +
					g.getState(g.getEdgeTarget(stopAction.getConcreteID())).getAbstract_R_ID() + // target state							
					" [color=black, label=\"" + stopAction.toString() + "\", style=solid];\n";
			sb3.append(toAppend);
			sb4.append(toAppend);
			sb5.append(toAppend);
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
		g.saveToXML(OUT_DIR + testSequenceFolder + "/" + "graph_" + usingGraphTime + ".xml");
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
	
	private static String reportGraphStats(IEnvironment env, TESTARGraph tGraph){
		StringBuffer report = new StringBuffer();
		
		report.append("\n-- TESTAR graph report ... --\n");

		report.append(reportClusters(env));

		IGraphAction[] orderedActions = env.getSortedActionsByOrder(Integer.MIN_VALUE, Integer.MAX_VALUE);
		int SEQUENCE_LENGTH = (int)Math.log10((double)orderedActions.length) + 1;
		if (SEQUENCE_LENGTH < 4)
			SEQUENCE_LENGTH = 4; // minimum column width
		reportDetailed(report,env,tGraph,orderedActions,SEQUENCE_LENGTH);
		reportSummary(report,env,tGraph,orderedActions,SEQUENCE_LENGTH);

		report.append("\n\n-- ... report end --");		
		
		return report.toString();
	}
	
	private static String reportClusters(IEnvironment env){
		StringBuffer report = new StringBuffer();

		final int CLUSTER_MEMBERS_PERLINE = 8;
		
		report.append("\n-- States clustering (abstract -> concrete) --\n");
		HashMap<String,Set<String>> absStateGroups = env.getGraphStateClusters();
		int gi = 1, i, nlc;
		for (String g : absStateGroups.keySet()){
			report.append("  Cluster (" + gi++ + ") " + g + " contains:\n\t");
			i = 1; nlc = 0;
			for (String s : absStateGroups.get(g)){
				nlc++;
				report.append("(" + i++ + ") " + s + (nlc % CLUSTER_MEMBERS_PERLINE == 0 ? "\n\t" : " "));
			}
			report.append(nlc % CLUSTER_MEMBERS_PERLINE != 0 ? "\n" : "");
		}

		report.append("\n-- Actions clustering (abstract -> concrete) --\n");
		absStateGroups = env.getGraphActionClusters();
		gi = 1;
		for (String g : absStateGroups.keySet()){
			report.append("  Cluster (" + gi++ + ") " + g + " contains:\n\t");
			i = 1; nlc = 0;
			for (String s : absStateGroups.get(g)){
				nlc++;
				report.append("(" + i++ + ") " + s + (nlc % CLUSTER_MEMBERS_PERLINE == 0 ? "\n\t" : " "));
			}
			report.append(nlc % CLUSTER_MEMBERS_PERLINE != 0 ? "\n" : "");
		}
		
		return report.toString();
	}
	
	private static void reportDetailed(StringBuffer report, IEnvironment env, TESTARGraph tGraph,
									   IGraphAction[] orderedActions, int SEQUENCE_LENGTH){
		report.append("\n=== Ordered test sequence actions list ===\n");
		report.append("\n\tACTION_TYPES:\n");
		for (String actionRole : BriefActionRolesMap.map.keySet())
			report.append("\t\t" + BriefActionRolesMap.map.get(actionRole) + " = " + actionRole + "\n");
		int ID_LENGTH = CodingManager.ID_LENTGH;
		String tableHead = String.format("%1$" + SEQUENCE_LENGTH + 
										 "s %2$7" + // RAM //"s %2$" + SEQUENCE_LENGTH +
										 "s %3$11" + // CPU (user)
										 "s %4$11" + // CPU (system)
										 "s %5$6" + // CPU %
										 "s %6$" + ID_LENGTH +
										 "s %7$" + SEQUENCE_LENGTH +
										 "s %8$" + ID_LENGTH +
										 "s %9$" + SEQUENCE_LENGTH +
										 "s %10$" + ID_LENGTH +
										 "s %11$" + SEQUENCE_LENGTH +
										 "s %12$s",
				"#",
				"RAM(KB)", //"Sync",
				"CPUser(ms)",
				"CPUsys(ms)",
				"CPU(%)",
				"FROM",
				"x",
				"TO",
				"x",
				"C_ACTION",
				"x",
				"ACTION_TYPE ( (WIDGET,ROLE,TITLE)[parameter*] )+ ");
		for (int i=0; i<tableHead.length(); i++)
			report.append("-");
		report.append("\n" + tableHead + "\n");
		for (int i=0; i<tableHead.length(); i++)
			report.append("-");
		report.append("\n");
		
		int i=1;
		long[] cpu;
		IGraphState from, to;
		List<Integer> movesSync = Grapher.getMovementsSync();
		for (IGraphAction edge : orderedActions){
			if (edge.knowledge()) continue;
			cpu = edge.getCPUsage();
			from = env.getSourceState(edge);
			to =  env.getTargetState(edge);
			String actionList = String.format("%1$" + SEQUENCE_LENGTH +
											  "d %2$7" + //"d %2$" + SEQUENCE_LENGTH +
											  "d %3$11" + // CPU (user)
											  "d %4$11" + // CPU (system)
											  "d %5$6" + // CPU %
											  "s %6$" + ID_LENGTH +
											  "s %7$" + SEQUENCE_LENGTH +
											  "d %8$" + ID_LENGTH +
											  "s %9$" + SEQUENCE_LENGTH +
											  "s %10$" + ID_LENGTH +
											  "s %11$" + SEQUENCE_LENGTH +
											  "d %12$s",
					i,
					edge.getMemUsage(), //movesSync.get(i-1),
					cpu[0], // user
					cpu[1], // system
					String.format("%.2f", (cpu[0] + cpu[1]) / (double)cpu[2] * 100), // cpu %
					from.getConcreteID(),
					from.getCount(),
					to.getConcreteID(),
					to.getCount(),
					edge.getConcreteID(),
					edge.getCount(),
					edge.getDetailedName());
			report.append(actionList + "\n");
			i++;
		}
	}
	
	private static void reportSummary(StringBuffer report, IEnvironment env, TESTARGraph tGraph,
									  IGraphAction[] orderedActions, int SEQUENCE_LENGTH){
		int unxStates = 0, unxActions = 0,
			totalStates = -1; // discard start node
		String verdict = null;
		for (IGraphState vertex : tGraph.vertexStates()){
			if (vertex.getUnexploredActionsSize() > 0)
				unxStates++;
			unxActions += vertex.getUnexploredActionsSize();
			if (vertex.toString().equals(Grapher.GRAPH_NODE_FAIL))
				verdict = Grapher.GRAPH_NODE_FAIL;
			else if (vertex.toString().equals(Grapher.GRAPH_NODE_PASS))
				verdict = Grapher.GRAPH_NODE_PASS;
			else
				totalStates += vertex.getCount();
		}
		
		report.append("\n=== Exploration curve ===\n");
		String headerS = String.format("%1$22s %2$16s %3$16s",
			"________________UNIQUE",
			"________ABSTRACT",
			"___________TOTAL");
		report.append(headerS + "\n");
		String explorationCurveS = String.format("%1$" + SEQUENCE_LENGTH + "s, %2$6s, %3$7s, %4$6s, %5$7s, %6$6s, %7$8s %8$18s %9$11s %10$6s %11$6s",
			"#",
			"states",
			"actions",
			"states",
			"actions",
			"unique",
			"abstract",
			"unexplored actions",
			"longestPath",
			"minCvg",
			"maxCvg");
		report.append(explorationCurveS + "\n");
		int idx = 1;
		String sampleS;
		List<int[]> explorationCurve = env.getExplorationCurve();
		for (int[] sample : explorationCurve){
			sampleS = String.format("%1$" + SEQUENCE_LENGTH + "s, %2$6d, %3$7d, %4$6d, %5$7d, %6$6d, %7$8d %8$18d %9$11d %10$6s %11$6s",
				idx++, sample[0], sample[1], sample[2], sample[3],
				sample[0] + sample[1], sample[2] + sample[3], sample[4], sample[5], sample[6]+"%", sample[7]+"%");
			report.append(sampleS + "\n");
			if (idx % 10 == 0)
				report.append("---------------------------------------------------------------------------\n");
		}
		
		report.append("\n=== SUT UI space explored ===\n");
		String statsMetahead = String.format("%1$27s %2$27s %3$17s",
				"______________________________STATES",
				"_____________________________ACTIONS",
				"____________TOTAL");
		report.append(statsMetahead + "\n");
		//String statsHead = String.format("%1$5s, %2$6s, %3$8s, %4$10s, %5$5s, %6$6s, %7$8s, %8$10s, %9$6s, %10$8s, ... %11$7s",
		String statsHead = String.format("%1$5s, %2$6s, %3$8s, %4$10s, %5$5s, %6$6s, %7$8s, %8$10s, %9$6s, %10$8s, %11$11s, %12$6s, %13$6s ... %14$7s", // by fraalpe2				
				
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
				"longestPath",
				"minCvg",
				"maxCvg",	
				// end by fraalpe2
				"VERDICT");		
		report.append(statsHead + "\n");
		int uniqueStates = tGraph.vertexSet().size() - 2,
			uniqueActions = tGraph.edgeSet().size() - 2,
			abstractStates = env.getGraphStateClusters().size(),
			longestPath = env.getLongestPathLength(),
			abstractActions = env.getGraphActionClusters().size();
		
		if (Grapher.testGenerator.equals(Grapher.EVOLUTIONARY_GENERATOR) &&
			verdict.equals(Grapher.GRAPH_NODE_PASS)){ // temporal fitness.txt patch for evolutionary algorithm
			try{
				java.io.Writer fitnessWriter = new java.io.FileWriter("output/fitness.txt");
				//fitnessWriter.write(new Double(abstractStates == 0 ? 2.0 : 1.0/(double)abstractStates).toString()); // fitness = 0.0 .. 1.0 (0 is best)
				fitnessWriter.write(new Double(abstractStates == 0 ? 0.0 : (double)abstractStates).toString()); // fitness = numero estados abstractos (higher is better)
				fitnessWriter.close();
			} catch(Exception e){}
		}
		
		double[] cvgMetrics = env.getCoverageMetrics(); // min x max coverage
		String minCvg = String.format("%.2f", cvgMetrics[0]), // min coverage
			   maxCvg = String.format("%.2f", cvgMetrics[1]); // max coverage
		//String stats = String.format("%1$5s, %2$6s, %3$8s, %4$10s, %5$5s, %6$6s, %7$8s, %8$10s, %9$6s, %10$8s, ... %11$7s",	
		String stats = String.format("%1$5s, %2$6s, %3$8s, %4$10s, %5$5s, %6$6s, %7$8s, %8$10s, %9$6s, %10$8s, %11$11s, %12$6s, %13$6s ... %14$7s", // by fraalpe2				
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
		report.append(stats + " -STATS\n");		
		
		int[] grm = env.getGraphResumingMetrics();
		report.append("\n=== Graph resuming data ===\n");
		report.append("Known states: " + grm[0] + "\n");
		report.append("Revisited states: " + grm[1] + "\n");
		report.append("New states: " + grm[2] + "\n");
		
		report.append("\n=== Test generator ===\n");
		report.append("Name: " + Grapher.testGenerator + "\n");
		if (Grapher.testGenerator.equals(Grapher.QLEARNING_GENERATOR)){
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
		for (IGraphAction ga : tGraph.edgeSet()){
			count = ga.getCount();;
			frac = count / sequenceLength;
			if (frac < idealFrac)
				dist += (count > 0 ? idealFrac / frac : 100 * (idealFrac / (1.0 / sequenceLength))) - 1.0; 
		}
		//dist = Math.sqrt(dist);
		
		double E = sequenceLength / tGraph.edgeSet().size();				
		report.append("Mean: " + E + "\n");
		report.append("Distance: " + dist +"\n");*/		
	}	
	
	public static String PrintResults(TESTAREnvironment env, TESTARGraph g){
		if (g.vertexSet().isEmpty() || g.edgeSet().isEmpty())
			return "EMPTY GRAPH";

		//System.out.println("\tWill save graphs ...");
		saveGraph(env,g);
		//System.out.println("\t... graphs saved!");
		
		//System.out.print("\tWill generate graph report ...");
		String report = reportGraphStats(env,g);
		//System.out.println("\t... graph report generated!");

		/*String reportPath = OUT_DIR + testSequenceFolder + "/" + 
				 			"graph_" + usingGraphTime + "_report.txt";
		try {
			PrintWriter writer = new PrintWriter(reportPath, "UTF-8");
			writer.println(report);
			writer.close();
			System.out.println("Graph report saved to: " + reportPath);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
					
		return report;
	}
	
}
