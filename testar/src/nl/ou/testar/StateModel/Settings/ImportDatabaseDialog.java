/***************************************************************************************************
*
* Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2019 Open Universiteit - www.ou.nl
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.tool.ODatabaseImport;

import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;

public class ImportDatabaseDialog extends JDialog {

	private static final long serialVersionUID = -6458970322120335243L;
	private JLabel labelStoreType = new JLabel("DataStoreType");
	private JLabel labelStoreServer = new JLabel("DataStoreServer");
	private JLabel labelRoot = new JLabel("RootUser");
	private JLabel labelPassword = new JLabel("RootPassword");
	private JLabel labelImportDB = new JLabel("DB to import");
	private JLabel labelNameDB = new JLabel("New DB name");
	
	private JTextField textFieldStoreType = new JTextField();
	private JTextField textFieldStoreServer = new JTextField();
	private JTextField textFieldRoot = new JTextField();
	private JPasswordField textFieldPassword = new JPasswordField();
	private JTextField textFieldImportDB = new JTextField();
	private JTextField textFieldNameDB = new JTextField();
	
	private JButton buttonSelectDB = new JButton("Select DB");
	private JButton buttonImport = new JButton("Import Selected DB");
	
	private Set<JComponent> components;

	public ImportDatabaseDialog(String storeType, String storeServer) {
		initialize(storeType, storeServer);
	}
	
	private void initialize(String storeType, String storeServer) {

		components = new HashSet<>();
		components.add(textFieldStoreType);
		components.add(textFieldStoreServer);
		components.add(textFieldRoot);
		components.add(textFieldPassword);

		setSize(500, 500);
		setLayout(null);
		setVisible(true);
		setLocationRelativeTo(null);

		labelStoreType.setBounds(10,14,150,27);
		add(labelStoreType);
		textFieldStoreType.setBounds(160,14,125,27);
		textFieldStoreType.setText(storeType);
		add(textFieldStoreType);

		labelStoreServer.setBounds(10,52,150,27);
		add(labelStoreServer);
		textFieldStoreServer.setBounds(160,52,125,27);
		textFieldStoreServer.setText(storeServer);
		add(textFieldStoreServer);

		labelRoot.setBounds(10,90,150,27);
		add(labelRoot);
		textFieldRoot.setBounds(160,90,125,27);
		textFieldRoot.setText("root");
		add(textFieldRoot);

		labelPassword.setBounds(10,128,150,27);
		add(labelPassword);
		textFieldPassword.setBounds(160,128,125,27);
		add(textFieldPassword);
		
		labelImportDB.setBounds(10,166,150,27);
		add(labelImportDB);
		textFieldImportDB.setBounds(160,166,325,27);
		add(textFieldImportDB);
		
		buttonSelectDB.setBounds(330,204,150,27);
		buttonSelectDB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseFileToImport();
			}
		});
		add(buttonSelectDB);
		
		labelNameDB.setBounds(10,242,150,27);
		add(labelNameDB);
		textFieldNameDB.setBounds(160,242,150,27);
		add(textFieldNameDB);
		
		buttonImport.setBounds(330,280,150,27);
		buttonImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkIfDatabaseExists())
					importDatabase();
			}
		});
		add(buttonImport);

	}
	
	private void chooseFileToImport() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    textFieldImportDB.setText(selectedFile.getAbsolutePath());
		    obtainDefaultDBname(selectedFile.getAbsolutePath());
		}
	}
	
	private void obtainDefaultDBname(String dbPath) {
		if(dbPath.contains(".export.gz")) {
			try {
				int startIndex = dbPath.lastIndexOf('\\');
				int endIndex = dbPath.lastIndexOf(".export.gz");
				String substring = dbPath.substring(startIndex+1, endIndex);
				textFieldNameDB.setText(substring);
			}catch(Exception e) {}
		}			
	}
	
	private boolean checkIfDatabaseExists() {
		
		Config config = new Config();
		config.setConnectionType(textFieldStoreType.getText());
		config.setServer(textFieldStoreServer.getText());
		config.setUser(textFieldRoot.getText());
		config.setPassword(getPassword());

		try {

			OrientDB orientDB = new OrientDB(config.getConnectionType() + ":" + config.getServer(), 
					config.getUser(), config.getPassword(), OrientDBConfig.defaultConfig());

			if(orientDB.list().contains(textFieldNameDB.getText())) {
				orientDB.close();
				JFrame frame = new JFrame();
				JOptionPane.showMessageDialog(frame, 
						"This database already exist, please select other name to create and import");
				return true;
			}	

			orientDB.close();

		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return false;

	}
	
	private void importDatabase() {
		Config config = new Config();
		config.setConnectionType(textFieldStoreType.getText());
		config.setServer(textFieldStoreServer.getText());
		config.setUser(textFieldRoot.getText());
		config.setPassword(getPassword());
		
		// First we need to create the database
		// create database remote:localhost/notepad root testar
		
		OrientDB orientDB = new OrientDB(config.getConnectionType() + ":" + config.getServer(), 
				config.getUser(), config.getPassword(), OrientDBConfig.defaultConfig());
		
		orientDB.create(textFieldNameDB.getText(), ODatabaseType.PLOCAL);
		config.setDatabase(textFieldNameDB.getText());
		
		String dbConnection = config.getConnectionType() + ":" + config.getServer() + "/database/" + config.getDatabase();
		
		try (ODatabaseSession sessionDB = orientDB.open(dbConnection, config.getUser(), config.getPassword())){

			OCommandOutputListener listener = new OCommandOutputListener() {
				@Override
				public void onMessage(String iText) {
					System.out.println(iText);
				}
			};

			ODatabaseImport importDB = new ODatabaseImport((ODatabaseDocumentInternal)sessionDB, textFieldImportDB.getText(), listener);
			importDB.importDatabase();
			importDB.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			orientDB.close();
		}
		
	}
	
	/**
     * Convert password field to string.
     * @return password as String.
     */
    private String getPassword() {
        StringBuilder result= new StringBuilder();
        for(char c : textFieldPassword.getPassword()) {
            result.append(c);
        }
        return  result.toString();
    }
	
}
