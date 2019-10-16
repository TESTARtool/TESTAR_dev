package nl.ou.testar.temporal.ui;

import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.temporal.behavior.TemporalController;
import nl.ou.testar.temporal.structure.APSelectorManager;
import nl.ou.testar.temporal.structure.TemporalConstraintedPattern;
import nl.ou.testar.temporal.structure.TemporalOracle;
import nl.ou.testar.temporal.structure.WidgetFilter;
import nl.ou.testar.temporal.util.*;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.fruit.monkey.Main.outputDir;

public class Temporalpanel2 {  //"extends JPanel" was manually added
    //****custom
    private Process webAnalyzerProcess = null;
    private String dataStoreText;
    private String dataStoreServerDNS;
    private String dataStoreDirectory;
    private String dataStoreDBText;
    private String dataStoreUser;
    private String dataStorePassword;
    private String dataStoreType;
    private String VisualizerURL;
    private String VisualizerURLStop;
    private StreamConsumer webAnalyzerErr;
    private StreamConsumer webAnalyzerOut;
    TemporalController tcontrol;
    //**** custom


    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTextArea textArea12;
    private JButton clearButton;
    private JTextField textField1;
    private JTextField textField5;
    private JTextField textField6;
    private JButton button1;
    private JButton button2;
    private JTextField textField7;
    private JButton button3;
    private JButton generateButton;
    private JTextField textField8;
    private JButton button4;
    private JButton startAnalyzerButton;
    private JButton stopAnalyzerButton;
    private JTextField textField9;
    private JButton button5;
    private JTextField textField12;
    private JButton button6;
    private JButton modelCheckButton;
    private JButton sampleOracleButton;
    private JButton temporalModelButton;

    private JButton defaultSelectorButton;
    private JButton writeSelectorButton;
    private JButton loadOracleTESTCSVButton;
    private JTextField textField2;
    private JCheckBox WSLCheckBox;
    private JTextField textField3;
    private JTextField textFieldPythonEnvironment;
    private JButton button9;
    private JTextField textFieldPythonVisualizer;
    private JButton button10;
    private JButton LTLModelCheckButton;
    private JTextField Test_automaton4;
    private JTextField Test_Formulas;
    private JButton button11;
    private JButton button12;
    private JTextField AlivePropositionTextField;
    private JButton testDbButton;
    private JButton parseLTL;
    private JTextField resultsTxtTextField;
    private JButton button8;
    private JComboBox comboBox2;
    private JButton graphMLButton;
    private JButton loadTestSelectorButton;
    private JTextField APSelectorManagerTESTJSONTextField;
    private JTextField temporalOracleTESTCsvTextField;
    private JButton defaultSelectorButton1;
    private JButton sampleOracleButton1;
    private JButton graphMLButton1;
    private JCheckBox verboseCheckBox;
    private JTextField textField10;
    private JPanel setupPanel;
    private JPanel minerPanel;
    private JPanel visualizerPanel;
    private JPanel alphaExplorePanel;
    private JPanel alphaLTLPanel;
    private JCheckBox enableTemporalOfflineOraclesCheckBox;

    public Temporalpanel2() {
        $$$setupUI$$$();


        startAnalyzerButton.addActionListener(this::startTemporalWebAnalyzer);
        stopAnalyzerButton.addActionListener(this::stopTemporalWebAnalyzer);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea12.setText("cleared");
            }
        });

        sampleOracleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testPatternCSV();
                testOracleCSV();
            }
        });


        temporalModelButton.addActionListener(new ActionListener() {
                                                  @Override
                                                  public void actionPerformed(ActionEvent e) {
                                                      exportTemporalmodel(e);
                                                  }
                                              }
        );
        defaultSelectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testSaveDefaultApSelectionManagerJSON();
            }
        });


        LTLModelCheckButton.addActionListener(this::LTLModelCheck);
        button11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fd.setCurrentDirectory(new File("./" + outputDir));//.getParentFile());

                if (fd.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
                    String file = fd.getSelectedFile().getAbsolutePath();
                    Test_automaton4.setText(file);
                }
                //
            }
        });
        button12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fd.setCurrentDirectory(new File("./" + outputDir));//.getParentFile());

                if (fd.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
                    String file = fd.getSelectedFile().getAbsolutePath();
                    Test_Formulas.setText(file);
                }
                //
            }
        });

        parseLTL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLTLFormulaCheck(e);
            }
        });
        testDbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testdbconnection(e);
            }
        });
        button8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fd.setCurrentDirectory(new File("./" + outputDir));//.getParentFile());

                if (fd.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
                    String file = fd.getSelectedFile().getAbsolutePath();
                    resultsTxtTextField.setText(file);
                }
                //
            }
        });
        graphMLButton.addActionListener(this::testgraphml);

        loadTestSelectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fd.setCurrentDirectory(new File("./" + outputDir));//.getParentFile());

                if (fd.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
                    String file = fd.getSelectedFile().getAbsolutePath();
                    APSelectorManagerTESTJSONTextField.setText(file);
                }
                //
            }
        });
        loadOracleTESTCSVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fd.setCurrentDirectory(new File("./" + outputDir));//.getParentFile());

                if (fd.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
                    String file = fd.getSelectedFile().getAbsolutePath();
                    temporalOracleTESTCsvTextField.setText(file);
                }
                //
            }
        });
        button9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fd.setCurrentDirectory(new File("./" + outputDir));//.getParentFile());

                if (fd.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
                    String file = fd.getSelectedFile().getAbsolutePath();
                    textFieldPythonEnvironment.setText(file);
                }
                //
            }
        });
        button10.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fd.setCurrentDirectory(new File("./" + outputDir));//.getParentFile());

                if (fd.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
                    String file = fd.getSelectedFile().getAbsolutePath();
                    textFieldPythonVisualizer.setText(file);
                }
                //
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fd.setCurrentDirectory(new File("./" + outputDir));//.getParentFile());

                if (fd.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
                    String file = fd.getSelectedFile().getAbsolutePath();
                    textField6.setText(file);
                }
                //
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fd.setCurrentDirectory(new File("./" + outputDir));//.getParentFile());

                if (fd.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
                    String file = fd.getSelectedFile().getAbsolutePath();
                    textField7.setText(file);
                }
                //
            }
        });

        modelCheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ModelCheck(e);
            }
        });
        defaultSelectorButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testSaveDefaultApSelectionManagerJSON();
            }
        });
        sampleOracleButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testOracleCSV();
                testPatternCSV();
            }
        });
        graphMLButton1.addActionListener(this::testgraphml);

    }

    public static Temporalpanel2 createTemporalPanel() {
        Temporalpanel2 panel = new Temporalpanel2();
        panel.initialize();
        return panel;
    }


    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:227px:noGrow,left:6dlu:noGrow,fill:10px:noGrow,right:max(p;42px):noGrow,left:4dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow,top:199px:noGrow,top:161px:noGrow"));
        panel1.setPreferredSize(new Dimension(621, 340));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2), null));
        final JSeparator separator1 = new JSeparator();
        CellConstraints cc = new CellConstraints();
        panel1.add(separator1, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setMinimumSize(new Dimension(617, 275));
        tabbedPane1.setPreferredSize(new Dimension(617, 275));
        tabbedPane1.setRequestFocusEnabled(true);
        tabbedPane1.setVisible(true);
        panel1.add(tabbedPane1, cc.xyw(1, 2, 15));
        setupPanel = new JPanel();
        setupPanel.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,left:330px:noGrow,left:4dlu:noGrow,right:95px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:noGrow,top:4dlu:noGrow,center:d:noGrow,top:4dlu:noGrow,center:d:noGrow"));
        setupPanel.setVisible(false);
        tabbedPane1.addTab("Setup", setupPanel);
        final JLabel label1 = new JLabel();
        label1.setText("LTL Checker:");
        label1.setToolTipText("Used for LTL model check. the usual command to invoke is:  spot_checker");
        setupPanel.add(label1, cc.xy(1, 3));
        textField2 = new JTextField();
        setupPanel.add(textField2, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label2 = new JLabel();
        label2.setText("Python Env. :");
        label2.setToolTipText("Path to Active Virtual environment");
        setupPanel.add(label2, cc.xy(1, 5));
        textFieldPythonEnvironment = new JTextField();
        setupPanel.add(textFieldPythonEnvironment, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        button9 = new JButton();
        button9.setText("...");
        setupPanel.add(button9, cc.xy(5, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JLabel label3 = new JLabel();
        label3.setText("Visualizer:");
        label3.setToolTipText("Usually this is the path to run.py");
        setupPanel.add(label3, cc.xy(1, 7));
        textFieldPythonVisualizer = new JTextField();
        setupPanel.add(textFieldPythonVisualizer, cc.xy(3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        button10 = new JButton();
        button10.setText("...");
        setupPanel.add(button10, cc.xy(5, 7, CellConstraints.LEFT, CellConstraints.DEFAULT));
        WSLCheckBox = new JCheckBox();
        WSLCheckBox.setText("WSL?");
        WSLCheckBox.setToolTipText("<html> Does this command need a WSL path?<br> \ne.g. starting with \"/mnt/C/...\"<br>\nWhen ticked then input files for the modelchecker are converted automatically.\n</html>");
        setupPanel.add(WSLCheckBox, cc.xy(5, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
        verboseCheckBox = new JCheckBox();
        verboseCheckBox.setText("Verbose?");
        verboseCheckBox.setToolTipText("<html> When checked: keeps the intermediate files on disk ,<br>else these files are deleted after the Checker has run.\"\n</html>");
        setupPanel.add(verboseCheckBox, cc.xy(7, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
        enableTemporalOfflineOraclesCheckBox = new JCheckBox();
        enableTemporalOfflineOraclesCheckBox.setText("Enable Temporal Offline Oracles?");
        setupPanel.add(enableTemporalOfflineOraclesCheckBox, cc.xyw(1, 1, 3));
        minerPanel = new JPanel();
        minerPanel.setLayout(new FormLayout("left:117px:noGrow,left:4dlu:noGrow,fill:146px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:37px:noGrow,left:4dlu:noGrow,left:4dlu:noGrow,fill:41px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:5dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,fill:d:noGrow,left:4dlu:noGrow,left:153px:noGrow,left:22dlu:noGrow", "center:41px:noGrow,center:10dlu:noGrow,center:max(d;4px):noGrow,top:5dlu:noGrow,center:d:noGrow,top:4dlu:noGrow,center:24dlu:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        tabbedPane1.addTab("Miner", minerPanel);
        final JLabel label4 = new JLabel();
        label4.setText("Generate:");
        minerPanel.add(label4, cc.xy(1, 7));
        final JLabel label5 = new JLabel();
        label5.setText("Oracles");
        minerPanel.add(label5, cc.xy(1, 5));
        final JLabel label6 = new JLabel();
        label6.setText("Oracle Patterns");
        minerPanel.add(label6, cc.xy(1, 3));
        textField7 = new JTextField();
        minerPanel.add(textField7, cc.xyw(3, 5, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        textField5 = new JTextField();
        minerPanel.add(textField5, cc.xyw(3, 3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label7 = new JLabel();
        label7.setText("APSelectorManager");
        minerPanel.add(label7, cc.xy(1, 1));
        textField6 = new JTextField();
        minerPanel.add(textField6, cc.xyw(3, 1, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        button2 = new JButton();
        button2.setText("...");
        minerPanel.add(button2, cc.xy(10, 5));
        button1 = new JButton();
        button1.setText("...");
        minerPanel.add(button1, cc.xy(10, 3));
        button3 = new JButton();
        button3.setText("...");
        minerPanel.add(button3, cc.xy(10, 1));
        final JSeparator separator2 = new JSeparator();
        minerPanel.add(separator2, cc.xyw(1, 2, 10, CellConstraints.FILL, CellConstraints.FILL));
        modelCheckButton = new JButton();
        modelCheckButton.setHorizontalTextPosition(0);
        modelCheckButton.setText("Check");
        modelCheckButton.setToolTipText("<html>Perform a ModelCheck of the Potential Oracles. <BR>\nThe model is computed by applying the filters from the APSelectorManager to a transformed model of the graph DB </html> ");
        minerPanel.add(modelCheckButton, cc.xyw(12, 5, 12));
        generateButton = new JButton();
        generateButton.setEnabled(false);
        generateButton.setHorizontalTextPosition(0);
        generateButton.setText("<html>Generate</html>");
        generateButton.setToolTipText("<html>Instantiated the Pattern parameters with Atomic Propositions (AP's) from the Model to generate (Potential) Oracles. \n<BR>The list of  AP's in the model is  computed by applying the filters from the APSelectorManager to a transformed model of the graph DB </html>");
        minerPanel.add(generateButton, cc.xyw(12, 3, 12));
        comboBox2 = new JComboBox();
        comboBox2.setEnabled(false);
        comboBox2.setMinimumSize(new Dimension(130, 38));
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("RndPerPattern1");
        defaultComboBoxModel1.addElement("RndPerPattern5");
        defaultComboBoxModel1.addElement("RndPerPattern10");
        defaultComboBoxModel1.addElement("RndPerPattern100");
        defaultComboBoxModel1.addElement("RndPerPattern1000");
        defaultComboBoxModel1.addElement("RndPerPattern10000");
        defaultComboBoxModel1.addElement("Random10");
        defaultComboBoxModel1.addElement("Random100");
        defaultComboBoxModel1.addElement("Random1000");
        defaultComboBoxModel1.addElement("Random10000");
        defaultComboBoxModel1.addElement("Random100000");
        defaultComboBoxModel1.addElement("Random1000000");
        defaultComboBoxModel1.addElement("All(permutation)");
        comboBox2.setModel(defaultComboBoxModel1);
        comboBox2.setPreferredSize(new Dimension(150, 38));
        comboBox2.setToolTipText("tactic to generate oracles from the supplied Pattern collection");
        comboBox2.setVerifyInputWhenFocusTarget(true);
        minerPanel.add(comboBox2, cc.xyw(24, 1, 2, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JLabel label8 = new JLabel();
        label8.setText("Tactic:");
        minerPanel.add(label8, cc.xyw(15, 1, 6, CellConstraints.LEFT, CellConstraints.DEFAULT));
        defaultSelectorButton1 = new JButton();
        defaultSelectorButton1.setMaximumSize(new Dimension(130, 38));
        defaultSelectorButton1.setMinimumSize(new Dimension(130, 38));
        defaultSelectorButton1.setPreferredSize(new Dimension(130, 38));
        defaultSelectorButton1.setText("Default Selector");
        defaultSelectorButton1.setToolTipText("Generate a default APSelectionManager");
        minerPanel.add(defaultSelectorButton1, cc.xyw(3, 7, 4, CellConstraints.LEFT, CellConstraints.DEFAULT));
        sampleOracleButton1 = new JButton();
        sampleOracleButton1.setPreferredSize(new Dimension(110, 38));
        sampleOracleButton1.setText("Sample Oracle");
        sampleOracleButton1.setToolTipText("Generate files with a sample Oracle and a sample Pattern.");
        sampleOracleButton1.putClientProperty("html.disable", Boolean.FALSE);
        minerPanel.add(sampleOracleButton1, cc.xyw(7, 7, 11, CellConstraints.LEFT, CellConstraints.CENTER));
        graphMLButton1 = new JButton();
        graphMLButton1.setText("GraphML");
        graphMLButton1.setToolTipText("Make a model from the graphDB. Requires APSelectorManagerTEST.json file in directory temporal");
        minerPanel.add(graphMLButton1, cc.xy(24, 7, CellConstraints.LEFT, CellConstraints.DEFAULT));
        visualizerPanel = new JPanel();
        visualizerPanel.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,left:319px:noGrow,left:4dlu:noGrow,left:4dlu:noGrow,right:40px:noGrow,left:4dlu:noGrow,left:4dlu:noGrow,left:128px:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        tabbedPane1.addTab("Visualizer", visualizerPanel);
        final JLabel label9 = new JLabel();
        label9.setText("Input Oracles");
        visualizerPanel.add(label9, cc.xy(1, 1));
        textField8 = new JTextField();
        textField8.setEditable(false);
        textField8.setEnabled(false);
        visualizerPanel.add(textField8, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        button4 = new JButton();
        button4.setEnabled(false);
        button4.setText("...");
        visualizerPanel.add(button4, cc.xy(6, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        final JLabel label10 = new JLabel();
        label10.setText("Input GraphML");
        visualizerPanel.add(label10, cc.xy(1, 3));
        textField9 = new JTextField();
        textField9.setEditable(false);
        textField9.setEnabled(false);
        visualizerPanel.add(textField9, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        button5 = new JButton();
        button5.setEnabled(false);
        button5.setText("...");
        visualizerPanel.add(button5, cc.xy(6, 3, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        startAnalyzerButton = new JButton();
        startAnalyzerButton.setText("Start Analyzer");
        visualizerPanel.add(startAnalyzerButton, cc.xy(1, 7));
        final JLabel label11 = new JLabel();
        label11.setText("Output Oracles");
        visualizerPanel.add(label11, cc.xy(1, 5));
        textField12 = new JTextField();
        textField12.setEditable(false);
        textField12.setEnabled(false);
        visualizerPanel.add(textField12, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        button6 = new JButton();
        button6.setEnabled(false);
        button6.setText("...");
        visualizerPanel.add(button6, cc.xy(6, 5));
        stopAnalyzerButton = new JButton();
        stopAnalyzerButton.setText("Stop Analyzer");
        visualizerPanel.add(stopAnalyzerButton, cc.xy(9, 7, CellConstraints.LEFT, CellConstraints.DEFAULT));
        alphaExplorePanel = new JPanel();
        alphaExplorePanel.setLayout(new FormLayout("fill:143px:noGrow,left:4dlu:noGrow,fill:d:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:5dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:31px:noGrow,left:5dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:15px:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:max(m;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,center:max(d;4px):noGrow"));
        alphaExplorePanel.setEnabled(false);
        alphaExplorePanel.setFocusable(false);
        alphaExplorePanel.setVisible(false);
        tabbedPane1.addTab("Alpha Explore", alphaExplorePanel);
        parseLTL = new JButton();
        parseLTL.setHideActionText(false);
        parseLTL.setText("Parse LTL Formula");
        parseLTL.setVisible(true);
        alphaExplorePanel.add(parseLTL, cc.xy(1, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JLabel label12 = new JLabel();
        label12.setText("Generate Files:");
        alphaExplorePanel.add(label12, cc.xywh(1, 5, 1, 3));
        defaultSelectorButton = new JButton();
        defaultSelectorButton.setText("Default Selector");
        defaultSelectorButton.setToolTipText("Generate default APSelectionManager");
        alphaExplorePanel.add(defaultSelectorButton, cc.xywh(3, 5, 1, 4, CellConstraints.LEFT, CellConstraints.DEFAULT));
        sampleOracleButton = new JButton();
        sampleOracleButton.setText("Sample Oracle");
        alphaExplorePanel.add(sampleOracleButton, cc.xywh(5, 5, 1, 4, CellConstraints.LEFT, CellConstraints.DEFAULT));
        graphMLButton = new JButton();
        graphMLButton.setText("GraphML");
        graphMLButton.setToolTipText("Make a model from the graphDB. Requires APSelectorManagerTEST.json file in directory temporal");
        alphaExplorePanel.add(graphMLButton, cc.xywh(9, 5, 2, 4, CellConstraints.LEFT, CellConstraints.DEFAULT));
        textField1 = new JTextField();
        textField1.setText("G(a->Fb)");
        textField1.setVisible(true);
        alphaExplorePanel.add(textField1, cc.xyw(3, 3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        temporalModelButton = new JButton();
        temporalModelButton.setText("Temporal Prepare");
        temporalModelButton.setToolTipText("<html>Exports/transforms the first model from the graphDB  into intermediate (JSON) format and then to HOA format. HOA can be loaded in LTL model checker SPOT. <br>Requires APSelectorManager file for filtering and<br>oracle file to instantiate LTL formulas");
        alphaExplorePanel.add(temporalModelButton, cc.xyw(16, 12, 2, CellConstraints.LEFT, CellConstraints.DEFAULT));
        loadTestSelectorButton = new JButton();
        loadTestSelectorButton.setText("...");
        alphaExplorePanel.add(loadTestSelectorButton, cc.xy(14, 10));
        APSelectorManagerTESTJSONTextField = new JTextField();
        APSelectorManagerTESTJSONTextField.setText("APSelectorManagerTEST.JSON");
        alphaExplorePanel.add(APSelectorManagerTESTJSONTextField, cc.xyw(3, 10, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label13 = new JLabel();
        label13.setText("APSelectorManager");
        alphaExplorePanel.add(label13, cc.xy(1, 10));
        loadOracleTESTCSVButton = new JButton();
        loadOracleTESTCSVButton.setText("...");
        alphaExplorePanel.add(loadOracleTESTCSVButton, cc.xy(14, 12));
        temporalOracleTESTCsvTextField = new JTextField();
        temporalOracleTESTCsvTextField.setText("temporalOracleTEST.csv");
        alphaExplorePanel.add(temporalOracleTESTCsvTextField, cc.xyw(3, 12, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label14 = new JLabel();
        label14.setText("Raw Oracles");
        alphaExplorePanel.add(label14, cc.xy(1, 12));
        textField3 = new JTextField();
        alphaExplorePanel.add(textField3, cc.xyw(3, 1, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        testDbButton = new JButton();
        testDbButton.setText("Test DB Connection");
        alphaExplorePanel.add(testDbButton, cc.xyw(10, 1, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JLabel label15 = new JLabel();
        label15.setText("LTL  Parser:");
        label15.setToolTipText("used for testing syntax of  a LTL formula");
        alphaExplorePanel.add(label15, cc.xy(1, 1));
        alphaLTLPanel = new JPanel();
        alphaLTLPanel.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,left:330px:noGrow,left:4dlu:noGrow,fill:31px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        alphaLTLPanel.setEnabled(false);
        alphaLTLPanel.setFocusable(false);
        alphaLTLPanel.setVisible(false);
        tabbedPane1.addTab("Alpha LTL", alphaLTLPanel);
        final Spacer spacer1 = new Spacer();
        alphaLTLPanel.add(spacer1, cc.xy(5, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label16 = new JLabel();
        label16.setText("HOAModel File");
        alphaLTLPanel.add(label16, cc.xy(1, 1));
        final JLabel label17 = new JLabel();
        label17.setText("LTLFormula File");
        alphaLTLPanel.add(label17, cc.xy(1, 5));
        Test_automaton4 = new JTextField();
        Test_automaton4.setText("~/testar/tests/test_automaton4.txt");
        alphaLTLPanel.add(Test_automaton4, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        Test_Formulas = new JTextField();
        Test_Formulas.setText("~/testar/tests/formulas-abc-100.txt");
        alphaLTLPanel.add(Test_Formulas, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        LTLModelCheckButton = new JButton();
        LTLModelCheckButton.setText("LTL Model Check");
        alphaLTLPanel.add(LTLModelCheckButton, cc.xy(1, 9));
        button11 = new JButton();
        button11.setMinimumSize(new Dimension(30, 30));
        button11.setText("...");
        alphaLTLPanel.add(button11, cc.xy(5, 1));
        final JLabel label18 = new JLabel();
        label18.setText("Output File");
        alphaLTLPanel.add(label18, cc.xy(1, 7));
        resultsTxtTextField = new JTextField();
        resultsTxtTextField.setText("checkerOutput.txt");
        alphaLTLPanel.add(resultsTxtTextField, cc.xy(3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        button8 = new JButton();
        button8.setMinimumSize(new Dimension(30, 30));
        button8.setText("...");
        alphaLTLPanel.add(button8, cc.xy(5, 7));
        button12 = new JButton();
        button12.setMinimumSize(new Dimension(30, 30));
        button12.setText("...");
        alphaLTLPanel.add(button12, cc.xy(5, 5));
        final JLabel label19 = new JLabel();
        label19.setText("'Alive' proposition");
        alphaLTLPanel.add(label19, cc.xy(1, 3));
        AlivePropositionTextField = new JTextField();
        AlivePropositionTextField.setMaximumSize(new Dimension(70, 2147483647));
        AlivePropositionTextField.setPreferredSize(new Dimension(60, 38));
        AlivePropositionTextField.setText("!dead");
        AlivePropositionTextField.setToolTipText("Only when Finite semantics are needed");
        alphaLTLPanel.add(AlivePropositionTextField, cc.xy(3, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setPreferredSize(new Dimension(1, 20));
        scrollPane1.setRequestFocusEnabled(false);
        panel1.add(scrollPane1, cc.xyw(1, 3, 11, CellConstraints.FILL, CellConstraints.FILL));
        textArea12 = new JTextArea();
        textArea12.setEnabled(true);
        textArea12.setMinimumSize(new Dimension(1, 20));
        textArea12.setOpaque(false);
        textArea12.setPreferredSize(new Dimension(1, 20));
        scrollPane1.setViewportView(textArea12);
        clearButton = new JButton();
        clearButton.setHorizontalAlignment(0);
        clearButton.setMaximumSize(new Dimension(-1, -1));
        clearButton.setMinimumSize(new Dimension(-1, -1));
        clearButton.setPreferredSize(new Dimension(60, 38));
        clearButton.setText("Clr");
        clearButton.setToolTipText("Clear the Log");
        panel1.add(clearButton, cc.xyw(12, 3, 4, CellConstraints.DEFAULT, CellConstraints.CENTER));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


    private void initialize() {
        /// customize
        //CSS: alpha panels are for debugging only
        tabbedPane1.remove(alphaExplorePanel);
        tabbedPane1.remove(alphaLTLPanel);

    }

    //***********TESTAR generic panel code
    public void populateFrom(final Settings settings) {
        outputDir = settings.get(ConfigTags.OutputDir);
        // check if the output directory has a trailing line separator
        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
            outputDir += File.separator;
        }
        outputDir = outputDir + settings.get(ConfigTags.TemporalDirectory);
        //new File(outputDir).mkdirs();
        String runFolder = Helper.CurrentDateToFolder();
        outputDir = outputDir + File.separator;
        outputDir = outputDir + runFolder;
        new File(outputDir).mkdirs();
        outputDir = outputDir + File.separator;

        textField2.setText(settings.get(ConfigTags.TemporalLTLChecker));
        WSLCheckBox.setSelected(settings.get(ConfigTags.TemporalLTLCheckerWSL));
        verboseCheckBox.setSelected(settings.get(ConfigTags.TemporalLTLVerbose));
        textField7.setText(settings.get(ConfigTags.TemporalLTLOracles));
        textField5.setText(settings.get(ConfigTags.TemporalLTLPatterns));
        textField6.setText(settings.get(ConfigTags.TemporalLTLAPSelectorManager));
        textField3.setText(settings.get(ConfigTags.TemporalSpotFormulaParser));
        textFieldPythonEnvironment.setText(settings.get(ConfigTags.TemporalPythonEnvironment));
        textFieldPythonVisualizer.setText(settings.get(ConfigTags.TemporalVisualizerServer));
        VisualizerURL = settings.get(ConfigTags.TemporalVisualizerURL);
        VisualizerURLStop = settings.get(ConfigTags.TemporalVisualizerURLStop);

        // used here, but controlled on StateModelPanel
        dataStoreText = settings.get(ConfigTags.DataStore);
        dataStoreServerDNS = settings.get(ConfigTags.DataStoreServer);
        dataStoreDirectory = settings.get(ConfigTags.DataStoreDirectory);
        dataStoreDBText = settings.get(ConfigTags.DataStoreDB);
        dataStoreUser = settings.get(ConfigTags.DataStoreUser);
        dataStorePassword = settings.get(ConfigTags.DataStorePassword);
        dataStoreType = settings.get(ConfigTags.DataStoreType);

        Config config = new Config();
        config.setConnectionType(dataStoreType);
        config.setServer(dataStoreServerDNS);
        config.setDatabase(dataStoreDBText);
        config.setUser(dataStoreUser);
        config.setPassword(dataStorePassword);
        config.setDatabaseDirectory(dataStoreDirectory);


        tcontrol = new TemporalController(config, outputDir);
    }


    public void extractInformation(final Settings settings) {
        settings.set(ConfigTags.TemporalLTLChecker, textField2.getText());
        settings.set(ConfigTags.TemporalLTLCheckerWSL, WSLCheckBox.isSelected());
        settings.set(ConfigTags.TemporalLTLVerbose, verboseCheckBox.isSelected());
        //settings.set(ConfigTags.TemporalDirectory, textField10.getText());
        settings.set(ConfigTags.TemporalLTLOracles, textField7.getText());
        settings.set(ConfigTags.TemporalLTLAPSelectorManager, textField6.getText());

        settings.set(ConfigTags.TemporalSpotFormulaParser, textField3.getText());
        settings.set(ConfigTags.TemporalPythonEnvironment, textFieldPythonEnvironment.getText());
        settings.set(ConfigTags.TemporalVisualizerServer, textFieldPythonVisualizer.getText());
        //settings.set(ConfigTags.PythonVisualizerURL, textFieldPythonVisualizer.getText());
        //settings.set(ConfigTags.PythonVisualizerURLStop, textFieldPythonVisualizer.getText());

    }
    //***********TESTAR****************


    //******************Eventhandlers
    private void performLTLFormulaCheck(ActionEvent evt) {
        String cli = textField3.getText() + " '" + textField1.getText() + "'";
        Helper.RunOSChildProcess(cli);
    }


    private void LTLModelCheck(ActionEvent evt) {

        Helper.LTLModelCheck(textField2.getText(), Test_automaton4.getText(), Test_Formulas.getText(), AlivePropositionTextField.getText(), resultsTxtTextField.getText());
        textArea12.setText("invoking : \n");
        textArea12.setText(textField2.getText());
        textArea12.append("\n");
    }

    private void ModelCheck(ActionEvent e) {
        tcontrol.ModelCheck(TemporalType.LTL, textField2.getText(), textField6.getText(), textField7.getText(), verboseCheckBox.isSelected());
    }


    private void startTemporalWebAnalyzer(ActionEvent evt) {

        String cli = textFieldPythonEnvironment.getText() + " " + textFieldPythonVisualizer.getText();

        textArea12.setText("invoking : \n");
        textArea12.append(cli + "\n");
        // call the external program
        try {
            if (webAnalyzerProcess == null) {
                webAnalyzerProcess = Runtime.getRuntime().exec(cli);
                textArea12.append("Visualizer Started. goto " + VisualizerURL);
                textArea12.append("\n");
                Desktop desktop = Desktop.getDesktop();
                URI uri = new URI(VisualizerURL);
                desktop.browse(uri);

                webAnalyzerErr = new StreamConsumer(webAnalyzerProcess.getErrorStream(), "ERROR");
                webAnalyzerOut = new StreamConsumer(webAnalyzerProcess.getInputStream(), "OUTPUT");
                // kick them off
                webAnalyzerErr.start();
                webAnalyzerOut.start();

            } else {
                textArea12.append("Visualizer was already running. goto " + VisualizerURL + "\n");
            }
        } catch (Exception e) {
            System.err.println("Error on exec() method");
            textArea12.append("Error on exec() method\n");
            e.printStackTrace();
        }
    }

    private void stopTemporalWebAnalyzer(ActionEvent evt) {
        try {
            Helper.HTTPGet(VisualizerURLStop);

            boolean ret = false;
            // in case the python is invoked via a OS batch command , the above may leave the command running.
            // conversely: killing just the OS batch process, may leave then the python process( is a server0 running)
            if (webAnalyzerProcess != null) webAnalyzerProcess.waitFor(1, TimeUnit.SECONDS);
            if (webAnalyzerProcess != null) {
                webAnalyzerProcess.destroyForcibly();
                textArea12.append("Forcing Visualizer  to Stop.\n");
                ret = webAnalyzerProcess.waitFor(2, TimeUnit.SECONDS);  //gently wait
            }
            textArea12.append("Visualizer Stopped. (exitcode was : " + webAnalyzerProcess.exitValue() + ")\n");
            if (ret) webAnalyzerProcess = null;
            webAnalyzerErr.stop();
            webAnalyzerOut.stop();
        } catch (Exception e) {
            System.err.println("Error on stopping Analyzer");
            textArea12.append("Error on stopping Analyzer\n");

            e.printStackTrace();
        }

    }

    private void testdbconnection(ActionEvent evt) {
        try {
            tcontrol.dbReopen();
            textArea12.append(tcontrol.pingDB() + "\n");
            tcontrol.dbClose();
        } catch (Exception e) {
            System.err.println("Error on testing db");
            textArea12.append("Error on testing db\n");
            textArea12.append("\n");
            e.printStackTrace();
        }

    }

    private void exportTemporalmodel(ActionEvent evt) {
        try {
            textArea12.append("connecting to: db\n");
            //APSelectorManager APmgr = loadApSelectionManagerJSON(APSelectorManagerTESTJSONTextField.getText());
            tcontrol.loadApSelectorManager(APSelectorManagerTESTJSONTextField.getText());
            tcontrol.dbReopen();


            tcontrol.computeTemporalModel();
            tcontrol.saveModelAsJSON("APEncodedModel.json");
            tcontrol.saveModelAsHOA("LTL_model.hoa");

            List<TemporalOracle> fromcoll;
            String file = temporalOracleTESTCsvTextField.getText();
            fromcoll = CSVHandler.load(file, TemporalOracle.class);
            if (fromcoll == null) {
                textArea12.append("verify the file at location '" + file + "' \n");
            } else {
                textArea12.append("csv loaded: \n");
                tcontrol.setOracleColl(fromcoll);
                tcontrol.saveFormulaFiles(fromcoll, "LTL_formulas.txt");

                String[] fileparts = file.split("\\.");
                String strippedfile = file.substring(0, file.length() - fileparts[fileparts.length - 1].length() - 2);

                CSVHandler.save(fromcoll, strippedfile + "_inputvalidation.csv");
                textArea12.append("inputvalidation results csv saved: \n");

            }

            textArea12.append(" saving to file done\n");
            tcontrol.dbClose();
        } catch (Exception e) {
            System.err.println("Error on testing db");
            textArea12.append("Error on testing db\n");
            textArea12.append("\n");
            e.printStackTrace();
        }

    }

    private void testgraphml(ActionEvent evt) {
        try {

            textArea12.append("connecting to: db\n");
            tcontrol.dbReopen();
            textArea12.repaint();
            boolean res = tcontrol.saveToGraphMLFile(outputDir + "GraphML.XML");
            textArea12.append(" saving to  graphml file done with result:" + res + "\n\n");
            tcontrol.dbClose();
        } catch (Exception e) {
            System.err.println("Error on testing db");
            textArea12.append("Error on testing db\n");
            textArea12.append("\n");
            e.printStackTrace();
        }

    }

    public void testOracleCSV() {
        textArea12.append("performing a small test: writing an oracle to CSV file and read back\n");
        TemporalOracle to = TemporalOracle.getSampleOracle();
        List<TemporalOracle> tocoll = new ArrayList<>();
        tocoll.add(to);
        CSVHandler.save(tocoll, outputDir + "temporalOracleSample.csv");
        textArea12.append("csv saved: \n");
    }

    public void testPatternCSV() {
        textArea12.append("performing a small test: writing a pattern to CSV file\n");
        TemporalConstraintedPattern pat = TemporalConstraintedPattern.getSamplePattern();
        List<TemporalConstraintedPattern> patcoll = new ArrayList<>();
        patcoll.add(pat);
        CSVHandler.save(patcoll, outputDir + "temporalPatternSample.csv");
        textArea12.append("csv saved: ");
    }

    public void testSaveDefaultApSelectionManagerJSON() {
        textArea12.append("performing a small test: writing an Selectionmanager.JSON\n");
        APSelectorManager APmgr = new APSelectorManager(true);
        JSONHandler.save(APmgr, outputDir + "APSelectorManager.json", true);
        textArea12.append("json saved: \n");
    }


    public APSelectorManager loadApSelectionManagerJSON(String file) {

        APSelectorManager APmgr1;
        APmgr1 = (APSelectorManager) JSONHandler.load(file, APSelectorManager.class);

        if (APmgr1 == null) {
            textArea12.append("verify the file at location '" + file + "' \n");
            System.out.println("verify the file at location '" + file + "' \n");

        } else {
            textArea12.append("json loaded: \n");
            Set<WidgetFilter> wfset = APmgr1.getWidgetfilters();
            Iterator<WidgetFilter> wfiter = wfset.iterator();
            WidgetFilter wf = wfiter.next();
            textArea12.append("widgetroles that were read from file: " + wf.getWidgetRolesMatches().toString() + "\n");
        }
        return APmgr1;
    }

//*******************Eventhandlers


}
