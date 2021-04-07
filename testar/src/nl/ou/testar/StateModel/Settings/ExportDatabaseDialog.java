/***************************************************************************************************
*
* Copyright (c) 2019, 2020 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2019, 2020 Open Universiteit - www.ou.nl
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.fruit.monkey.Main;

import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.tool.ODatabaseExport;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;

import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;

public class ExportDatabaseDialog extends JDialog {

	private static final long serialVersionUID = -8694666902430381117L;
	private JLabel labelStoreType = new JLabel("DataStoreType");
	private JLabel labelStoreServer = new JLabel("DataStoreServer");
	private JLabel labelStoreDirectory = new JLabel("DataStoreDirectory");
	private JLabel labelRoot = new JLabel("RootUser");
	private JLabel labelPassword = new JLabel("RootPassword");
	private JLabel labelStoreDB = new JLabel("Existing DB");
	private JLabel labelPathExport = new JLabel("Path to Export");

	private JTextField textFieldStoreServer = new JTextField();
	private JTextField textFieldStoreDirectory = new JTextField();
	private JTextField textFieldRoot = new JTextField();
	private JPasswordField textFieldPassword = new JPasswordField();
	private JTextField textFieldPathExport = new JTextField();

	private JComboBox<String> dataStoreTypeBox = new JComboBox<>(new String[]{"remote", "plocal"});

	private JButton buttonConnect = new JButton("Connect");
    private JButton dirButton = new JButton("..");
	private JButton buttonExport = new JButton("Export selected DB");
	private JButton buttonPathExport = new JButton("Choose Path");
	private JButton buttonCancel = new JButton("Cancel Export");

	private JComboBox<String> listDatabases = new JComboBox<>();

	// orient db instance that will create database sessions
    private transient OrientDB orientDB;

    // orient db configuration object
    private transient Config dbConfig;

	public ExportDatabaseDialog(String storeType, String storeServer, String storeDirectory) {
		initialize(storeType, storeServer, storeDirectory);
	}

	private void initialize(String storeType, String storeServer, String storeDirectory) {

		setTitle("TESTAR Export OrientDB database");

		setSize(500, 500);
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
				obtainAvailableDatabases();
			}
		});
		add(buttonConnect);

		labelStoreDB.setBounds(10,204,150,27);
		add(labelStoreDB);
		listDatabases.setBounds(160,204,150,27);
		add(listDatabases);

		labelPathExport.setBounds(10,242,150,27);
		add(labelPathExport);
		textFieldPathExport.setBounds(160,242,325,27);
		try {
			textFieldPathExport.setText(new File(Main.outputDir).getCanonicalPath());
		} catch (IOException e1) {
			textFieldPathExport.setText(Main.outputDir);
		}
		add(textFieldPathExport);

		buttonPathExport.setBounds(330,280,150,27);
		buttonPathExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseDirectoryToExport();
			}
		});
		add(buttonPathExport);

		buttonExport.setBounds(330, 318, 150, 27);
		buttonExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportDatabase();
			}
		});
		add(buttonExport);

		buttonCancel.setBounds(330, 408, 150, 27);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeOrientDB();
				dispose();
			}
		});
		add(buttonCancel);

		this.addWindowListener(new java.awt.event.WindowAdapter() {
	        @Override
	        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	        	closeOrientDB();
	        }
	    });
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

	private void chooseDirectoryToExport() {
		JFileChooser directoryChooser = new JFileChooser();
		directoryChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = directoryChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = directoryChooser.getSelectedFile();
		    textFieldPathExport.setText(selectedFile.getAbsolutePath());
		}
	}

	private void obtainAvailableDatabases() {
		dbConfig = new Config();
		dbConfig.setConnectionType(dataStoreTypeBox.getSelectedItem().toString());
		dbConfig.setServer(textFieldStoreServer.getText());
		dbConfig.setUser(textFieldRoot.getText());
		dbConfig.setPassword(getPassword());
		dbConfig.setDatabaseDirectory(textFieldStoreDirectory.getText());

		try{

			listDatabases.removeAllItems();

	        String connectionString = dbConfig.getConnectionType() + ":" + (dbConfig.getConnectionType().equals("remote") ?
	                dbConfig.getServer() : dbConfig.getDatabaseDirectory()) + "/";

			orientDB = new OrientDB(connectionString, dbConfig.getUser(), dbConfig.getPassword(), OrientDBConfig.defaultConfig());

			if(!orientDB.list().isEmpty())
				for(String database : orientDB.list())
					listDatabases.addItem(database);

		} catch(OSecurityAccessException e) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, 
					" User or password not valid for database: " + listDatabases.getSelectedItem().toString() + 
					"\n plocal databases do not use 'root' user" + 
					"\n try with customized user");
			frame.setAlwaysOnTop(true);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		} finally {
			orientDB.close();
		}

	}

	private void exportDatabase() {
		dbConfig = new Config();
		dbConfig.setConnectionType(dataStoreTypeBox.getSelectedItem().toString());
		dbConfig.setServer(textFieldStoreServer.getText());
		dbConfig.setUser(textFieldRoot.getText());
		dbConfig.setPassword(getPassword());
		dbConfig.setDatabase(listDatabases.getSelectedItem().toString());
		dbConfig.setDatabaseDirectory(textFieldStoreDirectory.getText());

        String connectionString = dbConfig.getConnectionType() + ":" + (dbConfig.getConnectionType().equals("remote") ?
                dbConfig.getServer() : dbConfig.getDatabaseDirectory()) + "/";

		orientDB = new OrientDB(connectionString, dbConfig.getUser(), dbConfig.getPassword(), OrientDBConfig.defaultConfig());

		try (ODatabaseSession sessionDB = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword())){

			OCommandOutputListener listener = new OCommandOutputListener() {
				@Override
				public void onMessage(String iText) {
					System.out.println(iText);
				}
			};

			String fileNameDB = dbConfig.getDatabase();

			if(new File(textFieldPathExport.getText()).exists())
				fileNameDB = textFieldPathExport.getText() + File.separator + dbConfig.getDatabase();

			ODatabaseExport exportDB = new ODatabaseExport((ODatabaseDocumentInternal)sessionDB, fileNameDB, listener);
			exportDB.exportDatabase();
			exportDB.close();

		} catch(OSecurityAccessException e) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, 
					" User or password not valid for database: " + listDatabases.getSelectedItem().toString() + 
					"\n plocal databases do not use 'root' user" + 
					"\n try with customized user");
			frame.setAlwaysOnTop(true);
		} catch(Exception e) {
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

    private void closeOrientDB() {
    	if(orientDB!=null && orientDB.isOpen())
    		orientDB.close();
    }

}