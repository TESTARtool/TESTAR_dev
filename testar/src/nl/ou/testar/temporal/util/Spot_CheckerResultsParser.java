package nl.ou.testar.temporal.util;

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
        Scanner scanner = new Scanner(rawInput);
        scanner.useDelimiter("\\s*=== Automaton\\s*");
        String headerLines="";
        if (scanner.hasNext()){headerLines = scanner.next();   }
        String automaton="";
        if (scanner.hasNext()){automaton = scanner.next();   }
        String remainder = "";
      //  if (scanner.hasNext()){remainder = scanner.next();   }

        System.out.println("debug: headerlines: "+headerLines);
        System.out.println("debug: automaton: "+automaton);
      //  System.out.println("debug: remainder: "+remainder);
        if (automaton.contains("=== ERROR")){
            return null;
        }else {


            List<String> formularesults = new ArrayList<>();
            scanner.useDelimiter("\\s*=== Formula\\s*");  //change the delimiter and now load chunks of formulas
            while (scanner.hasNext()) {
                formularesults.add(scanner.next());
            }
            System.out.println("debug: formularesults.size: " + formularesults.size());
            String lastLine = "";
            lastLine = formularesults.get(formularesults.size()-1);
            formularesults.remove(0); // dispose the === Automaton separator
            formularesults.remove(formularesults.size()-1);

            for (String f : formularesults) {
                int i = 0;
                String[] formulacomponents = f.split("\\s*===\\s*");
                for (String fcmp:formulacomponents
                     ) {
                    System.out.println("part " + i + ":" + fcmp);
                    i++;
                }

            }
            //this.oracleColl;
            System.out.println("debug: lastlines: " + lastLine);
            if (formularesults.size()!=oracleColl.size()){
                return null;
            }
            int i=0;
            for (String fResult:formularesults
                 ) {
                TemporalOracle Oracle = oracleColl.get(i);
                // get result status
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
