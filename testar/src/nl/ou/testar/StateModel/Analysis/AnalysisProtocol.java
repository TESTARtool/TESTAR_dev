/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

package nl.ou.testar.StateModel.Analysis;

import java.io.File;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import nl.ou.testar.StateModel.Analysis.HttpServer.JettyServer;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;

public class AnalysisProtocol {

    private String outputDir;
    private Config analysisConfig;
    private JettyServer jettyServer;

    public AnalysisProtocol (Settings settings) {
        // create a config object for the orientdb database connection info
        Config config = new Config();
        config.setConnectionType(settings.get(ConfigTags.DataStoreType, ""));
        config.setServer(settings.get(ConfigTags.DataStoreServer, ""));
        config.setDatabase(settings.get(ConfigTags.DataStoreDB, ""));
        config.setUser(settings.get(ConfigTags.DataStoreUser, ""));
        config.setPassword(settings.get(ConfigTags.DataStorePassword, ""));
        config.setDatabaseDirectory(settings.get(ConfigTags.DataStoreDirectory, ""));

        this.analysisConfig = config;

        outputDir = settings.get(ConfigTags.OutputDir, "");
        // check if the output directory has a trailing line separator
        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
            outputDir += File.separator;
        }
        outputDir = outputDir + "graphs" + File.separator;
    }

    public void startStateModelAnalysis(long maxTime) {
        try {
            AnalysisManager analysisManager = new AnalysisManager(analysisConfig, outputDir);
            jettyServer = new JettyServer();
            jettyServer.setMaxTime(maxTime);
            jettyServer.start(outputDir, analysisManager);
        } catch (Exception e) {
            System.out.println("Please check your connection credentials.");
        }
    }

    public boolean isAnalyzerActive() {
        return jettyServer.isJettyServerRunning() && !jettyServer.reachedMaxRunningTime();
    }

}
