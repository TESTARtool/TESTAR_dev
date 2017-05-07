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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility methods for JIProlog.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class PrologUtil {

    /*public static String normaliseQuotedAtoms(String atom){
		if (atom.startsWith("[")){ // atoms with quotes are returned as an array: [int, ..., int]
			StringTokenizer st = new StringTokenizer(atom.substring(1, atom.length()-1),",");
			StringBuilder sb = new StringBuilder();
			while (st.hasMoreTokens())
				sb.append((char) new Integer(st.nextToken()).intValue());
			return sb.toString();
		}
		else return atom;
	}*/
	
	public static String setToString(Set<String>... sets){
		StringBuilder sb = new StringBuilder();
		for (Set<String> set : sets){
			if (set != null){
				for (String s : set)
					sb.append(s + "\n");
			}
		}
		return sb.toString();
	}
	
	public static List<String> getSolutions(String var, List<List<String>> solutions){
		List<String> varSolutions = new ArrayList<String>();
		boolean b, varSol;
		for (List<String> solution : solutions){
			b = true; // is variable?
			varSol = false;
			for (String s : solution){
				if (b){
					if (s.equals(var))
						varSol = true;
				} else if (varSol){
					varSolutions.add(s);
					varSol = false;
				}
				b = !b;
			}			
		}
		return varSolutions;
	}
	
	public static void printSolutions(List<List<String>> solutions){
		if (solutions == null || solutions.isEmpty()){
			System.out.println(">>> Prolog: no solutions <<<\n");
			return;
		}
		Set<String> solSet = new HashSet<String>(); // fix equal solutions duplication
		StringBuilder sol;
		boolean b; // is variable?
		for (List<String> solution : solutions){
			sol = new StringBuilder();
			b = true;
			for (String s : solution){
				if (b)
					sol.append("\t" + s + " = ");
				else
					sol.append(s + "\n");
				b = !b;
			}
			solSet.add(sol.toString());
		}
		int idx = 1;
		for (String s : solSet)
			System.out.println("Prolog-Solution (" + (idx++) + "):\n" + s);
		System.out.println("");
	}

}
