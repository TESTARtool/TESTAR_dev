/***************************************************************************************************
 *
 * Copyright (c) 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.securityanalysis;

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
