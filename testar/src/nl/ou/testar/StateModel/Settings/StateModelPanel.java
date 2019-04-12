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
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Panel with settings for the state model inference module.
 */
public class StateModelPanel extends JPanel {

	private static final long serialVersionUID = -2815422165938356237L;
    private JLabel label1 = new JLabel("State model enabled");
    private JLabel label2 = new JLabel("DataStore");
    private JLabel label3 = new JLabel("DataStoreType");
    private JLabel label4 = new JLabel("DataStoreServer");
    private JLabel label5 = new JLabel("DataStoreDB");
    private JLabel label6 = new JLabel("DataStoreUser");
    private JLabel label7 = new JLabel("DataStorePassword");
    private JLabel label8 = new JLabel("DataStoreMode");
    private JLabel label9 = new JLabel("Reset database");
    private JLabel label10 = new JLabel("Application name");
    private JLabel label11 = new JLabel("Application version");
    private JLabel label12 = new JLabel("AccessBridge enabled");
    private JLabel label13 = new JLabel("DataStoreDirectory");


    private JCheckBox stateModelEnabledChkBox = new JCheckBox();
    private JTextField dataStoreTextfield = new JTextField();
    private JTextField dataStoreServerTextfield = new JTextField();
    private JTextField dataStoreDBTextfield = new JTextField();
    private JTextField dataStoreUserTextfield = new JTextField();
    private JPasswordField dataStorePasswordfield = new JPasswordField();
    private JCheckBox resetDatabaseCheckbox = new JCheckBox();
    private JTextField applicationNameField = new JTextField();
    private JTextField applicationVersionField = new JTextField();
    private JComboBox<String> dataStoreModeBox = new JComboBox<>(new String[]{"none", "instant", "delayed", "hybrid"});
    private JComboBox<String> dataStoreTypeBox = new JComboBox<>(new String[]{"remote", "plocal"});
    private Set<JComponent> components;
    private JCheckBox accessBridgeEnabledBox = new JCheckBox();
    private JTextField dataStoreDirectoryField = new JTextField();
    private JButton dirButton = new JButton("..");

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
        // add the components that can be enabled/disabled to the set
        components = new HashSet<>();
        components.add(dataStoreTextfield);
        components.add(dataStoreTypeBox);
        components.add(dataStoreServerTextfield);
        components.add(dataStoreDirectoryField);
        components.add(dataStoreDBTextfield);
        components.add(dataStoreUserTextfield);
        components.add(dataStorePasswordfield);
        components.add(resetDatabaseCheckbox);
        components.add(applicationNameField);
        components.add(applicationVersionField);
        components.add(dataStoreModeBox);
        components.add(accessBridgeEnabledBox);
        components.add(dirButton);

        // add the components to the panel
        setLayout(null);
        label1.setBounds(10,14,150,27);
        add(label1);
        stateModelEnabledChkBox.setBounds(160,14,50,27);
        stateModelEnabledChkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                components.forEach((component) -> component.setEnabled(stateModelEnabledChkBox.isSelected()));
                if (stateModelEnabledChkBox.isSelected()) {
                    checkDataType();
                }
            }
        });
        add(stateModelEnabledChkBox);

        label2.setBounds(10,52,150,27);
        add(label2);
        dataStoreTextfield.setBounds(160,52,125,27);
        add(dataStoreTextfield);

        label3.setBounds(10,90,150,27);
        add(label3);
        dataStoreTypeBox.setBounds(160,90,125,27);
        dataStoreTypeBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                checkDataType();
            }
        });
        add(dataStoreTypeBox);

        label4.setBounds(10,128,150,27);
        add(label4);
        dataStoreServerTextfield.setBounds(160,128,125,27);
        add(dataStoreServerTextfield);

        label13.setBounds(10,166,150,27);
        add(label13);
        dataStoreDirectoryField.setBounds(160,166,125,27);
        dataStoreDirectoryField.setEditable(false);
        add(dataStoreDirectoryField);

        dirButton.setBounds(290, 166, 20, 27);
        dirButton.addActionListener(this::chooseFileActionPerformed);
        dirButton.setToolTipText("Select the 'databases' folder in your orientdb installation. Make sure the OrientDB server is not running.");
        add(dirButton);

        label5.setBounds(10,204,150,27);
        add(label5);
        dataStoreDBTextfield.setBounds(160,204,125,27);
        add(dataStoreDBTextfield);

        label6.setBounds(10,242,150,27);
        add(label6);
        dataStoreUserTextfield.setBounds(160,242,125,27);
        add(dataStoreUserTextfield);

        label7.setBounds(10,280,150,27);
        add(label7);
        dataStorePasswordfield.setBounds(160,280,125,27);
        add(dataStorePasswordfield);

        label8.setBounds(10,318,150,27);
        add(label8);
        dataStoreModeBox.setBounds(160,318,125,27);
        add(dataStoreModeBox);

        // NEW COLUMN

        label10.setBounds(330,52,150,27);
        add(label10);
        applicationNameField.setBounds(480, 52, 125, 27);
        add(applicationNameField);

        label11.setBounds(330,90,150,27);
        add(label11);
        applicationVersionField.setBounds(480, 90, 125, 27);
        add(applicationVersionField);

        label12.setBounds(330, 128, 150, 27);
        add(label12);
        accessBridgeEnabledBox.setBounds(480, 128, 50, 27);
        add(accessBridgeEnabledBox);

        label9.setBounds(330,166,150,27);
        add(label9);
        resetDatabaseCheckbox.setBounds(480, 166, 50, 27);
        resetDatabaseCheckbox.setToolTipText("This will reset the database. All stored information will be lost.");
        add(resetDatabaseCheckbox);
    }

    /**
     * Populate GraphDBFields from Settings structure.
     * @param settings The settings to load.
     */
    public void populateFrom(final Settings settings) {
        stateModelEnabledChkBox.setSelected(settings.get(ConfigTags.StateModelEnabled));
        accessBridgeEnabledBox.setSelected(settings.get(ConfigTags.AccessBridgeEnabled));
        dataStoreTextfield.setText(settings.get(ConfigTags.DataStore));
        dataStoreServerTextfield.setText(settings.get(ConfigTags.DataStoreServer));
        dataStoreDirectoryField.setText(settings.get(ConfigTags.DataStoreDirectory));
        dataStoreDBTextfield.setText(settings.get(ConfigTags.DataStoreDB));
        dataStoreUserTextfield.setText(settings.get(ConfigTags.DataStoreUser));
        dataStorePasswordfield.setText(settings.get(ConfigTags.DataStorePassword));
        for (int i =0; i < dataStoreModeBox.getItemCount(); i++) {
            if (dataStoreModeBox.getItemAt(i).equals(settings.get(ConfigTags.DataStoreMode))) {
                dataStoreModeBox.setSelectedIndex(i);
                break;
            }
        }
        for (int i=0; i < dataStoreTypeBox.getItemCount(); i++) {
            if (dataStoreTypeBox.getItemAt(i).equals(settings.get(ConfigTags.DataStoreType))) {
                dataStoreTypeBox.setSelectedIndex(i);
            }
        }
        applicationNameField.setText(settings.get(ConfigTags.ApplicationName));
        applicationVersionField.setText(settings.get(ConfigTags.ApplicationVersion));
        // check if the fields should be enabled or not
        components.forEach((component) -> component.setEnabled(stateModelEnabledChkBox.isSelected()));
        checkDataType();
    }

    /**
     * Retrieve information from the GraphDB GUI.
     * @param settings reference to the object where the settings will be stored.
     */
    public void extractInformation(final Settings settings) {
        settings.set(ConfigTags.StateModelEnabled, stateModelEnabledChkBox.isSelected());
        settings.set(ConfigTags.DataStore, dataStoreTextfield.getText());
        settings.set(ConfigTags.DataStoreServer, dataStoreServerTextfield.getText());
        settings.set(ConfigTags.DataStoreDirectory, dataStoreDirectoryField.getText());
        settings.set(ConfigTags.DataStoreDB, dataStoreDBTextfield.getText());
        settings.set(ConfigTags.DataStoreUser, dataStoreUserTextfield.getText());
        settings.set(ConfigTags.DataStorePassword, getPassword());
        settings.set(ConfigTags.DataStoreMode, (String)dataStoreModeBox.getSelectedItem());
        settings.set(ConfigTags.DataStoreType, (String)dataStoreTypeBox.getSelectedItem());
        settings.set(ConfigTags.ResetDataStore, resetDatabaseCheckbox.isSelected());
        settings.set(ConfigTags.ApplicationName, applicationNameField.getText());
        settings.set(ConfigTags.ApplicationVersion, applicationVersionField.getText());
        settings.set(ConfigTags.AccessBridgeEnabled, accessBridgeEnabledBox.isSelected());
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

    private void checkDataType() {
        dataStoreServerTextfield.setEnabled(dataStoreTypeBox.getSelectedItem().equals("remote"));
        dataStoreDirectoryField.setEnabled(dataStoreTypeBox.getSelectedItem().equals("plocal"));
        dirButton.setEnabled(dataStoreTypeBox.getSelectedItem().equals("plocal"));
    }

    private void chooseFileActionPerformed(ActionEvent evt) {
        JFileChooser fd = new JFileChooser();
        fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fd.setCurrentDirectory(new File(dataStoreDirectoryField.getText()).getParentFile());

        if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String file = fd.getSelectedFile().getAbsolutePath();

            // Set the text from settings in txtSutPath
            dataStoreDirectoryField.setText(file);
        }
    }

}
