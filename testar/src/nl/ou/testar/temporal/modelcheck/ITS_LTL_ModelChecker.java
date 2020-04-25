package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.model.TemporalModel;
import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.foundation.Verdict;
import nl.ou.testar.temporal.util.Common;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

public class ITS_LTL_ModelChecker extends ModelChecker {

    // css20200309  this model check gives unexpected results: False Positive.

    public List<TemporalOracle> check(String pathToExecutable, boolean toWslPath, boolean counterExamples,
                                      String automatonFilePath, String formulaFilePath, File resultsFile, TemporalModel tModel, List<TemporalOracle> oracleList) {
        //String cli = "ubuntu1804 run ~/its/its-ltl -i model.etf -t ETF  -ltl formula.ltl -c -e &> results.txt;
        String cli = pathToExecutable;
        String cli_automaton = ((toWslPath) ? Common.toWSLPath(automatonFilePath) : automatonFilePath);
        String cli_resultsfile = " " + ((toWslPath) ? Common.toWSLPath(resultsFile.getAbsolutePath()) : resultsFile.getAbsolutePath());
        String cli_formulafile = " " + ((toWslPath) ? Common.toWSLPath(formulaFilePath) : formulaFilePath);

        cli = cli + " -i " + cli_automaton +" -t ETF -LTL " + cli_formulafile + " -c " + (counterExamples ? "-e" : "");
        cli = cli + " &> " + cli_resultsfile;
        Common.RunOSChildProcess(cli);
        setTmodel(tModel);
        setOracleColl(oracleList);
        return parseResultsFile(resultsFile);
    }
    public List<TemporalOracle> parseResultsString(String rawInput) {


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
            Oracle.set_comments(new ArrayList<>(Collections.singletonList("Encoded Formula: " + encodedFormula)));
            Oracle.setExampleRun_Cycle_States(emptyList);
            Oracle.setExampleRun_Cycle_Transitions(emptyList);
            if (formulaStatus.contains("FALSE")) Oracle.setOracle_verdict(Verdict.FAIL);
            if (formulaStatus.contains("TRUE")) Oracle.setOracle_verdict(Verdict.PASS);
            Oracle.setLog_RunDate(LocalDateTime.now().toString());
        }
        return this.oracleColl;
    }
}

