package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.oracle.TemporalFormalism;
import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.foundation.Verdict;
import nl.ou.testar.temporal.proposition.PropositionConstants;
import nl.ou.testar.temporal.util.Common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
//css ltsmin cannot provide counterexamples for CTL, only LTL
//LTSMIN-CTL bug: gives a segmentation fault when checking ctl, but same model can be checked on ltl . :-)
public class LTSMIN_CTL_ModelChecker extends ModelChecker {

     void delegatedCheck() {

        String contents =  tmodel.makeETFOutput(temporalFormalism.supportsMultiInitialStates);
        saveStringToFile(contents,this.automatonFile);

        //String cli = "ubuntu1804 run ~/ltsminv3.0.2/bin/etf2lts-sym  --ctl='..0..' --ctl='..n..'  model.etf &> results.txt;
        //LTSMIN does not provide counter examples for CTL, does not allow the implies ('->') operator, crashes on large ETF models.

        String cli = pathToExecutable;
        StringBuilder sb = new StringBuilder();

        try {//formulafile to --ctl strings
            List<String> lines = Files.readAllLines(Paths.get(formulaFile.getAbsolutePath()), StandardCharsets.UTF_8);
            for (String line : lines) {
                sb.append("--ctl='").append(line).append("' ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        cli = cli + " " + sb.toString() +" " +automat+ " &> " +  result;
        Common.RunOSChildProcess(cli);
    }

    public List<TemporalOracle> delegatedParseResults(String rawInput) {
        Scanner scanner = new Scanner(rawInput);
         while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.matches("\\s*\\*\\* error \\*\\*\\s*")) {
                return null; // there is a line with ** error ** somewhere in the results file
            }
        }
        scanner = new Scanner(rawInput);
        scanner.useDelimiter("\\s*Formula\\s+");
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
            String  hold="holds for the initial state";
            String  nothold="does not hold for the initial state";

            if (fResult.contains(hold)) {
                formulaStatus = "PASS";
                encodedFormula = fResult.split(hold)[0];
            }
            else {
                if (fResult.contains(nothold)) {
                    formulaStatus = "FAIL";
                    encodedFormula = fResult.split(nothold)[0];
                }
                else {
                    //in case there is a change in the future how LTSMIN provide log details
                    System.out.println("Error parsing results from model checker");
                }
            }
            List<String> emptyList = Collections.emptyList();
            Oracle.setExampleRun_Prefix_States(emptyList);
            Oracle.setExampleRun_Prefix_Transitions(emptyList); //test only
            Oracle.addLog("Encoded Formula: " + encodedFormula);
            Oracle.setExampleRun_Cycle_States(emptyList);
            Oracle.setExampleRun_Cycle_Transitions(emptyList);
            if (formulaStatus.equals(Verdict.FAIL.toString())) Oracle.setOracle_verdict(Verdict.FAIL);
            if (formulaStatus.equals(Verdict.PASS.toString())) Oracle.setOracle_verdict(Verdict.PASS);
            Oracle.setLog_RunDate(Common.prettyCurrentDateTime());
        }
        return this.oracleColl;
    }

    public List<String> delegatedFormulaValidation()  {
        List<String> tmpformulas = FormulaVerifier.INSTANCE.rewriteCTL(oracleColl,
                "!" +PropositionConstants.SETTING.terminalProposition);
        return tmpformulas;
    }
}

