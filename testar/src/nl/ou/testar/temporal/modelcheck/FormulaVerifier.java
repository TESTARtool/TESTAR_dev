package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.oracle.TemporalPatternBase;
import nl.ou.testar.temporal.proposition.PropositionConstants;
import nl.ou.testar.temporal.util.OShelper;
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
        String cli_resultsfile = " " + ((toWslPath) ? OShelper.toWSLPath(resultsFile.getAbsolutePath()) : resultsFile.getAbsolutePath());
        String cli_formulafile = " " + ((toWslPath) ? OShelper.toWSLPath(formulaFilePath) : formulaFilePath);

        String cli_ltlf=!aliveprop.equals("") ? " --ltlf "+ aliveprop:"";
        cli = cli + " --fonly --ff " +  cli_formulafile+cli_ltlf;
        cli = cli + " &> " + cli_resultsfile;
        OShelper.RunOSChildProcess(cli);
        return parse(resultsFile,!aliveprop.equals(""));

    }
    public   List<String> rewriteCTL(List<TemporalOracle> temporalOracleList,String aliveProp) {
        List<String> formulas=new ArrayList<>();
        for (TemporalOracle ora : temporalOracleList
        ) {
            // fragile: 'AX(' 'AX (' is detected, but  'AX  (' is not. Requirement: formulas are fully parenthesised!!
            TemporalPatternBase pat = ora.getPatternBase();
            String formula= pat.getPattern_Formula();
            if (!aliveProp.equals("")) {
                String prepend = aliveProp + " & ";
                //∀(Φ W Ψ) =   ¬∃( (Φ ∧ ¬Ψ) U(¬Φ ∧ ¬Ψ) )
                String phi = aliveProp;
                String theta = "AG(!" + aliveProp + ")";
                String append = " & !(E((" + phi + " & " + "!(" + theta + ")) U (!(" + phi + ") & " + "!(" + theta + "))))";
                String newformula = formula;
                newformula = StringFinder.findClosingAndInsert(newformula, "AF(", "!" + aliveProp + " | ");
                newformula = StringFinder.findClosingAndInsert(newformula, "EF(", "" + aliveProp + " & ");
                newformula = StringFinder.findClosingAndInsert(newformula, "AG(", "!" + aliveProp + " | ");
                newformula = StringFinder.findClosingAndInsert(newformula, "EG(", "" + aliveProp + " & ");
                newformula = StringFinder.findClosingAndInsert(newformula, "AX(", "!" + aliveProp + " | ");
                newformula = StringFinder.findClosingAndInsert(newformula, "EX(", "" + aliveProp + " & ");
                newformula = StringFinder.findUntilAndInsert(newformula, "!" + aliveProp + " | ", "" + aliveProp + " & ");
                // no support is given for W,R or M
                // newformula= StringFinder.findOpeningParenthesisAndInsert(newformula,")W", "(!" + aliveProp + ") | ", ")");

                newformula = StringFinder.findClosingAndInsert(newformula, "AF (", "!" + aliveProp + " | ");
                newformula = StringFinder.findClosingAndInsert(newformula, "EF (", "" + aliveProp + " & ");
                newformula = StringFinder.findClosingAndInsert(newformula, "AG (", "!" + aliveProp + " | ");
                newformula = StringFinder.findClosingAndInsert(newformula, "EG (", "" + aliveProp + " & ");
                newformula = StringFinder.findClosingAndInsert(newformula, "AX (", "!" + aliveProp + " | ");
                newformula = StringFinder.findClosingAndInsert(newformula, "EX (", "" + aliveProp + " & ");
                newformula = newformula.replaceAll("!!", ""); // remove double negations
                newformula = prepend + newformula + append;
                formulas.add(newformula);
            }else{
                formulas.add(formula);// no modification
            }
        }
        return formulas;
    }


    private static List<String> parse(File rawInput,boolean LTLFinite) {
        //refactor by using ANTLR?
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
               String syntaxVerdict = scanner.next();//read the verdict
               if (syntaxVerdict.contains("ERROR")) {
                   formula = "false";
               }
               else {
                   formulaline = endline; //not the end but a new formula
                   String modelVariant = "[LTLfl: ";
                   String traceVariant = "[LTLf: ";
                   //search can be refactored and choice of the LTL subtype can be improved
                   //currently it offers only the genuine LTL formula or the LTLfl- variant.
                   int indexmodel = formulaline.lastIndexOf(modelVariant);
                   int indextrace = formulaline.lastIndexOf(traceVariant);

                   if (!LTLFinite) {
                       formula = formulaline.substring(0, indextrace - 1);
                   } else {// keep fl- variant. this is last part of the string
                       formula = indexmodel != -1 ? formulaline.substring(indexmodel + modelVariant.length()) : formulaline.substring(0, indextrace - 1);
                   }
                   formula = formula.substring(0, formula.length() - 1);
               }
                formulasParsed.add(formula);
                //scanner.next(); // read the verdict and throw away
            }
            else {
                System.out.println("unexpected token <" + testtoken + "> to parse in File:" + rawInput.getName());
            }
        }
        return formulasParsed;
    }
}


