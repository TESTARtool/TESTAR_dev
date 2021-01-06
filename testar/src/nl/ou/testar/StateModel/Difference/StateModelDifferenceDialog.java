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

package nl.ou.testar.StateModel.Difference;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.fruit.Pair;

import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;

/**
 * Model Difference GUI Dialog that allows users to manually select two State Models to compare.
 */
public class StateModelDifferenceDialog extends JDialog {

	private static final long serialVersionUID = -7681898049512465816L;

	private JLabel labelStoreType = new JLabel("DataStoreType");
	private JLabel labelStoreServer = new JLabel("DataStoreServer");
	private JLabel labelStoreDirectory = new JLabel("DataStoreDirectory");
	private JLabel labelRoot = new JLabel("RootUser");
	private JLabel labelPassword = new JLabel("RootPassword");
	private JLabel labelStoreDB = new JLabel("Existing DB");

	private JTextField textFieldStoreServer = new JTextField();
	private JTextField textFieldStoreDirectory = new JTextField();
	private JTextField textFieldRoot = new JTextField();
	private JPasswordField textFieldPassword = new JPasswordField();

	private JComboBox<String> dataStoreTypeBox = new JComboBox<>(new String[]{"remote", "plocal"});

	private JButton buttonConnect = new JButton("Connect");
	private JButton dirButton = new JButton("..");
	private JButton buttonModelDiff = new JButton("Model-Diff");
	private JButton buttonCancel = new JButton("Cancel report");

	private JComboBox<String> listDatabases = new JComboBox<>();

	private JLabel labelApplicationNameOne = new JLabel("1. Application Name");
	private JLabel labelApplicationVersionOne = new JLabel("1. Application Version");
	private JLabel labelApplicationNameTwo = new JLabel("2. Application Name");
	private JLabel labelApplicationVersionTwo = new JLabel("2. Application Version");
	private JTextField textApplicationNameOne = new JTextField();
	private JTextField textApplicationVersionOne = new JTextField();
	private JTextField textApplicationNameTwo = new JTextField();
	private JTextField textApplicationVersionTwo = new JTextField();
	
	private StateModelDifferenceManager modelDifferenceManager;

	public StateModelDifferenceDialog(String storeType, String storeServer, String storeDirectory) {
		initialize(storeType, storeServer, storeDirectory);
		
		Config config = new Config();
		config.setConnectionType((String)dataStoreTypeBox.getSelectedItem());
		config.setServer(textFieldStoreServer.getText());
		config.setUser(textFieldRoot.getText());
		config.setPassword(getPassword(textFieldPassword));
		config.setDatabaseDirectory(textFieldStoreDirectory.getText());
		
		this.modelDifferenceManager = new StateModelDifferenceManager(config, "");
	}

	private void initialize(String storeType, String storeServer, String storeDirectory) {

		setTitle("TESTAR State Model Difference");

		setSize(1000, 500);
		setLayout(null);
		setVisible(true);
		setLocationRelativeTo(null);

		labelStoreType.setBounds(10,14,150,27);
		add(labelStoreType);
		dataStoreTypeBox.setBounds(160,14,125,27);
		dataStoreTypeBox.setSelectedItem(storeType);
		checkDataType();
		dataStoreTypeBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				checkDataType();
			}
		});
		add(dataStoreTypeBox);

		labelStoreServer.setBounds(10,52,150,27);
		add(labelStoreServer);
		textFieldStoreServer.setBounds(160,52,125,27);
		textFieldStoreServer.setText(storeServer);
		add(textFieldStoreServer);

		labelStoreDirectory.setBounds(10,90,150,27);
		add(labelStoreDirectory);
		textFieldStoreDirectory.setBounds(160,90,125,27);
		textFieldStoreDirectory.setText(storeDirectory);
		add(textFieldStoreDirectory);

		dirButton.setBounds(290, 90, 20, 27);
		dirButton.addActionListener(this::chooseFileActionPerformed);
		dirButton.setToolTipText("Select the 'databases' folder in your orientdb installation. Make sure the OrientDB server is not running.");
		add(dirButton);

		labelRoot.setBounds(10,128,150,27);
		add(labelRoot);
		textFieldRoot.setBounds(160,128,125,27);
		textFieldRoot.setText("root");
		add(textFieldRoot);

		labelPassword.setBounds(10,166,150,27);
		add(labelPassword);
		textFieldPassword.setBounds(160,166,125,27);
		add(textFieldPassword);

		buttonConnect.setBounds(330, 204, 150, 27);
		buttonConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Config config = new Config();
				config.setConnectionType((String)dataStoreTypeBox.getSelectedItem());
				config.setServer(textFieldStoreServer.getText());
				config.setUser(textFieldRoot.getText());
				config.setPassword(getPassword(textFieldPassword));
				config.setDatabaseDirectory(textFieldStoreDirectory.getText());
				
				modelDifferenceManager.obtainAvailableDatabases(config, listDatabases);
			}
		});
		add(buttonConnect);

		labelStoreDB.setBounds(10,204,150,27);
		add(labelStoreDB);
		listDatabases.setBounds(160,204,150,27);
		add(listDatabases);

		labelApplicationNameOne.setBounds(10,242,150,27);
		add(labelApplicationNameOne);
		textApplicationNameOne.setBounds(160,242,325,27);
		textApplicationNameOne.setText("NombreApp");
		add(textApplicationNameOne);

		labelApplicationVersionOne.setBounds(10,280,150,27);
		add(labelApplicationVersionOne);
		textApplicationVersionOne.setBounds(160,280,325,27);
		textApplicationVersionOne.setText("VersionApp");
		add(textApplicationVersionOne);

		labelApplicationNameTwo.setBounds(510,242,150,27);
		add(labelApplicationNameTwo);
		textApplicationNameTwo.setBounds(660,242,325,27);
		textApplicationNameTwo.setText("NombreApp");
		add(textApplicationNameTwo);

		labelApplicationVersionTwo.setBounds(510,280,150,27);
		add(labelApplicationVersionTwo);
		textApplicationVersionTwo.setBounds(660,280,325,27);
		textApplicationVersionTwo.setText("VersionApp");
		add(textApplicationVersionTwo);

		buttonModelDiff.setBounds(330, 356, 150, 27);
		buttonModelDiff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				executeModelDiffManager();
			}
		});
		add(buttonModelDiff);

		buttonCancel.setBounds(330, 408, 150, 27);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modelDifferenceManager.closeOrientDB();
				dispose();
			}
		});
		add(buttonCancel);

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				modelDifferenceManager.closeOrientDB();
			}
		});

	}

	private void executeModelDiffManager() {
		Config config = new Config();
		config.setConnectionType((String)dataStoreTypeBox.getSelectedItem());
		config.setServer(textFieldStoreServer.getText());
		config.setDatabase(listDatabases.getSelectedItem().toString());
		config.setUser(textFieldRoot.getText());
		config.setPassword(getPassword(textFieldPassword));
		config.setDatabaseDirectory(textFieldStoreDirectory.getText());
		
		Pair<String,String> stateModelOne = new Pair<>(textApplicationNameOne.getText(), textApplicationVersionOne.getText());
		Pair<String,String> stateModelTwo = new Pair<>(textApplicationNameTwo.getText(), textApplicationVersionTwo.getText());
		
		modelDifferenceManager.calculateModelDifference(config, stateModelOne, stateModelTwo);
	}

	// make sure the right text fields are enabled based on the selected data store type (remote or local)
	private void checkDataType() {
		textFieldStoreServer.setEnabled(dataStoreTypeBox.getSelectedItem().equals("remote"));
		textFieldStoreDirectory.setEnabled(dataStoreTypeBox.getSelectedItem().equals("plocal"));
		dirButton.setEnabled(dataStoreTypeBox.getSelectedItem().equals("plocal"));
	}

	// show a file dialog to choose the directory where the local install of OrientDB is located
	private void chooseFileActionPerformed(ActionEvent evt) {
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fd.setCurrentDirectory(new File(textFieldStoreDirectory.getText()).getParentFile());

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();

			// Set the text from settings in txtSutPath
			textFieldStoreDirectory.setText(file);
		}
	}

	/**
	 * Convert password field to string.
	 * @return password as String.
	 */
	private static String getPassword(JPasswordField passField) {
		StringBuilder result= new StringBuilder();
		for(char c : passField.getPassword()) {
			result.append(c);
		}
		return  result.toString();
	}  
}
