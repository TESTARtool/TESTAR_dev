package nl.ou.testar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import nl.ou.testar.tgherkin.TgherkinEditor;
import nl.ou.testar.tgherkin.protocol.DocumentProtocol;

import javax.swing.JLabel;

import static org.fruit.monkey.ConfigTags.MyClassPath;

import org.fruit.UnProc;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;


/**
 * Maintenance Tgherkin settings.
 *
 */
public class TgherkinPanel extends JPanel {
	/**
	 * Serial version UID. 
	 */
	private static final long serialVersionUID = 9033457654990065279L;
	private JComboBox<String> tgComboBoxTgherkinDocument;
	private JCheckBox tgApplyDefaultOnMismatchCheckBox;
	private JCheckBox tgContinueToApplyDefaultCheckBox;
	private JCheckBox tgRepeatTgherkinScenariosCheckBox;
	private JCheckBox tgGenerateTgherkinReportCheckBox;
	private JCheckBox tgTgherkinReportIncludeOCRCheckBox;
	private JCheckBox tgTgherkinReportIncludeImageRecognitionCheckBox;
	private JCheckBox tgStoreTgherkinReportCheckBox;
	private JCheckBox tgReportDerivedGesturesCheckBox;
	private JCheckBox tgReportStateCheckBox;
	private JSpinner tgConfidenceThreshold;
	private JButton btnEditTgherkinDocument;
	private String tgherkinDocument;
	private String protocolClass;
	private List<String> myClassPath;
	private boolean documentProtocol;
	private boolean active;
	
	/**
	 * Retrieve document protocol indicator.
	 * @return document protocol indicator
	 */
	public boolean isDocumentProtocol() {
		return documentProtocol;
	}

	/**
	 * Retrieve active indicator.
	 * @return active indicator
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Set active indicator.
	 * @param active the active indicator	  
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Constructor.
	 */
	public TgherkinPanel() {
		setLayout(null);
		addTgherkinControls();
		addTgherkinLabels();
	}
	
	private void addTgherkinControls() {
		tgComboBoxTgherkinDocument = new JComboBox<String>();
		tgComboBoxTgherkinDocument.setBounds(286, 10, 200, 20);
		tgComboBoxTgherkinDocument.setToolTipText("<html>\nSelect *.tgherkin document: specifications of features and scenarios in Tgherkin grammar.\n</html>");
		add(tgComboBoxTgherkinDocument);
		
		btnEditTgherkinDocument = new javax.swing.JButton("Edit Tgherkin Document");
		btnEditTgherkinDocument.setBounds(286, 34, 200, 20);
		btnEditTgherkinDocument.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnEditTgherkinDocumentActionPerformed(evt);
			}
		});
		btnEditTgherkinDocument.setToolTipText("Edit the Tgherkin document");
		btnEditTgherkinDocument.setMaximumSize(new java.awt.Dimension(160, 35));
		btnEditTgherkinDocument.setMinimumSize(new java.awt.Dimension(160, 35));
		btnEditTgherkinDocument.setPreferredSize(new java.awt.Dimension(160, 35));
		add(btnEditTgherkinDocument);

		tgApplyDefaultOnMismatchCheckBox = new JCheckBox("Apply default on step mismatch");
		tgApplyDefaultOnMismatchCheckBox.setBounds(10, 58, 200, 23);
		tgApplyDefaultOnMismatchCheckBox.setToolTipText("<html>\nIncicates whether default should be applied on step mismatches.\n</html>");
		add(tgApplyDefaultOnMismatchCheckBox);
		
		tgContinueToApplyDefaultCheckBox = new JCheckBox("Continue to apply default after a step mismatch");
		tgContinueToApplyDefaultCheckBox.setBounds(30, 82, 300, 23);
		tgContinueToApplyDefaultCheckBox.setToolTipText("<html>\nIncicates whether default should continue to be applied after a step mismatch occurred.\n</html>");
		add(tgContinueToApplyDefaultCheckBox);

		tgApplyDefaultOnMismatchCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!tgApplyDefaultOnMismatchCheckBox.isSelected() && tgContinueToApplyDefaultCheckBox.isSelected()){
					tgContinueToApplyDefaultCheckBox.setSelected(false);
				}
				tgContinueToApplyDefaultCheckBox.setEnabled(tgApplyDefaultOnMismatchCheckBox.isSelected());
			}
		});
		
		tgRepeatTgherkinScenariosCheckBox = new JCheckBox("Repeat Tgherkin scenarios");
		tgRepeatTgherkinScenariosCheckBox.setBounds(10, 106, 300, 23);
		tgRepeatTgherkinScenariosCheckBox.setToolTipText("<html>\nIncicates whether Tgherkin scenarios should be repeated until the number of sequences has been reached.\n</html>");
		add(tgRepeatTgherkinScenariosCheckBox);

		tgGenerateTgherkinReportCheckBox = new JCheckBox("Generate Tgherkin report");
		tgGenerateTgherkinReportCheckBox.setBounds(10, 130, 300, 23);
		tgGenerateTgherkinReportCheckBox.setToolTipText("<html>\nIndicates whether a Tgherkin report should be generated.\n</html>");
		add(tgGenerateTgherkinReportCheckBox);

		tgTgherkinReportIncludeOCRCheckBox = new JCheckBox("Include OCR in Tgherkin report");
		tgTgherkinReportIncludeOCRCheckBox.setBounds(30, 154, 300, 23);
		tgTgherkinReportIncludeOCRCheckBox.setToolTipText("<html>\nIndicates whether Optical Character Recognition (OCR) results should be included in the Tgherkin report.\nOCR will significantly slow down processing.\n</html>");
		add(tgTgherkinReportIncludeOCRCheckBox);

		tgGenerateTgherkinReportCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!tgGenerateTgherkinReportCheckBox.isSelected() && tgTgherkinReportIncludeOCRCheckBox.isSelected()){
					tgTgherkinReportIncludeOCRCheckBox.setSelected(false);
				}
				tgTgherkinReportIncludeOCRCheckBox.setEnabled(tgGenerateTgherkinReportCheckBox.isSelected());
			}
		});

		tgTgherkinReportIncludeImageRecognitionCheckBox = new JCheckBox("Include Image recognition in Tgherkin report");
		tgTgherkinReportIncludeImageRecognitionCheckBox.setBounds(30, 178, 300, 23);
		tgTgherkinReportIncludeImageRecognitionCheckBox.setToolTipText("<html>\nIndicates whether image recognition results should be included in the Tgherkin report.\nImage recognition will significantly slow down processing.\n</html>");
		add(tgTgherkinReportIncludeImageRecognitionCheckBox);

		tgGenerateTgherkinReportCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!tgGenerateTgherkinReportCheckBox.isSelected() && tgTgherkinReportIncludeImageRecognitionCheckBox.isSelected()){
					tgTgherkinReportIncludeImageRecognitionCheckBox.setSelected(false);
				}
				tgTgherkinReportIncludeImageRecognitionCheckBox.setEnabled(tgGenerateTgherkinReportCheckBox.isSelected());
			}
		});
		
		tgStoreTgherkinReportCheckBox = new JCheckBox("Store Tgherkin report in GraphDB");
		tgStoreTgherkinReportCheckBox.setBounds(10, 202, 300, 23);
		tgStoreTgherkinReportCheckBox.setToolTipText("<html>\nIndicates whether a Tgherkin report should be stored in the GraphDB database.\n</html>");
		add(tgStoreTgherkinReportCheckBox);

		tgReportDerivedGesturesCheckBox = new JCheckBox("Report Tgherkin derived gestures");
		tgReportDerivedGesturesCheckBox.setBounds(10, 226, 300, 23);
		tgReportDerivedGesturesCheckBox.setToolTipText("<html>\nIndicates whether Tgherkin derived gestures should be reported.\n</html>");
		add(tgReportDerivedGesturesCheckBox);

		tgReportStateCheckBox = new JCheckBox("Report state");
		tgReportStateCheckBox.setBounds(10, 250, 300, 23);
		tgReportStateCheckBox.setToolTipText("<html>\nIndicates whether state should be reported.\n</html>");
		add(tgReportStateCheckBox);
		
		tgConfidenceThreshold = new JSpinner();
		tgConfidenceThreshold.setBounds(280, 274, 100, 31);
		tgConfidenceThreshold.setModel(new SpinnerNumberModel(0.0d, 0.0d, 1.0d, 0.01d));
		tgConfidenceThreshold.setToolTipText("<html>\nNumber between zero and one that represents the confidence threshold\n</html>");
	    add(tgConfidenceThreshold);

	}

	private void addTgherkinLabels() {
		JLabel tgTgherkinDocumentLabel = new JLabel("Tgherkin document:");
		tgTgherkinDocumentLabel.setBounds(10, 10, 120, 23);
		add(tgTgherkinDocumentLabel);		

		JLabel tgConfidenceThresholdLabel = new JLabel("Confidence threshold for image recognition:");
		tgConfidenceThresholdLabel.setBounds(10, 274, 270, 23);
		add(tgConfidenceThresholdLabel);		

	}

	/**
	 * Populate Tgherkin Fields from Settings structure.
	 * @param settings The settings to load.
	 */
	public void populateFrom(final Settings settings) {
		tgApplyDefaultOnMismatchCheckBox.setSelected(settings.get(ConfigTags.ApplyDefaultOnMismatch));
		tgContinueToApplyDefaultCheckBox.setSelected(settings.get(ConfigTags.ContinueToApplyDefault));
		tgContinueToApplyDefaultCheckBox.setEnabled(tgApplyDefaultOnMismatchCheckBox.isSelected());
		tgRepeatTgherkinScenariosCheckBox.setSelected(settings.get(ConfigTags.RepeatTgherkinScenarios));
		tgGenerateTgherkinReportCheckBox.setSelected(settings.get(ConfigTags.GenerateTgherkinReport));
		tgTgherkinReportIncludeOCRCheckBox.setSelected(settings.get(ConfigTags.TgherkinReportIncludeOCR));
		tgTgherkinReportIncludeImageRecognitionCheckBox.setSelected(settings.get(ConfigTags.TgherkinReportIncludeImageRecognition));
		tgStoreTgherkinReportCheckBox.setSelected(settings.get(ConfigTags.StoreTgherkinReport));
		tgReportDerivedGesturesCheckBox.setSelected(settings.get(ConfigTags.ReportDerivedGestures));
		tgReportStateCheckBox.setSelected(settings.get(ConfigTags.ReportState));
		tgConfidenceThreshold.setValue(settings.get(ConfigTags.ConfidenceThreshold));
		tgherkinDocument = settings.get(ConfigTags.TgherkinDocument);
		protocolClass = settings.get(ConfigTags.ProtocolClass); 
		myClassPath = settings.get(MyClassPath);
		checkDocumentProtocol();
		if (documentProtocol) {
			populateTgherkinDocuments();
		}
	}

	/**
	 * Retrieve information from the Tgherkin GUI.
	 * @param settings reference to the object where the settings will be stored.
	 */
	public void extractInformation(final Settings settings) {
		if (tgComboBoxTgherkinDocument.getSelectedItem() != null) {
			settings.set(ConfigTags.TgherkinDocument, "./settings/" + settings.get(ConfigTags.ProtocolClass).split("/")[0] + "/" + (String)tgComboBoxTgherkinDocument.getSelectedItem());
		}else {
			settings.set(ConfigTags.TgherkinDocument, "");
		}
		settings.set(ConfigTags.ApplyDefaultOnMismatch, tgApplyDefaultOnMismatchCheckBox.isSelected());
		settings.set(ConfigTags.ContinueToApplyDefault, tgContinueToApplyDefaultCheckBox.isSelected());
		settings.set(ConfigTags.RepeatTgherkinScenarios, tgRepeatTgherkinScenariosCheckBox.isSelected());
		settings.set(ConfigTags.GenerateTgherkinReport, tgGenerateTgherkinReportCheckBox.isSelected());	
		settings.set(ConfigTags.TgherkinReportIncludeOCR, tgTgherkinReportIncludeOCRCheckBox.isSelected());	
		settings.set(ConfigTags.TgherkinReportIncludeImageRecognition, tgTgherkinReportIncludeImageRecognitionCheckBox.isSelected());	
		settings.set(ConfigTags.StoreTgherkinReport, tgStoreTgherkinReportCheckBox.isSelected());	
		settings.set(ConfigTags.ReportDerivedGestures, tgReportDerivedGesturesCheckBox.isSelected());
		settings.set(ConfigTags.ReportState, tgReportStateCheckBox.isSelected());
		settings.set(ConfigTags.ConfidenceThreshold, (Double) tgConfidenceThreshold.getValue());
	}

	private void btnEditTgherkinDocumentActionPerformed(java.awt.event.ActionEvent evt) {
		if (tgComboBoxTgherkinDocument.getSelectedItem() != null) {
			String fileName = "./settings/" + protocolClass.split("/")[0] + "/" + (String)tgComboBoxTgherkinDocument.getSelectedItem();
			JDialog dialog = new TgherkinEditor(fileName);
			dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
			dialog.setVisible(true);
		}
	}
	
	private void checkDocumentProtocol(){
		documentProtocol = false;
		URLClassLoader loader = null;
		try {
			URL[] classPath = new URL[myClassPath.size()];
			for(int i = 0; i < myClassPath.size(); i++) {
				classPath[i] = new File(myClassPath.get(i)).toURI().toURL();
			}	
			loader = new URLClassLoader(classPath);
			@SuppressWarnings("unchecked")
			UnProc<Settings> protocol = (UnProc<Settings>)loader.loadClass(protocolClass.split("/")[1]).getConstructor().newInstance();
			if (DocumentProtocol.class.isAssignableFrom(protocol.getClass())){
				documentProtocol = true;
			}
		}catch(Exception e) {
		}finally {
			if (loader != null) {
				try { 
					loader.close(); 
				}catch (IOException e) {}
			}	
		}
	}	
	
	private void populateTgherkinDocuments(){
		String[] tgherkinDocuments = new File("./settings/" + protocolClass.split("/")[0] + "/").list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return name.toLowerCase().endsWith(".tgherkin");
			}
		});
		Arrays.sort(tgherkinDocuments);
		tgComboBoxTgherkinDocument.setModel(new DefaultComboBoxModel<String>(tgherkinDocuments));
		int maxRows = 16;
		if (tgherkinDocuments.length < maxRows) {
			maxRows = tgherkinDocuments.length;
		}
		tgComboBoxTgherkinDocument.setMaximumRowCount(maxRows);
		if (tgherkinDocument.length() > 0) {
			// set selection to tgherkin document in settings
			String[] parts = tgherkinDocument.split("/");
			if (parts.length > 0) {
				String tgherkinDocumentName = parts[parts.length - 1];
				tgComboBoxTgherkinDocument.setSelectedItem(tgherkinDocumentName);
			}				
		}
		// tgherkin documents available for selection?
		if (tgherkinDocuments.length > 0) {
			btnEditTgherkinDocument.setEnabled(true);	
		}else {
			btnEditTgherkinDocument.setEnabled(false);
		}
		
	}
	
}
