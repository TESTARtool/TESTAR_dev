package nl.ou.testar.temporal.modelcheck;

import com.google.common.collect.HashBiMap;
import nl.ou.testar.temporal.foundation.ValStatus;
import nl.ou.testar.temporal.model.TemporalModel;
import nl.ou.testar.temporal.oracle.TemporalFormalism;
import nl.ou.testar.temporal.oracle.TemporalOracle;
import nl.ou.testar.temporal.oracle.TemporalPatternBase;
import nl.ou.testar.temporal.proposition.PropositionConstants;
import nl.ou.testar.temporal.util.OShelper;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
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




    public  List<TemporalOracle> modelcheck(){
        validateFormulasForChecker();
        delegatedCheck();
        String rawresults =parseResultsFile(resultsFile);
        List<TemporalOracle> oracleResults= delegatedParseResults(rawresults);
        removeFiles();
        return oracleResults;
    }
    private  String parseResultsFile(File rawInput) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(rawInput))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException f) {
            f.printStackTrace();
        }
        return contentBuilder.toString();

    }
    abstract  void delegatedCheck();
    abstract List<TemporalOracle> delegatedParseResults(String rawInput);
    abstract List<String> delegatedFormulaValidation(String aliveProp, boolean parenthesesNextOperator);



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
    /**
     *
     * @param oracleColl nnn collection is updated!
     * @param output nnn
     * @param doTransformation nn
     * @link saveStringToFile()
     */
    void saveFormulasForChecker(List<TemporalOracle> oracleColl, File output, boolean doTransformation) {

        String contents = validateAndMakeFormulas(oracleColl, doTransformation,this.temporalFormalism.parenthesesNextOperator );
        saveStringToFile(contents, output);
    }
    private void setFiles(){
        automatonFile = new File(outputDir +temporalFormalism+"_model."+temporalFormalism.fileExtension);
        formulaFile = new File(outputDir + temporalFormalism + "_formulas.txt");
        resultsFile = new File(outputDir + temporalFormalism + "_results.txt");
        syntaxformulaFile = new File(outputDir + temporalFormalism + "_syntaxcheckedformulas.txt");
        automat = ((toWslPath) ? OShelper.toWSLPath(automatonFile.getAbsolutePath()) : automatonFile.getAbsolutePath());
        formula = ((toWslPath) ? OShelper.toWSLPath(formulaFile.getAbsolutePath()) : formulaFile.getAbsolutePath());
        result = ((toWslPath) ? OShelper.toWSLPath(resultsFile.getAbsolutePath()) : resultsFile.getAbsolutePath());
        //System.out.println("debug: ");
    }

    private void validateFormulasForChecker() {
            String aliveprop = tmodel.getPropositionIndex("!" + PropositionConstants.SETTING.terminalProposition,true);
            String abstract_aliveprop = aliveprop.equals("")?"":"!" + PropositionConstants.SETTING.terminalProposition;

            List<String> tmpformulas =delegatedFormulaValidation(abstract_aliveprop,this.temporalFormalism.parenthesesNextOperator );
            List<TemporalOracle> tmporacleList = new ArrayList<>();
            int j = 0;
            for (TemporalOracle ora : oracleColl
            ) {
                try {
                    TemporalOracle oraClone = ora.clone();
                    TemporalPatternBase pat = oraClone.getPatternBase();
                    pat.setPattern_Formula(tmpformulas.get(j));
                    tmporacleList.add(oraClone);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                j++;
            }
            saveFormulasForChecker(tmporacleList, formulaFile, true);

        updateOracleCollMetaData(oracleColl);
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
    private void updateOracleCollMetaData(List<TemporalOracle> oracleList) {

        for (TemporalOracle ora : oracleList
        ) {
            ora.setApplicationName(tmodel.getApplicationName());
            ora.setApplicationVersion(tmodel.getApplicationVersion());
            ora.setApplication_ModelIdentifier(tmodel.getApplication_ModelIdentifier());
            ora.setApplication_AbstractionAttributes(tmodel.getApplication_AbstractionAttributes());
            ora.set_modifieddate(OShelper.prettyCurrentDateTime());
        }
    }
    private String validateAndMakeFormulas(List<TemporalOracle> oracleColl, boolean doTransformation, boolean parenthesesNextOperator) {

        StringBuilder Formulas = new StringBuilder();
        String rawFormula;
        for (TemporalOracle candidateOracle : oracleColl) {
            String formula;
            List<String> sortedparameters = new ArrayList<>(candidateOracle.getPatternBase().getPattern_Parameters());//clone list
            Collections.sort(sortedparameters);
            List<String> sortedsubstitionvalues = new ArrayList<>(candidateOracle.getSortedPattern_Substitutions().values());
            sortedsubstitionvalues.removeAll(Collections.singletonList(""));  // discard empty substitutions
            TemporalFormalism tFormalism = TemporalFormalism.valueOf(candidateOracle.getPatternTemporalType().name());

            boolean importStatus;
            rawFormula = candidateOracle.getPatternBase().getPattern_Formula();
            if (rawFormula.toUpperCase().equals("FALSE")){
                rawFormula="false"; // MS Excel converts 'false' to 'FALSE'. this is interpreted as eventually F(ALSE)
            }
            importStatus = sortedparameters.size() == sortedsubstitionvalues.size();
            if (!importStatus) {
                candidateOracle.addLog("inconsistent number of parameter <-> substitutions");
            }
            if (importStatus)
                importStatus = tmodel.getAtomicPropositions().containsAll(sortedsubstitionvalues);

            if (!importStatus) {
                candidateOracle.addLog("not all propositions (parameter-substitutions) are found in the Model:");
                for (String subst : sortedsubstitionvalues
                ) {
                    if (!tmodel.getAtomicPropositions().contains(subst)) candidateOracle.addLog("not found: " + subst);
                }
            }
            if (!importStatus) {
                String falseFormula="false";
                candidateOracle.addLog("setting formula to 'false'");
                String  formulalvl6= tFormalism.line_prepend+ StringUtils.replace(falseFormula,tFormalism.false_replace.getLeft(), tFormalism.false_replace.getRight()) + tFormalism.line_append;
                Formulas.append(formulalvl6);
                candidateOracle.setOracle_validationstatus(ValStatus.ERROR);
            } else {

                HashBiMap<Integer, String> aplookup = HashBiMap.create();
                aplookup.putAll(tmodel.getPropositionMap());
                ArrayList<String> apindex = new ArrayList<>();
                if (doTransformation) {

                    String deadprop = tmodel.getPropositionIndex(PropositionConstants.SETTING.terminalProposition, false);
                    if (!deadprop.equals("")) { // model has 'dead' as an atomic  property
                        sortedsubstitionvalues.add(PropositionConstants.SETTING.terminalProposition);
                        sortedparameters.add(PropositionConstants.SETTING.terminalProposition); // consider 'dead' as a kind of parameter
                    }

                    for (String v : sortedsubstitionvalues
                    ) {
                        if (aplookup.inverse().containsKey(v)) {
                            apindex.add(PropositionConstants.SETTING.outputPrefix + aplookup.inverse().get(v));
                        } else
                            apindex.add(PropositionConstants.SETTING.outputPrefix + "_indexNotFound");
                        // will certainly fail if during model-check, because parameters are not prefixed with 'ap'

                    }

                    {
                        String formulalvl0 = rawFormula;

                        if( !tFormalism.supportsMultiInitialStates && tmodel.getInitialStates().size()>1) {
                            //when there are initial states added to the model, the formula alters:
                            //satisfaction of the formula starts after the artificial state, hence the X-operator.
                            formulalvl0 = "X(" + rawFormula + ")";
                            if (parenthesesNextOperator) {
                                formulalvl0 = "(" + formulalvl0 + ")";
                            }
                            if (tFormalism.name().contains("CTL_")) {
                                formulalvl0 = "A" + formulalvl0;
                            }
                        }

                        String formulalvl1a =  tFormalism.line_prepend+ formulalvl0;
                        String formulalvl1 = formulalvl1a + tFormalism.line_append;
                        String formulalvl2 = StringUtils.replace(formulalvl1,
                                tFormalism.finally_replace.getLeft(), tFormalism.finally_replace.getRight());
                        String formulalvl3 = StringUtils.replace(formulalvl2,
                                tFormalism.globally_replace.getLeft(), tFormalism.globally_replace.getRight());
                        String formulalvl4 = StringUtils.replace(formulalvl3,
                                tFormalism.and_replace.getLeft(), tFormalism.and_replace.getRight());
                        String formulalvl5 = StringUtils.replace(formulalvl4,
                                tFormalism.or_replace.getLeft(), tFormalism.or_replace.getRight());
                        String formulalvl6 = StringUtils.replace(formulalvl5,
                                tFormalism.false_replace.getLeft(), tFormalism.false_replace.getRight());

                        apindex.replaceAll(s -> tFormalism.ap_prepend + s + tFormalism.ap_append);

                        formula = StringUtils.replaceEach(formulalvl6,
                                sortedparameters.toArray(new String[0]), apindex.toArray(new String[0]));
                    }
                } else {
                    formula = candidateOracle.getPatternBase().getPattern_Formula();
                }
                Formulas.append(formula);
            }
            Formulas.append("\n"); //always a new line . if formula is not validated a blank line appears
        }
        return Formulas.toString();
    }
}

