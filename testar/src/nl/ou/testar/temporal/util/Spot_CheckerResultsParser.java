package nl.ou.testar.temporal.util;

import nl.ou.testar.temporal.structure.StateEncoding;
import nl.ou.testar.temporal.structure.TemporalModel;
import nl.ou.testar.temporal.structure.TemporalOracle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Spot_CheckerResultsParser {


    private TemporalModel tmodel;
    private String rawInput;
    private List<TemporalOracle> oracleColl;


    private File log;


    public Spot_CheckerResultsParser(TemporalModel tmodel, List<TemporalOracle> oracleColl, File log) {
        this.tmodel = tmodel;
        setOracleColl(oracleColl);
        this.log = log;
    }

    public Spot_CheckerResultsParser(TemporalModel tmodel, List<TemporalOracle> oracleColl) {
        this.tmodel = tmodel;
        setOracleColl(oracleColl);
    }

    public Spot_CheckerResultsParser() {
    }

    public File getLog() {
        return log;
    }

    public void setLog(File log) {
        this.log = log;
    }


    public void setTmodel(TemporalModel tmodel) {
        this.tmodel = tmodel;
    }


    public void setOracleColl(List<TemporalOracle> oracleColl) {

        this.oracleColl = new ArrayList<>();
        for (TemporalOracle tOracle:oracleColl
             ) {
            try {
                this.oracleColl.add(tOracle.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<TemporalOracle> parse(String rawInput){
        this.rawInput = rawInput;
        List<StateEncoding> stateEncodings = tmodel.getStateEncodings();
        Scanner scanner = new Scanner(rawInput);
        scanner.useDelimiter("\\s*=== Automaton\\s*");
        String headerLines="";
        if (scanner.hasNext()){headerLines = scanner.next();   }
        String automaton="";
        if (scanner.hasNext()){automaton = scanner.next();   }
/*        String remainder = "";
      //  if (scanner.hasNext()){remainder = scanner.next();   }

        System.out.println("debug: headerlines: "+headerLines);
        System.out.println("debug: automaton: "+automaton);
      //  System.out.println("debug: remainder: "+remainder);*/
        if (automaton.contains("=== ERROR")){
            return null;
        }else {


            List<String> formularesults = new ArrayList<>();
            scanner.useDelimiter("\\s*=== Formula\\s*");  //change the delimiter and now load chunks of formulas
            while (scanner.hasNext()) {
                formularesults.add(scanner.next());
            }
         //   System.out.println("debug: formularesults.size: " + formularesults.size());
            String lastLine = "";
            lastLine = formularesults.get(formularesults.size()-1);
            formularesults.remove(0); // dispose the === Automaton separator
            formularesults.remove(formularesults.size()-1);

/*            for (String f : formularesults) {
                int i = 0;
                String[] formulacomponents = f.split("\\s*===\\s*");
                for (String fcmp:formulacomponents
                     ) {
                    System.out.println("part " + i + ":" + fcmp);
                    i++;
                }

            }*/
            //this.oracleColl;
         //   System.out.println("debug: lastlines: " + lastLine);
            if (formularesults.size()!=oracleColl.size()){
                return null;
            }
            int i=0;
            for (String fResult:formularesults
                 ) {
                TemporalOracle Oracle = oracleColl.get(i);
                // get result status
                String[] formulacomponents = fResult.split("\\s*===\\s*");
                String encodedFormula=formulacomponents[1];
                String encodedFormulaResult=formulacomponents[2];
                String[] formulaResult = encodedFormulaResult.split(",\\s*");
                String formulaStatus=formulaResult[0];

                String[]prefixResults=formulaResult[1].split("\\s*Prefix:\\s*");
                String[]cycleResults=formulaResult[1].split("\\s*Cycle:\\s*");

                List<String> prefixStateIndexList = new ArrayList<>();
                List<String> prefixAPConjunctList = new ArrayList<>();
                List<String> cycleStateIndexList = new ArrayList<>();
                List<String> cycleAPConjunctList = new ArrayList<>();

                if (prefixResults.length==2){
                    String cleanprefix=prefixResults[1];
                if(prefixResults[1].contains("Cycle:")){
                    cleanprefix=prefixResults[1].split("\\s*Cycle:")[0];
                }
                   String[] prefixLines=cleanprefix.split("\\n");
                    for (int j = 0; j <prefixLines.length ; j=j+2) {
                        prefixStateIndexList.add(prefixLines[j]);
                        //prefixAPConjunctList.add( prefixLines[j + 1].split("\\s*\\|\\s*")[1]);
                        prefixAPConjunctList.add( prefixLines[j + 1]);

                    }
               } // noprefix?
                if (cycleResults.length==2){
                    String cleancycle=cycleResults[1];
                    if(cycleResults[1].contains("Prefix:")){
                        cleancycle=cycleResults[1].split("Prefix:\\s*")[1];
                    }

                    String[] cycleLines=cleancycle.split("\\n");
                    for (int j = 0; j <cycleLines.length ; j=j+2) {
                        cycleStateIndexList.add(cycleLines[j]);
                  //      cycleAPConjunctList.add(cycleLines[j + 1].split("\\s*\\|\\s*")[1]);
                        cycleAPConjunctList.add(cycleLines[j + 1]);
                    }
                } // nocycle?

                    for (int j = 0; j <prefixStateIndexList.size() ; j++) {
                        prefixStateIndexList.set(j, stateEncodings.get(Integer.parseInt(prefixStateIndexList.get(j).trim())).getState());
                    }
                    Oracle.setExampleRun_Prefix_States(prefixStateIndexList);
                Oracle.setExampleRun_Prefix_Transitions(prefixAPConjunctList); //test only

                for (int j = 0; j <cycleStateIndexList.size() ; j++) {
                    cycleStateIndexList.set(j, stateEncodings.get(Integer.parseInt(cycleStateIndexList.get(j).trim())).getState());
                }
                Oracle.setExampleRun_Cycle_States(cycleStateIndexList);
                Oracle.setExampleRun_Cycle_Transitions(cycleAPConjunctList);  //test only



                //get run: decode prefix and decode cycle..... use the model
                // note that transition ap is a 'Set' or      use the encoded string ...maybe better as this was also supplied to the HOA file.
                // update oracle
                // add to coll? .. not needed

                i++;


            }
        }




        return  this.oracleColl;
    }


}
