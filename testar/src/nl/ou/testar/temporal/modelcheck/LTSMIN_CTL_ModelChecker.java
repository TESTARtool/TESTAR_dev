package nl.ou.testar.temporal.modelcheck;

import com.google.common.collect.Lists;
import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.util.foundation.Verdict;
import nl.ou.testar.temporal.util.io.OShelper;

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
        //repeat for chunks of 100 formulas max: relative inefficient as the automaton has to be loaded again for very formula.
        //length of commandline is limited by Java.io. (in linux the limit is much larger :-) )
        int chunksize=50; // to be safe

         try {
             List<String> lines = Files.readAllLines(Paths.get(formulaFile.getAbsolutePath()), StandardCharsets.UTF_8);
             List<List<String>> chunks = Lists.partition(lines,chunksize);
             boolean first = true;
             for (List<String> chunk : chunks){//formulafile to --ctl strings
                 String cli = pathToExecutable;
                 StringBuilder sb = new StringBuilder();
                 for (String line : chunk) {
                     sb.append("--ctl='").append(line).append("' ");
                 }
                 cli = cli + " " + sb.toString() +" " +automat;
                 cli = cli + (first ? " &> " : "&>>") + result;
                 first = false;
                 OShelper.RunOSChildProcess(cli);
            }

         } catch (IOException e) {
             e.printStackTrace();
         }
    }






    public List<TemporalOracle> delegatedParseResults(String rawInput) {
        Scanner scanner = new Scanner(rawInput);
         while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.matches(".*\\s*\\*\\* error \\*\\*\\s*.*")) {
                return null; // there is a line with ** error ** somewhere in the results file
            }
        }
        scanner = new Scanner(rawInput);
        scanner.useDelimiter("\\setf2lts-sym: Formula\\s+");
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
                    Oracle.addLog("Error parsing formula result from model checker");
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
            if (formulaStatus.equals(Verdict.ERROR.toString())) Oracle.setOracle_verdict(Verdict.ERROR);

            Oracle.setLog_RunDate(OShelper.prettyCurrentDateTime());
        }
        return this.oracleColl;
    }

    public List<String> delegatedFormulaValidation(String aliveProp, boolean parenthesesNextOperator)   {
        return FormulaVerifier.INSTANCE.rewriteCTL(oracleColl, aliveProp,parenthesesNextOperator );
    }
}

