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


package nl.ou.testar.temporal.ui;

import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.StateModel.Settings.StateModelPanel;
import nl.ou.testar.temporal.behavior.TemporalController;
import nl.ou.testar.temporal.structure.*;
import nl.ou.testar.temporal.util.CSVHandler;
import nl.ou.testar.temporal.util.JSONHandler;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.fruit.monkey.Main.outputDir;

/**
 * Panel with settings for the state model inference module.
 */
public class TemporalPanel extends JPanel {

    private static final long serialVersionUID = -2815422165938359999L;

    String dataStoreText;
    String dataStoreServerDNS;
    String dataStoreDirectory;
    String dataStoreDBText;
    String dataStoreUser;
    String dataStorePassword;
    String dataStoreType;

    private StateModelPanel stateModelPanel;
    private Process webAnalyzerProcess = null;
    private JLabel label00 = new JLabel("Temporal property");
    private JTextField temporalProperty = new JTextField("G(a->Fb)");
    private JButton modelcheckButton = new JButton("Check property");
    private JLabel label02 = new JLabel("Check result");
    private JTextArea logCheckResult = new JTextArea("...");
    private JPanel logPanel;
    private JScrollPane resultsScrollPane;
    private JButton startTLWebAnalyzerButton = new JButton("<html>Start Temporal<br>WebAnalyzer</html>");
    private JButton stopTLWebAnalyzerButton = new JButton("<html>Stop Temporal<br>WebAnalyzer</html>");
    private JButton temporalWindowButton = new JButton("<html>testDb</html>");


    private TemporalPanel() {
        super();
    }

    public static TemporalPanel createStateModelPanel() {
        TemporalPanel panel = new TemporalPanel();
        panel.initialize();
        return panel;
    }

    /**
     * Populate GraphDBFields from Settings structure.
     * @param settings The settings to load.
     */
    public void populateFrom(final Settings settings) {
        // used here, but controlled on StateModelPanel
        dataStoreText=settings.get(ConfigTags.DataStore);
        dataStoreServerDNS= settings.get(ConfigTags.DataStoreServer);
        dataStoreDirectory= settings.get(ConfigTags.DataStoreDirectory);
        dataStoreDBText=settings.get(ConfigTags.DataStoreDB);
        dataStoreUser = settings.get(ConfigTags.DataStoreUser);
        dataStorePassword= settings.get(ConfigTags.DataStorePassword);
        dataStoreType= settings.get(ConfigTags.DataStoreType);


        outputDir = settings.get(ConfigTags.OutputDir);
        // check if the output directory has a trailing line separator
        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
            outputDir += File.separator;
        }
        outputDir = outputDir + "temporal";
        new File(outputDir).mkdirs();
        outputDir = outputDir +  File.separator;
    }

    public void refreshsettings(){
        Settings dbsettings =new Settings();
        stateModelPanel.extractInformation(dbsettings);
        populateFrom(dbsettings);

    }


    /**
     * Initialize panel.
     */
    private void initialize() {

        setupMiner();
        // add the components to the panel
        //setLayout(null);
        label00.setBounds(10, 14, 350, 27);
        add(label00);
        temporalProperty.setBounds(160, 14, 125, 27);
        add(temporalProperty);


        modelcheckButton.setBounds(10, 42, 120, 27);
        modelcheckButton.addActionListener(this::performTemporalCheck);
        modelcheckButton.setToolTipText("Check whether the property holds on the current State Model.");
        add(modelcheckButton);
        label02.setBounds(10, 80, 350, 27);
        add(label02);
        logPanel = new JPanel();

        logCheckResult.setBounds(160, 80, 350, 277);
        logCheckResult.setAutoscrolls(true);
        logCheckResult.setLineWrap(true);
        logCheckResult.setWrapStyleWord(true);
        resultsScrollPane = new JScrollPane(logCheckResult,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //resultsScrollPane.setSize(340,210);
        //resultsScrollPane.setBounds(160,80,250,227);
        //logPanel.add(resultsScrollPane);
        //logPanel.setBounds(160,80,350,227);
        //logPanel.setSize(340,227);
        //add(logPanel);

        //add(logCheckResult);
        add(resultsScrollPane);

        startTLWebAnalyzerButton.setBounds(10, 126, 120, 45);
        startTLWebAnalyzerButton.setSize(startTLWebAnalyzerButton.getWidth(), 45);

        startTLWebAnalyzerButton.addActionListener(this::startTemporalWebAnalyzer);
        startTLWebAnalyzerButton.setToolTipText("Visual Analytics of the temporal property on the current State Model.\n Gto: http\\localhost:8050");
        add(startTLWebAnalyzerButton);

        stopTLWebAnalyzerButton.setBounds(10, 166, 120, 45);
        stopTLWebAnalyzerButton.setSize(startTLWebAnalyzerButton.getWidth(), 45);

        stopTLWebAnalyzerButton.addActionListener(this::stopTemporalWebAnalyzer);
        add(stopTLWebAnalyzerButton);

        temporalWindowButton.setBounds(10, 276, 120, 45);
        temporalWindowButton.setSize(temporalWindowButton.getWidth(), 45);

        temporalWindowButton.addActionListener(this::testdb);
        add(temporalWindowButton);


        // NEW COLUMN


    }


    /**
     * Retrieve information from the GraphDB GUI.
     *
     * @param settings reference to the object where the settings will be stored.
     */
    public void extractInformation(final Settings settings) {

    }
private void setupMiner(){
    Config config = new Config();
    config.setConnectionType(dataStoreType);
    config.setServer(dataStoreServerDNS);
    config.setDatabase(dataStoreDBText);
    config.setUser(dataStoreUser);
    config.setPassword(dataStorePassword);
    config.setDatabaseDirectory(dataStoreDirectory);
}

    private void performTemporalCheck(ActionEvent evt) {
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
               logCheckResult.append("Visualizer Started. goto http://localhost:8050");
               logCheckResult.append("\n");
               Desktop desktop = Desktop.getDesktop();
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
    private void testdb(ActionEvent evt) {
        try
        {
            testOracleCSV();
            testPatternCSV();
            APSelectorManager APmgr = testApSelectionManagerJSON();

            Config config = new Config();
            config.setConnectionType(dataStoreType);
            config.setServer(dataStoreServerDNS);
            config.setDatabase(dataStoreDBText);
            config.setUser(dataStoreUser);
            config.setPassword(dataStorePassword);

            String tmp= dataStoreDirectory;
            logCheckResult.append("connecting to: db\n");
            logCheckResult.repaint();
            config.setDatabaseDirectory(tmp);
            TemporalController tcontrol = new TemporalController(config,outputDir);
           //List<AbstractStateModel> models = tcontrol.fetchModels();

            //logCheckResult.append("model count: " + models.size()+"\n");
            //AbstractStateModel model = models.get(0);
            //logCheckResult.append("Model info:" + model.getApplicationName() + ", " + model.getModelIdentifier()+"\n");
            TemporalModel tmodel = tcontrol.getTemporalModel(APmgr);
            JSONHandler.save(tmodel, outputDir + "APEncodedModel.json");
            logCheckResult.append(" saving to file done\n");

            logCheckResult.append("\n");


            logCheckResult.append("\n");
            tcontrol.saveToGraphMLFile(outputDir + "GraphML.XML");
            logCheckResult.append(" saving to  graphml file done\n");

            logCheckResult.append("\n");
            tcontrol.shutdown();
        }
        catch(Exception e)
        {
            System.err.println("Error on testing db");
            logCheckResult.append("Error on testing db\n");
            logCheckResult.append("\n");
            e.printStackTrace();
        }

    }
    public void testOracleCSV() {
        logCheckResult.append("performing a small test: writing an oracle to CSV file and read back\n");


        TemporalOracle to= TemporalOracle.getSampleOracle();
        List<TemporalOracle> tocoll = new ArrayList<>();
        tocoll.add(to);

        CSVHandler.save(tocoll, outputDir + "temporalOracle1.csv");
        logCheckResult.append("csv saved: \n");

        List<TemporalOracle> fromcoll;
        fromcoll = CSVHandler.load(outputDir + "temporalOracle3.csv", TemporalOracle.class);
        if (fromcoll == null) {
            logCheckResult.append("place a file called 'temporalOracle3.csv' in the directory: " + outputDir+"\n");
        } else {
            logCheckResult.append("csv loaded: \n");
            logCheckResult.append("Formalism that was read from file: " + fromcoll.get(0).getTemporalFormalism()+"\n");
            CSVHandler.save(fromcoll, outputDir + "temporalOracle2.csv");
            logCheckResult.append("csv saved: \n");

        }
    }
    public void testPatternCSV() {
        logCheckResult.append("performing a small test: writing a pattern to CSV file\n");


        TemporalConstraintedPattern pat= TemporalConstraintedPattern.getSamplePattern();
        List<TemporalConstraintedPattern> patcoll = new ArrayList<>();
        patcoll.add(pat);

        CSVHandler.save(patcoll, outputDir + "temporalPatternSample.csv");
        logCheckResult.append("csv saved: ");

        List<TemporalConstraintedPattern> fromcoll;
        fromcoll = CSVHandler.load(outputDir + "temporalPattern1.csv", TemporalConstraintedPattern.class);
        if (fromcoll == null) {
            logCheckResult.append("place a file called 'temporalPattern1.csv' in the directory: " + outputDir+"\n");
        } else {
            logCheckResult.append("csv loaded: \n");
            logCheckResult.append("pattern that was read from file: " + fromcoll.get(0).getTemporalFormalism()+"\n");
            logCheckResult.append("widgetrole constraints that was read from file: " + fromcoll.get(0).getWidgetRoleParameterConstraints().toString()+"\n");

            CSVHandler.save(fromcoll, outputDir + "temporalPattern2.csv");
            logCheckResult.append("csv saved: \n");

        }

        }
    public APSelectorManager testApSelectionManagerJSON() {
        logCheckResult.append("performing a small test: writing an Selectionmanager.JSON and reading another\n");

        APSelectorManager APmgr = new APSelectorManager(true);
        JSONHandler.save(APmgr, outputDir + "APSelectorManager.json",true);
        logCheckResult.append("json saved: \n");

        APSelectorManager APmgr1 ;
        APmgr1 = (APSelectorManager) JSONHandler.load(outputDir + "APSelectorManagerTEST.json", APmgr.getClass());

        if (APmgr1 == null) {
            logCheckResult.append("place a file called 'APSelectorManagerTEST.json' in the directory: " + outputDir+"\n");
        } else {
            logCheckResult.append("json loaded: \n");
            Set<WidgetFilter> wfset = APmgr1.getWidgetfilters();
            Iterator<WidgetFilter> wfiter = wfset.iterator();
            WidgetFilter wf = wfiter.next();
            logCheckResult.append("widgetroles that were read from file: " + wf.getWidgetRolesMatches().toString()+"\n");


        }
        return APmgr1;
    }



}
