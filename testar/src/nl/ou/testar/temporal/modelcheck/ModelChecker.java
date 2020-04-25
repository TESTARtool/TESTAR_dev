package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.model.TemporalModel;
import nl.ou.testar.temporal.oracle.TemporalOracle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ModelChecker {

    protected TemporalModel tmodel;
    protected List<TemporalOracle> oracleColl;

    public ModelChecker() { }


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

    public abstract  List<TemporalOracle> check(String pathToExecutable, boolean toWslPath,
                                                boolean counterExamples,String automatonFilePath,
                                                String formulaFilePath, File resultsFile,
                                                TemporalModel tModel, List<TemporalOracle> oracleList);

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
}

