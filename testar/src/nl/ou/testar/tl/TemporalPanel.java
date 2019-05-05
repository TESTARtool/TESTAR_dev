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

import org.fruit.monkey.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;

/**
 * Panel with settings for the state model inference module.
 */
public class TemporalPanel extends JPanel {

    private static final long serialVersionUID = -2815422165938359999L;


    private JLabel label00 = new JLabel("Temporal property");
    private JTextField temporalProperty = new JTextField("G(a->Fb)");
    private JButton modelcheckButton = new JButton("Check property");
    private JLabel label02 = new JLabel("Check result");
    private JTextArea logCheckResult = new JTextArea("...");
    private JPanel logPanel;
    private JScrollPane resultsScrollPane;
    private JButton temporalButton = new JButton("<html>Start Temporal<br>WebAnalyzer</html>");


    private TemporalPanel() {
        super();
    }

    /**
     * Create and Initialize StateModelPanel.
     *
     * @return StateModelPanel.
     */
    public static TemporalPanel createStateModelPanel() {
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
        //resultsScrollPane = new JScrollPane(logCheckResult,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //resultsScrollPane.setSize(340,210);
        //resultsScrollPane.setBounds(160,80,250,227);
        //logPanel.add(resultsScrollPane);
        //logPanel.setBounds(160,80,350,227);
        //logPanel.setSize(340,227);
        //add(logPanel);

        add(logCheckResult);

        temporalButton.setBounds(10, 316, 120, 54);
        temporalButton.setSize(temporalButton.getWidth(), 45);

        temporalButton.addActionListener(this::startTemporalWebAnalyzer);
        temporalButton.setToolTipText("Visual Analytics of the temporal property on the current State Model.\n Gto: http\\localhost:8050");
        add(temporalButton);


        // NEW COLUMN


    }

    /**
     * Populate GraphDBFields from Settings structure.
     *
     * @param settings The settings to load.
     */
    public void populateFrom(final Settings settings) {

    }

    /**
     * Retrieve information from the GraphDB GUI.
     *
     * @param settings reference to the object where the settings will be stored.
     */
    public void extractInformation(final Settings settings) {

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


        Process theProcess = null;
        BufferedReader inStream = null;
        String cli_part1 = "C:\\Users\\c\\git\\Testar_viz\\run.py";
        String cli = "C:\\Users\\c\\git\\TESTAR_dev\\testar\\src\\nl\\ou\\testar\\tl\\Start_Python.bat "+cli_part1;

        String response;
        logCheckResult.setText("invoking : ");
        logCheckResult.append("\n");
        logCheckResult.setText(cli);
        logCheckResult.append("\n");
        // call the external program
        try
        {
            theProcess = Runtime.getRuntime().exec(cli);
        }
        catch(IOException e)
        {
            System.err.println("Error on exec() method");
            logCheckResult.append("Error on exec() method");
            logCheckResult.append("\n");
            e.printStackTrace();
        }

        // read from the called program's standard output stream
        try
        {
            inStream = new BufferedReader(new InputStreamReader
                    (theProcess.getInputStream()));
            while ((response = inStream.readLine()) != null) {
                System.out.println("response: " + response);
                logCheckResult.append(response);
                logCheckResult.append("\n");
            }

            logCheckResult.append("------------Action completed---------------"+"\n");
        }
        catch(IOException e)
        {
            System.err.println("Error on inStream.readLine()");
            logCheckResult.append("Error on inStream.readLine()");
            logCheckResult.append("\n");
            e.printStackTrace();
        }
    }

}
