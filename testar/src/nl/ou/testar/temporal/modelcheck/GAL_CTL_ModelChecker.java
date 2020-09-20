package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.foundation.Verdict;
import nl.ou.testar.temporal.model.StateEncoding;
import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.util.OShelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
//css: version without --witness : CTL counterexamples by its-ctl are not well described

public class GAL_CTL_ModelChecker extends ModelChecker {

      void delegatedCheck() {

        String contents = tmodel.makeGALOutput();
        saveStringToFile(contents,this.automatonFile);

        //String cli = "eclipsec.exe -i CTL_GAL_model1.gal -ctl -itsflags "--precise --backward --witness" 2> results.txt;
        // append formulas to the model file:
        Path path = Paths.get(formula);
        try {
            byte[] contentToAppend = Files.readAllBytes(path);
            Files.write(
                    Paths.get(automat), contentToAppend, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String cli = pathToExecutable;
        cli = cli + " -i " +  automat + "  -ctl "+ (counterExamples? " -itsflags \"--precise --backward --witness\"" : "");
        OShelper.RunOSChildProcess(cli,result);
    }

    public List<TemporalOracle> delegatedParseResults(String rawInput) {
        List<StateEncoding> stateEncodings = tmodel.getStateEncodings();
        Scanner scanner = new Scanner(rawInput);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.matches("\\s*Error\\s*")) {
                return null;
            }
        }
        scanner = new Scanner(rawInput);
        scanner.useDelimiter("\\s*original formula:\\s*");
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
            String formulaStatus = "ERROR";
            String encodedFormula = "";
            Scanner forumlascanner = new Scanner(fResult);
            if (forumlascanner.hasNext()) {
                encodedFormula = forumlascanner.nextLine(); //firstline contains the formula
                forumlascanner.useDelimiter("\\sFormula is\\s");
            if (forumlascanner.hasNext()) {forumlascanner.next();} //throw away
            if (forumlascanner.hasNext()) {
                formulaStatus=forumlascanner.nextLine().replaceAll(" !","");
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
    public List<String> delegatedFormulaValidation(String aliveProp, boolean parenthesesNextOperator)   {
        return FormulaVerifier.INSTANCE.rewriteCTL(oracleColl, aliveProp,parenthesesNextOperator );
    }

}

