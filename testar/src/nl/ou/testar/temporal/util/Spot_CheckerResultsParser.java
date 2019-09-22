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
        scanner.useDelimiter("\\s*=== Automaton Loading\\s*");
        String firstLine="";
        if (scanner.hasNext()){firstLine = scanner.next();   }
        String automaton="";
        if (scanner.hasNext()){automaton = scanner.next();   }
        String remainder = "";
        if (scanner.hasNext()){remainder = scanner.next();   }

        System.out.println("debug: firstline: "+firstLine);
        System.out.println("debug: automaton: "+automaton);
        System.out.println("debug: remainder: "+remainder);

        List<String> formularesults=new ArrayList<>();
        scanner.useDelimiter("\\s*=== Formula Checking\\s*");
        while (scanner.hasNext()){
            formularesults.add(scanner.next());
        }
        System.out.println("debug: formularesults.size: "+formularesults.size());
        String lastLine="";
        lastLine=formularesults.get(formularesults.size());
        formularesults.remove(formularesults.size());
        int i=0;
        for (String f:formularesults )
        {
        String[] formulacomponents =f.split("\\s*===\\s*");
        System.out.println("part "+i+":"+formulacomponents[i]);
        i++;
        }




        return  this.oracleColl;
    }


}
