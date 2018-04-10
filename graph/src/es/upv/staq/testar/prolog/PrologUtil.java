/***************************************************************************************************
*
* Copyright (c) 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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
	
	@SafeVarargs
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
			System.out.println("[PrologUtil] >>> Prolog: no solutions <<<\n");
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
			System.out.println("[PrologUtil] Prolog-Solution (" + (idx++) + "):\n" + s);
		System.out.println("[PrologUtil]  ");
	}

}
