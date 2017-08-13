/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2016):                                                                          *
 * Universitat Politecnica de Valencia                                                        *
 * Camino de Vera, s/n                                                                        *
 * 46022 Valencia, Spain                                                                      *
 * www.upv.es                                                                                 *
 *                                                                                            * 
 * D I S C L A I M E R:                                                                       *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)          *
 * in the context of the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open           *
 * source project under the BSD3 license (http://opensource.org/licenses/BSD-3-Clause)        *                                                                                        * 
 *                                                                                            *
 **********************************************************************************************/

package es.upv.staq.testar.prolog;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.fruit.alayer.Tags;

import es.upv.staq.testar.graph.GraphEdge;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;

/**
 * Facts for TESTAR graphs.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class FactsTests {

	public static final String FACT_ACTION_SOURCE = "source"; // source(S,A) : S source state of action A
	public static final String FACT_ACTION_TARGET = "target"; // target(S,A) : S target state of action A
	
	private static AbstractMap<String,Set<String>> stateFactsCache = new WeakHashMap<String,Set<String>>();
	private static AbstractMap<String,Set<String>> actionFactsCache = new WeakHashMap<String,Set<String>>();
	
    public static Set<String> getFacts(IEnvironment env){
    	Set<String> facts = new HashSet<String>();
    	
    	Set<String> cached;
    	
    	// atoms
    	String gsid;
    	for (IGraphState gs : env.getGraphStates()){
    		gsid = gs.getConcreteID();
    		cached = stateFactsCache.get(gsid);
    		if (cached != null)
    			facts.addAll(cached);
    		else{
	       		if (!gsid.equals(Grapher.GRAPH_NODE_ENTRY)){
	    			facts.add(FactsUIStates.FACT_STATE + "('" + gsid + "')."); // state(S)
	    			facts.addAll(getVertexAtoms(gs,gsid));
	    		}
    		}
    	}
    	int order = 1;
    	String sourceID, targetID;
    	IGraphAction ga;
    	for (GraphEdge edge : env.getSortedActionsByOrder(Integer.MIN_VALUE, Integer.MAX_VALUE)){
    		ga = env.getAction(edge.getActionID());
    		cached = actionFactsCache.get(edge);
    		if (cached != null)
    			facts.addAll(cached);
    		else{
	    		sourceID = ga.getSourceStateID();	    		
	    		for (IGraphState gs : env.getTargetStates(ga)){
		    		targetID = edge.getTargetStateID();
		    		facts.add(FactsUIActions.FACT_ACTION + "('" + edge.getActionID() + "','" + sourceID + "','" +
		    				  ga.getTargetWidgetID() + "','" + ga.getRole() + "'," + (order++) + ")."); // action(A,S,W,T,O)
		    		facts.add(FACT_ACTION_SOURCE + "('" + sourceID + "','" + edge.getActionID() + "')."); // source(S,A)
		    		facts.add(FACT_ACTION_TARGET + "('" + targetID + "','" + edge.getActionID() + "')."); // target(S,A)	    			
	    		}	    			
    		}
    	}
    	
    	return facts;
    }
    
    private static Set<String> getVertexAtoms(IGraphState gs, String vertex){
    	Set<String> facts = new HashSet<String>();

    	String property;
    	Map<String,String> widgetProperties;
    	for (String w : gs.getStateWidgetsExecCount().keySet()){
    		facts.add(FactsUIStates.FACT_WIDGET + "('" + w + "','" + vertex + "')."); // widget(W,S)
    		facts.add(FactsUIStates.FACT_PARENT + "('" + gs.getParent(w) + "','" + w + "')."); // parent(Wp,Wc)
    		widgetProperties = gs.getWidgetProperties(w);
    		
    		for (String p : widgetProperties.keySet()){
    			if (p.equals(Tags.Role.name()))
    				property = FactsUIStates.FACT_ROLE;
    			else if (p.equals(Tags.Title.name()))
    				property = FactsUIStates.FACT_TITLE;
    			else if (p.equals(Tags.Path.name()))
    				property = FactsUIStates.FACT_PATH;
    			else
    				property = null;
    			if (property != null)
    				facts.add(property + "('" + w + "','" + widgetProperties.get(p) + "')."); // ?property?(W,V) ... i.e. role(W,R)
    		}
    	}
    	
    	return facts;
    }
 
    public static void debugQueries(JIPrologWrapper wrapper){
    	wrapper.debugQuery("state(S).");
    	wrapper.debugQuery("widget(W,S).");
    	wrapper.debugQuery("parent(Wp,Wc).");
    	wrapper.debugQuery("ancestor(Wa,Wc).");
    	wrapper.debugQuery("role(W,R).");
    	wrapper.debugQuery("title(W,T).");
    	wrapper.debugQuery("path(W,P).");
    	wrapper.debugQuery("action(A,S,W,T,O).");
    	wrapper.debugQuery("source(S,A).");
    	wrapper.debugQuery("target(S,A).");
    }
    
}