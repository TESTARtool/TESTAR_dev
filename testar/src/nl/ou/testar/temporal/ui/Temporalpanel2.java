package nl.ou.testar.temporal.ui;

import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import nl.ou.testar.temporal.behavior.TemporalController;
import nl.ou.testar.temporal.structure.*;
import nl.ou.testar.temporal.util.*;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
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
    private JButton temporalModelButton;

    private JTextField textField2;
    private JCheckBox WSLCheckBox;
    private JTextField textField3;
    private JTextField textFieldPythonEnvironment;
    private JButton button9;
    private JTextField textFieldPythonVisualizer;
    private JButton button10;


    private JButton testDbButton;
    private JButton parseLTL;

    private JComboBox comboBox2;

    private JTextField APSelectorManagerTESTJSONTextField;
    private JTextField temporalOracleTESTCsvTextField;
    private JButton defaultSelectorButton1;
    private JButton sampleOracleButton1;
    private JButton graphMLButton1;
    private JCheckBox verboseCheckBox;
    private JPanel setupPanel;
    private JPanel minerPanel;
    private JPanel visualizerPanel;
    private JPanel alphaExplorePanel;
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


        temporalModelButton.addActionListener(new ActionListener() {
                                                  @Override
                                                  public void actionPerformed(ActionEvent e) {
                                                      exportTemporalmodel(e);
                                                  }
                                              }
        );


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


        panel1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                //     stateModelPanel.extractInformation(settings);
            }
        });
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
        panel1.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:227px:noGrow,fill:8px:noGrow,right:max(p;42px):noGrow,left:4dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow,top:199px:noGrow,top:161px:noGrow"));
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
        panel1.add(tabbedPane1, cc.xyw(1, 2, 14));
        setupPanel = new JPanel();
        setupPanel.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,left:330px:noGrow,left:4dlu:noGrow,right:95px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:noGrow,top:6dlu:noGrow,center:d:noGrow,top:4dlu:noGrow,center:d:noGrow"));
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
        minerPanel.setLayout(new FormLayout("left:117px:noGrow,left:4dlu:noGrow,fill:146px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:37px:noGrow,left:4dlu:noGrow,left:4dlu:noGrow,fill:41px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:5dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,fill:d:noGrow,left:4dlu:noGrow,left:57px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:38dlu:noGrow", "center:41px:noGrow,center:10dlu:noGrow,center:max(d;4px):noGrow,top:5dlu:noGrow,center:d:noGrow,top:4dlu:noGrow,center:24dlu:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
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
        modelCheckButton.setToolTipText("<html>Perform a ModelCheck of the Potential Oracles. <BR>\nThe model is computed by applying the filters from the APSelectorManager to a model of the graph DB <BR>\nthe model is chosen by criteria: Application Name and version and Abstrations. <BR><BR>\nEnsure that the parameter values on General panel and State model panel are saved before invoking this function!!! </html> ");
        minerPanel.add(modelCheckButton, cc.xyw(12, 5, 12));
        generateButton = new JButton();
        generateButton.setEnabled(false);
        generateButton.setHorizontalTextPosition(0);
        generateButton.setMaximumSize(new Dimension(81, 30));
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
        minerPanel.add(comboBox2, cc.xyw(24, 1, 6, CellConstraints.LEFT, CellConstraints.DEFAULT));
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
        graphMLButton1.setMaximumSize(new Dimension(83, 38));
        graphMLButton1.setPreferredSize(new Dimension(83, 38));
        graphMLButton1.setText("GraphML");
        graphMLButton1.setToolTipText("<html>Make a model from the graphDB. Requires APSelectorManagerTEST.json file in directory temporal.\n <BR><BR>\nEnsure that the parameter values on General panel and State model panel are saved before invoking this function!!! </html> ");
        minerPanel.add(graphMLButton1, cc.xyw(18, 7, 11, CellConstraints.LEFT, CellConstraints.DEFAULT));
        temporalModelButton = new JButton();
        temporalModelButton.setPreferredSize(new Dimension(78, 38));
        temporalModelButton.setText("Model");
        temporalModelButton.setToolTipText("<html>Exports/transforms the first model from the graphDB  into intermediate (JSON) format and then to HOA format. HOA can be loaded in LTL model checker SPOT. <br>Requires APSelectorManager file for filtering and<br>oracle file to instantiate LTL formulas\n <BR><BR>\nEnsure that the parameter values on General panel and State model panel are saved before invoking this function!!! </html> ");
        minerPanel.add(temporalModelButton, cc.xy(29, 7, CellConstraints.LEFT, CellConstraints.DEFAULT));
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
        alphaExplorePanel.setLayout(new FormLayout("fill:143px:noGrow,left:4dlu:noGrow,fill:d:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:5dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,fill:31px:noGrow,left:5dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,left:4dlu:noGrow,fill:d:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:max(m;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,center:max(d;4px):noGrow"));
        alphaExplorePanel.setEnabled(false);
        alphaExplorePanel.setFocusable(false);
        alphaExplorePanel.setVisible(false);
        tabbedPane1.addTab("Test", alphaExplorePanel);
        textField1 = new JTextField();
        textField1.setText("G(a->Fb)");
        textField1.setVisible(true);
        alphaExplorePanel.add(textField1, cc.xyw(3, 1, 21, CellConstraints.FILL, CellConstraints.DEFAULT));
        parseLTL = new JButton();
        parseLTL.setHideActionText(false);
        parseLTL.setText("Parse LTL Formula");
        parseLTL.setVisible(true);
        alphaExplorePanel.add(parseLTL, cc.xy(1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        testDbButton = new JButton();
        testDbButton.setText("Test DB Connection");
        alphaExplorePanel.add(testDbButton, cc.xy(1, 8, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JLabel label12 = new JLabel();
        label12.setText("LTL FormulaParser:");
        label12.setToolTipText("Used for LTL model check. the usual command to invoke is:  spot_checker");
        alphaExplorePanel.add(label12, cc.xy(1, 5));
        textField3 = new JTextField();
        alphaExplorePanel.add(textField3, cc.xyw(2, 5, 24, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setMaximumSize(new Dimension(1, 24));
        scrollPane1.setPreferredSize(new Dimension(1, 10));
        scrollPane1.setRequestFocusEnabled(false);
        scrollPane1.setVerticalScrollBarPolicy(22);
        panel1.add(scrollPane1, cc.xyw(1, 3, 9, CellConstraints.FILL, CellConstraints.FILL));
        textArea12 = new JTextArea();
        textArea12.setEnabled(true);
        textArea12.setMaximumSize(new Dimension(1, 20));
        textArea12.setMinimumSize(new Dimension(1, 30));
        textArea12.setOpaque(false);
        textArea12.setPreferredSize(new Dimension(1, 30));
        textArea12.setRows(0);
        scrollPane1.setViewportView(textArea12);
        clearButton = new JButton();
        clearButton.setHorizontalAlignment(0);
        clearButton.setMaximumSize(new Dimension(-1, -1));
        clearButton.setMinimumSize(new Dimension(-1, -1));
        clearButton.setPreferredSize(new Dimension(60, 38));
        clearButton.setText("Clr");
        clearButton.setToolTipText("Clear the Log");
        panel1.add(clearButton, cc.xyw(11, 3, 4, CellConstraints.DEFAULT, CellConstraints.CENTER));
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
        // tabbedPane1.remove(alphaExplorePanel);
        // tabbedPane1.remove(alphaLTLPanel);

    }

    //***********TESTAR generic panel code
    public void populateFrom(final Settings settings) {


        textField2.setText(settings.get(ConfigTags.TemporalLTLChecker));
        WSLCheckBox.setSelected(settings.get(ConfigTags.TemporalLTLCheckerWSL));
        verboseCheckBox.setSelected(settings.get(ConfigTags.TemporalLTLVerbose));
        enableTemporalOfflineOraclesCheckBox.setSelected(settings.get(ConfigTags.TemporalEnabled));
        textField7.setText(settings.get(ConfigTags.TemporalLTLOracles));
        textField5.setText(settings.get(ConfigTags.TemporalLTLPatterns));
        textField6.setText(settings.get(ConfigTags.TemporalLTLAPSelectorManager));
        textField3.setText(settings.get(ConfigTags.TemporalSpotFormulaParser));
        textFieldPythonEnvironment.setText(settings.get(ConfigTags.TemporalPythonEnvironment));
        textFieldPythonVisualizer.setText(settings.get(ConfigTags.TemporalVisualizerServer));
        VisualizerURL = settings.get(ConfigTags.TemporalVisualizerURL);
        VisualizerURLStop = settings.get(ConfigTags.TemporalVisualizerURLStop);
        tcontrol = new TemporalController(settings); // look for better location
    }


    public void extractInformation(final Settings settings) {
        settings.set(ConfigTags.TemporalLTLChecker, textField2.getText());
        settings.set(ConfigTags.TemporalLTLCheckerWSL, WSLCheckBox.isSelected());
        settings.set(ConfigTags.TemporalLTLVerbose, verboseCheckBox.isSelected());
        settings.set(ConfigTags.TemporalEnabled, enableTemporalOfflineOraclesCheckBox.isSelected());
        settings.set(ConfigTags.TemporalLTLOracles, textField7.getText());
        settings.set(ConfigTags.TemporalLTLAPSelectorManager, textField6.getText());
        settings.set(ConfigTags.TemporalSpotFormulaParser, textField3.getText());
        settings.set(ConfigTags.TemporalPythonEnvironment, textFieldPythonEnvironment.getText());
        settings.set(ConfigTags.TemporalVisualizerServer, textFieldPythonVisualizer.getText());

    }
    //***********TESTAR****************


    //******************Eventhandlers
    private void performLTLFormulaCheck(ActionEvent evt) {
        String cli = textField3.getText() + " '" + textField1.getText() + "'";
        Helper.RunOSChildProcess(cli);
        textArea12.append("check console output for the result\n");
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
        textArea12.append(tcontrol.pingDB() + "\n");

    }

    private void exportTemporalmodel(ActionEvent evt) {
        tcontrol.dumpTemporalModel(textField6.getText());

    }

    private void testgraphml(ActionEvent evt) {
        try {

            textArea12.append("connecting to: db\n");
            tcontrol.dbReopen();
            textArea12.repaint();
            boolean res;
            res = tcontrol.saveToGraphMLFile("GraphML.XML", false);
            res = tcontrol.saveToGraphMLFile( "GraphML_NoWidgets.XML", true);

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
        TemporalSampleOracle to = TemporalSampleOracle.getSampleLTLOracle();
        List<TemporalSampleOracle> tocoll = new ArrayList<>();
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
        tcontrol.setDefaultAPSelectormanager();
        tcontrol.saveAPSelectorManager("APSelectorManager.json");
        // APSelectorManager APmgr = tcontrol.new APSelectorManager(true);
        //   JSONHandler.save(APmgr, outputDir + "APSelectorManager.json", true);
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
