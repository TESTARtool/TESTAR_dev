package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.model.StateEncoding;
import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.model.TransitionEncoding;
import nl.ou.testar.temporal.foundation.Verdict;
import nl.ou.testar.temporal.util.OShelper;

import java.util.*;

public class SPOT_LTL_ModelChecker extends ModelChecker {


      void delegatedCheck() {

        String contents = tmodel.makeHOAOutput();
        saveStringToFile(contents,this.automatonFile);

        //String cli = "ubuntu1804 run ~/testar/spot_checker --a automaton4.txt --ff formulas-abc-100.txt  ";
        String cli = pathToExecutable;
        cli = cli + " --a " + automat + " --ff " + formula;
        if (counterExamples) cli = cli + " --witness ";
        cli = cli + " &> " + result;
        OShelper.RunOSChildProcess(cli);
    }



    public List<TemporalOracle> delegatedParseResults(String rawInput) {
        //refactor by using ANTLR?
        List<StateEncoding> stateEncodings = tmodel.getStateEncodings();
        Scanner scanner = new Scanner(rawInput);
        scanner.useDelimiter("\\s*=== Automaton\\s*");

        if (scanner.hasNext()) {scanner.next();
        }
        String automaton = "";
        if (scanner.hasNext()) {
            automaton = scanner.next();
        }

        if (automaton.contains("=== ERROR")) {
            return null;
        } else {
            List<String> formularesults = new ArrayList<>();
            scanner.useDelimiter("\\s*=== Formula\\s*");  //change the delimiter and now load chunks of formulas
            while (scanner.hasNext()) {
                formularesults.add(scanner.next());
            }
            formularesults.remove(0); // dispose the === Automaton separator
            formularesults.remove(formularesults.size() - 1);

            if ((formularesults.size() != oracleColl.size()) ) {
                return null;
            }
            int i = 0;
            for (String fResult : formularesults
            ) {
                TemporalOracle Oracle = oracleColl.get(i);
                // get result status
                String[] formulacomponents = fResult.split("\\s*===\\s*");
                String encodedFormula = formulacomponents[1];
                String encodedFormulaResult = formulacomponents[2];
                String[] formulaResult = encodedFormulaResult.split(",\\s*");
                String formulaStatus = formulaResult[0];
                if (!formulaStatus.equals("ERROR")) {

                    String[] prefixResults = formulaResult[1].split("\\s*Prefix:\\s*");
                    String[] cycleResults = formulaResult[1].split("\\s*Cycle:\\s*");

                    List<String> prefixStateList = new ArrayList<>();
                    List<String> prefixAPConjunctList = new ArrayList<>();
                    List<String> prefixTransitionList = new ArrayList<>();
                    List<String> cycleStateList = new ArrayList<>();
                    List<String> cycleAPConjunctList = new ArrayList<>();
                    List<String> cycleTransitionList = new ArrayList<>();
                    if (prefixResults.length == 2) {
                        String cleanprefix = prefixResults[1];
                        if (prefixResults[1].contains("Cycle:")) {
                            cleanprefix = prefixResults[1].split("\\s*Cycle:")[0]; //[1]: also cutoff 'witness' or counterexample'
                        }
                        String[] prefixLines = cleanprefix.split("\\n");
                        if (prefixLines.length>=2){  // only when there is  content in the prefix section
                        for (int j = 0; j < prefixLines.length; j = j + 2) {
                            int stateindex= Integer.parseInt(prefixLines[j].trim()); // was self-assignment
                            if (tmodel.getInitialStates().size()>1){ // SPOT adds the artificial state and silently increases the state count.
                                stateindex=stateindex-1;
                            }
                            StateEncoding sEnc = stateEncodings.get(stateindex);
                            prefixStateList.add(sEnc.getState());

                            String conjunctStr = prefixLines[j + 1].
                                    split("\\s*\\|\\s*")[1].    //cutoff leading pipe symbol
                                    split("\\s*\\{\\s*")[0];  //cutoff acceptanceset
                            prefixAPConjunctList.add(conjunctStr.replaceAll("\\s", "").replaceAll("ap", ""));
                        }

                        }
                    } // else : noprefix?
                    if (cycleResults.length == 2) {
                        String cleancycle = cycleResults[1];
                        if (cycleResults[1].contains("Prefix:")) {
                            cleancycle = cycleResults[1].split("Prefix:\\s*")[1]; //[1]: also cutoff 'witness' or counterexample'
                        }

                        String[] cycleLines = cleancycle.split("\\n");
                        if (cycleLines.length>=2){  // only when there is  content in the cycle section
                        for (int j = 0; j < cycleLines.length; j = j + 2) {
                            int stateindex= Integer.parseInt(cycleLines[j].trim()); // was self-assignment
                            if (tmodel.getInitialStates().size()>1){// SPOT adds the artificial state and silently increases the state count.
                                stateindex=stateindex-1;
                            }
                            StateEncoding sEnc = stateEncodings.get(stateindex);
                            cycleStateList.add(sEnc.getState());

                            String conjunctStr = cycleLines[j + 1].
                                    split("\\s*\\|\\s*")[1].    //cutoff leading pipe symbol
                                    split("\\s*\\{\\s*")[0];  //cutoff acceptanceset
                            cycleAPConjunctList.add(conjunctStr.replaceAll("\\s", "").replaceAll("ap", ""));
                        }

                        }
                    } // else: nocycle?

                    for (int j = 0; j < prefixStateList.size(); j++) {
                        if (cycleStateList.isEmpty()) {
                            System.out.println("debug:cannot complete trace, there is no cycle found for formula");
                        } else {
                            String targetState ;
                            String sourceState = prefixStateList.get(j);
                            if (j == prefixStateList.size() - 1) {
                                //get the first from cycle
                                targetState = cycleStateList.get(0);

                            } else targetState = prefixStateList.get(j + 1);
                            StateEncoding sEnc = stateEncodings.stream()
                                    .filter(enc -> sourceState.equals(enc.getState()))
                                    .findAny()
                                    .orElse(null);
                            for (TransitionEncoding t : sEnc.getTransitionColl()
                            ) {
                                if (t.getEncodedTransitionAPConjunct().equals(prefixAPConjunctList.get(j)) &&
                                        t.getTargetState().equals(targetState)) {
                                    //check target stae as model can be non-deterministic
                                    prefixTransitionList.add(t.getTransition());
                                    break;
                                }
                            }
                        }
                    }
                    Oracle.setExampleRun_Prefix_States(prefixStateList);
                    Oracle.setExampleRun_Prefix_Transitions(prefixTransitionList); //test only
                    Oracle.addLog("Encoded Formula: " + encodedFormula);
                    for (int j = 0; j < cycleStateList.size(); j++) {
                        String targetState ;
                        String sourceState = cycleStateList.get(j);
                        if (j == cycleStateList.size() - 1) {
                            //get the first from cycle
                            targetState = cycleStateList.get(0);

                        } else targetState = cycleStateList.get(j + 1);
                        StateEncoding sEnc = stateEncodings.stream()
                                .filter(enc -> sourceState.equals(enc.getState()))
                                .findAny()
                                .orElse(null);
                        for (TransitionEncoding t : sEnc.getTransitionColl()
                        ) {
                            if (t.getEncodedTransitionAPConjunct().equals(cycleAPConjunctList.get(j)) &&
                                    t.getTargetState().equals(targetState)) {
                                //check target state as model can be non-deterministic
                                cycleTransitionList.add(t.getTransition());
                                break;
                            }
                        }
                    }
                    Oracle.setExampleRun_Cycle_States(cycleStateList);
                    Oracle.setExampleRun_Cycle_Transitions(cycleTransitionList);

                    if (formulaStatus.equals(Verdict.FAIL.toString()))  Oracle.setOracle_verdict(Verdict.FAIL);
                    if (formulaStatus.equals(Verdict.PASS.toString()))  Oracle.setOracle_verdict(Verdict.PASS);
                    if (formulaStatus.equals(Verdict.ERROR.toString())) Oracle.setOracle_verdict(Verdict.ERROR);

                }
                else {//in case there is a change in the future how the checker provide log details
                    Oracle.addLog("Error parsing formula result from model checker");
                }
                Oracle.setLog_RunDate(OShelper.prettyCurrentDateTime());
                i++;
            }
        }
        return this.oracleColl;
    }

    public List<String> delegatedFormulaValidation(String aliveProp, boolean parenthesesNextOperator)
    {
        saveFormulasForChecker(oracleColl, formulaFile, false);
        return FormulaVerifier.INSTANCE.verifyLTL(formulaFile.getAbsolutePath(), syntaxformulaFile, aliveProp,parenthesesNextOperator );
    }


}


