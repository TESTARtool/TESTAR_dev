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

package org.testar.json;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import org.fruit.monkey.Main;
import org.testar.OutputStructure;
import org.testar.json.object.LogsJsonObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonArtefactLogs {

    static String tool = "TESTAR";
    static String natureOfReport = "TESTAR execution log";
    static String startRunningTime;
    static String endRunningTime;
    static LinkedList <String> messages = new LinkedList<>();
    static LinkedList<String> warnings = new LinkedList<>();
    static boolean status = true;

    private JsonArtefactLogs() {}

    public static String generateLogsArtefact() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String name = "ArtefactLogs_"  + OutputStructure.executedSUTname + "_" + OutputStructure.startOuterLoopDateString;
        String outputFile = Main.outputDir  + File.separator + name + ".json";

        LogsJsonObject artefactLogs = new LogsJsonObject(tool, natureOfReport, startRunningTime, endRunningTime, 
                messages, warnings, status);

        try{
            new File(outputFile).createNewFile();
            FileWriter fileWriter = new FileWriter(outputFile);
            String jsonLog = gson.toJson(artefactLogs);
            
            // Bad way to transform our json to array of jsons :D
            jsonLog = "[" + jsonLog + "]";
            // Replace variable names to match the schema
            jsonLog = jsonLog.replace("natureOfReport", "nature of report");
            jsonLog = jsonLog.replace("startRunningTime", "start running time");
            jsonLog = jsonLog.replace("endRunningTime", "end running time");
            
            fileWriter.write(jsonLog);
            fileWriter.flush();
            fileWriter.close();
            System.out.println("Created JSON Logs Artefact: " + outputFile);
        }catch(Exception e){
            System.out.println("ERROR! Creating JSON LogsArtefact!");
            e.printStackTrace();
            return "";
        }

        return outputFile;
    }

    public static void setStartRunningTime(String time) {
        startRunningTime = time;
    }

    public static void setEndRunningTime(String time) {
        endRunningTime = time;
    }

    public static void addMessage(String message) {
        messages.add(message);
    }

    public static void addWarning(String warning) {
        warnings.add(warning);
    }

    public static void setStatus(boolean b) {
        status = b;
    }

}
