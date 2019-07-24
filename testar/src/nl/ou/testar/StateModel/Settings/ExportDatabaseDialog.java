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
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.tool.ODatabaseExport;

import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;

public class ExportDatabaseDialog extends JDialog {

	private static final long serialVersionUID = -8694666902430381117L;
	private JLabel labelStoreType = new JLabel("DataStoreType");
	private JLabel labelStoreServer = new JLabel("DataStoreServer");
	private JLabel labelRoot = new JLabel("RootUser");
	private JLabel labelPassword = new JLabel("RootPassword");
	private JLabel labelStoreDB = new JLabel("Existing DB");

	private JTextField textFieldStoreType = new JTextField();
	private JTextField textFieldStoreServer = new JTextField();
	private JTextField textFieldRoot = new JTextField();
	private JPasswordField textFieldPassword = new JPasswordField();

	private JButton buttonConnect = new JButton("Connect");
	private JButton buttonExport = new JButton("Export selected DB");

	private JComboBox<String> listDatabases = new JComboBox<>();

	private Set<JComponent> components;

	public ExportDatabaseDialog(String storeType, String storeServer) {
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

		buttonConnect.setBounds(330, 166, 150, 27);
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

		buttonExport.setBounds(330, 242, 150, 27);
		buttonExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportDatabase();
			}
		});
		add(buttonExport);
	}

	private void obtainAvailableDatabases() {
		Config config = new Config();
		config.setConnectionType(textFieldStoreType.getText());
		config.setServer(textFieldStoreServer.getText());
		config.setUser(textFieldRoot.getText());
		config.setPassword(getPassword());

		try {
			
			listDatabases.removeAllItems();

			OrientDB orientDB = new OrientDB(config.getConnectionType() + ":" + config.getServer(), 
					config.getUser(), config.getPassword(), OrientDBConfig.defaultConfig());

			if(!orientDB.list().isEmpty())
				for(String database : orientDB.list())
					listDatabases.addItem(database);
			
			orientDB.close();
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	private void exportDatabase() {
		Config config = new Config();
		config.setConnectionType(textFieldStoreType.getText());
		config.setServer(textFieldStoreServer.getText());
		config.setUser(textFieldRoot.getText());
		config.setPassword(getPassword());
		config.setDatabase(listDatabases.getSelectedItem().toString());

		OrientDB orientDB = new OrientDB(config.getConnectionType() + ":" + config.getServer(), 
				config.getUser(), config.getPassword(), OrientDBConfig.defaultConfig());

		String dbConnection = config.getConnectionType() + ":" + config.getServer() + "/database/" + config.getDatabase();
		
		try (ODatabaseSession sessionDB = orientDB.open(dbConnection, config.getUser(), config.getPassword())){

			OCommandOutputListener listener = new OCommandOutputListener() {
				@Override
				public void onMessage(String iText) {
					System.out.println(iText);
				}
			};

			ODatabaseExport exportDB = new ODatabaseExport((ODatabaseDocumentInternal)sessionDB, config.getDatabase(), listener);
			exportDB.exportDatabase();
			exportDB.close();
			
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
