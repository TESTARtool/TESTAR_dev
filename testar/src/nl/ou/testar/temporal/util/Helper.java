package nl.ou.testar.temporal.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    public static void HTTPGet(String url){
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
    public static  void LTLModelCheck(String pathToExecutable, boolean toWslPath, String automatonFile, String formulaFile, String alivePropositionLTLF, String resultsFile) {
        //String cli = "ubuntu1804 run ~/testar/spot_checker --a automaton4.txt --ff formulas-abc-100.txt --ltlf !dead --o results.txt";
        String  cli = pathToExecutable;
        if(toWslPath) {
            cli = cli + " --a " + toWSLPath(automatonFile) + " --ff " + toWSLPath(formulaFile);
            if (!alivePropositionLTLF.equals("")) cli = cli + " --ltlf " + alivePropositionLTLF;
            if (!resultsFile.equals("")) cli = cli + " --o " + toWSLPath(resultsFile);
        }else{
            cli = cli + " --a " + (automatonFile) + " --ff " + (formulaFile);
            if (!alivePropositionLTLF.equals("")) cli = cli + " --ltlf " + alivePropositionLTLF;
            if (!resultsFile.equals("")) cli = cli + " --o " + (resultsFile);
        }

        Helper.RunOSChildProcess(cli);
    }
    public static  void CTLModelCheck(String pathToExecutable, boolean toWslPath, String automatonFile, String formulaFile,  String resultsFile) {
        //String cli = "ubuntu1804 run ~/ltsminv3.0.2/bin/etf3lts-sym  --ctl='A[](E<>(ap123=="true")))' --ctl='E<>(!ap321=="false")' -- ctl='...' model.etf &> results.txt;


        String  cli = pathToExecutable;
        File rFile = new File(formulaFile);
        StringBuilder sb = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(formulaFile),StandardCharsets.UTF_8);
            for (String line : lines)  { sb.append("--ctl='").append(line).append("' "); }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //formulafile to --ctl strings
        String formulalist=sb.toString();
        cli = cli + " "+formulalist;
        if(toWslPath) {
            cli = cli+  toWSLPath(automatonFile);
            if (!resultsFile.equals("")) cli = cli + " &> " + toWSLPath(resultsFile);
        }else{
            cli = cli  + (automatonFile) ;
            if (!resultsFile.equals("")) cli = cli + " &> " + (resultsFile);
        }
        Helper.RunOSChildProcess(cli);
    }
    public static  void CTLModelCheckV2(String pathToExecutable, boolean toWslPath, String automatonFile, String formulaFile,  String resultsFile) {
        //String cli = "ubuntu1804 run ~/its/its-ctl -i model.etf -t ETF  -ctl formula.ctl --witness &> results.txt;
        String  cli = pathToExecutable;
        if(toWslPath) {
            cli = cli+ " -i "+ toWSLPath(automatonFile)+" -t ETF -ctl "+toWSLPath(formulaFile);
            if (!resultsFile.equals("")) cli = cli + " &> " + toWSLPath(resultsFile);
        }else{
            cli = cli+ " -i "+ automatonFile+" -t ETF -ctl "+formulaFile;
            if (!resultsFile.equals("")) cli = cli + " &> " + (resultsFile);
        }
        Helper.RunOSChildProcess(cli);
    }

    public static String CurrentDateToFolder(){
        Date aDate = Calendar.getInstance().getTime();
        return DateToFolder(aDate);
    }
    public static String DateToFolder(Date aDate){
        // inspired from https://alvinalexander.com/java/simpledateformat-convert-date-to-string-formatted-parse
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return formatter.format(aDate);
    }

}