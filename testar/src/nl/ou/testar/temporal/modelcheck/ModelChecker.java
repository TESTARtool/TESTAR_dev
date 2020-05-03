package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.behavior.TemporalController;
import nl.ou.testar.temporal.model.TemporalModel;
import nl.ou.testar.temporal.oracle.TemporalFormalism;
import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.oracle.TemporalPatternBase;
import nl.ou.testar.temporal.util.Common;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public abstract class ModelChecker {

    protected TemporalModel tmodel;
    protected List<TemporalOracle> oracleColl;
    protected TemporalFormalism temporalFormalism;
    protected File automatonFile;
    protected File formulaFile;
    protected File resultsFile;
    protected File syntaxformulaFile;

    protected boolean verbose;
    protected String pathToExecutable;
    protected boolean toWslPath;
   protected boolean counterExamples;
   protected String outputDir;
    protected String automat;
    protected String formula;
    protected String result;

    public ModelChecker() { }



    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setTemporalFormalism(TemporalFormalism temporalFormalism) {
        this.temporalFormalism = temporalFormalism;
    }
    public void setTmodel(TemporalModel tmodel) {  this.tmodel = tmodel;   }

    public void setOracleColl(List<TemporalOracle> oracleColl) {

        this.oracleColl = new ArrayList<>();
        for (TemporalOracle tOracle : oracleColl
        ) {
            try {
                this.oracleColl.add(tOracle.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setExecutable(String pathToExecutable, boolean toWslPath) {
        this.pathToExecutable=pathToExecutable;
        this.toWslPath=toWslPath;
        setFiles(); //refactoring needed wrt toWSLPath
    }
    public void setupModel(boolean verbose,boolean counterExamples, String outputDir,
                           TemporalModel tModel, List<TemporalOracle> oracleList){
        this.verbose=verbose;
        this.tmodel=tModel;
        this.counterExamples=counterExamples;
        this.oracleColl=oracleList;
        this.outputDir=outputDir;
        setFiles();//refactoring needed
    }


    public abstract  List<TemporalOracle> check();


    public abstract List<TemporalOracle> parseResultsString(String rawInput);

    public  List<TemporalOracle> parseResultsFile(File rawInput) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(rawInput))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException f) {
            f.printStackTrace();
        }
        return parseResultsString(contentBuilder.toString());

    }


    /**
     * @see  #saveStringToFile(String, File)
     * @param contents jj
     * @param output jj
     */
    static void saveStringToFile(String contents, File output) {

        try {

            if (output.exists() || output.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output.getAbsolutePath()), StandardCharsets.UTF_8));
                writer.append(contents);
                writer.close();
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
    private void saveFormulasForChecker(List<TemporalOracle> oracleColl, File output, boolean doTransformation) {

        String contents = tmodel.validateAndMakeFormulas(oracleColl, doTransformation);
        saveStringToFile(contents, output);
    }
    public void setFiles(){
        automatonFile = new File(outputDir +temporalFormalism+"_model."+temporalFormalism.fileExtension);
        formulaFile = new File(outputDir + temporalFormalism + "_formulas.txt");
        resultsFile = new File(outputDir + temporalFormalism + "_results.txt");
        syntaxformulaFile = new File(outputDir + temporalFormalism + "_syntaxcheckedformulas.txt");
        automat = ((toWslPath) ? Common.toWSLPath(automatonFile.getAbsolutePath()) : automatonFile.getAbsolutePath());
        formula = ((toWslPath) ? Common.toWSLPath(formulaFile.getAbsolutePath()) : formulaFile.getAbsolutePath());
        result = ((toWslPath) ? Common.toWSLPath(resultsFile.getAbsolutePath()) : resultsFile.getAbsolutePath());
        //System.out.println("debug: ");
    }
    public  void validateAndSaveFormulas() {
        if ((temporalFormalism == TemporalFormalism.LTL_ITS) || (temporalFormalism == TemporalFormalism.LTL_LTSMIN) ||
                (temporalFormalism == TemporalFormalism.LTL) || (temporalFormalism == TemporalFormalism.LTL_SPOT)) {
            //formula ltl model variant converter
            // instrumentTerminalState will determine whether this return value is ""
            String aliveprop = tmodel.getPropositionIndex("!" + TemporalModel.getDeadProposition());
            if (!aliveprop.equals("")) {
                saveFormulasForChecker(oracleColl, formulaFile, false);
                List<String> tmpformulas = FormulaVerifier.INSTANCE.verifyLTL(formulaFile.getAbsolutePath(), syntaxformulaFile);
                List<TemporalOracle> tmporacleList = new ArrayList<>();
                int j = 0;
                for (TemporalOracle ora : oracleColl
                ) {
                    TemporalOracle oraClone = null;
                    try {
                        oraClone = ora.clone();
                        TemporalPatternBase pat = oraClone.getPatternBase();
                        pat.setPattern_Formula(tmpformulas.get(j));
                        tmporacleList.add(oraClone);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    j++;
                }
                saveFormulasForChecker(tmporacleList, formulaFile, true);
            } else {
                saveFormulasForChecker(oracleColl, formulaFile, true);
            }
        } else {
            saveFormulasForChecker(oracleColl, formulaFile, true);
        }
    }
    public void removeFiles(){
        if (!verbose) {
            try {
            if (automatonFile.exists()) Files.delete(automatonFile.toPath());
            if (resultsFile.exists())Files.delete(resultsFile.toPath());
            if (formulaFile.exists())Files.delete(formulaFile.toPath());
            if (syntaxformulaFile.exists())Files.delete(syntaxformulaFile.toPath());
            }  catch (IOException e) {
                e.printStackTrace();
                }
        }
    }
}

