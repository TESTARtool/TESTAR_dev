package org.testar.securityanalysis;

import com.google.gson.Gson;
import com.orientechnologies.orient.core.db.record.OCurrentStorageComponentsFactory;
import jdk.nashorn.internal.parser.JSONParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SecurityResultWriter {
    private HashMap<String, SecurityResult> currentResults;
    private List<String> visitList;
    private String vulnerabilityPath = "SecurityResult.json";
    private String visitPath = "VisitResult.json";

    public SecurityResultWriter()
    {
        System.out.println("Start securityResultWriter");
        Gson gson = new Gson();

        System.out.println("Start securityResultWriter Gson");
        try {
            Reader reader1 = Files.newBufferedReader(Paths.get(vulnerabilityPath));
            System.out.println("Start securityResultWriter Reader");

            currentResults = gson.fromJson(reader1, HashMap.class);
            System.out.println("Start securityResultWriter fromJson");
        }
        catch (Exception e) {
            System.out.println("Creating new currentResults list");
            currentResults = new HashMap<>();
        }

        try {
            Reader reader2 = Files.newBufferedReader(Paths.get(visitPath));
            System.out.println("Start securityResultWriter Reader");

            visitList = gson.fromJson(reader2, List.class);
            System.out.println("Start securityResultWriter fromJson");
        }
        catch (Exception e) {
            System.out.println("Creating new currentResults list");
            visitList = new ArrayList<>();
        }
    }

    public void WriteVisit(String url)
    {
        visitList.add(url);
        WriteVisitFile();
    }

    public void WriteResult(String url, String cwe, String description)
    {
        String key = url + "-" + cwe;
        if (currentResults.get(key) != null)
            return;

        SecurityResult result = new SecurityResult(url, cwe, description);
        currentResults.put(key, result);
        WriteResultFile();
    }

    private void WriteResultFile()
    {
        String json = "";
        try {
            Gson gson = new Gson();
            json = gson.toJson(currentResults).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(vulnerabilityPath))) {
            out.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void WriteVisitFile()
    {
        String json = "";
        try {
            Gson gson = new Gson();
            json = gson.toJson(visitList).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(visitPath))) {
            out.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
