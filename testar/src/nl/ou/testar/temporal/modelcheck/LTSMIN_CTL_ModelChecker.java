package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.model.TemporalModel;
import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.foundation.Verdict;
import nl.ou.testar.temporal.util.Common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
//css ltsmin cannot provide counterexamples for CTL, only LTL
//LTSMIN-CTL bug: gives a segmentation fault when checking ctl, but same model can be checked on ltl . :-)
public class LTSMIN_CTL_ModelChecker extends ModelChecker {

    public List<TemporalOracle> check(String pathToExecutable, boolean toWslPath, boolean counterExamples,
                                      String automatonFilePath, String formulaFilePath, File resultsFile, TemporalModel tModel, List<TemporalOracle> oracleList) {
        //String cli = "ubuntu1804 run ~/ltsminv3.0.2/bin/etf2lts-sym  --ctl='..0..' --ctl='..n..'  model.etf &> results.txt;
        //LTSMIN does not provide counter examples for CTL, does not allow the implies ('->') operator, crashes on some ETF models.
        String cli = pathToExecutable;
        StringBuilder sb = new StringBuilder();
        try {//formulafile to --ctl strings
            List<String> lines = Files.readAllLines(Paths.get(formulaFilePath), StandardCharsets.UTF_8);
            for (String line : lines) {
                sb.append("--ctl='").append(line).append("' ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String formulalist = sb.toString();
        String cli_automaton = ((toWslPath) ? Common.toWSLPath(automatonFilePath) : automatonFilePath);
        String cli_resultsfile = " " + ((toWslPath) ? Common.toWSLPath(resultsFile.getAbsolutePath()) : resultsFile.getAbsolutePath());
        cli = cli + " " + formulalist;
        cli = cli + cli_automaton;
        cli = cli + " &> " +  cli_resultsfile;
        Common.RunOSChildProcess(cli);

        setTmodel(tModel);
        setOracleColl(oracleList);
        return parseResultsFile(resultsFile);
    }

    public List<TemporalOracle> parseResultsString(String rawInput) {
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
                    formulaStatus = "PASS";
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
            Oracle.set_comments(new ArrayList<>(Collections.singletonList("Encoded Formula: " + encodedFormula)));
            Oracle.setExampleRun_Cycle_States(emptyList);
            Oracle.setExampleRun_Cycle_Transitions(emptyList);
            if (formulaStatus.equals(Verdict.FAIL.toString())) Oracle.setOracle_verdict(Verdict.FAIL);
            if (formulaStatus.equals(Verdict.PASS.toString())) Oracle.setOracle_verdict(Verdict.PASS);
            Oracle.setLog_RunDate(LocalDateTime.now().toString());
        }
        return this.oracleColl;
    }
}

