package nl.ou.testar.temporal.util;

import nl.ou.testar.temporal.behavior.TemporalController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.net.HttpHeaders.USER_AGENT;

public class Helper {
    public static String toWSLPath(String windowsFilePath) {
        Path winpath = Paths.get(windowsFilePath);
        StringBuilder wslpath = new StringBuilder();
        wslpath.append("/mnt/");
        wslpath.append(winpath.getRoot().toString().split(":")[0].toLowerCase());  // convert C:\\ --> c
        for (int i = 0; i < winpath.getNameCount(); i++) {
            wslpath.append("/" + winpath.getName(i));
        }
        return wslpath.toString();
    }

    public static void HTTPGet(String url) {
        // HTTP GET request from https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");// optional default is GET
            con.setRequestProperty("User-Agent", USER_AGENT);//add request header
            int responseCode = con.getResponseCode(); //consume and discard responsecode

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();               //consume and discard result
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static void RunOSChildProcess(String command) {

        Process theProcess = null;
        BufferedReader inStream = null;
        BufferedReader errStream = null;
        String response;
        String errorresponse = null;

        // call the external program
        try {
            theProcess = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.err.println("Error on exec() method");
            e.printStackTrace();
        }

        // read from the called program's standard output stream
        try {
            inStream = new BufferedReader(new InputStreamReader
                    (theProcess.getInputStream()));
            errStream = new BufferedReader(new InputStreamReader
                    (theProcess.getErrorStream()));
            while ((response = inStream.readLine()) != null || (errorresponse = errStream.readLine()) != null) {
                if (response != null) {
                    System.out.println("response: " + response);
                }
                if (errorresponse != null) {
                    System.out.println("error response: " + errorresponse);
                }
            }

        } catch (IOException e) {
            System.err.println("Error on inStream.readLine()");
            e.printStackTrace();
        }
    }

    public static void LTLMC_BySPOT(String pathToExecutable, boolean toWslPath, boolean counterExamples,
                                    String automatonFile, String formulaFile, String alivePropositionLTLF, String resultsFile) {
        //String cli = "ubuntu1804 run ~/testar/spot_checker --a automaton4.txt --ff formulas-abc-100.txt --ltlf !dead ";
        String cli = pathToExecutable;
        cli = cli + " --a " + toWSLPath(automatonFile) + " --ff " + ((toWslPath) ? toWSLPath(formulaFile) : formulaFile);
        if (!alivePropositionLTLF.equals("")) cli = cli + " --ltlf " + alivePropositionLTLF;
        if (counterExamples) cli = cli + " --witness ";
        if (!resultsFile.equals("")) cli = cli + " &> " + ((toWslPath) ? toWSLPath(resultsFile) : resultsFile);
        Helper.RunOSChildProcess(cli);
    }

    public static void LTLVerifyFormula_BySPOT(String pathToExecutable, boolean toWslPath, String formulaFile, String resultsFile) {
        //String cli = "ubuntu1804 run ~/testar/spot_checker  --fonly --ff formulas-abc-100.txt ";
        String cli = pathToExecutable;
        cli = cli + " --fonly --ff " + ((toWslPath) ? toWSLPath(formulaFile) : formulaFile);
        if (!resultsFile.equals("")) cli = cli + " &> " + ((toWslPath) ? toWSLPath(resultsFile) : resultsFile);
        Helper.RunOSChildProcess(cli);
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
        cli = cli + ((toWslPath) ? toWSLPath(automatonFile) : automatonFile);
        if (!resultsFile.equals("")) cli = cli + " &> " + ((toWslPath) ? toWSLPath(resultsFile) : resultsFile);
        Helper.RunOSChildProcess(cli);
    }


    public static void LTLMC_ByLTSMIN(String pathToExecutable, boolean toWslPath, boolean counterExamples,
                                      String automatonFile, String formulaFile, String resultsFile) {
        //String cli = "ubuntu1804 run ~/ltsminv3.0.2/bin/etf3lts-seq  --ltl='..0..'  model.etf &> results.txt;
        //repeat for each formula: relative inefficient as the automaton has to be loaded again for very formula.
        if (pathToExecutable.equals("")) {
            String message = " **error ERROR : This modelchecker was not enabled";
            File messageFile = new File(resultsFile);
            TemporalController.saveStringToFile(message, messageFile);
        }
        else {

            try {
                List<String> lines = Files.readAllLines(Paths.get(formulaFile), StandardCharsets.UTF_8);
                boolean first = true;
                String cli = "";
                String cli_automaton = ((toWslPath) ? toWSLPath(automatonFile) : automatonFile);// no witness nor counterexamples
                String cli_resultsfile = " " + ((toWslPath) ? toWSLPath(resultsFile) : resultsFile);
                for (String line : lines) {
                    cli = pathToExecutable + " --ltl='" + line + "' " + cli_automaton;
                    if (!resultsFile.equals("")) {
                        cli = cli + (first ? " &> " : "&>>") + cli_resultsfile;
                        first = false;
                    }
                    Helper.RunOSChildProcess(cli);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void CTLMC_ByITS(String pathToExecutable, boolean toWslPath, boolean counterExamples,
                                   String automatonFile, String formulaFile, String resultsFile) {
        //String cli = "ubuntu1804 run ~/its/its-ctl -i model.etf -t ETF  -ctl formula.ctl --witness &> results.txt;
        //counterexamples are not shown in the vizualizer. as they are
        //not well documented, hard to parse, not complete traces and difficult to understand
        String cli = pathToExecutable;
        cli = cli + " -i " + toWSLPath(automatonFile) + " -t ETF -ctl " +
                ((toWslPath) ? toWSLPath(formulaFile) : formulaFile) + (counterExamples ? "" : "");// no witness
        if (!resultsFile.equals("")) cli = cli + " &> " + ((toWslPath) ? toWSLPath(resultsFile) : resultsFile);
        Helper.RunOSChildProcess(cli);
    }

    public static void LTLMC_ByITS(String pathToExecutable, boolean toWslPath, boolean counterExamples,
                                   String automatonFile, String formulaFile, String resultsFile) {
        //String cli = "ubuntu1804 run ~/its/its-ltl -i model.etf -t ETF  -ltl formula.ltl -c -e &> results.txt;
        String cli = pathToExecutable;
        cli = cli + " -i " + toWSLPath(automatonFile) + " -t ETF -LTL " + ((toWslPath) ? toWSLPath(formulaFile) : formulaFile) +
                " -c " + (counterExamples ? "-e" : "");
        if (!resultsFile.equals("")) cli = cli + " &> " + ((toWslPath) ? toWSLPath(resultsFile) : resultsFile);
        Helper.RunOSChildProcess(cli);
    }

    public static String CurrentDateToFolder() {
        Date aDate = Calendar.getInstance().getTime();
        return DateToFolder(aDate);
    }

    public static String DateToFolder(Date aDate) {
        // inspired from https://alvinalexander.com/java/simpledateformat-convert-date-to-string-formatted-parse
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return formatter.format(aDate);
    }

}
