package nl.ou.testar.temporal.util;

import nl.ou.testar.temporal.structure.StateEncoding;
import nl.ou.testar.temporal.structure.TemporalOracle;
import nl.ou.testar.temporal.structure.TransitionEncoding;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Spot_CheckerResultsParser extends CheckerResultsParser{


    public List<TemporalOracle> parse(String rawInput) {

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
            boolean toggle=false;
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
                            StateEncoding sEnc = stateEncodings.get(Integer.parseInt(prefixLines[j].trim()));
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

                            StateEncoding sEnc = stateEncodings.get(Integer.parseInt(cycleLines[j].trim()));
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
                    Oracle.set_comments(new ArrayList<>(Arrays.asList("Encoded Formula: "+encodedFormula)));
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

                }
                Oracle.setLog_RunDate(LocalDateTime.now().toString());
                i++;
            }
        }
        return this.oracleColl;
    }
}

