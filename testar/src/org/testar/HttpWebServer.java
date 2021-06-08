/***************************************************************************************************
 *
 * Copyright (c) 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2021 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar;

import java.io.File;
import java.io.IOException;

import org.fruit.Util;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;
import org.fruit.monkey.RuntimeControlsProtocol.Modes;
import org.testar.pkm.PkmRequest;

import nl.ou.testar.StateModel.Analysis.AnalysisProtocol;

public class HttpWebServer {

    private Settings settings;
    private long maxTime;

    public HttpWebServer(Settings settings, long maxTime) {
        this.settings = settings;
        this.maxTime = maxTime;
    }

    public void runWebServer() {
        // TODO: Extract this information from the PKM
        settings.set(ConfigTags.DataStoreType, "remote");
        settings.set(ConfigTags.DataStoreServer, "127.0.0.1");
        settings.set(ConfigTags.DataStoreUser, "testar");
        settings.set(ConfigTags.DataStorePassword, "testar");
        settings.set(ConfigTags.PKMaddress, "ow2-decoder.xsalto.net");
        settings.set(ConfigTags.PKMport, "8080");
        settings.set(ConfigTags.PKMdatabase, "mythaistar");

        //First validate DECODER PKM User Key
        if(PkmRequest.validDecoderUserProject(settings)) {
            if(settings.get(ConfigTags.Mode).equals(Modes.Analysis)) {
                runAnalysisMode();
            } else if(settings.get(ConfigTags.Mode).equals(Modes.Report)) {
                runReportMode();
            }
        }
    }

    /**
     * Analysis Mode starts a web Jetty server to Analyze the State Model remotely. 
     * 
     * Example to Execute with command line:
     * testar ShowVisualSettingsDialogOnStartup=false Mode=Analysis DataStoreType=plocal DataStoreDirectory="C:\\Users\\testar\\Desktop\\orientdb-3.0.28\\databases" DataStoreDB=testar DataStoreUser=testar DataStorePassword=testar 
     * testar ShowVisualSettingsDialogOnStartup=false Mode=Analysis DataStoreType=remote DataStoreServer=10.0.0.1 DataStoreDB=testar DataStoreUser=testar DataStorePassword=testar
     * 
     * Running on:
     * http://127.0.0.1:8090/models
     * 
     * Stop with:
     * http://127.0.0.1:8090/shutdown
     */
    private void runAnalysisMode() {
        AnalysisProtocol stateModelAnalyzer = new AnalysisProtocol(settings);
        stateModelAnalyzer.startStateModelAnalysis(maxTime);
        Util.pause(5);
        while(stateModelAnalyzer.isAnalyzerActive()) {
            // Jetty server is running...
            // This will stop when user send a GET localhost:8090/shutdown request
        }
    }

    /**
     * Report Mode starts a web Jetty server to View the HTML reports remotely. 
     * For DECODER purposes we use the ArtifactId associated with the HTML Test Report. 
     * 
     * Example to Execute with command line:
     * testar ShowVisualSettingsDialogOnStartup=false Mode=Report HTMLreportServerFile=5f64ba3bf87fe8088421ba7e
     * 
     * Running on:
     * http://127.0.0.1:8091
     * 
     * Stop with:
     * http://127.0.0.1:8091/shutdown
     */
    private void runReportMode() {
        try {
            File file = new File(settings.get(ConfigTags.HTMLreportServerFile)).getCanonicalFile();

            File extractedArtefactHtml = null;
            if(!isHtmlFile() && (extractedArtefactHtml = getArtefactIdOutputFolder()) != null) {
                file = extractedArtefactHtml;
            }
            HttpReportServer httpReportServer = new HttpReportServer(file);
            httpReportServer.runHtmlReport();
            while(httpReportServer.isJettyServerRunning()) {
                // HttpReportServer is running...
                // This will stop when user send a GET localhost:8091/shutdown request
            }
        }catch (Exception e) {
            System.out.println("Exception: Check the path of the file, something is wrong");
            e.printStackTrace();
        }
    }

    /**
     * If we want to launch TESTAR with the HTML report web service
     * using DECODER ArtefactId. Find the artefactId output directory.
     * 
     * @return
     */
    private File getArtefactIdOutputFolder() {
        try {

            String artefactId = settings.get(ConfigTags.HTMLreportServerFile);
            File testarDirectory = new File(Main.outputDir);
            // Find Directory with ArtefactId name
            for(String foldersName : testarDirectory.list()) {
                if(foldersName.contains(artefactId)) {
                    return new File(foldersName + File.separator + "output" + File.separator).getCanonicalFile();
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR DefaultProtocol: getArtefactIdOutputFolder");
            e.printStackTrace();
            return null;
        }

        return null;
    }

    /**
     * Check if the selected file to View is a html file
     */
    private boolean isHtmlFile() {
        if(settings.get(ConfigTags.PathToReplaySequence).contains(".html")) {
            return true;
        } else if(settings.get(ConfigTags.HTMLreportServerFile).contains(".html")) {
            return true;
        }

        return false;
    }

}
