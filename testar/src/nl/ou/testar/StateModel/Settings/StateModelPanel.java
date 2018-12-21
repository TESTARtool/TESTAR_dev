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


package nl.ou.testar.StateModel.Settings;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;

/**
 * Panel with settings for the state model inference module.
 */
public class StateModelPanel extends JPanel {

	private static final long serialVersionUID = -2815422165938356237L;
	private JCheckBox stateModelEnabledChkBox = new JCheckBox();
    private JLabel label1 = new JLabel("State model enabled");
    private JLabel label2 = new JLabel("DataStore");
    private JLabel label3 = new JLabel("DataStoreType");
    private JLabel label4 = new JLabel("DataStoreServer");
    private JLabel label5 = new JLabel("DataStoreDB");
    private JLabel label6 = new JLabel("DataStoreUser");
    private JLabel label7 = new JLabel("DataStorePassword");

    private JTextField dataStoreTextfield = new JTextField();
    private JTextField dataStoreTypeTextfield = new JTextField();
    private JTextField dataStoreServerTextfield = new JTextField();
    private JTextField dataStoreDBTextfield = new JTextField();
    private JTextField dataStoreUserTextfield = new JTextField();
    private JPasswordField dataStorePasswordfield = new JPasswordField();

    private StateModelPanel(){
        super();
    }

    /**
     * Create and Initialize StateModelPanel.
     * @return StateModelPanel.
     */
    public static StateModelPanel createStateModelPanel() {
        StateModelPanel panel = new StateModelPanel();
        panel.initialize();
        return panel;
    }

    /**
     * Initialize panel.
     */
    private void initialize() {
        setLayout(null);
        label1.setBounds(10,14,120,27);
        add(label1);
        stateModelEnabledChkBox.setBounds(130,14,50,27);
        add(stateModelEnabledChkBox);
        label2.setBounds(10,52,120,27);
        add(label2);
        dataStoreTextfield.setBounds(130,52,200,27);
        add(dataStoreTextfield);
        label3.setBounds(10,90,120,27);
        add(label3);
        dataStoreTypeTextfield.setBounds(130,90,200,27);
        add(dataStoreTypeTextfield);
        label4.setBounds(10,128,120,27);
        add(label4);
        dataStoreServerTextfield.setBounds(130,128,200,27);
        add(dataStoreServerTextfield);
        label5.setBounds(10,166,120,27);
        add(label5);
        dataStoreDBTextfield.setBounds(130,166,200,27);
        add(dataStoreDBTextfield);
        label6.setBounds(10,204,120,27);
        add(label6);
        dataStoreUserTextfield.setBounds(130,204,200,27);
        add(dataStoreUserTextfield);
        label7.setBounds(10,242,120,27);
        add(label7);
        dataStorePasswordfield.setBounds(130,242,200,27);
        add(dataStorePasswordfield);
    }

    /**
     * Populate GraphDBFields from Settings structure.
     * @param settings The settings to load.
     */
    public void populateFrom(final Settings settings) {
        stateModelEnabledChkBox.setSelected(settings.get(ConfigTags.StateModelEnabled));
        dataStoreTextfield.setText(settings.get(ConfigTags.DataStore));
        dataStoreTypeTextfield.setText(settings.get(ConfigTags.DataStoreType));
        dataStoreServerTextfield.setText(settings.get(ConfigTags.DataStoreServer));
        dataStoreDBTextfield.setText(settings.get(ConfigTags.DataStoreDB));
        dataStoreUserTextfield.setText(settings.get(ConfigTags.DataStoreUser));
        dataStorePasswordfield.setText(settings.get(ConfigTags.DataStorePassword));
    }

    /**
     * Retrieve information from the GraphDB GUI.
     * @param settings reference to the object where the settings will be stored.
     */
    public void extractInformation(final Settings settings) {
        settings.set(ConfigTags.StateModelEnabled, stateModelEnabledChkBox.isSelected());
        settings.set(ConfigTags.DataStore, dataStoreTextfield.getText());
        settings.set(ConfigTags.DataStoreType, dataStoreTypeTextfield.getText());
        settings.set(ConfigTags.DataStoreServer, dataStoreServerTextfield.getText());
        settings.set(ConfigTags.DataStoreDB, dataStoreDBTextfield.getText());
        settings.set(ConfigTags.DataStoreUser, dataStoreUserTextfield.getText());
        settings.set(ConfigTags.DataStorePassword, getPassword());
    }

    /**
     * Convert password field to string.
     * @return password as String.
     */
    private String getPassword() {
        StringBuilder result= new StringBuilder();
        for(char c : dataStorePasswordfield.getPassword()) {
            result.append(c);
        }
        return  result.toString();
    }
}
