package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.oracle.TemporalPatternBase;
import nl.ou.testar.temporal.util.Common;
import nl.ou.testar.temporal.util.StringFinder;

import java.io.*;
import java.util.*;

public enum FormulaVerifier {
    INSTANCE;//singleton
    private String pathToExecutable;



    private boolean toWslPath;

    public void setPathToExecutable(String pathToExecutable) {
        this.pathToExecutable = pathToExecutable;
    }
    public void setToWslPath(boolean toWslPath) {
        this.toWslPath = toWslPath;
    }


    public   List<String> verifyLTL(  String formulaFilePath, File resultsFile, String aliveprop) {
        //String cli = "ubuntu1804 run ~/testar/spot_checker  --fonly --ff formulas-abc-100.txt ";
        String cli = pathToExecutable;
        String cli_resultsfile = " " + ((toWslPath) ? Common.toWSLPath(resultsFile.getAbsolutePath()) : resultsFile.getAbsolutePath());
        String cli_formulafile = " " + ((toWslPath) ? Common.toWSLPath(formulaFilePath) : formulaFilePath);
        String cli_ltlf=" --ltlf "+aliveprop;
        cli = cli + " --fonly --ff " +  cli_formulafile+cli_ltlf;
        cli = cli + " &> " + cli_resultsfile;
        Common.RunOSChildProcess(cli);
        return parse(resultsFile);

    }
    public   List<String> rewriteCTL(List<TemporalOracle> temporalOracleList,String aliveProp) {
        List<String> formulas=new ArrayList<>();

        int j = 0;
        for (TemporalOracle ora : temporalOracleList
        ) {
            // fragile: 'AX(' 'AX (' is detected, but  'AX  ('  is not
            // requires that formulas are fully parenthesised!!!

                TemporalPatternBase pat = ora.getPatternBase();
                String formula = pat.getPattern_Formula();
                String prepend= aliveProp+" & ";
                //∀(Φ W Ψ) =   ¬∃( (Φ ∧ ¬Ψ) U(¬Φ ∧ ¬Ψ) )
            String phi = aliveProp;
            String theta ="AG(!"+aliveProp+")";
            //String append= " & (A("+aliveProp+" U (AG (!"+aliveProp+")))";
            String append = " & !(E(("+phi+" & "+ "!("+theta+")) U (!("+phi+") & "+ "!("+theta+"))))";
            String newformula=formula;
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"AF(", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"EF(", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"AG(", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"EG(", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"AX(", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"EX(", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"U(", "(!" + aliveProp + ") | ", ")");
           // newformula= StringFinder.findOpeningParenthesisAndInsert(newformula,")W", "(!" + aliveProp + ") | ", ")");

            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"AF (", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"EF (", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"AG (", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"EG (", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"AX (", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"EX (", "(!" + aliveProp + ") | ", ")");
            newformula= StringFinder.findClosingParenthesisAndInsert(newformula,"U (", "(!" + aliveProp + ") | ", ")");
           // newformula= StringFinder.findOpeningParenthesisAndInsert(newformula,") W", "(!" + aliveProp + ") | ", ")");

            // no support is given for W,R or M
            newformula=prepend+newformula+append;
                formulas.add(newformula);
            j++;
        }


        return formulas;

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


