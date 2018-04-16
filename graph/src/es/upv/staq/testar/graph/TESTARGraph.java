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


package es.upv.staq.testar.graph;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jgrapht.graph.DirectedPseudograph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.upv.staq.testar.ProgressFileInputStream;
import es.upv.staq.testar.serialisation.LogSerialiser;

/**
 * A directed pseudo graph (loops and multiple edges) that represents TESTAR tests.
 * Edges can be n-ary, from one source state to n-target states.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class TESTARGraph extends DirectedPseudograph<String, GraphEdge> { // state concrete ID x < action concrete ID , target state concrete ID >

	private static final long serialVersionUID = -6766749840561297953L;

	private Map<String,IGraphState> graphStates; // state concreteID -> graph state
	private Map<String,IGraphAction> graphActions; // action concreteID -> graph action	
	
	private HashMap<String,Set<String>> stateClusters; // abstract_R -> Set<concrete>
	private HashMap<String,Set<String>> actionClusters; // abstract -> Set<concrete>

	private List<GraphEdge> orderedSequenceActions; // < action concrete ID , target state concrete ID >
	private int actionOrder = -1;

	private List<String> currentPath, // states ID
						 longestPath = null; // how deep from START graph state?
	
	private static String syncXMLGraph = "";

	public static final String XML_GRAPH_VERSION = "1.0.20160331",
							   XML_TAG_TESTAR_GRAPH = "TESTAR_GRAPH",
							   XML_TAG_GRAPH_STATES = "graph_states",
							   XML_TAG_GRAPH_ACTIONS = "graph_actions",
							   XML_TAG_STATE = "state",
							   XML_TAG_ACTION = "action",
							   XML_ATTRIBUTE_STATE_ID = "id", // concrete id
							   XML_ATTRIBUTE_STATE_ABS_ID = "aid", // abstract id
							   XML_ATTRIBUTE_STATE_VISITED = "visited",
							   XML_ATTRIBUTE_STATE_WIDGET_COUNT = "wcount",
							   XML_ATTRIBUTE_STATE_WIDGETS = "widgets",
							   XML_ATTRIBUTE_STATE_UNEXECUTED = "unexecuted",
							   XML_ATTRIBUTE_ACTION_ORDER = "order",
							   XML_ATTRIBUTE_ACTION_ID = "id", // concrete id
							   XML_ATTRIBUTE_ACTION_ABS_ID = "aid", // abstract id
							   XML_ATTRIBUTE_ACTION_VISITED = "visited",
							   XML_ATTRIBUTE_ACTION_FROM = "from",
							   XML_ATTRIBUTE_ACTION_TO = "to";
	
	private TESTARGraph() {
		super(GraphEdge.class);
		this.graphStates = new HashMap<String,IGraphState>();
		this.graphActions = new HashMap<String,IGraphAction>();
		this.stateClusters = new HashMap<String,Set<String>>();
		this.actionClusters = new HashMap<String,Set<String>>();
		orderedSequenceActions = new ArrayList<GraphEdge>();
		actionOrder = 0;
		currentPath = new ArrayList<String>();
		longestPath = new ArrayList<String>();

		//buildSampleGraph();

	}

	public static TESTARGraph buildEmptyGraph(){
		return new TESTARGraph();		
	}

	private void buildSampleGraph(){
		String vx = "x", vy = "y", vz = "z";
		IGraphState gs1 = new GraphState(vx),
					gs2 = new GraphState(vy),
					gs3 = new GraphState(vz);
		super.addVertex(vx); super.addVertex(vy); super.addVertex(vz);
		this.graphStates.put(vx, gs1);
		this.graphStates.put(vy, gs2);
		this.graphStates.put(vz, gs3);
		this.updateCluster(gs1); this.updateCluster(gs2); this.updateCluster(gs3);

		GraphEdge e1 = new GraphEdge(vx, vy);
		GraphEdge e2 = new GraphEdge(vx, vz);
		super.addEdge(vx, vy, e1); super.addEdge(vx, vz, e2);
		GraphAction ga = new GraphAction(vx); ga.setSourceStateID(vx); ga.addTargetStateID(vy);; ga.addTargetStateID(vz);; ga.setCount(2);
		this.graphActions.put(vx, ga);
		this.updateCluster(ga);

		this.edgeAdded(ga, e1); this.edgeAdded(ga, e2);
		this.orderedSequenceActions.add(e1);
		this.orderedSequenceActions.add(e2);
	}

	public boolean stateAtGraph(String stateID){
		synchronized(this.graphStates){
			return this.graphStates.containsKey(stateID);
		}
	}
	
	public IGraphState getState(String stateID){
		if (stateID == null)
			return null;
		synchronized(this.graphStates){
			return this.graphStates.get(stateID);
		}
	}

	public boolean stateAtGraph(IGraphState gs){
		if (gs == null) return false;
		return stateAtGraph(gs.getConcreteID());
	}

	public Collection<IGraphState> vertexStates(){
		synchronized(this.graphStates){
			return this.graphStates.values();
		}
	}
	
	private void updateCluster(IGraphState gs){
		String id = gs.getConcreteID();
		if (id.equals(Grapher.GRAPH_NODE_ENTRY) ||
			id.equals(Grapher.GRAPH_NODE_PASS) ||
			id.equals(Grapher.GRAPH_NODE_FAIL))
			return;
		Set<String> c = this.stateClusters.get(gs.getAbstract_R_ID());
		if (c == null){
			c = new HashSet<String>();
			this.stateClusters.put(gs.getAbstract_R_ID(), c);
		}
		c.add(gs.getConcreteID());
	}

	private void updateCluster(IGraphAction ga){
		if (ga.getConcreteID().equals(Grapher.GRAPH_ACTION_START) ||
			ga.getConcreteID().equals(Grapher.GRAPH_ACTION_STOP))
			return;
		Set<String> c = this.actionClusters.get(ga.getAbstractID());
		if (c == null){
			c = new HashSet<String>();
			this.actionClusters.put(ga.getAbstractID(), c);
		}
		c.add(ga.getConcreteID());
	}

	public HashMap<String,Set<String>> getStateClusters(){
		return this.stateClusters;
	}

	public HashMap<String,Set<String>> getActionClusters(){
		return this.actionClusters;
	}

	public void addVertex(IEnvironment env, IGraphState v){
		updateLongestPath(v);
		synchronized(this.graphStates){
			if (this.graphStates.containsKey(v.getConcreteID()))
				v.incCount();
			else {
				if (super.addVertex(v.getConcreteID())){
					this.graphStates.put(v.getConcreteID(),v);
					updateCluster(v);
				} else
					System.out.println("WARNING - failed to add new state vertex to graph: " + v.toString());
			}
		}
	}
	
	public void updateLongestPath(IGraphState v){ // note: longest path is not found in all cases!
		String id = v.getConcreteID();
		if(id.equals(Grapher.GRAPH_NODE_PASS) || id.equals(Grapher.GRAPH_NODE_FAIL))
			return;
		int idx = currentPath.indexOf(id);
		if (idx == -1){ // not found
			currentPath.add(id);
			if(currentPath.size() > longestPath.size())
				longestPath = new ArrayList<String>(currentPath);
		} else
			currentPath.subList(idx + 1, currentPath.size()).clear(); // remove loop
	}
	
	public String getLongestPath(){
		String returnS = "Longest path (";
		if (longestPath != null){
			returnS += longestPath.size() + ") = ";
			for(String id : longestPath)
				returnS += id + " ";
		} else
			returnS += "NULL)";
		return returnS;
	}
	
	public List<String> getLongestPathArray(){
		if (longestPath != null)
			return longestPath;
		else
			return new ArrayList<String>(); // empty list
	}

	public boolean actionAtGraph(String actionID){
		synchronized(this.graphActions){
			return this.graphActions.containsKey(actionID);
		}
	}

	public boolean actionAtGraph(IGraphAction ga){
		if (ga == null) return false;
		return actionAtGraph(ga.getConcreteID());
	}
	
	public IGraphAction getAction(String actionID){
		synchronized(this.graphActions){
			return this.graphActions.get(actionID);
		}
	}
		
	public Collection<IGraphAction> edgeActions(){
		synchronized(this.graphActions){
			return this.graphActions.values();
		}
	}
	
	private void edgeAdded(IGraphAction ga, GraphEdge edge){
		String actionID = ga.getConcreteID();
		if (actionID.equals(Grapher.GRAPH_ACTION_START) || actionID.equals(Grapher.GRAPH_ACTION_STOP))
			return;
		actionOrder++;
		ga.addOrder(edge.getTargetStateID(), new Integer(actionOrder).toString());
		orderedSequenceActions.add(edge);
	}
	
	/**
	 * Note: edge nodes must be added first.
	 * see addVertex(IEnvironment env, GraphState v)
	 */
	public void addEdge(IEnvironment env, IGraphState from, IGraphState to, IGraphAction e){
		if (!this.containsVertex(from.getConcreteID())){
			System.out.println("WARNING - Adding missing vertex: " + from.getConcreteID());
			this.addVertex(env, from);
		}
		if (!this.containsVertex(to.getConcreteID())){
			System.out.println("WARNING - Adding missing vertex: " + to.getConcreteID());
			this.addVertex(env, to);
		}
		GraphEdge edge = new GraphEdge(e.getConcreteID(),to.getConcreteID());
		synchronized(this.graphActions){
			if (super.containsEdge(edge)){
				e.incCount();
				edgeAdded(e,edge);
			} else if (this.graphActions.containsKey(e.getConcreteID())){ // action is at graph, but the edge not => this is multi-target
				if (super.addEdge(from.getConcreteID(), to.getConcreteID(), edge)){
					e.incCount();
					edgeAdded(e,edge);
					e.addTargetStateID(to.getConcreteID());
				} else
					System.out.println("WARNING - failed to add multi-target edge to graph: " + edge.toString());
			} else{
				if (super.addEdge(from.getConcreteID(), to.getConcreteID(), edge)){
					edgeAdded(e,edge);
					e.setSourceStateID(from.getConcreteID());
					e.addTargetStateID(to.getConcreteID());
					this.graphActions.put(e.getConcreteID(),e);
					updateCluster(e);
				} else
					System.out.println("WARNING - failed to add new action edge to graph: " + edge.toString());
			}
		}
	}

	public List<GraphEdge> getOrderedActions(){
		return this.orderedSequenceActions;
	}
	
	public GraphEdge[] getSortedActionsByOrder(int fromOrder, int toOrder) {
		List<GraphEdge> orderedSequenceActions = this.getOrderedActions();
		int low = fromOrder >= orderedSequenceActions.size() ?
				orderedSequenceActions.size() - 1 :
				fromOrder < 0 ? 0 : fromOrder;
		if (toOrder < fromOrder)
			return null;
		int high = toOrder >= orderedSequenceActions.size() ? orderedSequenceActions.size() - 1 : toOrder;
		int actionsN = high - low + 1;
		GraphEdge[] gas = new GraphEdge[actionsN];
		for (int i=0; i < actionsN; i++)
			gas[i] = orderedSequenceActions.get(low + i);
		return gas;
	}	
	
	public Iterator<GraphEdge> getForwardActions(){
		return this.orderedSequenceActions.iterator();
	}
	
	public ListIterator<GraphEdge> getBackwardActions(){
		return  this.orderedSequenceActions.listIterator(this.orderedSequenceActions.size());
	}
	
	private void syncXMLGraph(String xmlPath){
		System.out.print("Sync XML graph: ");
		synchronized(TESTARGraph.syncXMLGraph){
			while (!TESTARGraph.syncXMLGraph.equals("")){
				try {
					System.out.print(".");
					TESTARGraph.syncXMLGraph.wait(1000);
				} catch (InterruptedException e) {}
			}
			TESTARGraph.syncXMLGraph = xmlPath;
		}
		System.out.println("synced");
	}
	
	public void saveToXML(IEnvironment env, String xmlPath){
		syncXMLGraph(xmlPath);
		PrintWriter writer = null;
 		try {
			writer = new PrintWriter(xmlPath, Charset.defaultCharset().name());
			writer.write("<?xml version=\"1.0\"?>\n");
			writer.write("<" + XML_TAG_TESTAR_GRAPH + " version=\"" + XML_GRAPH_VERSION + "\">\n");
			writer.write("\t<" + XML_TAG_GRAPH_STATES + ">\n");
			String stateID;
			for (IGraphState gs : this.vertexStates()){
				stateID = gs.getConcreteID();
				if (!stateID.equals(Grapher.GRAPH_NODE_ENTRY) &&
					!stateID.equals(Grapher.GRAPH_NODE_PASS) && !stateID.equals(Grapher.GRAPH_NODE_FAIL)){
					writer.write("\t\t<" + XML_TAG_STATE + " " + XML_ATTRIBUTE_STATE_ID + "=\"" + gs.getConcreteID() + "\" " +
								 XML_ATTRIBUTE_STATE_ABS_ID + "=\"" + gs.getAbstract_R_ID() + "\" " +
								 XML_ATTRIBUTE_STATE_VISITED + "=\"" + gs.getCount() + "\" " +
								 XML_ATTRIBUTE_STATE_WIDGET_COUNT + "=\"" + gs.getStateWidgetsExecCount().size() + "\" " +
								 XML_ATTRIBUTE_STATE_WIDGETS + "=\"" + gs.getStateWidgetsExecCount() + "\" " +
								 XML_ATTRIBUTE_STATE_UNEXECUTED + "=\"" + gs.getUnexploredActionsString() + "\"/>\n");
				}
			}
			writer.write("\t</" + XML_TAG_GRAPH_STATES + ">\n");
			writer.write("\t<" +  XML_TAG_GRAPH_ACTIONS + ">\n");
			int idx = 1;
			IGraphAction ga;
			GraphEdge[] orderedActions = this.getSortedActionsByOrder(Integer.MIN_VALUE, Integer.MAX_VALUE);
			for (GraphEdge edge : orderedActions){
				ga = env.getAction(edge.getActionID());
				writer.write("\t\t<" + XML_TAG_ACTION + " " + XML_ATTRIBUTE_ACTION_ORDER + "=\"" + idx++ + "\" " +
							  XML_ATTRIBUTE_ACTION_ID + "=\"" + ga.getConcreteID() + "\" " +
							  XML_ATTRIBUTE_ACTION_ABS_ID + "=\"" + ga.getAbstractID() + "\" " +
							  XML_ATTRIBUTE_ACTION_VISITED + "=\"" + ga.getCount() +  "\" " +
							  XML_ATTRIBUTE_ACTION_FROM + "=\"" + ga.getSourceStateID() +  "\" " +
							  XML_ATTRIBUTE_ACTION_TO + "=\"" + edge.getTargetStateID() +  "\"/>\n");
			}
			writer.write("\t</" + XML_TAG_GRAPH_ACTIONS + ">\n");
			writer.write("</" + XML_TAG_TESTAR_GRAPH + ">\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) writer.close();
			synchronized(TESTARGraph.syncXMLGraph){
				TESTARGraph.syncXMLGraph = "";
			}
		}
	}

	private static float progress, lastProgress;
	private void printGraphLoadingProgress(float nidx, float nlength){
    	progress = Math.round(nidx / nlength * 100.0);
    	if (progress - lastProgress > 1){
    		lastProgress = progress;
    		System.out.println("\t[" + (int)nidx + "] / [" + nlength + "]");
    	}
	}
	
	// rationale: some UI parts might only be reachable starting from scratch and going through a different path
	public int loadFromXML(String xmlPath, IEnvironment env){
		syncXMLGraph(xmlPath);
        int graphMovements = 0;
		File f = new File(xmlPath);
		if (f.exists()){		
			ProgressFileInputStream pfis = null;
			BufferedInputStream stream = null;
			Document doc = null;
			try {
				pfis = new ProgressFileInputStream(f);
				stream = new BufferedInputStream(pfis);
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				doc = docBuilder.parse(stream);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch(ParserConfigurationException e){
				e.printStackTrace();
			} finally{
				if (stream != null) {
					try { stream.close(); } catch (IOException e) {e.printStackTrace();}
				}
				if (pfis != null) {
					try { pfis.close(); } catch (IOException e) { e.printStackTrace(); }
				}
			}
			if (doc == null)
				LogSerialiser.log("Exception loading graph filet: " + xmlPath, LogSerialiser.LogLevel.Critical);
			else{
				
				Grapher.getWalker().enablePreviousWalk();

				IGraphState gs; GraphAction ga;
				Map<String,IGraphState> graphStates = new HashMap<String,IGraphState>();
				
				Node node; Element element;
		        NodeList nList = doc.getElementsByTagName(XML_TAG_STATE);
		        lastProgress = 0; System.out.println("Loading graph vertex nodes [" + nList.getLength() + "]:");
		        int nListLength = nList.getLength();
		        for (int nidx = 0; nidx < nListLength; nidx++){
		        	printGraphLoadingProgress((float)nidx,(float)nListLength);
		        	node = nList.item(nidx);
		        	if (node.getNodeType() == Node.ELEMENT_NODE){
		        		element = (Element) node;
		        		String wid = element.getAttribute(XML_ATTRIBUTE_STATE_ID),
		        			   abswid = element.getAttribute(XML_ATTRIBUTE_STATE_ABS_ID);
		        		gs = new GraphState(wid,abswid);
		        		String visited = element.getAttribute(XML_ATTRIBUTE_STATE_VISITED);
		        		try{
		        			int v = new Integer(visited).intValue();
		        			gs.setCount(v);
		        		} catch (NumberFormatException nfe){
		        			nfe.printStackTrace();
		        		}
		        		
		        		//String widgets = element.getAttribute(XML_ATTRIBUTE_STATE_WIDGETS);
		        		// TODO: add widgets to graph state
		        		
		        		//System.out.println("\nZ:" + widgets + " ...");
		        		//Properties p = new Properties();
		        		//p.load(new StringReader(widgets.substring(1, widgets.length() - 1).replace("=","\n").replace(", ","\n")));
		        		
		        		//System.out.println("A: " + widgets.substring(1, widgets.length() - 1));
		        		//System.out.println("B: " + widgets.substring(1, widgets.length() - 1).replace("=","\n").replace(", ","\n"));
		        		
		        		//Map<String, String> m = new HashMap<String, String>();
		        		//for (Map.Entry<Object, Object> e : p.entrySet()) {
		        		//    m.put((String)e.getKey(), (String)e.getValue());
		        		//}		        		
		        		//for (String k : m.keySet()){
		        		//	System.out.println("key = " + k + " - value = " + m.get(k));
		        		//}
		        		//System.out.println("END: ... " + m.toString());
		        		
		        		
		        		String unexecuted = element.getAttribute(XML_ATTRIBUTE_STATE_UNEXECUTED);
		        		unexecuted = unexecuted.substring(1,unexecuted.length()-1); // remove []
		        		if (!unexecuted.isEmpty()){
			        		StringTokenizer st = new StringTokenizer(unexecuted, ",");
			        		while (st.hasMoreTokens())
			        			gs.actionUnexplored(st.nextToken());
		        		}

		        		graphStates.put(wid,gs);
		        		//Grapher.notify(gs,null);
		        	}
		        }
		        
				int graphActionsCount = 0;
		        nList = doc.getElementsByTagName(XML_TAG_ACTION);
		        lastProgress = 0; System.out.println("Loading graph edge nodes [" + nList.getLength() + "]:");
		        String wid, absid, from, to = null;
		        nListLength = nList.getLength();
		        for (int nidx = 0; nidx < nListLength; nidx++){
		        	printGraphLoadingProgress((float)nidx,(float)nListLength);
		        	node = nList.item(nidx);
		        	if (node.getNodeType() == Node.ELEMENT_NODE){
		        		element = (Element) node;
		        		wid = element.getAttribute(XML_ATTRIBUTE_ACTION_ID);
		        		absid = element.getAttribute(XML_ATTRIBUTE_ACTION_ABS_ID);
		        		from = element.getAttribute(XML_ATTRIBUTE_ACTION_FROM);
		        		to = element.getAttribute(XML_ATTRIBUTE_ACTION_TO);
		        		ga = new GraphAction(wid,absid);
		        		String visited = element.getAttribute(XML_ATTRIBUTE_ACTION_VISITED);
		        		try{
		        			int v = new Integer(visited).intValue();
		        			ga.setCount(v);
		        		} catch (NumberFormatException nfe){}
		        		gs = graphStates.get(from);
		        		Grapher.notify(gs, ga);
		        		graphMovements++;
		        		graphActionsCount++;
		        	}
		        }
		        if (to != null)
		        	Grapher.notify(graphStates.get(to),null); // ending state
		        else
		        	System.out.println("End state missing at XML loading");

				System.out.println("\tGraph loaded (nodes: " + graphStates.size() + "; edges: " + graphActionsCount + ")");
			}
		} else
			LogSerialiser.log("Graph file does not exist: " + xmlPath, LogSerialiser.LogLevel.Critical);
		synchronized(TESTARGraph.syncXMLGraph){
			TESTARGraph.syncXMLGraph = "";
		}

		Grapher.syncMovements(); // synchronize graph movements consumption for consistent XML loading

		Grapher.getWalker().disablePreviousWalk();

		return graphMovements;
	}
	
}
