package nl.ou.testar.temporal.modelcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SPOT_LTLFormula_ResultsParser {


    public static List<String> parse(File rawInput, boolean keepLTLFModelVariant) {
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
        String formulaline = "";
        String formula = "";

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



