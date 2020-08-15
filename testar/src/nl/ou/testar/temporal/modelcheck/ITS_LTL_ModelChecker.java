package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.foundation.Verdict;
import nl.ou.testar.temporal.proposition.PropositionConstants;
import nl.ou.testar.temporal.util.OShelper;

import java.util.*;

public class ITS_LTL_ModelChecker extends ModelChecker {

    // css20200309  this model check gives unexpected results: False Positive.

     void delegatedCheck() {

        String contents =  tmodel.makeETFOutput(temporalFormalism.supportsMultiInitialStates);
        saveStringToFile(contents,this.automatonFile);

        //String cli = "ubuntu1804 run ~/its/its-ltl -i model.etf -t ETF  -ltl formula.ltl -c -e &> results.txt;
        String cli = pathToExecutable;


        cli = cli + " -i " + automat +" -t ETF -LTL " + formula + " -c " + (counterExamples ? "-e" : "");
        cli = cli + " &> " + result;
        OShelper.RunOSChildProcess(cli);
    }
    public List<TemporalOracle> delegatedParseResults(String rawInput) {


//an accepting run exists false
// Formula 0 is TRUE no accepting run found.
        Scanner scanner = new Scanner(rawInput);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.matches("\\s*Error\\s*")) {
                return null;
            }
        }
        scanner = new Scanner(rawInput);
        scanner.useDelimiter("\\s*Checking formula\\s*");
        if (scanner.hasNext()) scanner.next(); // throw away the content before the first formula result
        List<String> formularesults = new ArrayList<>();
        while (scanner.hasNext()) formularesults.add(scanner.next());
        if ((formularesults.size() != oracleColl.size())) {
            return null;
        }
        int i = 0;
        boolean toggle = false;
        for (String fResult : formularesults
        ) {
            TemporalOracle Oracle = oracleColl.get(i);
            i++;
            String formulaStatus = "ERROR";
            String encodedFormula = "";
            Scanner forumlascanner = new Scanner(fResult);
            if (forumlascanner.hasNext()) {
                encodedFormula = forumlascanner.nextLine().split(":")[1]; //firstline contains the formula
                forumlascanner.useDelimiter("\\saccepting run found\\.\\s*"); // leading space and period is crucial

                if (forumlascanner.hasNext()) {
                    formulaStatus=forumlascanner.next();//.contains("TRUE")?"PASS":"FAIL";
                    //process witness?
                }
                else {//in case there is a change in the future how the checker provide log details
                    System.out.println("Error parsing results from model checker");
                }
            }
            List<String> emptyList = Collections.emptyList();
            Oracle.setExampleRun_Prefix_States(emptyList);
            Oracle.setExampleRun_Prefix_Transitions(emptyList); //test only
            Oracle.addLog("Encoded Formula: " + encodedFormula);
            Oracle.setExampleRun_Cycle_States(emptyList);
            Oracle.setExampleRun_Cycle_Transitions(emptyList);
            if (formulaStatus.contains("FALSE")) Oracle.setOracle_verdict(Verdict.FAIL);
            if (formulaStatus.contains("TRUE")) Oracle.setOracle_verdict(Verdict.PASS);
            Oracle.setLog_RunDate(OShelper.prettyCurrentDateTime());
        }
        return this.oracleColl;
    }

    public List<String> delegatedFormulaValidation()
    {
        saveFormulasForChecker(oracleColl, formulaFile, false);
        return FormulaVerifier.INSTANCE.verifyLTL(formulaFile.getAbsolutePath(), syntaxformulaFile,
                "!" + PropositionConstants.SETTING.terminalProposition);
    }
}

