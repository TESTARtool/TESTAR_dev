/***************************************************************************************************
 *
 * Copyright (c) 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2021 Open Universiteit - www.ou.nl
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

package org.testar.pkm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.testar.json.JsonArtefactLogs;

public class PkmRequest {

    private PkmRequest() {}

    /**
     * Validate DECODER PKM user key and project. 
     * GET request to http://10.101.0.224:8080/project/myproject
     * 
     * @param settings
     * @return true or false
     */
    public static boolean validDecoderUserProject(Settings settings) {
        String pkm = "http://" + settings.get(ConfigTags.PKMaddress) + ":" + settings.get(ConfigTags.PKMport) + "/project/" + settings.get(ConfigTags.PKMdatabase);

        try {
            URL url = new URL(pkm);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("key", settings.get(ConfigTags.PKMkey));
            con.connect();

            int status = con.getResponseCode();
            String codeMessage = con.getResponseMessage();

            con.disconnect();

            if(status != 200) {
                String msg = "Not valid key or not found project in the database";
                System.out.println(msg);
                System.err.println(msg);
                System.out.println(codeMessage);
                return false;
            }

        } catch (NoSuchTagException | IOException e) {
            String msg = "Error trying to verify user key or not found project in the database";
            System.out.println(msg);
            System.err.println(msg);
            System.out.println(e.getMessage());
            return false;
        }

        System.out.println("Valid user key and valid project");
        return true;
    }

    /**
     * Insert the Artefact Test Results inside the PKM. 
     * POST http://10.101.0.224:8080/testar/test_results/myproject
     * 
     * @param settings
     * @param artefactTestResults
     * @return artefactId
     */
    public static String postArtefactTestResults(Settings settings, String artefactTestResults) {
        String pkm = "http://" + settings.get(ConfigTags.PKMaddress) + ":" + settings.get(ConfigTags.PKMport) + "/testar/test_results/" + settings.get(ConfigTags.PKMdatabase);

        try {
            URL url = new URL(pkm);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("key", settings.get(ConfigTags.PKMkey));
            con.setRequestProperty("Content-Type", "application/json");

            // Extract json data from the Test Results Artefact
            String jsonInputString = FileUtils.readFileToString(new File(artefactTestResults), StandardCharsets.UTF_8);
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);           
            }

            con.connect();

            int status = con.getResponseCode();

            if(status == 200) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    // This print is used by the API to return the artefact value
                    System.out.println(response.toString());
                    JsonArtefactLogs.addMessage("TESTAR TestResults inserted: " + response.toString());
                    return substringArtefactId(response.toString(), "TESTARTestResults artefactId\":\"");
                }
            }

            con.disconnect();
        } catch (NoSuchTagException | IOException e) {
            e.printStackTrace();
        }

        return "TestResultsErrorArtefactId";
    }

    /**
     * Insert the Artefact State Model inside the PKM. 
     * POST http://10.101.0.224:8080/testar/state_model/myproject
     * 
     * @param settings
     * @param artefactStateModel
     * @return artefactId
     */
    public static String postArtefactStateModel(Settings settings, String artefactStateModel) {
        String pkm = "http://" + settings.get(ConfigTags.PKMaddress) + ":" + settings.get(ConfigTags.PKMport) + "/testar/state_model/" + settings.get(ConfigTags.PKMdatabase);

        try {
            URL url = new URL(pkm);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("key", settings.get(ConfigTags.PKMkey));
            con.setRequestProperty("Content-Type", "application/json");

            // Extract json data from the State Model Artefact
            String jsonInputString = FileUtils.readFileToString(new File(artefactStateModel), StandardCharsets.UTF_8);
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);           
            }

            con.connect();

            int status = con.getResponseCode();

            if(status == 200) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    // This print is used by the API to return the artefact value
                    System.out.println(response.toString());
                    JsonArtefactLogs.addMessage("TESTAR StateModel inserted: " + response.toString());
                    return substringArtefactId(response.toString(), "TESTARStateModels artefactId\":\"");
                }
            }

            con.disconnect();
        } catch (NoSuchTagException | IOException e) {
            e.printStackTrace();
        }

        return "StateModelErrorArtefactId";
    }

    /**
     * With the TESTAR output message, obtain the Artefact Identifier of desired Artefact Name.
     * 
     * @param executionOutput
     * @param find
     * @return artefactId
     */
    private static String substringArtefactId(String executionOutput, String find) {
        String artefactId = "ERROR";

        try {
            String pkmOutputInfo = executionOutput.substring(executionOutput.indexOf(find) + find.length());
            artefactId = StringUtils.split(pkmOutputInfo, " ")[0];
            artefactId = artefactId.replace("\"}","").replace("\n", "").replace("\r", "");
            artefactId = artefactId.trim();
        } catch(Exception e) {
            System.err.println("ERROR! : Trying to obtain : " + find);
        }

        return artefactId;
    }
    
    /**
     * Insert the Artefact Log inside the PKM. 
     * POST http://10.101.0.224:8080/log/myproject
     * 
     * @param settings
     * @param artefactLog
     * @return
     */
    public static void postArtefactLogs(Settings settings, String artefactLog) {
        String pkm = "http://" + settings.get(ConfigTags.PKMaddress) + ":" + settings.get(ConfigTags.PKMport) + "/log/" + settings.get(ConfigTags.PKMdatabase);

        try {
            URL url = new URL(pkm);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("key", settings.get(ConfigTags.PKMkey));
            con.setRequestProperty("Content-Type", "application/json");

            // Extract json data from the State Model Artefact
            String jsonInputString = FileUtils.readFileToString(new File(artefactLog), StandardCharsets.UTF_8);

            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);           
            }

            con.connect();

            int status = con.getResponseCode();

            if(status == 200) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return;
                }
            }

            con.disconnect();
        } catch (NoSuchTagException | IOException e) {
            e.printStackTrace();
        }
    }

}
