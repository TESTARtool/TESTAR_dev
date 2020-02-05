package nl.ou.testar.temporal.util;

import nl.ou.testar.temporal.structure.TemporalOracle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class SPOT_LTLFormula_ResultsParser {


    public static  String parse(File rawInput, boolean keepLTLFModelVariant) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(rawInput))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException f) {
            f.printStackTrace();
        }
        return parse(contentBuilder.toString(),keepLTLFModelVariant);

    }


    public  static String parse(String resultsFile, boolean keepLTLFModelVariant) {
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
                    formula = formulaline.substring(indextrace,indexmodel - 1);//keep the trace variant
                }
                formulasParsed.append(formula).append("\n");
            }
            System.out.println("unexpected token <" + testtoken + "> to parse in File: " + resultsFile);
        }
        return formulasParsed.toString();
    }


}
