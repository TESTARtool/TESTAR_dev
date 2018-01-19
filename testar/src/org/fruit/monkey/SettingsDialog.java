/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fruit.monkey;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.ToolTipManager;

import org.fruit.Pair;
import org.fruit.Util;

import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.serialisation.LogSerialiser;

public class SettingsDialog extends javax.swing.JFrame {

	private static final long serialVersionUID = 5156320008281200950L;

	public static final String TESTAR_VERSION = "v1.3";

	String settingsFile;
	Settings settings, ret;

	public SettingsDialog() throws IOException {
		getContentPane().setBackground(Color.WHITE);
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(SettingsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(SettingsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(SettingsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(SettingsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		//By: mimarmu1
		this.setIconImage(loadIcon("/resources/icons/logos/TESTAR.jpg"));

		ToolTipManager.sharedInstance().setDismissDelay(25000);
		ToolTipManager.sharedInstance().setInitialDelay(100);
		initComponents();
		
		// begin by urueda
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e)
			{
				saveCurrentSettings();
			}
		});		
		// end by urueda
	}

	public Settings run(Settings settings, String settingsFile){
		this.settings = settings;
		this.settingsFile = settingsFile;
		this.ret = null;
		this.setVisible(true);
		populateInformation(settings);

		while(this.isShowing())
			Util.pause(0.1);

		return ret;
	}

	private Image loadIcon(String path) throws IOException{
		return ImageIO.read(this.getClass().getResourceAsStream(path));
	}

	private void start(AbstractProtocol.Modes mode){
		try{
			extractInformation(settings);
			checkSettings(settings);
			saveCurrentSettings(); // by urueda
			settings.set(ConfigTags.Mode, mode);
			ret = settings;
			this.dispose();
		}catch(IllegalStateException ise){
			JOptionPane.showMessageDialog(this, ise.getMessage(), "Invalid Settings!", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void checkSettings(Settings settings) throws IllegalStateException{
		String userInputPattern = settings.get(ConfigTags.ProcessesToKillDuringTest);
		try {
			Pattern.compile(userInputPattern);
		} catch (PatternSyntaxException exception) {
			throw new IllegalStateException("Your ProcessFilter is not a valid regular expression!");
		}

		userInputPattern = settings.get(ConfigTags.ClickFilter);
		try {
			Pattern.compile(userInputPattern);
		} catch (PatternSyntaxException exception) {
			throw new IllegalStateException("Your ClickFilter is not a valid regular expression!");
		}

		userInputPattern = settings.get(ConfigTags.SuspiciousTitles);
		try {
			Pattern.compile(userInputPattern);
		} catch (PatternSyntaxException exception) {
			throw new IllegalStateException("Your Oracle is not a valid regular expression!");
		}

		if(! new File(settings.get(ConfigTags.OutputDir)).exists())
			throw new IllegalStateException("Output Directory does not exist!");
		if(! new File(settings.get(ConfigTags.TempDir)).exists())
			throw new IllegalStateException("Temp Directory does not exist!");


		for(int i = 0; i < tblCopyFromTo.getRowCount(); i++){
			String left = (String) tblCopyFromTo.getValueAt(i, 0);
			String right = (String) tblCopyFromTo.getValueAt(i, 1);

			if(left != null || right != null){
				if((left != null && right == null) ||
						(left == null && right != null) ||
						left.trim().equals("") ||
						right.trim().equals(""))
					throw new IllegalStateException("CopyFromTo Table has unfinished entries!");
			}
		}		
	}

	// by urueda
	private void saveCurrentSettings(){
		extractInformation(settings);
		try {
			Util.saveToFile(settings.toFileString(), settingsFile);
			System.out.println("Saved current settings to <" + settingsFile + ">");
		} catch (IOException e1) {
			LogSerialiser.log("Unable to save current settings to <" + settingsFile + ">: " + e1.toString() + "\n");
		}		
	}
	
	// by urueda
	private void switchSettings(String sutSettings){
		String previousSSE = Main.getSSE()[0];
		String sse = sutSettings + Main.SUT_SETTINGS_EXT;
		if (previousSSE.equals(sse)) return;
		saveCurrentSettings();
		new File("./settings/" + previousSSE).renameTo(new File("./settings/" + sse));
    	try {
			settingsFile = "./settings/" + sutSettings + "/" + Main.SETTINGS_FILE;
			settings = Main.loadSettings(new String[0], settingsFile);
			populateInformation(settings);
			System.out.println("Switched to <" + settingsFile + ">");
		} catch (ConfigException cfe) {
			LogSerialiser.log("Unable to switch to <" + sutSettings + "> settings!\n");
		}
	}

	public void populateInformation(Settings settings){
		cboxSUTconnector.setSelectedItem(settings.get(ConfigTags.SUTConnector));
		checkStopOnFault.setSelected(settings.get(ConfigTags.StopGenerationOnFault));
		checkFormsFilling.setSelected(settings.get(ConfigTags.AlgorithmFormsFilling));
		checkUseRecordedTimes.setSelected(settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay));
		txtSutPath.setText(settings.get(ConfigTags.SUTConnectorValue));		
		comboBoxProtocol.setSelectedItem(settings.get(ConfigTags.ProtocolClass).split("/")[0]); // by urueda
		spnActionWaitTime.setValue(settings.get(ConfigTags.TimeToWaitAfterAction));
		spnActionDuration.setValue(settings.get(ConfigTags.ActionDuration));
		spnSutStartupTime.setValue(settings.get(ConfigTags.StartupTime));
		txtSuspTitles.setText(settings.get(ConfigTags.SuspiciousTitles));
		txtClickFilter.setText(settings.get(ConfigTags.ClickFilter));
		txtProcessFilter.setText(settings.get(ConfigTags.ProcessesToKillDuringTest));
		comboBoxTestGenerator.setSelectedItem(settings.get(ConfigTags.TestGenerator));
		esiSpinner.setValue(settings.get(ConfigTags.ExplorationSampleInterval));
		gaCheckbox.setSelected(settings.get(ConfigTags.GraphsActivated));
		garesumeCheckBox.setSelected(settings.get(ConfigTags.GraphResuming));
		garesumeCheckBox.setEnabled(gaCheckbox.isSelected());
		offlineGraphConversionCheckBox.setSelected(settings.get(ConfigTags.OfflineGraphConversion));
		f2slCheckBox.setSelected(settings.get(ConfigTags.ForceToSequenceLength));
		paCheckbox.setSelected(settings.get(ConfigTags.PrologActivated));
		spnNumSequences.setValue(settings.get(ConfigTags.Sequences));
		spnSequenceLength.setValue(settings.get(ConfigTags.SequenceLength));
		checkForceForeground.setSelected(settings.get(ConfigTags.ForceForeground));
		comboboxVerbosity.setSelectedIndex(settings.get(ConfigTags.LogLevel));
		spnMaxTime.setValue(settings.get(ConfigTags.MaxTime));
		txtOutputDir.setText(settings.get(ConfigTags.OutputDir));
		txtTempDir.setText(settings.get(ConfigTags.TempDir));
		spnFreezeTime.setValue(settings.get(ConfigTags.TimeToFreeze));
		txtStrategy.setText(settings.get(ConfigTags.Strategy));


		for(int i = 0; i < tblCopyFromTo.getRowCount(); i++){
			tblCopyFromTo.setValueAt(null, i, 0);
			tblCopyFromTo.setValueAt(null, i, 1);
		}

		int i = 0;
		for(Pair<String, String> fromTo : settings.get(ConfigTags.CopyFromTo)){
			tblCopyFromTo.setValueAt(fromTo.left(), i, 0);
			tblCopyFromTo.setValueAt(fromTo.right(), i, 1);
			i++;
		}

		for(i = 0; i < tblDelete.getRowCount(); i++)
			tblDelete.setValueAt(null, i, 0);

		i = 0;
		for(String f : settings.get(ConfigTags.Delete)){
			tblDelete.setValueAt(f, i, 0);
			i++;
		}
	}

	public void extractInformation(Settings settings){
		settings.set(ConfigTags.SUTConnector, (String)cboxSUTconnector.getSelectedItem());
		settings.set(ConfigTags.StopGenerationOnFault, checkStopOnFault.isSelected());
		settings.set(ConfigTags.AlgorithmFormsFilling, checkFormsFilling.isSelected());
		settings.set(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay, checkUseRecordedTimes.isSelected());
		settings.set(ConfigTags.SUTConnectorValue, txtSutPath.getText());
		settings.set(ConfigTags.ActionDuration, (Double)spnActionDuration.getValue());
		settings.set(ConfigTags.TimeToWaitAfterAction, (Double)spnActionWaitTime.getValue());
		settings.set(ConfigTags.StartupTime, (Double)spnSutStartupTime.getValue());
		settings.set(ConfigTags.SuspiciousTitles, txtSuspTitles.getText());
		settings.set(ConfigTags.ClickFilter, txtClickFilter.getText());
		settings.set(ConfigTags.ProcessesToKillDuringTest, txtProcessFilter.getText());
		settings.set(ConfigTags.TestGenerator, (String)comboBoxTestGenerator.getSelectedItem());
		settings.set(ConfigTags.ExplorationSampleInterval, (Integer)esiSpinner.getValue());
		settings.set(ConfigTags.GraphsActivated, gaCheckbox.isSelected());
		settings.set(ConfigTags.GraphResuming, garesumeCheckBox.isSelected());
		settings.set(ConfigTags.ForceToSequenceLength, f2slCheckBox.isSelected());
		settings.set(ConfigTags.PrologActivated, paCheckbox.isSelected());
		settings.set(ConfigTags.OfflineGraphConversion, offlineGraphConversionCheckBox.isSelected());
		settings.set(ConfigTags.Sequences, (Integer)spnNumSequences.getValue()); 
		settings.set(ConfigTags.LogLevel, comboboxVerbosity.getSelectedIndex()); 
		settings.set(ConfigTags.SequenceLength, (Integer)spnSequenceLength.getValue());
		settings.set(ConfigTags.ForceForeground, checkForceForeground.isSelected());
		settings.set(ConfigTags.MaxTime, (Double) spnMaxTime.getValue());
		settings.set(ConfigTags.OutputDir, txtOutputDir.getText()); 
		settings.set(ConfigTags.TempDir, txtTempDir.getText()); 
		settings.set(ConfigTags.TimeToFreeze, (Double)spnFreezeTime.getValue());
		settings.set(ConfigTags.Strategy, txtStrategy.getText());


		List<Pair<String, String>> copyFromTo = Util.newArrayList();
		for(int i = 0; i < tblCopyFromTo.getRowCount(); i++){
			String left = (String) tblCopyFromTo.getValueAt(i, 0);
			String right = (String) tblCopyFromTo.getValueAt(i, 1);

			if(left != null && right != null)
				copyFromTo.add(Pair.from(left, right)); 
		}
		settings.set(ConfigTags.CopyFromTo, copyFromTo);

		List<String> delete = Util.newArrayList();
		for(int i = 0; i < tblDelete.getRowCount(); i++){
			String value = (String) tblDelete.getValueAt(i, 0);			
			if(value != null)
				delete.add(value); 
		}
		settings.set(ConfigTags.Delete, delete);
	}


	private void initComponents() throws IOException {

		jButton1 = new javax.swing.JButton();
		btnGenerate = new javax.swing.JButton();
		btnSpy = new javax.swing.JButton();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanelOracles = new javax.swing.JPanel();
		//jPanel4.setBackground(Color.WHITE);
		jLabel10 = new javax.swing.JLabel();
		jLabel13 = new javax.swing.JLabel();
		spnFreezeTime = new javax.swing.JSpinner();
		jLabel14 = new javax.swing.JLabel();
		jPanelTimings = new javax.swing.JPanel();
		//jPanel2.setBackground(Color.WHITE);
		spnActionDuration = new javax.swing.JSpinner();
		spnActionDuration.setBounds(143, 14, 100, 27);
		jLabel2 = new javax.swing.JLabel();
		jLabel2.setBounds(10, 14, 130, 14);
		jLabel3 = new javax.swing.JLabel();
		jLabel3.setBounds(256, 17, 52, 14);
		jLabel4 = new javax.swing.JLabel();
		jLabel4.setBounds(10, 52, 130, 14);
		spnActionWaitTime = new javax.swing.JSpinner();
		spnActionWaitTime.setBounds(143, 52, 100, 27);
		jLabel5 = new javax.swing.JLabel();
		jLabel5.setBounds(256, 55, 52, 14);
		jLabel6 = new javax.swing.JLabel();
		jLabel6.setBounds(10, 90, 130, 14);
		spnSutStartupTime = new javax.swing.JSpinner();
		spnSutStartupTime.setBounds(143, 90, 100, 27);
		jLabel7 = new javax.swing.JLabel();
		jLabel7.setBounds(256, 93, 52, 14);
		jLabel22 = new javax.swing.JLabel();
		jLabel22.setBounds(10, 128, 130, 14);
		spnMaxTime = new javax.swing.JSpinner();
		spnMaxTime.setBounds(143, 128, 100, 31);
		jLabel23 = new javax.swing.JLabel();
		jLabel23.setBounds(256, 131, 52, 14);
		jLabel24 = new javax.swing.JLabel();
		jLabel24.setBounds(10, 177, 255, 14);
		checkUseRecordedTimes = new javax.swing.JCheckBox();
		checkUseRecordedTimes.setBounds(271, 177, 21, 21);
		jPanelMisc = new javax.swing.JPanel();
		//jPanel3.setBackground(Color.WHITE);
		jLabel8 = new javax.swing.JLabel();
		txtOutputDir = new javax.swing.JTextField();
		txtOutputDir.setEditable(false);
		btnSetOutputDir = new javax.swing.JButton();
		btnSetOutputDir.setEnabled(false);
		jLabel9 = new javax.swing.JLabel();
		txtTempDir = new javax.swing.JTextField();
		txtTempDir.setEditable(false);
		btnSetTempDir = new javax.swing.JButton();
		btnSetTempDir.setEnabled(false);
		jLabel16 = new javax.swing.JLabel();
		jScrollPane5 = new javax.swing.JScrollPane();
		tblCopyFromTo = new javax.swing.JTable();
		jLabel20 = new javax.swing.JLabel();
		jScrollPane4 = new javax.swing.JScrollPane();
		tblDelete = new javax.swing.JTable();
		btnReplay = new javax.swing.JButton();
		btnView = new javax.swing.JButton();
		jLabelm01 = new javax.swing.JLabel();
		jLabelm01.setBounds(10, 177, 255, 14);
		

		jButton1.setText("jButton1");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("TESTAR " + TESTAR_VERSION); //setTitle("TESTAR Settings");
		setLocationByPlatform(true);
		setName("TESTAR Settings"); // NOI18N
		setResizable(false);

		btnGenerate.setBackground(new java.awt.Color(255, 255, 255));
		btnGenerate.setIcon(new ImageIcon(loadIcon("/resources/icons/engine.jpg")));
		btnGenerate.setToolTipText("<html>\nStart in Generation-Mode:<br>\nThis mode will start the SUT and execute a full test.\n</html>");
		btnGenerate.setFocusPainted(false);
		btnGenerate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnGenerateActionPerformed(evt);
			}
		});

		btnSpy.setBackground(new java.awt.Color(255, 255, 255));
		btnSpy.setIcon(new ImageIcon(loadIcon("/resources/icons/magnifier.png")));
		btnSpy.setToolTipText("<html>\nStart in Spy-Mode: <br>\nThis mode does allows you to inspect the GUI of the System under Test. <br>\nSimply use the mouse cursor to point on a widget and TESTAR<br>\nwill display everything it knows about it. The Spy-Mode will also visualize<br>\nthe set of actions that TESTAR recognizes, so that you can see<br>\nwhich ones will be executed during a test.\n</html>");
		btnSpy.setFocusPainted(false);
		btnSpy.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSpyActionPerformed(evt);
			}
		});

		jTabbedPane1.setName("");
		aboutPanel = new javax.swing.JPanel();
		aboutPanel.setBackground(Color.WHITE);
		jLabel26 = new javax.swing.JLabel();
		jLabel27 = new javax.swing.JLabel();
		jLabel28 = new javax.swing.JLabel();



		// jLabel26.setIcon(new ImageIcon(loadIcon("/resources/icons/logos/TESTAR.jpg")));  //("/resources/icons/fittest_logo.png")));
		jLabel26.setIcon(new ImageIcon(loadIcon("/resources/icons/logos/testar_logo.png")));
		jLabel27.setIcon(new ImageIcon(loadIcon("/resources/icons/logos/pros.png"))); //("/resources/icons/fp7_logo.png")));

		jLabel28.setFont(new java.awt.Font("Arial", Font.BOLD, 14)); // NOI18N
		jLabel28.setText("TESTAR " + TESTAR_VERSION);

		JLabel lblUPVLogo = new JLabel();
		lblUPVLogo.setIcon(new ImageIcon(loadIcon("/resources/icons/logos/upv.png"))); //("/resources/icons/fp7_logo.png")));


		javax.swing.GroupLayout aboutPanelLayout = new javax.swing.GroupLayout(aboutPanel);
		aboutPanelLayout.setHorizontalGroup(
				aboutPanelLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(aboutPanelLayout.createSequentialGroup()
						.addGroup(aboutPanelLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(aboutPanelLayout.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel27, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
										.addGap(40)
										.addComponent(jLabel28, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
										.addComponent(lblUPVLogo, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE))
										.addGroup(aboutPanelLayout.createSequentialGroup()
												.addGap(144)
												.addComponent(jLabel26, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE)))
												.addContainerGap())
				);
		aboutPanelLayout.setVerticalGroup(
				aboutPanelLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(aboutPanelLayout.createSequentialGroup()
						.addGap(87)
						.addComponent(jLabel26, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
						.addGroup(aboutPanelLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblUPVLogo, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel27, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel28, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)))
				);
		aboutPanel.setLayout(aboutPanelLayout);


		jTabbedPane1.addTab("About", aboutPanel);
		jPanelGeneral = new javax.swing.JPanel();
		btnSutPath = new javax.swing.JButton();
		btnSutPath.setBounds(456, 11, 24, 20);
		jLabel15 = new javax.swing.JLabel();
		jLabel15.setBounds(10, 164, 135, 14);
		spnNumSequences = new javax.swing.JSpinner();
		spnNumSequences.setBounds(160, 161, 81, 30);
		jLabel17 = new javax.swing.JLabel();
		jLabel17.setBounds(10, 202, 148, 14);
		spnSequenceLength = new javax.swing.JSpinner();
		spnSequenceLength.setBounds(160, 199, 81, 31);
		checkForceForeground = new javax.swing.JCheckBox();
		checkForceForeground.setText("Force SUT to Foreground");
		checkForceForeground.setBounds(10, 177, 171, 21);
		//btnLoadSettings = new javax.swing.JButton();
		//btnLoadSettings.setBounds(400, 292, 35, 35);
		//btnSaveSettingsAs = new javax.swing.JButton();
		//btnSaveSettingsAs.setBounds(445, 292, 35, 35);
		checkStopOnFault = new javax.swing.JCheckBox();
		checkStopOnFault.setText("Stop Test on Fault");
		checkStopOnFault.setBounds(10, 258, 192, 21);
		jLabel25 = new javax.swing.JLabel();
		jLabel25.setBounds(286, 197, 120, 14);

		comboboxVerbosity = new JComboBox<String>();
		comboboxVerbosity.setModel(new DefaultComboBoxModel<String>(new String[]{"Critical","Information","Debug"}));
		comboboxVerbosity.setSelectedIndex(1);
		comboboxVerbosity.setBounds(373, 222, 107, 20);
		comboboxVerbosity.setMaximumRowCount(3);
		
		btnEditProtocol = new javax.swing.JButton("Edit Protocol");
		btnEditProtocol.setBounds(286, 298, 194, 35);

		btnEditProtocol.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnEditProtocolActionPerformed(evt);
			}
		});

		btnEditProtocol.setToolTipText("Edit the protocol");
		btnEditProtocol.setMaximumSize(new java.awt.Dimension(160, 35));
		btnEditProtocol.setMinimumSize(new java.awt.Dimension(160, 35));
		btnEditProtocol.setPreferredSize(new java.awt.Dimension(160, 35));

		btnSutPath.setText("...");
		btnSutPath.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSutPathActionPerformed(evt);
			}
		});

		jLabel15.setText("Number of Sequences:");
		jLabel15.setToolTipText("<html>\nNumber of sequences to generate.\n</html>");

		spnNumSequences.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spnNumSequences.setToolTipText("<html> Number of sequences to generate. </html>");

		jLabel17.setText("Sequence actions:");
		jLabel17.setToolTipText("<html>\nSequence length: After having executed the given amount of<br>\nactions, TESTAR will stop the SUT and proceed with the next<br>\nsequence.\n</html>");

		spnSequenceLength.setModel(new SpinnerNumberModel(new Integer(999), new Integer(1), null, new Integer(1)));
		spnSequenceLength.setToolTipText("<html> Sequence length: After having executed the given amount of<br> actions, TESTAR will stop the SUT and proceed with the next<br> sequence. </html>");

		checkForceForeground.setToolTipText("<html> Force the SUT to the foreground: During test generation, windows might get minimized or other<br>  processes might block the SUT's GUI. If you check this option, TESTAR will force the SUT to the<br> foreground. </html>");

		//btnLoadSettings.setIcon(new ImageIcon(loadIcon("/resources/icons/open.jpg")));
		//btnLoadSettings.setToolTipText("Load a settings file");
		//btnLoadSettings.setMaximumSize(new java.awt.Dimension(35, 35));
		//btnLoadSettings.setMinimumSize(new java.awt.Dimension(35, 35));
		//btnLoadSettings.setPreferredSize(new java.awt.Dimension(35, 35));
		//btnLoadSettings.addActionListener(new java.awt.event.ActionListener() {
		//	public void actionPerformed(java.awt.event.ActionEvent evt) {
		//		btnLoadSettingsActionPerformed(evt);
		//	}
		//});

		/*btnSaveSettingsAs.setIcon(new ImageIcon(loadIcon("/resources/icons/save.jpg")));
		btnSaveSettingsAs.setToolTipText("Save current settings to file");
		btnSaveSettingsAs.setMaximumSize(new java.awt.Dimension(35, 35));
		btnSaveSettingsAs.setMinimumSize(new java.awt.Dimension(35, 35));
		btnSaveSettingsAs.setPreferredSize(new java.awt.Dimension(35, 35));
		btnSaveSettingsAs.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSaveSettingsAsActionPerformed(evt);
			}
		})*/;

		checkStopOnFault.setToolTipText("<html> Stop sequence generation on fault: If TESTAR detects and error, it will immediately stop sequence generation. </html>");

		jLabel25.setText("Logging Verbosity:");
		jLabel25.setToolTipText("<html>\nLogging verbosity: The higher the value, the more information<br>\nwill be written to TESTAR's log-file.\n</html>");

		comboboxVerbosity.setToolTipText("<html> Logging verbosity: Sets how much information will be written to TESTAR's log-file. </html>");

		jTabbedPane1.addTab("General Settings", jPanelGeneral);
		jPanelGeneral.setLayout(null);
		jPanelGeneral.add(btnSutPath);
		jPanelGeneral.add(jLabel25);
		jPanelGeneral.add(comboboxVerbosity);
		jPanelGeneral.add(btnEditProtocol);
		//jPanelGeneral.add(btnLoadSettings);
		//jPanelGeneral.add(btnSaveSettingsAs);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 42, 470, 108);
		jPanelGeneral.add(scrollPane);
		//jPanel1.setBackground(Color.WHITE);
		txtSutPath = new JTextArea();
		txtSutPath.setLineWrap(true);
		scrollPane.setViewportView(txtSutPath);

		txtSutPath.setToolTipText("<html> Path to the SUT: Pick the executable of the SUT or insert a custom command line. </html>");
		jPanelGeneral.add(jLabel15);
		jPanelGeneral.add(spnNumSequences);
		jPanelGeneral.add(jLabel17);
		jPanelGeneral.add(spnSequenceLength);
		jPanelGeneral.add(checkStopOnFault);

		esiSpinner = new JSpinner();
		esiSpinner.setBounds(417, 161, 63, 30);
		esiSpinner.setValue(10);
		jPanelGeneral.add(esiSpinner);

		paCheckbox = new JCheckBox("Prolog activated");
		paCheckbox.setBounds(10, 282, 192, 21);
		jPanelGeneral.add(paCheckbox);

		JLabel lblSamplingInterval = new JLabel("Sampling interval:");
		lblSamplingInterval.setBounds(286, 161, 121, 17);
		jPanelGeneral.add(lblSamplingInterval);
				
		cboxSUTconnector = new JComboBox<String>();
		cboxSUTconnector.setModel(new DefaultComboBoxModel<String>(new String[] {
			Settings.SUT_CONNECTOR_CMDLINE,
			Settings.SUT_CONNECTOR_PROCESS_NAME,
			Settings.SUT_CONNECTOR_WINDOW_TITLE
		}));
		cboxSUTconnector.setSelectedIndex(0);
		cboxSUTconnector.setBounds(114, 12, 171, 18);
		cboxSUTconnector.setToolTipText("How does TESTAR engage with the SUT");
		cboxSUTconnector.setMaximumRowCount(3);		
		jPanelGeneral.add(cboxSUTconnector);
		
		JLabel lblSUTconnector = new JLabel("SUT connector:");
		lblSUTconnector.setBounds(10, 11, 97, 20);
		jPanelGeneral.add(lblSUTconnector);
		
		f2slCheckBox = new JCheckBox("Force to sequence length");
		f2slCheckBox.setBounds(10, 235, 192, 20);
		jPanelGeneral.add(f2slCheckBox);
		
		offlineGraphConversionCheckBox = new JCheckBox("Offline graph conversion");
		offlineGraphConversionCheckBox.setBounds(10, 304, 192, 23);
		jPanelGeneral.add(offlineGraphConversionCheckBox);

		jPanelWalker = new JPanel();
		jTabbedPane1.addTab("UI-walker", null, jPanelWalker, null);
		jPanelWalker.setLayout(null);

		lblActionSelection = new JLabel("UI Actions selection:");
		lblActionSelection.setBounds(10, 27, 125, 14);
		jPanelWalker.add(lblActionSelection);
		
		gaCheckbox = new JCheckBox("Graphs activated");
		gaCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				garesumeCheckBox.setEnabled(gaCheckbox.isSelected());
			}
		});
		gaCheckbox.setBounds(10, 227, 171, 21);
		jPanelWalker.add(gaCheckbox);		
		
		garesumeCheckBox = new JCheckBox("Graphs resuming");
		garesumeCheckBox.setBounds(10, 251, 171, 23);
		jPanelWalker.add(garesumeCheckBox);
		
		jPanelWalker.add(checkForceForeground);

		comboBoxTestGenerator = new JComboBox<String>();
		String[] algorithms = Grapher.getRegisteredAlgorithms();
		comboBoxTestGenerator.setModel(new DefaultComboBoxModel<String>(algorithms));
		comboBoxTestGenerator.setSelectedIndex(0);
		comboBoxTestGenerator.setBounds(145, 23, 173, 23);
		comboBoxTestGenerator.setToolTipText("Determines how the UI actions are selected during tests");
		comboBoxTestGenerator.setMaximumRowCount(algorithms.length);
		jPanelWalker.add(comboBoxTestGenerator);
		
		jLabelm01.setText("In case of tree-based strategy, enter the action selection strategy below:");
		jLabelm01.setToolTipText("<html>\nPlease use colons to separate commands.\n</html>");
		jLabelm01.setBounds(10, 50, 450, 23);
		jPanelWalker.add(jLabelm01);

		txtStrategy = new JTextArea();
		txtStrategy.setLineWrap(true);
		txtStrategy.setBounds(10, 70, 480, 100);
		jPanelWalker.add(txtStrategy);
		
		checkFormsFilling = new JCheckBox("Forms filling");
		checkFormsFilling.setBounds(10, 201, 171, 23);
		jPanelWalker.add(checkFormsFilling);
		jPanelFilters = new javax.swing.JPanel();
		//jPanel5.setBackground(Color.WHITE);
		jLabel11 = new javax.swing.JLabel();
		jLabel12 = new javax.swing.JLabel();

		//jLabel11.setText("Click Filter:");
		jLabel11.setText("Disabled actions by widgets' TITLE property (regular expression):"); // by urueda
		jLabel11.setToolTipText("<html>\nClick-filter: Certain actions that TESTARs wants to execute might be dangerous or<br>\nundesirable, such as printing out documents, creating, moving or deleting files.<br>\nTESTAR will not execute clicks on any widget whose title matches the given regular expression.<br>\nTo see whether or not your expression works, simply start TESTAR in Spy-Mode, which will<br>\nvisualize the detected actions.\n</html>");

		//jLabel12.setText("Process Filter:");
		jLabel12.setText("Kill processes by name (regular expression):"); // by urueda
		jLabel12.setToolTipText("<html>\nProcesses to kill: Some SUTs start other processes during test sequence generation. These might<br>\npopup in the foreground and block the SUTs GUI. They might also consume excessive memory, etc.<br>\nTESTAR will kill any process whose name matches the given regular expression.\n</html>");

		scrollPane_2 = new JScrollPane();

		scrollPane_3 = new JScrollPane();
		
		javax.swing.GroupLayout gl_jPanelFilters = new javax.swing.GroupLayout(jPanelFilters);
		gl_jPanelFilters.setHorizontalGroup(
				gl_jPanelFilters.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_jPanelFilters.createSequentialGroup()
						.addGroup(gl_jPanelFilters.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_jPanelFilters.createSequentialGroup()
										.addContainerGap()
										.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, 445, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_jPanelFilters.createSequentialGroup()
												.addGap(24)
												.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 447, GroupLayout.PREFERRED_SIZE))
												.addGroup(gl_jPanelFilters.createSequentialGroup()
														.addContainerGap()
														.addGroup(gl_jPanelFilters.createParallelGroup(Alignment.LEADING)
																.addComponent(jLabel12, GroupLayout.PREFERRED_SIZE, 357, GroupLayout.PREFERRED_SIZE)
																.addComponent(jLabel11, GroupLayout.PREFERRED_SIZE, 381, GroupLayout.PREFERRED_SIZE))))
																.addContainerGap(29, Short.MAX_VALUE))
				);
		gl_jPanelFilters.setVerticalGroup(
				gl_jPanelFilters.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_jPanelFilters.createSequentialGroup()
						.addContainerGap()
						.addComponent(jLabel11)
						.addGap(18)
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
						.addGap(18)
						.addComponent(jLabel12)
						.addGap(18)
						.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addGap(22))
				);

		txtProcessFilter = new JTextArea();
		txtProcessFilter.setLineWrap(true);
		scrollPane_3.setViewportView(txtProcessFilter);

		txtClickFilter = new JTextArea();
		txtClickFilter.setLineWrap(true);
		scrollPane_2.setViewportView(txtClickFilter);
		jPanelFilters.setLayout(gl_jPanelFilters);

		jTabbedPane1.addTab("Filters", jPanelFilters);

		jLabel10.setText("Suspicious Titles:");
		jLabel10.setToolTipText("<html>\nThis is a very simple oracle in the form of a regular expression, which is applied to each<br>widget's Title property. If TESTAR finds a widget on the screen, whose title matches the given<br>\nexpression, it will consider the current sequence to be erroneous.\n</html>");

		jLabel13.setText("Freeze Time:");

		spnFreezeTime.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(1.0d), Double.valueOf(1.0d), null, Double.valueOf(1.0d)));

		jLabel14.setText("seconds");

		JScrollPane scrollPane_1 = new JScrollPane();

		javax.swing.GroupLayout gl_jPanelOracles = new javax.swing.GroupLayout(jPanelOracles);
		gl_jPanelOracles.setHorizontalGroup(
				gl_jPanelOracles.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_jPanelOracles.createSequentialGroup()
						.addGap(10)
						.addGroup(gl_jPanelOracles.createParallelGroup(Alignment.LEADING, false)
								.addGroup(gl_jPanelOracles.createSequentialGroup()
										.addGroup(gl_jPanelOracles.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_jPanelOracles.createSequentialGroup()
														.addGap(98)
														.addComponent(jLabel13, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(spnFreezeTime, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
														.addGap(18)
														.addComponent(jLabel14)
														.addPreferredGap(ComponentPlacement.RELATED, 121, Short.MAX_VALUE))
														.addGroup(Alignment.TRAILING, gl_jPanelOracles.createSequentialGroup()
																.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 461, GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(ComponentPlacement.RELATED)))
																.addGap(23))
																.addGroup(gl_jPanelOracles.createSequentialGroup()
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(jLabel10, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
																		.addContainerGap(348, Short.MAX_VALUE))))
				);
		gl_jPanelOracles.setVerticalGroup(
				gl_jPanelOracles.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_jPanelOracles.createSequentialGroup()
						.addGap(18)
						.addComponent(jLabel10)
						.addGap(226)
						.addGroup(gl_jPanelOracles.createParallelGroup(Alignment.BASELINE)
								.addComponent(spnFreezeTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel14)
								.addComponent(jLabel13))
								.addContainerGap(120, Short.MAX_VALUE))
								.addGroup(gl_jPanelOracles.createSequentialGroup()
										.addGap(50)
										.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE)
										.addContainerGap(156, Short.MAX_VALUE))
				);

		txtSuspTitles = new JTextArea();
		scrollPane_1.setViewportView(txtSuspTitles);
		txtSuspTitles.setLineWrap(true);
		jPanelOracles.setLayout(gl_jPanelOracles);

		jTabbedPane1.addTab("Oracles", jPanelOracles);

		spnActionDuration.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(0.1d)));
		spnActionDuration.setToolTipText("<html> Action Duration: The higher this value, the longer the execution of actions will take.<br> Mouse movements and typing become slower, so that it is easier to follow what the<br> TESTAR is doing. This can be useful during Replay-Mode, in order to replay<br> a recorded sequence with less speed to better understand a fault. </html>");

		jLabel2.setText("Action Duration:");
		jLabel2.setToolTipText("<html>\nAction Duration: The higher this value, the longer the execution of actions will take.<br>\nMouse movements and typing become slower, so that it is easier to follow what the<br>\nTESTAR is doing. This can be useful during Replay-Mode, in order to replay<br>\na recorded sequence with less speed to better understand a fault.\n</html>");

		jLabel3.setText("seconds");
		jLabel3.setToolTipText("<html> Action Duration: The higher this value, the longer the execution of actions will take.<br> Mouse movements and typing become slower, so that it is easier to follow what the<br> TESTAR is doing. This can be useful during Replay-Mode, in order to replay<br> a recorded sequence with less speed to better understand a fault. </html>");

		jLabel4.setText("Action Wait Time:");
		jLabel4.setToolTipText("<html>\nTime to wait after execution of an action: This is the time that TESTAR<br>\npauses after having executed an action in Generation-Mode. Usually, this value<br>\nis set to 0. However, sometimes it can make sense to give the GUI of the SUT<br>\nmore time to react, before executing the next action. If this value is set to a<br>\nvalue > 0, it can greatly enhance reproducibility of sequences at the expense<br>\nof longer testing times.\n</html>");

		spnActionWaitTime.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(0.1d)));
		spnActionWaitTime.setToolTipText("<html> Time to wait after execution of an action: This is the time that TESTAR<br> pauses after having executed an action in Generation-Mode. Usually, this value<br> is set to 0. However, sometimes it can make sense to give the GUI of the SUT<br> more time to react, before executing the next action. If this value is set to a<br> value > 0, it can greatly enhance reproducibility of sequences at the expense<br> of longer testing times. </html>");

		jLabel5.setText("seconds");
		jLabel5.setToolTipText("<html> Time to wait after execution of an action: This is the time that TESTAR<br> pauses after having executed an action in Generation-Mode. Usually, this value<br> is set to 0. However, sometimes it can make sense to give the GUI of the SUT<br> more time to react, before executing the next action. If this value is set to a<br> value > 0, it can greatly enhance reproducibility of sequences at the expense<br> of longer testing times. </html>");

		jLabel6.setText("SUT Startup Time:");
		final String sutStartupTimeTTT = "<html>\nSUT startup time: This is the threshold time that TESTAR waits for the SUT to load.<br>\nLarge and complex SUTs might need more time than small ones.,<br>\nOnce the SUT UI is ready, TESTAR will start the test sequence.\n</html>";
		jLabel6.setToolTipText(sutStartupTimeTTT);

		spnSutStartupTime.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(1.0d)));
		spnSutStartupTime.setToolTipText(sutStartupTimeTTT);

		jLabel7.setText("seconds");
		jLabel7.setToolTipText(sutStartupTimeTTT);

		jLabel22.setText("Max. Test Time:");
		jLabel22.setToolTipText("<html>\nMaximum test time (seconds): TESTAR will cease to generate any sequences after this time has elapsed.<br>\nThis is useful for specifying a test time out, e.g. one hour, one day, one week.\n</html>");

		spnMaxTime.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(1.0d)));
		spnMaxTime.setToolTipText("<html> Maximum test time (seconds): TESTAR will cease to generate any sequences after this time has elapsed.<br> This is useful for specifying a test time out, e.g. one hour, one day, one week. </html>");

		jLabel23.setText("seconds");
		jLabel23.setToolTipText("<html> Maximum test time (seconds): TESTAR will cease to generate any sequences after this time has elapsed.<br> This is useful for specifying a test time out, e.g. e.g. one hour, one day, one week. </html>");

		jLabel24.setText("Use Recorded Action Timing during Replay:");
		jLabel24.setToolTipText("<html>\nThis option only affects Replay-Mode. If checked, TESTAR will use the action duration and action<br>\nwait time that was used during sequence generation. If you uncheck the option, you can specify<br>\nyour own values.\n</html>");

		checkUseRecordedTimes.setToolTipText("<html> This option only affects Replay-Mode. If checked, TESTAR will use the action duration and action<br> wait time that was used during sequence generation. If you uncheck the option, you can specify<br> your own values. </html>");

		jTabbedPane1.addTab("Time Settings", jPanelTimings);
		jPanelTimings.setLayout(null);
		jPanelTimings.add(jLabel24);
		jPanelTimings.add(checkUseRecordedTimes);
		jPanelTimings.add(jLabel22);
		jPanelTimings.add(spnMaxTime);
		jPanelTimings.add(jLabel6);
		jPanelTimings.add(spnSutStartupTime);
		jPanelTimings.add(jLabel4);
		jPanelTimings.add(spnActionWaitTime);
		jPanelTimings.add(jLabel2);
		jPanelTimings.add(spnActionDuration);
		jPanelTimings.add(jLabel3);
		jPanelTimings.add(jLabel5);
		jPanelTimings.add(jLabel7);
		jPanelTimings.add(jLabel23);

		jLabel8.setText("Output Directory:");

		/*jLabelm01.setText("Use the below action selection strategy:");
		jLabelm01.setToolTipText("<html>\nPlease use colons to separate commands.\n</html>");

		jPanelSelector.setLayout(null);
		jPanelSelector.add(jLabelm01);
		txtStrategy = new JTextArea();
		txtStrategy.setLineWrap(true);
		jPanelSelector.add(txtStrategy);
		/////
		scrollPane_m1 = new JScrollPane();

		javax.swing.GroupLayout gl_jPanelSelector = new javax.swing.GroupLayout(jPanelSelector);
		gl_jPanelSelector.setHorizontalGroup(
				gl_jPanelSelector.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_jPanelSelector.createSequentialGroup()
						.addGroup(gl_jPanelSelector.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_jPanelSelector.createSequentialGroup()
										.addContainerGap()
												.addComponent(scrollPane_m1, GroupLayout.PREFERRED_SIZE, 447, GroupLayout.PREFERRED_SIZE))
												.addGroup(gl_jPanelSelector.createSequentialGroup()
														.addContainerGap()
														.addGroup(gl_jPanelSelector.createParallelGroup(Alignment.LEADING)
																.addComponent(jLabelm01, GroupLayout.PREFERRED_SIZE, 357, GroupLayout.PREFERRED_SIZE))))
																.addContainerGap(29, Short.MAX_VALUE))
				);
		gl_jPanelSelector.setVerticalGroup(
				gl_jPanelSelector.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_jPanelSelector.createSequentialGroup()
						.addContainerGap()
						.addComponent(jLabelm01)
						.addGap(18)
						.addComponent(scrollPane_m1, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
						.addGap(22))
				);

		scrollPane_m1.setViewportView(txtStrategy);

		jPanelSelector.setLayout(gl_jPanelSelector);

		jTabbedPane1.addTab("Selector", jPanelSelector);
		///////*/
		btnSetOutputDir.setText("...");
		btnSetOutputDir.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSetOutputDirActionPerformed(evt);
			}
		});

		jLabel9.setText("Temp Directory:");

		btnSetTempDir.setText("...");
		btnSetTempDir.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSetTempDirActionPerformed(evt);
			}
		});

		jLabel16.setText("Copy Files on SUT Startup:");
		jLabel16.setToolTipText("<html> Files to copy before SUT start. It is useful to restore certain<br>  configuration files to their default. Therefore you can define pairs of paths (copy from / to).<br> TESTAR will copy each specified file from the given source location to the given destination.<br> Simply double-click the text-area and a file dialog will pop up. </html>");

		tblCopyFromTo.setModel(new javax.swing.table.DefaultTableModel(
				new Object [][] {
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null}
				},
				new String [] {
						"Source File / Directory", "Destination"
				}
				) {
			private static final long serialVersionUID = 1L;
			Class<?>[] types = new Class<?> [] {
					java.lang.String.class, java.lang.String.class
			};

			public Class<?> getColumnClass(int columnIndex) {
				return types [columnIndex];
			}
		});
		tblCopyFromTo.setToolTipText("<html>\nFiles to copy before SUT start. It is useful to restore certain<br>\n configuration files to their default. Therefore you can define pairs of paths (copy from / to).<br>\nTESTAR will copy each specified file from the given source location to the given destination.<br>\nSimply double-click the text-area and a file dialog will pop up.\n</html>");
		tblCopyFromTo.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				tblCopyFromToMouseClicked(evt);
			}
		});
		jScrollPane5.setViewportView(tblCopyFromTo);

		jLabel20.setText("Delete Files on SUT Startup:");
		jLabel20.setToolTipText("<html> Files to delete before SUT start: Certain SUTs generate configuration files, temporary files and files<br> that save the system's state. This might be problematic during sequence replay, when you want a<br> system to always start in the same state. Therefore, you can specify these files, to be deleted<br> before the SUT gets started. If you double-click the text-area a file dialog will pop up which allows<br> selecting files and directories to be deleted. </html>");

		tblDelete.setModel(new javax.swing.table.DefaultTableModel(
				new Object [][] {
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null},
						{null}
				},
				new String [] {
						"File / Directory"
				}
				) {
			private static final long serialVersionUID = 1L;
			Class<?>[] types = new Class<?> [] {
					java.lang.String.class
			};

			public Class<?> getColumnClass(int columnIndex) {
				return types [columnIndex];
			}
		});
		tblDelete.setToolTipText("<html>\nFiles to delete before SUT start: Certain SUTs generate configuration files, temporary files and files<br>\nthat save the system's state. This might be problematic during sequence replay, when you want a<br>\nsystem to always start in the same state. Therefore, you can specify these files, to be deleted<br>\nbefore the SUT gets started. If you double-click the text-area a file dialog will pop up which allows<br>\nselecting files and directories to be deleted.\n</html>");
		tblDelete.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				tblDeleteMouseClicked(evt);
			}
		});
		jScrollPane4.setViewportView(tblDelete);

		javax.swing.GroupLayout gl_jPanelMisc = new javax.swing.GroupLayout(jPanelMisc);
		jPanelMisc.setLayout(gl_jPanelMisc);
		gl_jPanelMisc.setHorizontalGroup(
				gl_jPanelMisc.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(gl_jPanelMisc.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_jPanelMisc.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jScrollPane5)
								.addComponent(jScrollPane4)
								.addGroup(gl_jPanelMisc.createSequentialGroup()
										.addGroup(gl_jPanelMisc.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(gl_jPanelMisc.createSequentialGroup()
														.addGroup(gl_jPanelMisc.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
																.addGroup(gl_jPanelMisc.createSequentialGroup()
																		.addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(18, 18, 18)
																		.addComponent(txtOutputDir))
																		.addGroup(gl_jPanelMisc.createSequentialGroup()
																				.addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addGap(18, 18, 18)
																				.addComponent(txtTempDir, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE))
																				.addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE))
																				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addGroup(gl_jPanelMisc.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(btnSetOutputDir, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(btnSetTempDir, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
																						.addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGap(0, 0, Short.MAX_VALUE)))
																						.addContainerGap())
				);
		gl_jPanelMisc.setVerticalGroup(
				gl_jPanelMisc.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(gl_jPanelMisc.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_jPanelMisc.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtOutputDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel8)
								.addComponent(btnSetOutputDir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(gl_jPanelMisc.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(txtTempDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel9)
										.addComponent(btnSetTempDir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jLabel16)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jLabel20)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(22, Short.MAX_VALUE))
				);

		jTabbedPane1.addTab("Misc", jPanelMisc);

		jTabbedPane1.setSelectedComponent(jPanelGeneral); // by urueda
		
		comboBoxProtocol = new JComboBox<String>();
		comboBoxProtocol.setBounds(286, 258, 194, 20);
		String[] sutSettings = new File("./settings/").list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		comboBoxProtocol.setModel(new DefaultComboBoxModel<String>(sutSettings));
		comboBoxProtocol.setMaximumRowCount(sutSettings.length > 16 ? 16 : sutSettings.length);
		comboBoxProtocol.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					switchSettings((String)e.getItem());
			}
		});
		
		cboxSUTconnector.setToolTipText("How does TESTAR engage with the SUT");

		jPanelGeneral.add(comboBoxProtocol);
		
		JLabel lblProtocol = new JLabel("Protocol:");
		lblProtocol.setBounds(229, 258, 64, 20);
		jPanelGeneral.add(lblProtocol);
		
		btnReplay.setBackground(new java.awt.Color(255, 255, 255));
		btnReplay.setIcon(new ImageIcon(loadIcon("/resources/icons/rewind.jpg")));
		btnReplay.setToolTipText("<html>\nStart in Replay-Mode: This mode replays a previously recorded sequence.<br>\nTESTAR will ask you for the sequence to replay.\n</html>");
		btnReplay.setFocusPainted(false);
		btnReplay.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnReplayActionPerformed(evt);
			}
		});

		btnView.setBackground(new java.awt.Color(255, 255, 255));
		btnView.setIcon(new ImageIcon(loadIcon("/resources/icons/view.jpg")));
		btnView.setToolTipText("<html>\nStart in View-Mode:<br>\nThe View-Mode allows you to inspect all steps of a previously recorded<br>\nsequence. Contrary to the Replay-Mode, it will not execute any actions,<br>\nbut only show you the screenshots that were recorded during sequence<br>\ngeneration. This is ideal if a sequence turns out not to be reproducible.\n</html>");
		btnView.setFocusPainted(false);
		btnView.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnViewActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(layout.createSequentialGroup()
										.addComponent(btnSpy, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(2, 2, 2)
										.addComponent(btnGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(2, 2, 2)
										.addComponent(btnReplay, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(btnView, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(0, 0, Short.MAX_VALUE)))
										.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(btnGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnSpy, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnReplay, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnView, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap())
				);

		pack();
		Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();
		//By mimarmu1 
		setBounds((int) scrDim.getWidth() / 2 - getWidth() / 2, (int) scrDim.getHeight() / 2 - getHeight() / 2, getWidth(), getHeight());
	}// </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
	}//GEN-LAST:event_jButton1ActionPerformed

	private void btnGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateActionPerformed
		start(AbstractProtocol.Modes.Generate);
	}//GEN-LAST:event_btnGenerateActionPerformed

	private void btnSetOutputDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetOutputDirActionPerformed
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fd.setCurrentDirectory(new File(settings.get(ConfigTags.OutputDir)).getParentFile());
		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();
			txtOutputDir.setText(file);
		}
	}//GEN-LAST:event_btnSetOutputDirActionPerformed

	private void btnSetTempDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetTempDirActionPerformed
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fd.setCurrentDirectory(new File(settings.get(ConfigTags.TempDir)).getParentFile());

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();
			txtTempDir.setText(file);
		}
	}//GEN-LAST:event_btnSetTempDirActionPerformed

	private void btnSpyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSpyActionPerformed
		start(AbstractProtocol.Modes.Spy);
	}//GEN-LAST:event_btnSpyActionPerformed

	private void btnReplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReplayActionPerformed
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setCurrentDirectory(new File(settings.get(ConfigTags.PathToReplaySequence)).getParentFile());

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();
			settings.set(ConfigTags.PathToReplaySequence, file);
			start(AbstractProtocol.Modes.Replay);
		}
	}//GEN-LAST:event_btnReplayActionPerformed

	private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setCurrentDirectory(new File(settings.get(ConfigTags.PathToReplaySequence)).getParentFile());

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();
			settings.set(ConfigTags.PathToReplaySequence, file);
			start(AbstractProtocol.Modes.View);
		}
	}//GEN-LAST:event_btnViewActionPerformed

	/*private void btnSaveSettingsAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveSettingsAsActionPerformed
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setCurrentDirectory(new File(settingsFile).getParentFile());

		//By: mimarmu1
		fd.setApproveButtonText("Save"); 
		fd.setDialogTitle("Save"); 

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();
			extractInformation(settings);
			try {
				Util.saveToFile(settings.toFileString(), file);
				settingsFile = file;
			} catch (IOException e1) {
				LogSerialiser.log("Unable to save file: " + e1.toString() + "\n");
			}

		}
	}//GEN-LAST:event_btnSaveSettingsAsActionPerformed*/

	private void btnEditProtocolActionPerformed(java.awt.event.ActionEvent evt) {
		//JDialog dialog = new ProtocolEditor();
		JDialog dialog = new ProtocolEditor(settings.get(ConfigTags.ProtocolClass)); // by urueda        		
		dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
		//dialog.setModalExclusionType(JDialog.ModalExclusionType.NO_EXCLUDE);
		dialog.setVisible(true);
	}

	/*private void btnLoadSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadSettingsActionPerformed
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setCurrentDirectory(new File(settingsFile).getParentFile());
		//By: mimarmu1
		fd.setDialogTitle("Load"); 
		fd.setApproveButtonText("Load"); 

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();
			try {
				settings = Main.loadSettings(new String[0], file);
				settingsFile = file;
				populateInformation(settings);
			} catch (ConfigException cfe) {
				LogSerialiser.log("Unable to load settings file!\n");
			}
		}
	}//GEN-LAST:event_btnLoadSettingsActionPerformed*/

	private void btnSutPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSutPathActionPerformed
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setCurrentDirectory(new File(settings.get(ConfigTags.SUTConnectorValue)).getParentFile());

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();
			txtSutPath.setText(file);
		}
	}//GEN-LAST:event_btnSutPathActionPerformed

	private void tblCopyFromToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCopyFromToMouseClicked
		if (tblCopyFromTo.getSelectedColumn() >= 0 && tblCopyFromTo.getSelectedRow() >= 0) {
			JFileChooser fd = new JFileChooser();
			fd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				String file = fd.getSelectedFile().getAbsolutePath();
				tblCopyFromTo.setValueAt(file, tblCopyFromTo.getSelectedRow(), tblCopyFromTo.getSelectedColumn());
			} else {
				tblCopyFromTo.setValueAt(null, tblCopyFromTo.getSelectedRow(), tblCopyFromTo.getSelectedColumn());
			}
		}
	}//GEN-LAST:event_tblCopyFromToMouseClicked

	private void tblDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDeleteMouseClicked
		if (tblDelete.getSelectedRow() >= 0 && tblDelete.getSelectedColumn() >= 0) {
			JFileChooser fd = new JFileChooser();
			fd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				String file = fd.getSelectedFile().getAbsolutePath();
				tblDelete.setValueAt(file, tblDelete.getSelectedRow(), tblDelete.getSelectedColumn());
			} else {
				tblDelete.setValueAt(null, tblDelete.getSelectedRow(), tblDelete.getSelectedColumn());
			}
		}
	}//GEN-LAST:event_tblDeleteMouseClicked

	//By mimarmu1
	//Kill browser's process before starting the SUT
	/*private void killProcess(String name){
		Process child;

		try{
			child = Runtime.getRuntime().exec("tskill " + name);
			child.waitFor();
			if(child.exitValue()==0){
				System.out.println("SettingsDialoge - " + name + " killed");
			}else{
				System.out.println("SettingsDialoge - Cannot kill " + name);
			}
		}catch (IOException ex){
			System.out.println("SettingsDialoge - Cannot kill " + name);
		}catch (InterruptedException ie){
			System.out.println("SettingsDialoge - Cannot kill " + name);
		}
	}*/

	private javax.swing.JButton btnGenerate;
	private javax.swing.JButton btnEditProtocol;
	//private javax.swing.JButton btnLoadSettings;
	private javax.swing.JButton btnReplay;
	//private javax.swing.JButton btnSaveSettingsAs;
	private javax.swing.JButton btnSetOutputDir;
	private javax.swing.JButton btnSetTempDir;
	private javax.swing.JButton btnSpy;
	private javax.swing.JButton btnSutPath;
	private javax.swing.JButton btnView;
	private javax.swing.JCheckBox checkForceForeground;
	private javax.swing.JCheckBox checkStopOnFault;
	private javax.swing.JCheckBox checkUseRecordedTimes;
	private javax.swing.JButton jButton1;
	private javax.swing.JPanel aboutPanel;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel13;
	private javax.swing.JLabel jLabel14;
	private javax.swing.JLabel jLabel15;
	private javax.swing.JLabel jLabel16;
	private javax.swing.JLabel jLabel17;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel20;
	private javax.swing.JLabel jLabel22;
	private javax.swing.JLabel jLabel26;
	private javax.swing.JLabel jLabel27;
	private javax.swing.JLabel jLabel28;
	private javax.swing.JLabel jLabel23;
	private javax.swing.JLabel jLabel24;
	private javax.swing.JLabel jLabel25;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JPanel jPanelGeneral;
	private javax.swing.JPanel jPanelTimings;
	private javax.swing.JPanel jPanelMisc;
	private javax.swing.JPanel jPanelOracles;	
	private javax.swing.JPanel jPanelFilters;
	private javax.swing.JScrollPane jScrollPane4;
	private javax.swing.JScrollPane jScrollPane5;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JSpinner spnActionDuration;
	private javax.swing.JSpinner spnActionWaitTime;
	private javax.swing.JSpinner spnFreezeTime;
	private javax.swing.JSpinner spnMaxTime;
	private javax.swing.JSpinner spnNumSequences;
	private javax.swing.JSpinner spnSequenceLength;
	private javax.swing.JSpinner spnSutStartupTime;
	private JComboBox<String> comboboxVerbosity;
	private javax.swing.JTable tblCopyFromTo;
	private javax.swing.JTable tblDelete;
	private javax.swing.JTextField txtOutputDir;
	private JTextArea txtSutPath;
	private javax.swing.JTextField txtTempDir;
	private JPanel jPanelWalker;
	private JLabel lblActionSelection;
	private JComboBox<String> comboBoxTestGenerator;
	private JScrollPane scrollPane;
	private JTextArea txtSuspTitles;
	private JScrollPane scrollPane_2;
	private JTextArea txtClickFilter;
	private JScrollPane scrollPane_3;
	private JTextArea txtProcessFilter;
	private JCheckBox checkFormsFilling;
	private JSpinner esiSpinner;
	private JCheckBox gaCheckbox, paCheckbox;
	private JCheckBox garesumeCheckBox;
	private JCheckBox f2slCheckBox;
	private JCheckBox offlineGraphConversionCheckBox;
	private JComboBox<String> cboxSUTconnector;
	private JComboBox<String> comboBoxProtocol;
	private javax.swing.JLabel jLabelm01;
	private JTextArea txtStrategy;
	
}
