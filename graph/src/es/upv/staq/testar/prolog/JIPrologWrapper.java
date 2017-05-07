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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPVariable;

import es.upv.staq.testar.graph.IEnvironment;

/**
 * A wrapper for JIProlog.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class JIPrologWrapper{ // implements JIPEventListener{
	
	private JIPEngine jipEngine = null;
	//private int queryHandle = -1;

	private PrologBase prologBase = null;
	
	private class PrologBase{
		Set<String> stateRules, actionRules, envRules,
					stateFacts, actionFacts, envFacts,
					factsNrules; // additional facts/rules
	}
	
	private AbstractMap<String,Set<String>> stateFactsCache = null;
	private boolean queryBaseSync = false;

	public JIPrologWrapper(){
		this.jipEngine = new JIPEngine();
		//this.jipEngine.addEventListener(this);
		this.prologBase = new PrologBase();
		setStateRules(getStateRules());
		setActionRules(getActionsRules());
		this.stateFactsCache = new WeakHashMap<String,Set<String>>();
	}
	
	private Set<String> getStateRules(){
		return FactsState.getRules();
	}
	
	public Set<String> getActionsRules(){
		return FactsAction.getRules();
	}
	
	private Set<String> getFacts(State state){
		String sid = state.get(Tags.ConcreteID);
		Set<String> facts = this.stateFactsCache.get(sid);
		if (facts != null)
			return facts;
		else{
			facts = FactsState.getFacts(state);
			this.stateFactsCache.put(sid,facts);
			return facts;
		}
	}
	private Set<String> getFacts(State state, Action action){
		Set<Action> actions = new HashSet<Action>();
		actions.add(action);
		return getFacts(state,actions);
	}
	private Set<String> getFacts(State state, Set<Action> actions){
		return FactsAction.getFacts(state,actions);
	}
	private Set<String> getFacts(IEnvironment env){
		return FactsEnvironment.getFacts(env);
	}	

	public void setStateRules(Set<String> prologRules){
		synchronized(this.prologBase){
			this.prologBase.stateRules = prologRules;
			this.queryBaseSync = true;
		}
	}

	public void setActionRules(Set<String> prologRules){
		synchronized(this.prologBase){
			this.prologBase.actionRules = prologRules;
			this.queryBaseSync = true;
		}
	}

	public void setEnvironmentRules(Set<String> prologRules){
		synchronized(this.prologBase){
			this.prologBase.envRules = prologRules;
			this.queryBaseSync = true;
		}
	}

	public void setFacts(State state){
		synchronized(this.prologBase){
			Set<String> facts = getFacts(state);
			if (facts != this.prologBase.stateFacts){
				this.prologBase.stateFacts = facts;
				this.queryBaseSync = true;
			}
		}
	}
	
	public void addFacts(State state){
		synchronized(this.prologBase){
			this.prologBase.stateFacts.addAll(getFacts(state));
			this.queryBaseSync = true;
		}		
	}

	public void setFacts(State state, Action action){
		synchronized(this.prologBase){
			this.prologBase.actionFacts = getFacts(state,action);
			this.queryBaseSync = true;
		}
	}

	public void setFacts(State state, Set<Action> actions){
		synchronized(this.prologBase){
			this.prologBase.actionFacts = getFacts(state,actions);
			this.queryBaseSync = true;
		}
	}

	public void addFacts(State state, Action action){
		synchronized(this.prologBase){
			this.prologBase.actionFacts.addAll(getFacts(state,action));
			this.queryBaseSync = true;
		}
	}

	public void setFacts(IEnvironment env){
		if (env != null){
			synchronized(this.prologBase){
				this.prologBase.envFacts = getFacts(env);
				this.queryBaseSync = true;
			}
		}
	}
	
	public void addFactsNrules(String factsNrules){
		synchronized(this.prologBase){
			this.prologBase.factsNrules.add(factsNrules);
			this.queryBaseSync = true;
		}
	}

	private void debugStateFactsNrules(){
		System.out.println("\n>>> State-Rules <<<\n-------------------\n");
		if (this.prologBase.stateRules != null){
			for (String s : this.prologBase.stateRules)
				System.out.println(s);
		}
		System.out.println("\n>>> State-Facts <<<\n-------------------\n");    	
		if (this.prologBase.stateFacts != null){
			for (String s : this.prologBase.stateFacts)
				System.out.println(s);
		}
	}

	private void debugActionFactsNrules(){    	
		System.out.println(">>> Action-Rules <<<\n--------------------\n");
		if (this.prologBase.actionRules != null){
			for (String s : this.prologBase.actionRules)
				System.out.println(s);
		}
		System.out.println(">>> Action-Facts <<<\n--------------------\n");
		if (this.prologBase.actionFacts != null){
			for (String s : this.prologBase.actionFacts)
				System.out.println(s);
		}
	}

	private void debugEnvironmentFactsNrules(){
		System.out.println(">>> Environment-Rules <<<\n-------------------------\n");
		if (this.prologBase.envRules != null){
			for (String s : this.prologBase.envRules)
				System.out.println(s);
		}
		System.out.println(">>> Environment-Facts <<<\n-------------------------\n");    	
		if (this.prologBase.envFacts != null){
			for (String s : this.prologBase.envFacts)
				System.out.println(s);
		}
	}

	public void updatePrologFactsNrules(){
		synchronized(this.prologBase){
			// debug facts and rules
			//this.debugStateFactsNrules();
			//this.debugActionFactsNrules();
			//this.debugEnvironmentFactsNrules();
	
			String prologString = PrologUtil.setToString(
					this.prologBase.stateRules,this.prologBase.actionRules,this.prologBase.envRules,
					this.prologBase.stateFacts, this.prologBase.actionFacts, this.prologBase.envFacts,
					this.prologBase.factsNrules);
			//System.out.println("Prolog-base:\n" + prologString);
			setPrologFactsNrules(prologString);
	
			// debug queries
			//FactsState.debugQueries(this);
			//FactsAction.debugQueries(this);
			//FactsEnvironment.debugQueries(this);
	
			// advanced queries
			//debugQuery("write('hello world'), nl.");
			//debugQuery("findall(W,widget(W,S),Z), length(Z,N)."); // N = widgets count in state S
			//debugQuery("findall(W,widget(W,S),Z1), length(Z1,N1), findall(A,action(A,T),Z2), length(Z2,N2), >(N1,N2).");
		}
	}

	private void setPrologFactsNrules(String prologBase){
		try{
			this.jipEngine.consultStream(new ByteArrayInputStream(prologBase.getBytes("UTF-8")), "UTF8ByteArrayStream");
		} catch (UnsupportedEncodingException uee) {
			System.out.println("Prolog-Facts/Rules unsupported encoding: " + uee.getMessage());
			System.out.println(prologBase);
		} catch(JIPSyntaxErrorException jsee){
			System.out.println("Prolog-Facts/Rules are not correct: " + jsee.getMessage());
			System.out.println(prologBase);
		}    	
	}

	public int debugPrologBase(){
		return (this.prologBase.stateRules == null ? 0 : this.prologBase.stateRules.size()) +
			   (this.prologBase.actionRules == null ? 0 : this.prologBase.actionRules.size()) +
			   (this.prologBase.envRules == null ? 0 : this.prologBase.envRules.size()) +
			   (this.prologBase.stateFacts == null ? 0 : this.prologBase.stateFacts.size()) +
			   (this.prologBase.actionFacts == null ? 0 : this.prologBase.actionFacts.size()) +
			   (this.prologBase.envFacts == null ? 0 : this.prologBase.envFacts.size()) +
			   (this.prologBase.factsNrules == null ? 0 : this.prologBase.factsNrules.size());		
	}
	
	public void debugQuery(String query){
		System.out.println("Prolog-Query: " + query);
		PrologUtil.printSolutions(this.setQuery(query));
	}
	
	public synchronized List<List<String>> setQuery(String queryString){
		if (this.queryBaseSync){
			this.queryBaseSync = false;
			updatePrologFactsNrules();
		}
		JIPTerm jipTerm = null;
		try{
			jipTerm = this.jipEngine.getTermParser().parseTerm(queryString);
		} catch(JIPSyntaxErrorException jsee){
			System.out.println("Prolog-Query is not correct: " + jsee.getMessage());
			return null;
		}
		// synchronized(this.jipEngine){
		//	this.queryHandle = this.jipEngine.openQuery(jipTerm);
		// }
		JIPQuery jipQuery = this.jipEngine.openSynchronousQuery(jipTerm);
		List<List<String>> solutions = new ArrayList<List<String>>();
		List<String> solution;
		JIPVariable[] jipVars;
		while (jipQuery.hasMoreChoicePoints()){
			jipTerm = jipQuery.nextSolution();
			if (jipTerm != null){
				jipVars = jipTerm.getVariables();
				solution = new ArrayList<String>(jipVars.length);
				for (JIPVariable jipVar : jipVars) {
					if (!jipVar.isAnonymous()){
						solution.add(jipVar.getName());
						//solution.add(PrologUtil.normaliseQuotedAtoms(jipVar.toString(this.jipEngine)));
						solution.add(jipVar.toString(this.jipEngine));
					}
				}
				solutions.add(solution);
			}
		}
		//System.out.println(">>> Prolog-Query: " + queryString + "\n-----------------");
		return solutions;
	}

	// -------------------------
	//   AsSynchronous queries
	// -------------------------

	/*@Override
    public void openNotified(JIPEvent jipEvent){
        synchronized(jipEvent.getSource()){
            if(this.queryHandle == jipEvent.getQueryHandle())
                System.out.println("Prolog-Query is open");
        }
    }

	@Override
    public void moreNotified(JIPEvent jipEvent){
        synchronized(jipEvent.getSource()){
            if(this.queryHandle == jipEvent.getQueryHandle())
                System.out.println("Prolog-Query: " + jipEvent.getTerm().toString());
        }
    }

	@Override
    public void solutionNotified(JIPEvent jipEvent){
        synchronized(jipEvent.getSource()){
            if(this.queryHandle == jipEvent.getQueryHandle()){
                JIPTerm jipTerm = jipEvent.getTerm();
                System.out.println("Prolog-Solution: " + jipTerm);
                for (JIPVariable termVar : jipTerm.getVariables()) {
                    if (!termVar.isAnonymous())
                        System.out.println("\t" + termVar.getName() + ": " + termVar.toString(jipEvent.getSource()));
                }
                jipEvent.getSource().nextSolution(jipEvent.getQueryHandle());
            }
        }
    }

	@Override
    public void termNotified(JIPEvent jipEvent){
        synchronized(jipEvent.getSource()){
            if(this.queryHandle == jipEvent.getQueryHandle())
                System.out.println("Prolog-Term: " + jipEvent.getTerm());
        }
    }

	@Override
    public synchronized void endNotified(JIPEvent jipEvent){
        synchronized(jipEvent.getSource()){
            if(this.queryHandle == jipEvent.getQueryHandle()){
                System.out.println("Prolog-End");
                jipEvent.getSource().closeQuery(this.queryHandle);
                this.queryHandle = -1;
            }
        }
    }

	@Override
    public synchronized void closeNotified(JIPEvent jipEvent){
        synchronized(jipEvent.getSource()){
            if(this.queryHandle == jipEvent.getQueryHandle())
                System.out.println("Prolog-Close");
        }
    }

	@Override
    public synchronized void errorNotified(JIPErrorEvent jipErrorEvent){
        synchronized(jipErrorEvent.getSource()){
            if(this.queryHandle == jipErrorEvent.getQueryHandle()){
                System.out.println("Prolog-Error: " + jipErrorEvent.getError());
                jipErrorEvent.getSource().closeQuery(this.queryHandle);
            }
        }
    }*/

}
