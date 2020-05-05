package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.model.StateEncoding;
import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.foundation.Verdict;
import nl.ou.testar.temporal.util.Common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

//css ltsmin cannot provide counterexamples for CTL, only LTL
public class LTSMIN_LTL_ModelChecker extends ModelChecker {

     void delegatedCheck() {

        String contents =  tmodel.makeETFOutput(temporalFormalism.supportsMultiInitialStates);
        saveStringToFile(contents,this.automatonFile);
        validateAndSaveFormulas();

        //String cli = "ubuntu1804 run ~/ltsminv3.0.2/bin/etf3lts-seq  --ltl='..0..'  model.etf &> results.txt;
        //repeat for each formula: relative inefficient as the automaton has to be loaded again for very formula.
        try {
            List<String> lines = Files.readAllLines(Paths.get(formulaFile.getAbsolutePath()), StandardCharsets.UTF_8);
            boolean first = true;
            String cli;

            for (String line : lines) {
                cli = pathToExecutable + " --ltl='" + line + "' " + automat;
                    cli = cli + (first ? " &> " : "&>>") + result;
                    first = false;
                Common.RunOSChildProcess(cli);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<TemporalOracle> delegatedParseResults(String rawInput) {
        List<StateEncoding> stateEncodings = tmodel.getStateEncodings();
        Scanner scanner = new Scanner(rawInput);
         while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.matches("\\s*\\*\\* error \\*\\*\\s*")) {
                return null; // there is a line with ** error ** somewhere in the results file
            }
        }
        scanner = new Scanner(rawInput);
        scanner.useDelimiter("\\s*LTL layer: formula:\\s+");
        if (scanner.hasNext()) scanner.next(); // throw away the content before the first formula result
        List<String> formularesults = new ArrayList<>();
        while (scanner.hasNext()) formularesults.add(scanner.next());

        if ((formularesults.size() != oracleColl.size())) {
            return null;
        }
        int i = 0;
         for (String fResult : formularesults
        ) {
            TemporalOracle Oracle = oracleColl.get(i);
            i++;
            // get result status
            String formulaStatus = "ERROR";
            String encodedFormula = "";
            String  hold="Empty product with LTL!";
            String  nothold="Accepting cycle FOUND!";

            if (fResult.contains(hold)) {
                formulaStatus = "PASS";
                encodedFormula = fResult.split("\\r?\\n")[0];
            }
            else {
                if (fResult.contains(nothold)) {
                    formulaStatus = "FAIL";
                    encodedFormula = fResult.split("\\r?\\n")[0];
                }
                else {
                    //in case there is a change in the future how LTSMIN provide log details
                    System.out.println("Error parsing results from model checker");
                }
            }
            List<String> emptyList = Collections.emptyList();
            Oracle.setExampleRun_Prefix_States(emptyList);
            Oracle.setExampleRun_Prefix_Transitions(emptyList); //test only
            Oracle.set_comments(new ArrayList<>(Collections.singletonList("Encoded Formula: " + encodedFormula)));
            Oracle.setExampleRun_Cycle_States(emptyList);
            Oracle.setExampleRun_Cycle_Transitions(emptyList);
            if (formulaStatus.equals(Verdict.FAIL.toString())) Oracle.setOracle_verdict(Verdict.FAIL);
            if (formulaStatus.equals(Verdict.PASS.toString())) Oracle.setOracle_verdict(Verdict.PASS);
            Oracle.setLog_RunDate(Common.prettyCurrentDateTime());
        }
        return this.oracleColl;
    }

}

