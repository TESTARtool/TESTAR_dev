/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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


package nl.ou.testar.tl;

import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.fruit.monkey.Main.outputDir;

/**
 * Panel with settings for the state model inference module.
 */
public class TemporalPanel extends JPanel {

    private static final long serialVersionUID = -2815422165938359999L;

    private Process webAnalyzerProcess = null;
    private JLabel label00 = new JLabel("Temporal property");
    private JTextField temporalProperty = new JTextField("G(a->Fb)");
    private JButton MCtestButton = new JButton("Check property");
    private JLabel label02 = new JLabel("Check result");
    private JTextArea logCheckResult = new JTextArea("...");
    private JPanel logPanel;
    private JScrollPane resultsScrollPane;
    private JButton convertGraphToMCformat = new JButton("<html>create HOA file<br>from Graphdb</html>");
    private JButton startTLWebAnalyzerButton = new JButton("<html>Start Temporal<br>WebAnalyzer</html>");
    private JButton stopTLWebAnalyzerButton = new JButton("<html>Stop Temporal<br>WebAnalyzer</html>");
    private String dataStore;
    private String dataStoreServer;
    private String dataStoreDirectory;
    private String dataStoreDB;
    private String dataStoreUser;
    private String dataStorePassword;
    private String dataStoreMode;
    private String dataStoreType;
    private String applicationVersion;
    private String applicationName;


    private TemporalPanel() {
        super();
    }

    /**
     * Create and Initialize StateModelPanel.
     *
     * @return StateModelPanel.
     */
    public static TemporalPanel createTemporalPanel() {
        TemporalPanel panel = new TemporalPanel();
        panel.initialize();
        return panel;
    }

    /**
     * Initialize panel.
     */
    private void initialize() {


        // add the components to the panel
        setLayout(null);
        label00.setBounds(10, 14, 350, 27);
        add(label00);
        temporalProperty.setBounds(160, 14, 125, 27);
        add(temporalProperty);



        MCtestButton.setBounds(10, 42, 120, 27);
        MCtestButton.addActionListener(this::performLtlTranslation);
        MCtestButton.setToolTipText("Check whether the property holds on the current State Model.");
        add(MCtestButton);
        label02.setBounds(10, 80, 350, 27);
        add(label02);
        logPanel = new JPanel();

        logCheckResult.setBounds(160, 80, 350, 277);
        logCheckResult.setAutoscrolls(true);
        logCheckResult.setLineWrap(true);
        logCheckResult.setWrapStyleWord(true);
        add(logCheckResult);

        convertGraphToMCformat.setBounds(10, 116, 120, 45);
        convertGraphToMCformat.setSize(convertGraphToMCformat.getWidth(), 45);
        convertGraphToMCformat.addActionListener(this::createMCfile);
        convertGraphToMCformat.setToolTipText("create an HOA interface file for model checking LTL properties in SPOT");
        add(convertGraphToMCformat);

        startTLWebAnalyzerButton.setBounds(10, 216, 120, 45);
        startTLWebAnalyzerButton.setSize(startTLWebAnalyzerButton.getWidth(), 45);

        startTLWebAnalyzerButton.addActionListener(this::startTemporalWebAnalyzer);
        startTLWebAnalyzerButton.setToolTipText("Visual Analytics of the temporal property on the current State Model.\n Gto: http\\localhost:8050");
        add(startTLWebAnalyzerButton);

        stopTLWebAnalyzerButton.setBounds(10, 276, 120, 45);
        stopTLWebAnalyzerButton.setSize(startTLWebAnalyzerButton.getWidth(), 45);

        stopTLWebAnalyzerButton.addActionListener(this::stopTemporalWebAnalyzer);
        add(stopTLWebAnalyzerButton);



        // NEW COLUMN


    }

    /**
     * Populate GraphDBFields from Settings structure.
     * @param settings The settings to load.
     */
    public void populateFrom(final Settings settings) {
        //stateModelEnabledChkBox.setSelected(settings.get(ConfigTags.StateModelEnabled));
        //accessBridgeEnabledBox.setSelected(settings.get(ConfigTags.AccessBridgeEnabled));
        dataStore =(settings.get(ConfigTags.DataStore));
        dataStoreServer = settings.get(ConfigTags.DataStoreServer);
        dataStoreDirectory= settings.get(ConfigTags.DataStoreDirectory);
        dataStoreDB= settings.get(ConfigTags.DataStoreDB);
        dataStoreUser= settings.get(ConfigTags.DataStoreUser);
        dataStorePassword= settings.get(ConfigTags.DataStorePassword);

        dataStoreMode=settings.get(ConfigTags.DataStoreMode);


        dataStoreType=settings.get(ConfigTags.DataStoreType);
        applicationName= settings.get(ConfigTags.ApplicationName);
        applicationVersion= settings.get(ConfigTags.ApplicationVersion);

        outputDir = settings.get(ConfigTags.OutputDir);
        // check if the output directory has a trailing line separator
        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
            outputDir += File.separator;
        }
        outputDir = outputDir + "temporaloracle" + File.separator;
    }

    /**
     * Retrieve information from the GraphDB GUI.
     *
     * @param settings reference to the object where the settings will be stored.
     */
    public void extractInformation(final Settings settings) {

    }
    private void createMCfile(ActionEvent evt) {
        // code goes here
        // create a config object for the orientdb database connection info
        Config config = new Config();
        config.setConnectionType(dataStore);
        config.setServer(dataStoreServer);
        config.setDatabase(dataStoreDB);
        config.setUser(dataStoreUser);
        config.setPassword(dataStorePassword);
        config.setDatabaseDirectory(dataStoreDirectory);
        //AnalysisManager analysisManager = new AnalysisManager(config, outputDir);
    }




    private void performLtlTranslation(ActionEvent evt) {
        // code goes here
        Process theProcess = null;
        BufferedReader inStream = null;
        String cli = "ubuntu1804 run /mnt/c/Users/c/OneDrive/OU/AF/Ubuntu/spotparse '" + temporalProperty.getText() + "'";
        String response;
        logCheckResult.setText("invoking : ");
        logCheckResult.append("\n");
        logCheckResult.setText(cli);
        logCheckResult.append("\n");
        // call the external program
        try {
            theProcess = Runtime.getRuntime().exec(cli);
        } catch (IOException e) {
            System.err.println("Error on exec() method");
            logCheckResult.append("Error on exec() method");
            logCheckResult.append("\n");
            e.printStackTrace();
        }

        // read from the called program's standard output stream
        try {
            inStream = new BufferedReader(new InputStreamReader
                    (theProcess.getInputStream()));
            while ((response = inStream.readLine()) != null) {
                System.out.println("response: " + response);
                logCheckResult.append(response);
                logCheckResult.append("\n");
            }

            logCheckResult.append("------------Action completed---------------" + "\n");
        } catch (IOException e) {
            System.err.println("Error on inStream.readLine()");
            logCheckResult.append("Error on inStream.readLine()");
            logCheckResult.append("\n");
            e.printStackTrace();
        }
    }

    private void startTemporalWebAnalyzer(ActionEvent evt) {
        // code goes here



        BufferedReader inStream = null;
        String cli_part1 = "python C:\\Users\\c\\git\\Testar_viz\\run.py";
        String cli = "C:\\Users\\c\\Anaconda3\\condabin\\conda.bat activate"+ " && " +cli_part1;

        String response;
        logCheckResult.setText("invoking : ");
        logCheckResult.append("\n");
        logCheckResult.append(cli_part1);
        logCheckResult.append("\n");
        // call the external program
        try
        {
           if (webAnalyzerProcess==null) {
               webAnalyzerProcess = Runtime.getRuntime().exec(cli_part1);
               logCheckResult.append("Visualizer Starting at http://localhost:8050");
               logCheckResult.append("\n");

               Desktop desktop = java.awt.Desktop.getDesktop();
               URI uri = new URI("http://localhost:8050");
               desktop.browse(uri);
               // any error message?
               try
               {
               StreamConsumer errorConsumer = new
                       StreamConsumer (webAnalyzerProcess.getErrorStream(), "ERROR");


               // any output?
                   StreamConsumer  outputConsumer = new
                           StreamConsumer(webAnalyzerProcess.getInputStream(), "OUTPUT");


               // kick them off
               errorConsumer.start();
               outputConsumer.start();

               // any error???
           } catch (Throwable t)
            {
                t.printStackTrace();
            }
           }

           else {
               logCheckResult.append("Visualizer was already running. goto http://localhost:8050");
               logCheckResult.append("\n");
           }
        }
        catch(Exception e)
        {
            System.err.println("Error on exec() method");
            logCheckResult.append("Error on exec() method");
            logCheckResult.append("\n");
            e.printStackTrace();
        }
    }
    private void stopTemporalWebAnalyzer(ActionEvent evt) {
        try
        {
            if(webAnalyzerProcess!=null) {
                webAnalyzerProcess.destroyForcibly();
                logCheckResult.append("Forcing Visualizer  to Stop.");
                boolean ret = webAnalyzerProcess.waitFor(5,  TimeUnit.SECONDS);  //gently wait
                if (ret){ webAnalyzerProcess=null; }
                logCheckResult.append("Visualizer Stopped. (exitcode was : "+ret+")");
                logCheckResult.append("\n");
            }
        }
        catch(Exception e)
        {
            System.err.println("Error on stopping");
            logCheckResult.append("Error on stopping");
            logCheckResult.append("\n");
            e.printStackTrace();
        }

    }


}
