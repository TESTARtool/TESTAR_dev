/***************************************************************************************************
*
* Copyright (c) 2017 - 2025 Open Universiteit - www.ou.nl
* Copyright (c) 2017 - 2025 Universitat Politecnica de Valencia - www.upv.es
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


package org.testar.settings.dialog;

import org.testar.CodingManager;
import org.testar.StateManagementTags;

import org.testar.statemodel.StateModelTags;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.analysis.webserver.JettyServer;
import org.testar.statemodel.persistence.orientdb.entity.Config;

import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel with settings for the state model inference module.
 */
public class StateModelPanel extends SettingsPanel {

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
    private JLabel label13 = new JLabel("DataStoreDirectory");
    private JLabel label14 = new JLabel();
    private JLabel label15 = new JLabel("Action selection");
    private JLabel label16 = new JLabel("Store Widgets");


    private JCheckBox stateModelEnabledChkBox = new JCheckBox();
    private JCheckBox stateModelWidgetStoreChkBox = new JCheckBox();
    private JTextField dataStoreTextfield = new JTextField();
    private JTextField dataStoreServerTextfield = new JTextField();
    private JTextField dataStoreDBTextfield = new JTextField();
    private JTextField dataStoreUserTextfield = new JTextField();
    private JPasswordField dataStorePasswordfield = new JPasswordField();
    private JCheckBox resetDatabaseCheckbox = new JCheckBox();
    private JComboBox<String> dataStoreModeBox = new JComboBox<>(new String[]{"none", "instant", "delayed", "hybrid"});
    private JComboBox<String> actionSelectionBox = new JComboBox<>(new String[]{"Random selection", "Unvisited actions first"});
    private JComboBox<String> dataStoreTypeBox = new JComboBox<>(new String[]{"remote", "plocal"});
    private Set<JComponent> components;
    private JTextField dataStoreDirectoryField = new JTextField();
    private JButton dirButton = new JButton("..");
    private JButton stateTagsButton = new JButton("Advanced");
    private AbstractStateSettings stateTagsDialog;
    private JButton analysisButton = new JButton("Analysis");
    private Tag<?>[] allStateManagementTags;
    private Tag<?>[] selectedStateManagementTags;

    private String outputDir;

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
        // fetch the available state management tags
        allStateManagementTags = StateManagementTags.getAllTags().toArray(new Tag<?>[0]);
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
        components.add(dataStoreModeBox);
        components.add(dirButton);
        components.add(analysisButton);
        components.add(stateTagsButton);
        components.add(actionSelectionBox);
        components.add(stateModelWidgetStoreChkBox);

        // add the components to the panel
        setLayout(null);
        label1.setBounds(10, 14, 150, 27);
        add(label1);
        stateModelEnabledChkBox.setBounds(160, 14, 50, 27);
        stateModelEnabledChkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                components.forEach((component) -> component.setEnabled(stateModelEnabledChkBox.isSelected()));
                if (stateModelEnabledChkBox.isSelected()) {
                    checkDataType();
                }
                checkAnalysisButtonActive();
            }
        });
        add(stateModelEnabledChkBox);

        label2.setBounds(10, 52, 150, 27);
        add(label2);
        dataStoreTextfield.setBounds(160, 52, 125, 27);
        add(dataStoreTextfield);

        label3.setBounds(10, 90, 150, 27);
        add(label3);
        dataStoreTypeBox.setBounds(160, 90, 125, 27);
        dataStoreTypeBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                checkDataType();
            }
        });
        add(dataStoreTypeBox);

        label4.setBounds(10, 128, 150, 27);
        add(label4);
        dataStoreServerTextfield.setBounds(160, 128, 125, 27);
        add(dataStoreServerTextfield);

        label13.setBounds(10, 166, 150, 27);
        add(label13);
        dataStoreDirectoryField.setBounds(160, 166, 125, 27);
        dataStoreDirectoryField.setEditable(false);
        add(dataStoreDirectoryField);

        dirButton.setBounds(290, 166, 20, 27);
        dirButton.addActionListener(this::chooseFileActionPerformed);
        dirButton.setToolTipText("Select the 'databases' folder in your orientdb installation. Make sure the OrientDB server is not running.");
        add(dirButton);

        label5.setBounds(10, 204, 150, 27);
        add(label5);
        dataStoreDBTextfield.setBounds(160, 204, 125, 27);
        add(dataStoreDBTextfield);

        label6.setBounds(10, 242, 150, 27);
        add(label6);
        dataStoreUserTextfield.setBounds(160, 242, 125, 27);
        add(dataStoreUserTextfield);

        label7.setBounds(10, 280, 150, 27);
        add(label7);
        dataStorePasswordfield.setBounds(160, 280, 125, 27);
        add(dataStorePasswordfield);

        label8.setBounds(10, 318, 150, 27);
        add(label8);
        dataStoreModeBox.setBounds(160, 318, 125, 27);
        add(dataStoreModeBox);

        // NEW COLUMN
        String performanceWarning = "<html><b>Warning:</b> Enabling this feature will save all concrete widgets in the database.<br>"
        		+ "This can significantly <b>reduce the performance</b> during state model inference.</html>";
        label16.setBounds(330, 52, 150, 27);
        label16.setToolTipText(performanceWarning);
        add(label16);
        stateModelWidgetStoreChkBox.setBounds(480, 52, 50, 27);
        stateModelWidgetStoreChkBox.setToolTipText(performanceWarning);
        add(stateModelWidgetStoreChkBox);

        label9.setBounds(330,90,150,27);
        add(label9);
        resetDatabaseCheckbox.setBounds(480, 90, 50, 27);
        resetDatabaseCheckbox.setToolTipText("This will reset the database. All stored information will be lost.");
        add(resetDatabaseCheckbox);

        analysisButton.setBounds(330, 128, 150, 27);
        analysisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openServer();
                checkAnalysisButtonActive();
            }
        });
        add(analysisButton);

        stateTagsButton.setBounds(330, 166, 150, 27);
        stateTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openStateTagSelection();
            }
        });
        add(stateTagsButton);

        label15.setBounds(330, 204, 100, 27);
        add(label15);
        actionSelectionBox.setBounds(430, 204, 175, 27);
        add(actionSelectionBox);

        label14.setBounds(330, 242, 300, 27);
        add(label14);

    }

    /**
     * Populate GraphDBFields from Settings structure.
     * @param settings The settings to load.
     */
    public void populateFrom(final Settings settings) {
        stateModelEnabledChkBox.setSelected(settings.get(StateModelTags.StateModelEnabled));
        stateModelWidgetStoreChkBox.setSelected(settings.get(StateModelTags.StateModelStoreWidgets));
        dataStoreTextfield.setText(settings.get(StateModelTags.DataStore));
        dataStoreServerTextfield.setText(settings.get(StateModelTags.DataStoreServer));
        dataStoreDirectoryField.setText(settings.get(StateModelTags.DataStoreDirectory));
        dataStoreDBTextfield.setText(settings.get(StateModelTags.DataStoreDB));
        dataStoreUserTextfield.setText(settings.get(StateModelTags.DataStoreUser));
        dataStorePasswordfield.setText(settings.get(StateModelTags.DataStorePassword));
        for (int i =0; i < dataStoreModeBox.getItemCount(); i++) {
            if (dataStoreModeBox.getItemAt(i).equals(settings.get(StateModelTags.DataStoreMode))) {
                dataStoreModeBox.setSelectedIndex(i);
                break;
            }
        }
        for (int i=0; i < dataStoreTypeBox.getItemCount(); i++) {
            if (dataStoreTypeBox.getItemAt(i).equals(settings.get(StateModelTags.DataStoreType))) {
                dataStoreTypeBox.setSelectedIndex(i);
            }
        }
        // check if the fields should be enabled or not
        components.forEach((component) -> component.setEnabled(stateModelEnabledChkBox.isSelected()));
        checkDataType();
        checkAnalysisButtonActive();
        outputDir = settings.get(ConfigTags.OutputDir);
        // check if the output directory has a trailing line separator
        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
            outputDir += File.separator;
        }
        outputDir = outputDir + "graphs" + File.separator;

        // set the selected state management tags
        if (settings.get(ConfigTags.AbstractStateAttributes) != null) {
            List<String> abstractStateAttributes = settings.get(ConfigTags.AbstractStateAttributes);
            selectedStateManagementTags = abstractStateAttributes.stream().map(StateManagementTags::getTagFromSettingsString).filter(tag -> tag != null).toArray(Tag<?>[]::new);
        }
        else {
            selectedStateManagementTags = new Tag<?>[0];
        }

        // for now, only two options, so we'll do this the quick and easy way, without creating a list model
        String currentAlgorithm = settings.get(StateModelTags.ActionSelectionAlgorithm);
        for (int i =0; i < actionSelectionBox.getItemCount(); i++) {
            if (actionSelectionBox.getItemAt(i).equals("Random selection") && currentAlgorithm.equals("random")) {
                actionSelectionBox.setSelectedIndex(i);
                break;
            }
            if (actionSelectionBox.getItemAt(i).equals("Unvisited actions first") && currentAlgorithm.equals("unvisited")) {
                actionSelectionBox.setSelectedIndex(i);
                break;
            }
        }

    }

    /**
     * Retrieve information from the GraphDB GUI.
     * @param settings reference to the object where the settings will be stored.
     */
    public void extractInformation(final Settings settings) {
        settings.set(StateModelTags.StateModelEnabled, stateModelEnabledChkBox.isSelected());
        settings.set(StateModelTags.StateModelStoreWidgets, stateModelWidgetStoreChkBox.isSelected());
        settings.set(StateModelTags.DataStore, dataStoreTextfield.getText());
        settings.set(StateModelTags.DataStoreServer, dataStoreServerTextfield.getText());
        settings.set(StateModelTags.DataStoreDirectory, dataStoreDirectoryField.getText());
        settings.set(StateModelTags.DataStoreDB, dataStoreDBTextfield.getText());
        settings.set(StateModelTags.DataStoreUser, dataStoreUserTextfield.getText());
        settings.set(StateModelTags.DataStorePassword, getPassword());
        settings.set(StateModelTags.DataStoreMode, (String)dataStoreModeBox.getSelectedItem());
        settings.set(StateModelTags.DataStoreType, (String)dataStoreTypeBox.getSelectedItem());
        settings.set(StateModelTags.ResetDataStore, resetDatabaseCheckbox.isSelected());
        settings.set(ConfigTags.AbstractStateAttributes, Arrays.stream(selectedStateManagementTags).map(StateManagementTags::getSettingsStringFromTag).collect(Collectors.toList()));
        switch ((String) actionSelectionBox.getSelectedItem()) {
            case "Unvisited actions first":
                settings.set(StateModelTags.ActionSelectionAlgorithm, "unvisited");
                break;

            default:
                settings.set(StateModelTags.ActionSelectionAlgorithm, "random");
        }
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

    // make sure the right text fields are enabled based on the selected data store type (remote or local)
    private void checkDataType() {
        dataStoreServerTextfield.setEnabled(dataStoreTypeBox.getSelectedItem().equals("remote") && stateModelEnabledChkBox.isSelected());
        dataStoreDirectoryField.setEnabled(dataStoreTypeBox.getSelectedItem().equals("plocal"));
        dirButton.setEnabled(dataStoreTypeBox.getSelectedItem().equals("plocal"));
    }

    // helper method to ensure that the state model enabled box is selected
    private void checkAnalysisButtonActive() {
        analysisButton.setEnabled(stateModelEnabledChkBox.isSelected());
    }

    // show a file dialog to choose the directory where the local install of OrientDB is located
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

    // this helper method will start a jetty integrated server and show the model listings page
    public void openServer() {
        try {
            label14.setText("");
            // create a config object for the orientdb database connection info
            Config config = new Config();
            config.setConnectionType((String)dataStoreTypeBox.getSelectedItem());
            config.setServer(dataStoreServerTextfield.getText());
            config.setDatabase(dataStoreDBTextfield.getText());
            config.setUser(dataStoreUserTextfield.getText());
            config.setPassword(getPassword());
            config.setDatabaseDirectory(dataStoreDirectoryField.getText());
            AnalysisManager analysisManager = new AnalysisManager(config, outputDir);
            JettyServer jettyServer = new JettyServer();
            jettyServer.start(outputDir, analysisManager);
        } catch (ODatabaseException de) {
        	// There it can be a root cause that indicates that the IP address or server is not running
        	if(de.getCause() != null && de.getCause().getMessage() != null && de.getCause().getMessage().contains("Cannot create a connection")) {
        		popupMessage(de.getCause().getMessage());
        		return;
        	}
        	// If the database does not exists
        	else if(de.getMessage() != null && de.getMessage().contains("Cannot open database")) {
        		popupMessage(de.getMessage());
        		return;
        	}
        	// Not expected exception, throw trace in the console
        	else {
        		de.printStackTrace();
        		return;
        	}
        } catch (OSecurityAccessException se) {
        	// If the user credential are wrong
        	if(se.getMessage() != null && se.getMessage().contains("User or password not valid")) {
        		popupMessage(se.getMessage());
        		return;
        	}
        	// Not expected exception, throw trace in the console
        	else {
        		se.printStackTrace();
        		return;
        	}
        } catch (IOException e) {
        	// If the exception is because the server is already running, just catch and connect
        	if(e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().contains("Address already in use")) {
        		System.out.println(e.getCause().getMessage());
        		// Continue and try to open the browser to the running server
        	} else {
        		label14.setText("Please check your connection credentials.");
        		e.printStackTrace();
        		// Something wrong with the database connection, return because we don't want to open the browser
        		return;
        	}
        } catch (Exception e) {
            // the plain Exception is coming from 3rd party code
            label14.setText("Please check your connection credentials.");
            e.printStackTrace();
            // Something wrong with the database connection, return because we don't want to open the browser
            return;
        }

        openBrowser();
    }

    private void popupMessage(String connectionMessage) {
    	JFrame frame = new JFrame();
    	String header = "Incorrect OrientDB settings";
    	String userMessage = "Cannot connect with OrientDB to view the State Model";
    	userMessage = userMessage.concat("\nReason: " + connectionMessage);
    	userMessage = userMessage.concat("\nPlease review the OrientDB connection settings");
    	JOptionPane.showMessageDialog(frame, userMessage, header, JOptionPane.ERROR_MESSAGE);
    }

    private void openBrowser() {
    	try {
    		Desktop desktop = java.awt.Desktop.getDesktop();
    		URI uri = new URI("http://localhost:8090/models");
    		desktop.browse(uri);
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (URISyntaxException e) {
    		e.printStackTrace();
    	}
    }

    private void openStateTagSelection() {
        stateTagsDialog = new AbstractStateSettings(allStateManagementTags, selectedStateManagementTags, CodingManager.getDefaultAbstractStateTags());
        stateTagsDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // tell the manager to shut down its connection
               selectedStateManagementTags = stateTagsDialog.getCurrentlySelectedStateTags();
            }
        });
    }

}
