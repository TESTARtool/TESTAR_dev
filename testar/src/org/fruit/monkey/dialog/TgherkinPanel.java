package org.fruit.monkey.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import nl.ou.testar.tgherkin.TgherkinEditor;
import nl.ou.testar.tgherkin.model.Document;
import nl.ou.testar.tgherkin.protocol.DocumentProtocol;
import org.fruit.UnProc;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

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
  private JComboBox<String> tgComboBoxExecutionMode;
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
  private JSpinner tgMinimumPercentageForImageRecognition;
  private JSpinner tgNumberOfNOPRetries;
  private JButton btnEditTgherkinDocument;
  private String tgherkinDocument;
  private String protocolClass;
  private boolean documentProtocol;
  private boolean active;

  private final Number spinValue = 0.0d;
  private final Comparable<Double> spinMinimum = 0.0d;
  private final Comparable<Double> spinMaximum1 = 1.0d;
  private final Comparable<Double> spinMaximum2 = 100.0d;
  private final Number spinStepsize1 = 0.01d;
  private final Number spinStepsize2 = 1.0d;

  private List<String> myClassPath;

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
    tgComboBoxTgherkinDocument
        .setToolTipText("<html>\nSelect *.tgherkin document: specifications of "
        + "features and scenarios in Tgherkin grammar.\n</html>");
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

    tgComboBoxExecutionMode = new JComboBox<String>();
    String[] executionModes = Document.getRegisteredExecutionModes();
    tgComboBoxExecutionMode.setModel(new DefaultComboBoxModel<>(executionModes));
    tgComboBoxExecutionMode.setSelectedIndex(0);
    tgComboBoxExecutionMode.setBounds(120, 34, 120, 20);
    tgComboBoxExecutionMode.setToolTipText("<html>\nSelect execution mode.\n</html>");
    add(tgComboBoxExecutionMode);

    tgApplyDefaultOnMismatchCheckBox = new JCheckBox("Apply default on step mismatch");
    tgApplyDefaultOnMismatchCheckBox.setBounds(10, 58, 200, 23);
    tgApplyDefaultOnMismatchCheckBox
        .setToolTipText("<html>\nIncicates whether default should "
        + "be applied on step mismatches.\n</html>");
    add(tgApplyDefaultOnMismatchCheckBox);

    tgContinueToApplyDefaultCheckBox =
        new JCheckBox("Continue to apply default after a step mismatch");
    tgContinueToApplyDefaultCheckBox.setBounds(30, 82, 300, 23);
    tgContinueToApplyDefaultCheckBox
        .setToolTipText("<html>\nIncicates whether default should "
        + "continue to be applied after a step mismatch occurred.\n</html>");
    add(tgContinueToApplyDefaultCheckBox);

    tgApplyDefaultOnMismatchCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!tgApplyDefaultOnMismatchCheckBox.isSelected()
            && tgContinueToApplyDefaultCheckBox.isSelected()) {
          tgContinueToApplyDefaultCheckBox.setSelected(false);
        }
          tgContinueToApplyDefaultCheckBox
              .setEnabled(tgApplyDefaultOnMismatchCheckBox.isSelected());
        }
      });

    tgRepeatTgherkinScenariosCheckBox = new JCheckBox("Repeat Tgherkin scenarios");
    tgRepeatTgherkinScenariosCheckBox.setBounds(10, 106, 300, 23);
    tgRepeatTgherkinScenariosCheckBox
        .setToolTipText("<html>\nIncicates whether Tgherkin scenarios should "
        + "be repeated until the number of sequences has been reached.\n</html>");
    add(tgRepeatTgherkinScenariosCheckBox);

    tgGenerateTgherkinReportCheckBox = new JCheckBox("Generate Tgherkin report");
    tgGenerateTgherkinReportCheckBox.setBounds(10, 130, 300, 23);
    tgGenerateTgherkinReportCheckBox
         .setToolTipText("<html>\nIndicates whether a Tgherkin report should "
         + "be generated.\n</html>");
    add(tgGenerateTgherkinReportCheckBox);

    tgStoreTgherkinReportCheckBox = new JCheckBox("Store Tgherkin report in GraphDB");
    tgStoreTgherkinReportCheckBox.setBounds(10, 154, 300, 23);
    tgStoreTgherkinReportCheckBox
        .setToolTipText("<html>\nIndicates whether a Tgherkin report "
        + "should be stored in the GraphDB database.\n</html>");
    add(tgStoreTgherkinReportCheckBox);

    tgReportDerivedGesturesCheckBox = new JCheckBox("Report Tgherkin derived gestures");
    tgReportDerivedGesturesCheckBox.setBounds(10, 178, 300, 23);
    tgReportDerivedGesturesCheckBox
        .setToolTipText("<html>\nIndicates whether Tgherkin derived gestures "
        + "should be reported.\n</html>");
    add(tgReportDerivedGesturesCheckBox);

    tgReportStateCheckBox = new JCheckBox("Report state");
    tgReportStateCheckBox.setBounds(10, 202, 300, 23);
    tgReportStateCheckBox
       .setToolTipText("<html>\nIndicates whether state should be reported.\n</html>");
    add(tgReportStateCheckBox);

    tgTgherkinReportIncludeOCRCheckBox = new JCheckBox("Include OCR in Tgherkin state report");
    tgTgherkinReportIncludeOCRCheckBox.setBounds(30, 226, 300, 23);
    tgTgherkinReportIncludeOCRCheckBox
        .setToolTipText("<html>\nIndicates whether Optical Character Recognition (OCR) "
        + "results should be included in the Tgherkin state report.\nOCR "
        + "will significantly slow down processing.\n</html>");
    add(tgTgherkinReportIncludeOCRCheckBox);

    tgReportStateCheckBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (!tgReportStateCheckBox.isSelected()
              && tgTgherkinReportIncludeOCRCheckBox.isSelected()) {
            tgTgherkinReportIncludeOCRCheckBox.setSelected(false);
          }
            tgTgherkinReportIncludeOCRCheckBox
                .setEnabled(tgGenerateTgherkinReportCheckBox.isSelected());
        }
      });

    tgTgherkinReportIncludeImageRecognitionCheckBox
        = new JCheckBox("Include Image recognition in Tgherkin state report");
    tgTgherkinReportIncludeImageRecognitionCheckBox.setBounds(30, 250, 300, 23);
    tgTgherkinReportIncludeImageRecognitionCheckBox
        .setToolTipText("<html>\nIndicates whether image recognition results "
        + "should be included in the Tgherkin state report.\nImage recognition "
        + "will significantly slow down processing.\n</html>");
    add(tgTgherkinReportIncludeImageRecognitionCheckBox);

    tgReportStateCheckBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (!tgReportStateCheckBox.isSelected()
              && tgTgherkinReportIncludeImageRecognitionCheckBox.isSelected()) {
            tgTgherkinReportIncludeImageRecognitionCheckBox.setSelected(false);
          }
          tgTgherkinReportIncludeImageRecognitionCheckBox
              .setEnabled(tgGenerateTgherkinReportCheckBox.isSelected());
        }
      });
    tgConfidenceThreshold = new JSpinner();
    tgConfidenceThreshold.setBounds(280, 274, 100, 25);
    tgConfidenceThreshold.setModel(new SpinnerNumberModel(spinValue, spinMinimum, spinMaximum1, spinStepsize1));
    tgConfidenceThreshold
        .setToolTipText("<html>\nNumber between zero and one that represents "
        + "the confidence threshold.\n</html>");
    add(tgConfidenceThreshold);

    tgMinimumPercentageForImageRecognition = new JSpinner();
    tgMinimumPercentageForImageRecognition.setBounds(280, 298, 100, 25);
    tgMinimumPercentageForImageRecognition
        .setModel(new SpinnerNumberModel(spinValue, spinMinimum, spinMaximum2, spinStepsize2));
    tgMinimumPercentageForImageRecognition
        .setToolTipText("<html>\nMinimum percentage of entire widget screenshot"
        + "for the rectangle that contains the recognized image.<br>"
        + "The percentage has to be a value between 0 and 100.\n</html>");
    add(tgMinimumPercentageForImageRecognition);

    tgNumberOfNOPRetries = new JSpinner();
    tgNumberOfNOPRetries.setBounds(280, 322, 100, 25);
    tgNumberOfNOPRetries.setModel(new SpinnerNumberModel(spinValue, spinMinimum, spinMaximum2, spinStepsize2));
    tgNumberOfNOPRetries
        .setToolTipText("<html>\nNumber of NOP (no operation) action retries "
        + "if a mismatch occurs during the derivation of actions.\n</html>");
    add(tgNumberOfNOPRetries);
  }

  private void addTgherkinLabels() {
    JLabel tgTgherkinDocumentLabel = new JLabel("Tgherkin document:");
    tgTgherkinDocumentLabel.setBounds(10, 10, 120, 23);
    add(tgTgherkinDocumentLabel);

    JLabel tgExecutionModeLabel = new JLabel("Execution mode:");
    tgExecutionModeLabel.setBounds(10, 34, 100, 23);
    add(tgExecutionModeLabel);

    JLabel tgConfidenceThresholdLabel = new JLabel("Confidence threshold for image recognition:");
    tgConfidenceThresholdLabel.setBounds(10, 274, 270, 23);
    add(tgConfidenceThresholdLabel);

    JLabel tgMinimumPercentageForImageRecognitionLabel =
        new JLabel("Minimum percentage for image recognition:");
    tgMinimumPercentageForImageRecognitionLabel.setBounds(10, 298, 270, 23);
    add(tgMinimumPercentageForImageRecognitionLabel);

    JLabel tgNumberOfNOPRetriesLabel =
        new JLabel("Number of NOP (no operation) action retries:");
    tgNumberOfNOPRetriesLabel.setBounds(10, 322, 270, 23);
    add(tgNumberOfNOPRetriesLabel);
  }

  /**
   * Populate Tgherkin Fields from Settings structure.
   * @param settings The settings to load
   */
  public void populateFrom(final Settings settings) {
    tgComboBoxTgherkinDocument.setSelectedItem(settings.get(ConfigTags.TgherkinDocument));
    tgComboBoxExecutionMode.setSelectedItem(settings.get(ConfigTags.TgherkinExecutionMode));
    tgApplyDefaultOnMismatchCheckBox.setSelected(settings.get(ConfigTags.ApplyDefaultOnMismatch));
    tgContinueToApplyDefaultCheckBox.setSelected(settings.get(ConfigTags.ContinueToApplyDefault));
    tgContinueToApplyDefaultCheckBox.setEnabled(tgApplyDefaultOnMismatchCheckBox.isSelected());
    tgRepeatTgherkinScenariosCheckBox.setSelected(settings.get(ConfigTags.RepeatTgherkinScenarios));
    tgGenerateTgherkinReportCheckBox.setSelected(settings.get(ConfigTags.GenerateTgherkinReport));
    tgTgherkinReportIncludeOCRCheckBox
        .setSelected(settings.get(ConfigTags.TgherkinReportIncludeOCR));
    tgTgherkinReportIncludeImageRecognitionCheckBox
        .setSelected(settings.get(ConfigTags.TgherkinReportIncludeImageRecognition));
    tgStoreTgherkinReportCheckBox.setSelected(settings.get(ConfigTags.StoreTgherkinReport));
    tgReportDerivedGesturesCheckBox.setSelected(settings.get(ConfigTags.ReportDerivedGestures));
    tgReportStateCheckBox.setSelected(settings.get(ConfigTags.ReportState));
    tgConfidenceThreshold.setValue(settings.get(ConfigTags.ConfidenceThreshold));
    tgMinimumPercentageForImageRecognition
        .setValue(settings.get(ConfigTags.MinimumPercentageForImageRecognition));
    tgNumberOfNOPRetries.setValue(settings.get(ConfigTags.TgherkinNrOfNOPRetries));
    tgherkinDocument = settings.get(ConfigTags.TgherkinDocument);
    protocolClass = settings.get(ConfigTags.ProtocolClass);
    myClassPath = settings.get(ConfigTags.MyClassPath);
    checkDocumentProtocol();
    if (documentProtocol) {
      populateTgherkinDocuments();
    }
  }

  /**
   * Retrieve information from the Tgherkin GUI.
   * @param settings reference to the object where the settings will be stored
   */
  public void extractInformation(final Settings settings) {
    if (tgComboBoxTgherkinDocument.getSelectedItem() != null) {
      settings.set(ConfigTags.TgherkinDocument, "./resources/settings/"
          + settings.get(ConfigTags.ProtocolClass).split("/")[0] + "/"
          + (String)tgComboBoxTgherkinDocument.getSelectedItem());
    } else {
      settings.set(ConfigTags.TgherkinDocument, "");
    }
    settings.set(ConfigTags.TgherkinExecutionMode,
        (String) tgComboBoxExecutionMode.getSelectedItem());
    settings.set(ConfigTags.ApplyDefaultOnMismatch, tgApplyDefaultOnMismatchCheckBox.isSelected());
    settings.set(ConfigTags.ContinueToApplyDefault, tgContinueToApplyDefaultCheckBox.isSelected());
    settings.set(ConfigTags.RepeatTgherkinScenarios,
        tgRepeatTgherkinScenariosCheckBox.isSelected());
    settings.set(ConfigTags.GenerateTgherkinReport,
        tgGenerateTgherkinReportCheckBox.isSelected());
    settings.set(ConfigTags.TgherkinReportIncludeOCR,
        tgTgherkinReportIncludeOCRCheckBox.isSelected());
    settings.set(ConfigTags.TgherkinReportIncludeImageRecognition,
        tgTgherkinReportIncludeImageRecognitionCheckBox.isSelected());
    settings.set(ConfigTags.StoreTgherkinReport, tgStoreTgherkinReportCheckBox.isSelected());
    settings.set(ConfigTags.ReportDerivedGestures, tgReportDerivedGesturesCheckBox.isSelected());
    settings.set(ConfigTags.ReportState, tgReportStateCheckBox.isSelected());
    settings.set(ConfigTags.ConfidenceThreshold, (Double) tgConfidenceThreshold.getValue());
    settings.set(ConfigTags.MinimumPercentageForImageRecognition,
        (Double) tgMinimumPercentageForImageRecognition.getValue());
   // settings.set(ConfigTags.TgherkinNrOfNOPRetries, (Integer) tgNumberOfNOPRetries.getValue());
  }

  private void btnEditTgherkinDocumentActionPerformed(java.awt.event.ActionEvent evt) {
    if (tgComboBoxTgherkinDocument.getSelectedItem() != null) {
      String fileName =
          "./resources/settings/" + protocolClass.split("/")[0]
          + "/" + (String)tgComboBoxTgherkinDocument.getSelectedItem();
      JDialog dialog = new TgherkinEditor(fileName);
      dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
      dialog.setVisible(true);
    }
  }

  private void checkDocumentProtocol() {
    documentProtocol = false;
    URLClassLoader loader = null;
    try {
      URL[] classPath = new URL[myClassPath.size()];
      for (int i = 0; i < myClassPath.size(); i++) {
        classPath[i] = new File(myClassPath.get(i)).toURI().toURL();
      }
      // URL[] classPath = {new File("bin/oracle/").toURI().toURL()};
      loader = new URLClassLoader(classPath);

      @SuppressWarnings("unchecked")
      UnProc<Settings> protocol =
          (UnProc<Settings>) loader.loadClass(protocolClass.replace("/", "."))
          .getConstructor().newInstance();
      if (DocumentProtocol.class.isAssignableFrom(protocol.getClass())) {
        documentProtocol = true;
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (loader != null) {
        try {
          loader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void populateTgherkinDocuments() {
    String[] tgherkinDocuments =
        new File("./resources/settings/" + protocolClass.split("/")[0] + "/")
            .list(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                  return name.toLowerCase().endsWith(".tgherkin");
                }
            });
    Arrays.sort(tgherkinDocuments);
    tgComboBoxTgherkinDocument.setModel(new DefaultComboBoxModel<String>(tgherkinDocuments));
    final int maxRows = 16;
    int maxRowCount = maxRows;
    if (tgherkinDocuments.length < maxRows) {
      maxRowCount = tgherkinDocuments.length;
    }
    tgComboBoxTgherkinDocument.setMaximumRowCount(maxRowCount);
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
    } else {
      btnEditTgherkinDocument.setEnabled(false);
    }
  }
}
