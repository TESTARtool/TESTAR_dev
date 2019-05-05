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

/**
 * Panel with settings for the state model inference module.
 */
public class TemporalPanel extends JPanel {

	private static final long serialVersionUID = -2815422165938359999L;


    private JLabel label20 = new JLabel("LTL property");
    private JTextField temporalProperty = new JTextField("G(a->Fb)");
    private JButton modelcheckButton = new JButton("Check property");
    private JTextArea checkResult = new JTextArea("...");
    private JButton temporalButton = new JButton("Temporal Analyzer");



    private TemporalPanel(){
        super();
    }

    /**
     * Create and Initialize StateModelPanel.
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
        label20.setBounds(10,14,150,27);
        add(label20);
        temporalProperty.setBounds(160,14,125,27);
        add(temporalProperty);


        modelcheckButton.setBounds(10, 42, 120, 27);
        modelcheckButton.addActionListener(this::performTemporalCheck);
        modelcheckButton.setToolTipText("Select the 'databases' folder in your orientdb installation. Make sure the OrientDB server is not running.");
        add(modelcheckButton);

        checkResult.setBounds(10,80,150,227);
        add(checkResult);

        temporalButton.setBounds(10, 316, 120, 27);
        temporalButton.addActionListener(this::performTemporalCheck);
        temporalButton.setToolTipText("Select the 'databases' folder in your orientdb installation. Make sure the OrientDB server is not running.");
        add(temporalButton);


        // NEW COLUMN


    }

    /**
     * Populate GraphDBFields from Settings structure.
     * @param settings The settings to load.
     */
    public void populateFrom(final Settings settings) {

    }

    /**
     * Retrieve information from the GraphDB GUI.
     * @param settings reference to the object where the settings will be stored.
     */
    public void extractInformation(final Settings settings) {

    }





    private void performTemporalCheck(ActionEvent evt) {
       // code goes here
        }
    }


