package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.util.Common;
import java.io.*;
import java.util.*;

public class FormulaVerifier {



    public static List<String> verifyLTL(String pathToExecutable, boolean toWslPath, String formulaFilePath, File resultsFile) {
        //String cli = "ubuntu1804 run ~/testar/spot_checker  --fonly --ff formulas-abc-100.txt ";
        String cli = pathToExecutable;
        String cli_resultsfile = " " + ((toWslPath) ? Common.toWSLPath(resultsFile.getAbsolutePath()) : resultsFile.getAbsolutePath());
        String cli_formulafile = " " + ((toWslPath) ? Common.toWSLPath(formulaFilePath) : formulaFilePath);

        cli = cli + " --fonly --ff " +  cli_formulafile;
        cli = cli + " &> " + cli_resultsfile;
        Common.RunOSChildProcess(cli);
        return parse(resultsFile);

    }



    private static List<String> parse(File rawInput) {
        boolean keepLTLFModelVariant=true;
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(rawInput))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException f) {
            f.printStackTrace();
        }
        Scanner scanner = new Scanner(contentBuilder.toString());
        scanner.useDelimiter("\\s*===\\s*");

        if (scanner.hasNext()) {
            scanner.next();
            scanner.next(); //throw away 2 headerlines
        }
        String formulaline;
        String formula;

        List<String> formulasParsed = new ArrayList<>();

        while (scanner.hasNext()) {
            String testtoken = scanner.next();
            if (testtoken.startsWith("Formula")) {
                String endline = scanner.next();
                if (endline.contains("LTL model-check End")) {
                    break;
                }
                formulaline = endline; //not the end but a new formula
                String modelVariant = "[LTLF Modelvariant: ";
                String traceVariant = "[LTLF G&V-2013 variant: ";

                int indexmodel = formulaline.lastIndexOf(modelVariant);
                int indextrace = formulaline.lastIndexOf(traceVariant);
                if (keepLTLFModelVariant) {
                    formula = indexmodel != -1 ? formulaline.substring(indexmodel+modelVariant.length()) : formulaline.substring(0, indextrace - 1);
                } else {
                    formula = formulaline.substring(indextrace+traceVariant.length(), indexmodel - 1);//keep the trace variant
                }
                formula=formula.substring(0,formula.length()-1);

                formulasParsed.add(formula);
                scanner.next(); // read the verdict and throw away
            }
            else {
                System.out.println("unexpected token <" + testtoken + "> to parse in File:" + rawInput.getName());
            }
        }
        return formulasParsed;
    }
}


