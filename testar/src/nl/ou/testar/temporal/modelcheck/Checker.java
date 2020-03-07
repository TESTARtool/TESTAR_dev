package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.behavior.TemporalController;
import nl.ou.testar.temporal.util.Common;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Checker {


    public static void LTLMC_BySPOT(String pathToExecutable, boolean toWslPath, boolean counterExamples,
                                    String automatonFile, String formulaFile, String alivePropositionLTLF, String resultsFile) {
        //String cli = "ubuntu1804 run ~/testar/spot_checker --a automaton4.txt --ff formulas-abc-100.txt --ltlf !dead ";
        String cli = pathToExecutable;
        cli = cli + " --a " + Common.toWSLPath(automatonFile) + " --ff " + ((toWslPath) ? Common.toWSLPath(formulaFile) : formulaFile);
        if (!alivePropositionLTLF.equals("")) cli = cli + " --ltlf " + alivePropositionLTLF;
        if (counterExamples) cli = cli + " --witness ";
        if (!resultsFile.equals("")) cli = cli + " &> " + ((toWslPath) ? Common.toWSLPath(resultsFile) : resultsFile);
        Common.RunOSChildProcess(cli);
    }

    public static void LTLVerifyFormula_BySPOT(String pathToExecutable, boolean toWslPath, String formulaFile, String resultsFile) {
        //String cli = "ubuntu1804 run ~/testar/spot_checker  --fonly --ff formulas-abc-100.txt ";
        String cli = pathToExecutable;
        cli = cli + " --fonly --ff " + ((toWslPath) ? Common.toWSLPath(formulaFile) : formulaFile);
        if (!resultsFile.equals("")) cli = cli + " &> " + ((toWslPath) ? Common.toWSLPath(resultsFile) : resultsFile);
        Common.RunOSChildProcess(cli);
    }

    public static String LTLParse_VerifiedFormula_BySPOT(String resultsFile, boolean keepLTLFModelVariant) {
        Scanner scanner = new Scanner(resultsFile);
        scanner.useDelimiter("\\s*===\\s*");

        if (scanner.hasNext()) {
            scanner.next();
            scanner.next(); //throw away 2 headerlines
        }
        String formulaline = "";
        String formula = "";

        StringBuilder formulasParsed = new StringBuilder();

        while (scanner.hasNext()) {
            String testtoken = scanner.next();
            if (testtoken.startsWith("Formula")) {
                String endline = scanner.nextLine();
                if (endline.contains("LTL model-check End")) {
                    break;
                }
                formulaline = endline; //not the end but a new formula
                int indexmodel = formulaline.lastIndexOf("[LTLF Model]");
                int indextrace = formulaline.lastIndexOf("[LTLF G&V]");
                if (keepLTLFModelVariant) {
                    formula = indexmodel != -1 ? formulaline.substring(indexmodel) : formulaline.substring(0, indextrace - 1);
                } else {
                    formula = formulaline.substring(indextrace, indexmodel - 1);//keep the trace variant
                }
                formulasParsed.append(formula).append("\n");
            }
            System.out.println("unexpected token <" + testtoken + "> to parse in File: " + resultsFile);
        }
        return formulasParsed.toString();
    }


    public static void CTLMC_ByLTSMIN(String pathToExecutable, boolean toWslPath, String automatonFile,
                                      String formulaFile, String resultsFile) {
        //String cli = "ubuntu1804 run ~/ltsminv3.0.2/bin/etf2lts-sym  --ctl='..0..' --ctl='..n..'  model.etf &> results.txt;
        //LTSMIN does not provide counter examles for CTL, does not allow the implies ('->') operator, crashes on some ETF models.
        String cli = pathToExecutable;
        StringBuilder sb = new StringBuilder();
        try {//formulafile to --ctl strings
            List<String> lines = Files.readAllLines(Paths.get(formulaFile), StandardCharsets.UTF_8);
            for (String line : lines) {
                sb.append("--ctl='").append(line).append("' ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String formulalist = sb.toString();
        cli = cli + " " + formulalist;
        cli = cli + ((toWslPath) ? Common.toWSLPath(automatonFile) : automatonFile);
        if (!resultsFile.equals("")) cli = cli + " &> " + ((toWslPath) ? Common.toWSLPath(resultsFile) : resultsFile);
        Common.RunOSChildProcess(cli);
    }


    public static void LTLMC_ByLTSMIN(String pathToExecutable, boolean toWslPath, boolean counterExamples,
                                      String automatonFile, String formulaFile, String resultsFile) {
        //String cli = "ubuntu1804 run ~/ltsminv3.0.2/bin/etf3lts-seq  --ltl='..0..'  model.etf &> results.txt;
        //repeat for each formula: relative inefficient as the automaton has to be loaded again for very formula.
            try {
                List<String> lines = Files.readAllLines(Paths.get(formulaFile), StandardCharsets.UTF_8);
                boolean first = true;
                String cli;
                String cli_automaton = ((toWslPath) ? Common.toWSLPath(automatonFile) : automatonFile);// no witness nor counterexamples
                String cli_resultsfile = " " + ((toWslPath) ? Common.toWSLPath(resultsFile) : resultsFile);
                for (String line : lines) {
                    cli = pathToExecutable + " --ltl='" + line + "' " + cli_automaton;
                    if (!resultsFile.equals("")) {
                        cli = cli + (first ? " &> " : "&>>") + cli_resultsfile;
                        first = false;
                    }
                    Common.RunOSChildProcess(cli);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public static void CTLMC_ByITS(String pathToExecutable, boolean toWslPath, boolean counterExamples,
                                   String automatonFile, String formulaFile, String resultsFile) {
        //String cli = "ubuntu1804 run ~/its/its-ctl -i model.etf -t ETF  -ctl formula.ctl --witness &> results.txt;
        //counterexamples are not shown in the vizualizer. as they are
        //not well documented, hard to parse, not complete traces and difficult to understand
        String cli = pathToExecutable;
        cli = cli + " -i " + Common.toWSLPath(automatonFile) + " -t ETF -ctl " +
                ((toWslPath) ? Common.toWSLPath(formulaFile) : formulaFile) + (counterExamples ? "" : "");// no witness
        if (!resultsFile.equals("")) cli = cli + " &> " + ((toWslPath) ? Common.toWSLPath(resultsFile) : resultsFile);
        Common.RunOSChildProcess(cli);
    }

    public static void LTLMC_ByITS(String pathToExecutable, boolean toWslPath, boolean counterExamples,
                                   String automatonFile, String formulaFile, String resultsFile) {
        //String cli = "ubuntu1804 run ~/its/its-ltl -i model.etf -t ETF  -ltl formula.ltl -c -e &> results.txt;
        String cli = pathToExecutable;
        cli = cli + " -i " + Common.toWSLPath(automatonFile) + " -t ETF -LTL " + ((toWslPath) ? Common.toWSLPath(formulaFile) : formulaFile) +
                " -c " + (counterExamples ? "-e" : "");
        if (!resultsFile.equals("")) cli = cli + " &> " + ((toWslPath) ? Common.toWSLPath(resultsFile) : resultsFile);
        Common.RunOSChildProcess(cli);
    }

}
