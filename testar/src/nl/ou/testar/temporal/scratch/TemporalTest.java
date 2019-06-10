package nl.ou.testar.temporal.scratch;

import nl.ou.testar.temporal.structure.*;
import nl.ou.testar.temporal.util.JSONHandler;
import nl.ou.testar.temporal.util.TemporalType;
import nl.ou.testar.temporal.util.ValStatus;


import java.io.IOException;
import java.util.*;

public class TemporalTest {

    public static void main(String[] args) throws IOException, CloneNotSupportedException{


        System.out.println("Hello World!");

        Set attrib =new HashSet();
        attrib.add("R");
        attrib.add("T");
        attrib.add("P");
        attrib.add("E");


        TemporalOracleCollection toColl =new TemporalOracleCollection("notepad","v10","34d23", attrib);
        TemporalOracle to = new TemporalOracle(1, TemporalType.LTL,"precedence","!b U a");
        to.setValidationStatus(ValStatus.CANDIDATE);
        toColl.addOracle(to);
        String tlFile= "temporaltest.json";
        JSONHandler.save(toColl,tlFile);
        TemporalOracleCollection newColl= (TemporalOracleCollection) JSONHandler.load(tlFile,TemporalOracleCollection.class);
        TemporalOracle toclone = (TemporalOracle) to.clone();
        toclone.setId(123);
        newColl.addOracle(toclone);
        JSONHandler.save(toColl,"A"+tlFile); //2 oracles
        List<TemporalOracle> toList = newColl.getPropertyCollection();
        to = toList.get(0);
        newColl.removeOracle(to);
        JSONHandler.save(newColl,"B"+tlFile);  //1 oracle, the new one
        newColl.removeOracle(toclone);
        JSONHandler.save(newColl,"C"+tlFile);  //no oracles
        Map check = newColl.peekNode("B"+tlFile);
        System.out.println("check on file: "+"B"+tlFile);
        System.out.println(check);
        check = newColl.peekNode("B"+tlFile,false);
        System.out.println(" composite data also:");
        System.out.println(check);

        check = newColl.peekNode("B"+tlFile,"a",false);
        System.out.println(" filtered composite data also:");
        System.out.println(check);
        check = newColl.peekNode("C"+tlFile,false);
        System.out.println("check on file: "+"C"+tlFile);
        System.out.println(check);
        System.out.println("load file: "+"B"+tlFile+" and modify it" );
        TemporalOracleCollection secondColl =(TemporalOracleCollection) JSONHandler.load("B"+tlFile, TemporalOracleCollection.class);
        TemporalOracle to2 = new TemporalOracle(1, TemporalType.LTL,"precedence","!a U b");
        to2.setValidationStatus(ValStatus.REJECTED);
        secondColl.addOracle(to2);
        JSONHandler.save(secondColl,"D"+tlFile);
        check = newColl.peekNode("D"+tlFile);
        System.out.println(check);
        TemporalModel tModelInfo =new TemporalModel("notepad", "v10", "34d23", attrib);
        JSONHandler.save(tModelInfo,"E"+tlFile);
        check = newColl.peekNode("E"+tlFile);
        System.out.println(check);
        System.out.println("JSON peek on modelIdentifier");
        ArrayList<String> peekresult = JSONHandler.peek("modelIdentifier","B"+tlFile);
        System.out.println(peekresult);
        System.out.println("JSON peek2 on abstractionAttributes");
        peekresult = JSONHandler.peek("abstractionAttributes","B"+tlFile);
        System.out.println(peekresult);


    }
}

