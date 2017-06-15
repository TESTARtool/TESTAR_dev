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

package es.upv.staq.testar.graph;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.fruit.Assert;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.upv.staq.testar.ProgressFileInputStream;
import es.upv.staq.testar.serialisation.LogSerialiser;

/**
 * Graph representation for TESTAR.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class TESTARGraph extends DefaultDirectedGraph<String, String> { // state concrete ID x action concrete ID

	private static final long serialVersionUID = -6766749840561297953L;
	
	private Boolean vertexSem = new Boolean(true);
	private Boolean edgeSem = new Boolean(false);
	
	private Map<String,IGraphState> graphStates; // state concreteID -> graph state
	private Map<String,IGraphAction> graphActions; // action concreteID -> graph action	
	
	List<IGraphAction> orderedSequenceActions;
	int actionOrder = -1;
		
	// begin by fraalpe2
	private int longestPathSize = 0; // how deep from START graph state?
	private List<IGraphState> currentPath; 
	// end by fraalpe2
	private List<IGraphState> longestPath = null; // by urueda
	
	private static String syncXMLGraph = "";

	public static final String XML_GRAPH_VERSION = "1.0.20160331",
							   XML_TAG_TESTAR_GRAPH = "TESTAR_GRAPH",
							   XML_TAG_GRAPH_STATES = "graph_states",
							   XML_TAG_GRAPH_ACTIONS = "graph_actions",
							   XML_TAG_STATE = "state",
							   XML_TAG_ACTION = "action",
							   XML_ATTRIBUTE_STATE_WID = "wid",
							   XML_ATTRIBUTE_STATE_ABS_WID = "abswid", // abstract id
							   XML_ATTRIBUTE_STATE_VISITED = "visited",
							   XML_ATTRIBUTE_STATE_WIDGET_COUNT = "wcount",
							   XML_ATTRIBUTE_STATE_WIDGETS = "widgets",
							   XML_ATTRIBUTE_STATE_UNEXECUTED = "unexecuted",
							   XML_ATTRIBUTE_ACTION_ORDER = "order",
							   XML_ATTRIBUTE_ACTION_AID = "aid",
							   XML_ATTRIBUTE_ACTION_VISITED = "visited",
							   XML_ATTRIBUTE_ACTION_FROM = "from",
							   XML_ATTRIBUTE_ACTION_TO = "to";
	
	private TESTARGraph() {
		super(String.class);
		this.graphStates = new HashMap<String,IGraphState>();
		this.graphActions = new HashMap<String,IGraphAction>();		
		orderedSequenceActions = new ArrayList<IGraphAction>();
		actionOrder = 0;
		longestPathSize = 0;
		currentPath = new ArrayList<IGraphState>();
	}

	public static TESTARGraph buildEmptyGraph(){
		return new TESTARGraph();		
	}
	
	public boolean stateAtGraph(String stateID){
		synchronized(vertexSem){
			return this.graphStates.containsKey(stateID);
		}
	}
	
	public IGraphState getState(String stateID){
		synchronized(vertexSem){
			return this.graphStates.get(stateID);
		}
	}

	public boolean stateAtGraph(IGraphState gs){
		return stateAtGraph(gs.getConcreteID());
	}

	public Collection<IGraphState> vertexStates(){
		synchronized(vertexSem){			
			return this.graphStates.values();
		}
	}

   /**
    * Add Vertex to the graph and update LongestPath.
    * @param v Vertex to add.
    * @return
    */
	public boolean addVertex(IGraphState v){
		updateLongestPath(v);
		synchronized(vertexSem){
			if (this.graphStates.containsKey(v.getConcreteID())){
				v.incCount();
				return false;
			} else{
				boolean added = super.addVertex(v.getConcreteID());
				if (added)
					this.graphStates.put(v.getConcreteID(),v);
				return added;
			}
		}
	}
	
	//by fraalpe2
	public void updateLongestPath(IGraphState v){
		// begin by urueda
		if(v.getConcreteID().equals(Grapher.GRAPH_NODE_PASS) || v.getConcreteID().equals(Grapher.GRAPH_NODE_FAIL))
			return;
		int idx = currentPath.indexOf(v);
		if (idx != -1){
			currentPath.subList(idx + 1, currentPath.size()).clear(); // remove loop
		// end by urueda
		} else{
			currentPath.add(v);
			if(currentPath.size() > longestPathSize){
				longestPathSize = currentPath.size();
				longestPath = new ArrayList<IGraphState>(currentPath);
			}
		}
	}
	
	public String getLongestPath(){
		String returnS = "Longest path (";
		if (longestPath != null){
			returnS += longestPath.size() + ") = ";
			for(IGraphState gs : longestPath)
				returnS += gs.getConcreteID() + " ";
		} else
			returnS += "NULL)";
		return returnS;
	}
	
	public List<IGraphState> getLongestPathArray(){
		if (longestPath != null)
			return longestPath;
		else
			return new ArrayList<IGraphState>(); // empty list
	}
	
	private void edgeAdded(IGraphAction e){
		actionOrder++;
		e.addOrder(new Integer(actionOrder).toString());
		orderedSequenceActions.add(e);
	}

	public boolean actionAtGraph(String actionID){
		synchronized(edgeSem){
			return this.graphActions.containsKey(actionID);
		}
	}

	public boolean actionAtGraph(IGraphAction ga){
		return actionAtGraph(ga.getConcreteID());
	}
	
	public IGraphAction getAction(String actionID){
		synchronized(edgeSem){
			return this.graphActions.get(actionID);
		}
	}
		
	public Collection<IGraphAction> edgeActions(){
		synchronized(edgeSem){
			return this.graphActions.values();
		}
	}
	
	private int mutationIdx = 1;
	
	/**
	 * Note: edge nodes must be added first.
	 * see addVertex(IEnvironment env, GraphState v)
	 */
	public boolean addEdge(IEnvironment env, IGraphState from, IGraphState to, IGraphAction e){
		synchronized(edgeSem){
			boolean edgeAtGraph = true, edgeMutated = false;
			if (this.graphActions.containsKey(e.getConcreteID())){ // the edge already exists at graph?
				edgeAtGraph &= getEdgeSource(e.getConcreteID()).equals(from.toString()); // and the edge source state is the same?
				edgeAtGraph &= getEdgeTarget(e.getConcreteID()).equals(to.toString()); // and the edge target state is the same? 
				if (!edgeAtGraph)
					edgeMutated = true; // linked states changed! (maybe the SUT did not react on time to actions)	
			} else
				edgeAtGraph = false;
			e.incCount();
			if (edgeAtGraph){
				edgeAdded(e);
				return false;
			} else{
				IGraphAction ga = edgeMutated ? e.clone("_MUTATED_" + mutationIdx++) // keep the graph completely connected
											  : e;
				if (!e.getConcreteID().equals(Grapher.GRAPH_ACTION_START) && !e.getConcreteID().equals(Grapher.GRAPH_ACTION_STOP))
					edgeAdded(ga);
				try{
					Assert.isTrue(this.graphStates.containsKey(from.getConcreteID()));
					Assert.isTrue(this.graphStates.containsKey(to.getConcreteID()));
					boolean added = super.addEdge(from.getConcreteID(), to.getConcreteID(), ga.getConcreteID());
					if (added)
						this.graphActions.put(ga.getConcreteID(),ga);
					return added;
				} catch(NullPointerException ex){
					ex.printStackTrace();
					return false;
				}
			}
		}
	}

	public List<IGraphAction> getOrderedActions(){
		return this.orderedSequenceActions;
	}
	
	public IGraphAction[] getSortedActionsByOrder(int fromOrder, int toOrder) {
		List<IGraphAction> orderedSequenceActions = this.getOrderedActions();
		int low = fromOrder >= orderedSequenceActions.size() ?
				orderedSequenceActions.size() - 1 :
				fromOrder < 0 ? 0 : fromOrder;
		if (toOrder < fromOrder)
			return null;
		int high = toOrder >= orderedSequenceActions.size() ? orderedSequenceActions.size() - 1 : toOrder;
		int actionsN = high - low + 1;
		IGraphAction[] gas = new IGraphAction[actionsN];
		for (int i=0; i < actionsN; i++)
			gas[i] = orderedSequenceActions.get(low + i);
		return gas;
	}	
	
	public Iterator<IGraphAction> getForwardActions(){
		return this.orderedSequenceActions.iterator();
	}
	
	public ListIterator<IGraphAction> getBackwardActions(){
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
	
	public void saveToXML(String xmlPath){
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
					writer.write("\t\t<" + XML_TAG_STATE + " " + XML_ATTRIBUTE_STATE_WID + "=\"" + gs.getConcreteID() + "\"");
					writer.write(" " + XML_ATTRIBUTE_STATE_ABS_WID + "=\"" + gs.getAbstract_R_ID() + "\"");
					writer.write(" " + XML_ATTRIBUTE_STATE_VISITED + "=\"" + gs.getCount() + "\"");
					writer.write(" " + XML_ATTRIBUTE_STATE_WIDGET_COUNT + "=\"" + gs.getStateWidgetsExecCount().size() + "\"");
					writer.write(" " + XML_ATTRIBUTE_STATE_WIDGETS + "=\"" + gs.getStateWidgetsExecCount() + "\"");
					writer.write(" " + XML_ATTRIBUTE_STATE_UNEXECUTED + "=\"" + gs.getUnexploredActionsString() + "\"");
					writer.write("/>\n");
				}
			}
			writer.write("\t</" + XML_TAG_GRAPH_STATES + ">\n");
			writer.write("\t<" +  XML_TAG_GRAPH_ACTIONS + ">\n");
			int idx = 1;
			IGraphAction[] orderedActions = this.getSortedActionsByOrder(Integer.MIN_VALUE, Integer.MAX_VALUE);
			for (IGraphAction ga : orderedActions){
				writer.write("\t\t<" + XML_TAG_ACTION + " " + XML_ATTRIBUTE_ACTION_ORDER + "=\"" + idx++ + "\" " + XML_ATTRIBUTE_ACTION_AID + "=\"" + ga.getConcreteID() + "\"");				
				writer.write(" " + XML_ATTRIBUTE_ACTION_VISITED + "=\"" + ga.getCount() +  "\"");
				writer.write(" " + XML_ATTRIBUTE_ACTION_FROM + "=\"" + this.getEdgeSource(ga.getConcreteID()) +  "\"");
				writer.write(" " + XML_ATTRIBUTE_ACTION_TO + "=\"" + this.getEdgeTarget(ga.getConcreteID()) +  "\"/>\n");
			}
			writer.write("\t</" + XML_TAG_GRAPH_ACTIONS + ">\n");
			writer.write("</" + XML_TAG_TESTAR_GRAPH + ">\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) writer.close();
		}
		synchronized(TESTARGraph.syncXMLGraph){
			TESTARGraph.syncXMLGraph = "";
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
				
				GraphState gs; GraphAction ga;
				Map<String,GraphState> graphStates = new HashMap<String,GraphState>();
				
				Node node; Element element;
		        NodeList nList = doc.getElementsByTagName(XML_TAG_STATE);
		        lastProgress = 0; System.out.println("Loading graph vertex nodes [" + nList.getLength() + "]:");
		        int nListLength = nList.getLength();
		        for (int nidx = 0; nidx < nListLength; nidx++){
		        	printGraphLoadingProgress((float)nidx,(float)nListLength);
		        	node = nList.item(nidx);
		        	if (node.getNodeType() == Node.ELEMENT_NODE){
		        		element = (Element) node;
		        		String wid = element.getAttribute(XML_ATTRIBUTE_STATE_WID),
		        			   abswid = element.getAttribute(XML_ATTRIBUTE_STATE_ABS_WID);
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
		        		
		        		gs.knowledge(true);
		        		graphStates.put(wid,gs);
		        	}
		        }
		        
				int graphActionsCount = 0;
		        nList = doc.getElementsByTagName(XML_TAG_ACTION);
		        lastProgress = 0; System.out.println("Loading graph edge nodes [" + nList.getLength() + "]:");
		        nListLength = nList.getLength();
		        for (int nidx = 0; nidx < nListLength; nidx++){
		        	printGraphLoadingProgress((float)nidx,(float)nListLength);
		        	node = nList.item(nidx);
		        	if (node.getNodeType() == Node.ELEMENT_NODE){
		        		element = (Element) node;
		        		String wid = element.getAttribute(XML_ATTRIBUTE_ACTION_AID);
		        		String from = element.getAttribute(XML_ATTRIBUTE_ACTION_FROM);
		        		String to = element.getAttribute(XML_ATTRIBUTE_ACTION_TO);
		        		ga = new GraphAction(wid);
		        		String visited = element.getAttribute(XML_ATTRIBUTE_ACTION_VISITED);
		        		try{
		        			int v = new Integer(visited).intValue();
		        			ga.setCount(v);
		        		} catch (NumberFormatException nfe){}
		        		ga.addOrder("0");
		        		ga.knowledge(true);		        
		        		Grapher.notify(graphStates.get(from),ga);
		        		graphMovements++;
		        		graphActionsCount++;
		        	}
		        }
		        
				System.out.println("\tGraph loaded (nodes: " + graphStates.size() + "; edges: " + graphActionsCount + ")");
			}
		} else
			LogSerialiser.log("Graph file does not exist: " + xmlPath, LogSerialiser.LogLevel.Critical);
		synchronized(TESTARGraph.syncXMLGraph){
			TESTARGraph.syncXMLGraph = "";
		}
		return graphMovements;
	}
	
}
