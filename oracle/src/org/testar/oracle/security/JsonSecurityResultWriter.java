/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.security;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testar.OutputStructure;

public class JsonSecurityResultWriter implements SecurityResultWriter {
    private HashMap<String, SecurityResultDto> currentResults;
    private List<String> visitList;
    private String vulnerabilityPath = OutputStructure.outerLoopOutputDir + File.separator + "SecurityResult.json";
    private String visitPath = OutputStructure.outerLoopOutputDir + File.separator + "VisitResult.json";

    public JsonSecurityResultWriter()
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

        SecurityResultDto result = new SecurityResultDto(url, cwe, description);
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
