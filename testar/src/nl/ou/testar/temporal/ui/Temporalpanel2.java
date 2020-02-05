package nl.ou.testar.temporal.ui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import nl.ou.testar.temporal.behavior.TemporalController;
import nl.ou.testar.temporal.structure.*;
import nl.ou.testar.temporal.util.*;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.fruit.monkey.ConfigTags.TemporalGeneratorTactics;

public class Temporalpanel2 {
    //****custom
    private Process webAnalyzerProcess = null;
    private String VisualizerURL;
    private String VisualizerURLStop;
    private StreamConsumer webAnalyzerErr;
    private StreamConsumer webAnalyzerOut;
    TemporalController tcontrol;
    private String outputDir;
    //**** custom


    private JPanel mainTemporalPanel;
    private JTabbedPane containerTab;
    private JTextArea logArea;
    private JButton clearBtn;
    private JTextField patternFile;
    private JTextField ApSelectorManagerFile;
    private JButton selectFilePatterns;
    private JButton selectFileOracles;
    private JTextField oracleFile;
    private JButton selectFileApSelectorManager;
    private JButton generateBtn;
    private JButton startAnalyzerBtn;
    private JButton stopAnalyzerBtn;
    private JButton modelCheckBtn;
    private JButton ModelOnlyBtn;
    private JTextField spotLTLChecker;
    private JCheckBox WSLCheckBoxLTLSpot;
    private JTextField itsCTLChecker;
    private JTextField PythonEnv_Path;
    private JButton selectFilePython_ENV;
    private JTextField PythonVisualizer_Path;
    private JButton selectFilePython_VIZ;
    private JButton testDbButton;
    private JComboBox tacticComboBox;
    private JButton defaultSelectorBtn;
    private JButton sampleOracleBtn;
    private JButton graphMLBtn;
    private JCheckBox verboseCheckBox;
    private JPanel setupPanel;
    private JPanel minerPanel;
    private JPanel visualizerPanel;
    private JCheckBox enableTemporalOfflineOraclesCheckBox;
    private JCheckBox enforceAbstractionEquality;
    private JTextField patternConstraintsFile;
    private JCheckBox WSLCheckBoxCTLITS;
    private JCheckBox instrumentDeadlockStatesCheckBox;
    private JCheckBox WSLCheckBoxLTLITS;
    private JTextField itsLTLChecker;
    private JCheckBox CounterExamples;
    private JButton selectFilePatternConstraints;

    public Temporalpanel2() {
        $$$setupUI$$$();


        startAnalyzerBtn.addActionListener(this::startTemporalWebAnalyzer);
        stopAnalyzerBtn.addActionListener(this::stopTemporalWebAnalyzer);
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("cleared");
            }
        });
        ModelOnlyBtn.addActionListener(new ActionListener() {
               @Override
        public void actionPerformed(ActionEvent e) {
            exportTemporalmodel(e);
        }
    }
        );
        testDbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testdbconnection(e);
            }
        });
        selectFilePython_ENV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooserHelper(PythonEnv_Path);
            }
        });
        selectFilePython_VIZ.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooserHelper(PythonVisualizer_Path);
            }
        });
        selectFileApSelectorManager.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooserHelper(ApSelectorManagerFile);
            }
        });
        selectFileOracles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooserHelper(oracleFile);
            }
        });
        modelCheckBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ModelCheck(e);
            }
        });
        defaultSelectorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testSaveDefaultApSelectionManagerJSON();
            }
        });
        sampleOracleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testOracleCSV();
                testPatternCSV();
                testPatternConstraintCSV();
            }
        });
        graphMLBtn.addActionListener(this::testgraphml);
        generateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateOracles(e);
            }
        });
        selectFilePatterns.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooserHelper(patternFile);
            }
        });
        selectFilePatternConstraints.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooserHelper(patternConstraintsFile);
            }
        });
    }

    public static Temporalpanel2 createTemporalPanel() {
        Temporalpanel2 panel = new Temporalpanel2();
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
        mainTemporalPanel = new JPanel();
        mainTemporalPanel.setLayout(new FormLayout("right:245px:grow,left:4dlu:noGrow,fill:45px:noGrow,left:7dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:195px:noGrow,fill:8px:noGrow,right:max(p;42px):noGrow,left:4dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow,top:257px:noGrow,top:82px:noGrow"));
        mainTemporalPanel.setPreferredSize(new Dimension(621, 340));
        mainTemporalPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2), null));
        final JSeparator separator1 = new JSeparator();
        CellConstraints cc = new CellConstraints();
        mainTemporalPanel.add(separator1, cc.xyw(1, 1, 3, CellConstraints.FILL, CellConstraints.FILL));
        containerTab = new JTabbedPane();
        containerTab.setMinimumSize(new Dimension(617, 275));
        containerTab.setPreferredSize(new Dimension(617, 275));
        containerTab.setRequestFocusEnabled(true);
        containerTab.setVisible(true);
        mainTemporalPanel.add(containerTab, cc.xyw(1, 2, 16));
        setupPanel = new JPanel();
        setupPanel.setLayout(new FormLayout("fill:118px:noGrow,left:4dlu:noGrow,left:149px:noGrow,left:4dlu:noGrow,fill:104px:noGrow,left:6dlu:noGrow,right:66px:noGrow,fill:116px:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:noGrow,top:4dlu:noGrow,center:37px:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        setupPanel.setVisible(false);
        containerTab.addTab("Setup", setupPanel);
        final JLabel label1 = new JLabel();
        label1.setText("SPOT LTL Checker:");
        label1.setToolTipText("Used for LTL model check. the usual command to invoke is:  spot_checker");
        setupPanel.add(label1, cc.xy(1, 5));
        spotLTLChecker = new JTextField();
        spotLTLChecker.setToolTipText("<html>Command to invoke the SPOT-based LTL model checker<br>\n(counterexamples will be computed)");
        setupPanel.add(spotLTLChecker, cc.xyw(3, 5, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        WSLCheckBoxLTLSpot = new JCheckBox();
        WSLCheckBoxLTLSpot.setText("WSL?");
        WSLCheckBoxLTLSpot.setToolTipText("<html> Does this command need a WSL path?<br> \ne.g. starting with \"/mnt/C/...\"<br>\nWhen ticked then input files for the modelchecker are converted automatically.\n</html>");
        setupPanel.add(WSLCheckBoxLTLSpot, cc.xy(7, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JLabel label2 = new JLabel();
        label2.setText("ITS CTL Checker:");
        label2.setToolTipText("Used for CTL model check. the usual command to invoke is:  its-ctl");
        setupPanel.add(label2, cc.xy(1, 7));
        itsCTLChecker = new JTextField();
        itsCTLChecker.setToolTipText("<html>Command to invoke the ITS-based CTL model checker.<br>\n(no visualization of counterexample possible)</html>");
        setupPanel.add(itsCTLChecker, cc.xyw(3, 7, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        enableTemporalOfflineOraclesCheckBox = new JCheckBox();
        enableTemporalOfflineOraclesCheckBox.setText("Enable Temporal Offline Oracles?");
        enableTemporalOfflineOraclesCheckBox.setToolTipText("Temporal oracles are automatically evaluated after each TESTAR run by using the settigns supplied in this form.");
        setupPanel.add(enableTemporalOfflineOraclesCheckBox, cc.xyw(1, 1, 3));
        enforceAbstractionEquality = new JCheckBox();
        enforceAbstractionEquality.setSelected(true);
        enforceAbstractionEquality.setText("Enforce Abstraction equality");
        enforceAbstractionEquality.setToolTipText("<html>Concrete Abstraction attributes in new models are the same as on the Abstract Layer.<br>\n(this overrules the ConcreteStateAttributes parameter in the settings file and <br>\nthe default setting that uses ALL StateAttributes as ConcreteStateAttributes)<br>\nIt is advised to leave this setting enabled.</html>");
        setupPanel.add(enforceAbstractionEquality, cc.xyw(1, 3, 4));
        verboseCheckBox = new JCheckBox();
        verboseCheckBox.setSelected(true);
        verboseCheckBox.setText("Verbose?");
        verboseCheckBox.setToolTipText("<html> When checked: keeps the intermediate files on disk ,<br>else these files are deleted after the Checker has run.\"\n</html>");
        setupPanel.add(verboseCheckBox, cc.xy(5, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        instrumentDeadlockStatesCheckBox = new JCheckBox();
        instrumentDeadlockStatesCheckBox.setText("Instrument deadlock states?");
        instrumentDeadlockStatesCheckBox.setToolTipText("<html> In case that the Graph-model has deadlock states (~ no outgoing transitions):<BR>\n1. A new state with atomic property 'dead==true' with a selfloop is added<BR>\n2. forall deadlock states, a transition will be added to that newly created state.<BR>\nThe additions wil be added to the model that will be provided to the model-checker.<BR>\n(not the Graph-model) </html>");
        setupPanel.add(instrumentDeadlockStatesCheckBox, cc.xyw(5, 3, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
        testDbButton = new JButton();
        testDbButton.setText("Show DB Models");
        setupPanel.add(testDbButton, cc.xy(8, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));
        WSLCheckBoxCTLITS = new JCheckBox();
        WSLCheckBoxCTLITS.setText("WSL?");
        WSLCheckBoxCTLITS.setToolTipText("<html> Does this command need a WSL path?<br> \ne.g. starting with \"/mnt/C/...\"<br>\nWhen ticked then input files for the modelchecker are converted automatically.\n</html>");
        setupPanel.add(WSLCheckBoxCTLITS, cc.xy(7, 7, CellConstraints.LEFT, CellConstraints.DEFAULT));
        itsLTLChecker = new JTextField();
        itsLTLChecker.setText("");
        itsLTLChecker.setToolTipText("<html>Command to invoke the ITS-based LTL model checker<br>\n(counterexamples are computed in the raw output, but not visualized)");
        setupPanel.add(itsLTLChecker, cc.xyw(3, 9, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        WSLCheckBoxLTLITS = new JCheckBox();
        WSLCheckBoxLTLITS.setText("WSL?");
        WSLCheckBoxLTLITS.setToolTipText("<html> Does this command need a WSL path?<br> \ne.g. starting with \"/mnt/C/...\"<br>\nWhen ticked then input files for the modelchecker are converted automatically.\n</html>");
        setupPanel.add(WSLCheckBoxLTLITS, cc.xy(7, 9, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JLabel label3 = new JLabel();
        label3.setText("ITS LTL Checker:");
        label3.setToolTipText("Used for LTL model check. the usual command to invoke is:  its-ltl");
        setupPanel.add(label3, cc.xy(1, 9));
        CounterExamples = new JCheckBox();
        CounterExamples.setText("Counter Examples?");
        CounterExamples.setToolTipText("<html>Produce traces of counter examples (when verdict=FAIL) or witness (when verdict=PASS).<BR>\n(not all implemented modelcheckers can produce such traces) </html>");
        setupPanel.add(CounterExamples, cc.xyw(7, 1, 2, CellConstraints.LEFT, CellConstraints.DEFAULT));
        minerPanel = new JPanel();
        minerPanel.setLayout(new FormLayout("left:132px:noGrow,fill:133px:noGrow,fill:37px:noGrow,fill:43px:noGrow,fill:max(d;4px):noGrow,fill:max(d;4px):noGrow,fill:max(d;4px):noGrow,left:9dlu:noGrow,fill:max(d;4px):noGrow,fill:d:noGrow,left:90px:noGrow,left:40dlu:noGrow,left:33dlu:noGrow,fill:max(d;4px):noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,center:41px:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,center:4dlu:noGrow,center:max(d;4px):noGrow,top:5dlu:noGrow,center:42px:noGrow"));
        containerTab.addTab("Miner", minerPanel);
        final JLabel label4 = new JLabel();
        label4.setText("Oracles:");
        minerPanel.add(label4, cc.xy(1, 10, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        final JLabel label5 = new JLabel();
        label5.setText("Oracle Patterns:");
        minerPanel.add(label5, cc.xy(1, 8, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        oracleFile = new JTextField();
        minerPanel.add(oracleFile, cc.xyw(2, 10, 2, CellConstraints.FILL, CellConstraints.DEFAULT));
        patternFile = new JTextField();
        patternFile.setText("");
        minerPanel.add(patternFile, cc.xyw(2, 8, 2, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label6 = new JLabel();
        label6.setText("APSelectorManager:");
        minerPanel.add(label6, cc.xy(1, 4, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        ApSelectorManagerFile = new JTextField();
        minerPanel.add(ApSelectorManagerFile, cc.xyw(2, 4, 2, CellConstraints.FILL, CellConstraints.DEFAULT));
        selectFileOracles = new JButton();
        selectFileOracles.setText("...");
        minerPanel.add(selectFileOracles, cc.xy(4, 10));
        selectFilePatterns = new JButton();
        selectFilePatterns.setText("...");
        minerPanel.add(selectFilePatterns, cc.xy(4, 8));
        selectFileApSelectorManager = new JButton();
        selectFileApSelectorManager.setText("...");
        minerPanel.add(selectFileApSelectorManager, cc.xy(4, 4));
        modelCheckBtn = new JButton();
        modelCheckBtn.setHorizontalTextPosition(0);
        modelCheckBtn.setText("Model Check");
        modelCheckBtn.setToolTipText("<html>Perform a ModelCheck of the Potential Oracles. <BR>\nThe model is computed by applying the filters from the APSelectorManager to a model of the graph DB <BR>\nthe model is chosen by criteria: Application Name and version and Abstrations. <BR><BR>\nEnsure that the parameter values on General panel and State model panel are saved before invoking this function!!! </html> ");
        minerPanel.add(modelCheckBtn, cc.xyw(5, 10, 7));
        generateBtn = new JButton();
        generateBtn.setEnabled(true);
        generateBtn.setHorizontalTextPosition(0);
        generateBtn.setMaximumSize(new Dimension(81, 30));
        generateBtn.setText("<html>Generate</html>");
        generateBtn.setToolTipText("<html>Instantiated the Pattern parameters with Atomic Propositions (AP's) from the Model to generate (Potential) Oracles. \n<BR>The list of  AP's in the model is  computed by applying the filters from the APSelectorManager to a transformed model of the graph DB </html>");
        minerPanel.add(generateBtn, cc.xyw(5, 8, 7));
        final JLabel label7 = new JLabel();
        label7.setText("Pattern Constraints:");
        minerPanel.add(label7, cc.xy(1, 6, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        patternConstraintsFile = new JTextField();
        minerPanel.add(patternConstraintsFile, cc.xyw(2, 6, 2, CellConstraints.FILL, CellConstraints.DEFAULT));
        selectFilePatternConstraints = new JButton();
        selectFilePatternConstraints.setText("...");
        minerPanel.add(selectFilePatternConstraints, cc.xy(4, 6));
        tacticComboBox = new JComboBox();
        tacticComboBox.setEnabled(true);
        tacticComboBox.setMaximumSize(new Dimension(130, 38));
        tacticComboBox.setMinimumSize(new Dimension(130, 38));
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("1");
        defaultComboBoxModel1.addElement("5");
        defaultComboBoxModel1.addElement("10");
        defaultComboBoxModel1.addElement("50");
        defaultComboBoxModel1.addElement("100");
        defaultComboBoxModel1.addElement("500");
        defaultComboBoxModel1.addElement("1000");
        tacticComboBox.setModel(defaultComboBoxModel1);
        tacticComboBox.setPreferredSize(new Dimension(150, 38));
        tacticComboBox.setToolTipText("<HTML>tactic to generate oracles from the supplied Pattern collection.<BR>\nShows the number of potential oracles to generate per pattern\n</HTML>");
        tacticComboBox.setVerifyInputWhenFocusTarget(true);
        minerPanel.add(tacticComboBox, cc.xy(12, 8, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        final JLabel label8 = new JLabel();
        label8.setText("Generate:");
        minerPanel.add(label8, cc.xy(1, 3, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        defaultSelectorBtn = new JButton();
        defaultSelectorBtn.setMaximumSize(new Dimension(130, 38));
        defaultSelectorBtn.setMinimumSize(new Dimension(130, 38));
        defaultSelectorBtn.setPreferredSize(new Dimension(130, 38));
        defaultSelectorBtn.setText("Default Selector");
        defaultSelectorBtn.setToolTipText("Generate a default APSelectionManager");
        minerPanel.add(defaultSelectorBtn, cc.xy(2, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
        sampleOracleBtn = new JButton();
        sampleOracleBtn.setPreferredSize(new Dimension(110, 38));
        sampleOracleBtn.setText("Sample Oracle");
        sampleOracleBtn.setToolTipText("Generate files with a sample Oracle and a sample Pattern.");
        sampleOracleBtn.putClientProperty("html.disable", Boolean.FALSE);
        minerPanel.add(sampleOracleBtn, cc.xyw(3, 3, 2, CellConstraints.LEFT, CellConstraints.CENTER));
        graphMLBtn = new JButton();
        graphMLBtn.setMaximumSize(new Dimension(83, 38));
        graphMLBtn.setPreferredSize(new Dimension(90, 38));
        graphMLBtn.setText("GraphML");
        graphMLBtn.setToolTipText("<html>Make a model from the graphDB. Requires APSelectorManagerTEST.json file in directory temporal.\n <BR><BR>\nEnsure that the parameter values on General panel and State model panel are saved before invoking this function!!! </html> ");
        minerPanel.add(graphMLBtn, cc.xy(11, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
        ModelOnlyBtn = new JButton();
        ModelOnlyBtn.setPreferredSize(new Dimension(78, 38));
        ModelOnlyBtn.setText("Model");
        ModelOnlyBtn.setToolTipText("<html>Exports/transforms the first model from the graphDB  into intermediate (JSON) format and then to HOA format. HOA can be loaded in LTL model checker SPOT. <br>Requires APSelectorManager file for filtering and<br>oracle file to instantiate LTL formulas\n <BR><BR>\nEnsure that the parameter values on General panel and State model panel are saved before invoking this function!!! </html> ");
        minerPanel.add(ModelOnlyBtn, cc.xy(12, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
        visualizerPanel = new JPanel();
        visualizerPanel.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,left:100dlu:noGrow,left:4dlu:noGrow,left:128px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:49px:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        containerTab.addTab("Visualizer", visualizerPanel);
        PythonEnv_Path = new JTextField();
        visualizerPanel.add(PythonEnv_Path, cc.xyw(3, 1, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label9 = new JLabel();
        label9.setText("Python Env. :");
        label9.setToolTipText("Path to Active Virtual environment");
        visualizerPanel.add(label9, cc.xy(1, 1));
        selectFilePython_ENV = new JButton();
        selectFilePython_ENV.setText("...");
        visualizerPanel.add(selectFilePython_ENV, cc.xy(7, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        PythonVisualizer_Path = new JTextField();
        visualizerPanel.add(PythonVisualizer_Path, cc.xyw(3, 3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label10 = new JLabel();
        label10.setText("Visualizer:");
        label10.setToolTipText("Usually this is the path to run.py");
        visualizerPanel.add(label10, cc.xy(1, 3));
        selectFilePython_VIZ = new JButton();
        selectFilePython_VIZ.setText("...");
        visualizerPanel.add(selectFilePython_VIZ, cc.xy(7, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
        startAnalyzerBtn = new JButton();
        startAnalyzerBtn.setText("Start Analyzer");
        visualizerPanel.add(startAnalyzerBtn, cc.xy(1, 5));
        stopAnalyzerBtn = new JButton();
        stopAnalyzerBtn.setText("Stop Analyzer");
        visualizerPanel.add(stopAnalyzerBtn, cc.xy(5, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));
        clearBtn = new JButton();
        clearBtn.setHorizontalAlignment(0);
        clearBtn.setMaximumSize(new Dimension(-1, -1));
        clearBtn.setMinimumSize(new Dimension(-1, -1));
        clearBtn.setPreferredSize(new Dimension(60, 38));
        clearBtn.setText("Clr");
        clearBtn.setToolTipText("Clear the Log");
        mainTemporalPanel.add(clearBtn, cc.xyw(13, 3, 4, CellConstraints.DEFAULT, CellConstraints.CENTER));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainTemporalPanel.add(scrollPane1, cc.xyw(1, 3, 11, CellConstraints.FILL, CellConstraints.FILL));
        logArea = new JTextArea();
        logArea.setEnabled(true);
        logArea.setMaximumSize(new Dimension(1, 20));
        logArea.setMinimumSize(new Dimension(1, 30));
        logArea.setOpaque(false);
        logArea.setPreferredSize(new Dimension(1, 30));
        logArea.setRows(0);
        scrollPane1.setViewportView(logArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainTemporalPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


    //***********TESTAR generic panel code
    public void populateFrom(final Settings settings) {
        spotLTLChecker.setText(settings.get(ConfigTags.TemporalLTL_SPOTChecker));
        WSLCheckBoxLTLSpot.setSelected(settings.get(ConfigTags.TemporalLTL_SPOTCheckerWSL));
        itsCTLChecker.setText(settings.get(ConfigTags.TemporalCTL_ITSChecker));
        WSLCheckBoxCTLITS.setSelected(settings.get(ConfigTags.TemporalCTL_ITSCheckerWSL));
        itsLTLChecker.setText(settings.get(ConfigTags.TemporalLTL_ITSChecker));
        WSLCheckBoxLTLITS.setSelected(settings.get(ConfigTags.TemporalLTL_ITSCheckerWSL));
        instrumentDeadlockStatesCheckBox.setSelected(settings.get(ConfigTags.TemporalInstrumentDeadlockState));
        verboseCheckBox.setSelected(settings.get(ConfigTags.TemporalVerbose));
        CounterExamples.setSelected(settings.get(ConfigTags.TemporalCounterExamples));
        enableTemporalOfflineOraclesCheckBox.setSelected(settings.get(ConfigTags.TemporalOffLineEnabled));
        enforceAbstractionEquality.setSelected(settings.get(ConfigTags.TemporalConcreteEqualsAbstract));
        oracleFile.setText(settings.get(ConfigTags.TemporalOracles));
        patternFile.setText(settings.get(ConfigTags.TemporalPatterns));
        ApSelectorManagerFile.setText(settings.get(ConfigTags.TemporalAPSelectorManager));
        patternConstraintsFile.setText(settings.get(ConfigTags.TemporalPatternConstraints));
        String[] comboBoxLabels = settings.get(TemporalGeneratorTactics).stream().filter(Objects::nonNull).toArray(String[]::new);
        DefaultComboBoxModel cbModel = new DefaultComboBoxModel(comboBoxLabels); // read only
        tacticComboBox.setModel(cbModel);
        PythonEnv_Path.setText(settings.get(ConfigTags.TemporalPythonEnvironment));
        PythonVisualizer_Path.setText(settings.get(ConfigTags.TemporalVisualizerServer));
        VisualizerURL = settings.get(ConfigTags.TemporalVisualizerURL);
        VisualizerURLStop = settings.get(ConfigTags.TemporalVisualizerURLStop);
        if (outputDir != null && !outputDir.equals("")) {// when triggered by save button on the general panel
            tcontrol = new TemporalController(settings, outputDir);// look for better location
        } else
            tcontrol = new TemporalController(settings);
        outputDir = tcontrol.getOutputDir();
    }


    public void extractInformation(final Settings settings) {
        settings.set(ConfigTags.TemporalLTL_SPOTChecker, spotLTLChecker.getText());
        settings.set(ConfigTags.TemporalLTL_SPOTCheckerWSL, WSLCheckBoxLTLSpot.isSelected());
        settings.set(ConfigTags.TemporalVerbose, verboseCheckBox.isSelected());
        settings.set(ConfigTags.TemporalCounterExamples, CounterExamples.isSelected());
        settings.set(ConfigTags.TemporalOffLineEnabled, enableTemporalOfflineOraclesCheckBox.isSelected());
        settings.set(ConfigTags.TemporalConcreteEqualsAbstract, enforceAbstractionEquality.isSelected());
        settings.set(ConfigTags.TemporalOracles, oracleFile.getText());
        settings.set(ConfigTags.TemporalAPSelectorManager, ApSelectorManagerFile.getText());
        settings.set(ConfigTags.TemporalPatternConstraints, patternConstraintsFile.getText());
        settings.set(ConfigTags.TemporalCTL_ITSChecker, itsCTLChecker.getText());
        settings.set(ConfigTags.TemporalCTL_ITSCheckerWSL, WSLCheckBoxCTLITS.isSelected());
        settings.set(ConfigTags.TemporalLTL_ITSChecker, itsLTLChecker.getText());
        settings.set(ConfigTags.TemporalLTL_ITSCheckerWSL, WSLCheckBoxLTLITS.isSelected());
        settings.set(ConfigTags.TemporalInstrumentDeadlockState, instrumentDeadlockStatesCheckBox.isSelected());
        settings.set(ConfigTags.TemporalPythonEnvironment, PythonEnv_Path.getText());
        settings.set(ConfigTags.TemporalVisualizerServer, PythonVisualizer_Path.getText());

    }

    //******************Eventhandlers

    private void chooserHelper(JTextField textfield) {
        JFileChooser fd = new JFileChooser();
        fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fd.setCurrentDirectory(new File(textfield.getText()).getParentFile());
        if (fd.showOpenDialog(mainTemporalPanel) == JFileChooser.APPROVE_OPTION) {
            String file = fd.getSelectedFile().getAbsolutePath();
            textfield.setText(file);
        }
    }

    private void ModelCheck(ActionEvent e) {

        tcontrol.MCheck(
                ApSelectorManagerFile.getText(),
                oracleFile.getText(),
                verboseCheckBox.isSelected(),
                CounterExamples.isSelected(),
                instrumentDeadlockStatesCheckBox.isSelected(),
                spotLTLChecker.getText(), WSLCheckBoxLTLSpot.isSelected(),
                itsCTLChecker.getText(), WSLCheckBoxCTLITS.isSelected(),
                itsLTLChecker.getText(), WSLCheckBoxLTLITS.isSelected());
    }

    private void generateOracles(ActionEvent e) {

        tcontrol.generateOraclesFromPatterns(
                ApSelectorManagerFile.getText(),
                patternFile.getText(),
                patternConstraintsFile.getText(),
                Integer.parseInt(tacticComboBox.getSelectedItem().toString()));
    }


    private void startTemporalWebAnalyzer(ActionEvent evt) {
        String cli = PythonEnv_Path.getText() + " " + PythonVisualizer_Path.getText();
        logArea.setText("invoking : \n");
        logArea.append(cli + "\n");
        // call the external program
        try {
            if (webAnalyzerProcess == null) {
                webAnalyzerProcess = Runtime.getRuntime().exec(cli);
                logArea.append("Visualizer Started. goto " + VisualizerURL);
                logArea.append("\n");
                Desktop desktop = Desktop.getDesktop();
                URI uri = new URI(VisualizerURL);
                desktop.browse(uri);
                webAnalyzerErr = new StreamConsumer(webAnalyzerProcess.getErrorStream(), "ERROR");
                webAnalyzerOut = new StreamConsumer(webAnalyzerProcess.getInputStream(), "OUTPUT");
                // kick them off
                webAnalyzerErr.start();
                webAnalyzerOut.start();

            } else {
                logArea.append("Visualizer was already running. goto " + VisualizerURL + "\n");
            }
        } catch (Exception e) {
            System.err.println("Error on exec() method");
            logArea.append("Error on exec() method\n");
            e.printStackTrace();
        }
    }

    private void stopTemporalWebAnalyzer(ActionEvent evt) {
        try {
            Helper.HTTPGet(VisualizerURLStop);

            boolean ret = false;
            // in case the python is invoked via a OS batch command , the above may leave the command running.
            // equivalently:  killing just the OS batch process, may leave then the python process( which is a server) running)
            if (webAnalyzerProcess != null) webAnalyzerProcess.waitFor(1, TimeUnit.SECONDS);
            if (webAnalyzerProcess != null) {
                webAnalyzerProcess.destroyForcibly();
                logArea.append("Forcing Visualizer  to Stop.\n");
                ret = webAnalyzerProcess.waitFor(2, TimeUnit.SECONDS);  //gently wait
            }
            logArea.append("Visualizer Stopped. (exitcode was : " + webAnalyzerProcess.exitValue() + ")\n");
            if (ret) webAnalyzerProcess = null;
            webAnalyzerErr.stop();
            webAnalyzerOut.stop();
        } catch (Exception e) {
            System.err.println("Error on stopping Analyzer");
            logArea.append("Error on stopping Analyzer\n");

            e.printStackTrace();
        }

    }

    private void testdbconnection(ActionEvent evt) {
        logArea.append(tcontrol.pingDB() + "\n");
    }

    private void exportTemporalmodel(ActionEvent evt) {
        tcontrol.makeTemporalModel(ApSelectorManagerFile.getText(), verboseCheckBox.isSelected(), true);
    }

    private void testgraphml(ActionEvent evt) {
        try {

            logArea.append("connecting to: db\n");
            logArea.repaint();
            boolean res;
            res = tcontrol.saveToGraphMLFile("GraphML.XML", false);
            res = tcontrol.saveToGraphMLFile("GraphML_NoWidgets.XML", true);

            logArea.append(" saving to  graphml file done with result:" + res + "\n\n");
            //tcontrol.dbClose();
        } catch (Exception e) {
            System.err.println("Error on connecting to db");
            logArea.append("Error on connecting to db\n");
            logArea.append("\n");
            e.printStackTrace();
        }

    }

    public void testOracleCSV() {
        logArea.append("test: writing an oracle to CSV file\n");
        TemporalOracle to = TemporalOracle.getSampleLTLOracle();
        List<TemporalOracle> tocoll = new ArrayList<>();
        tocoll.add(to);
        CSVHandler.save(tocoll, outputDir + "temporalOracleSample.csv");
        logArea.append("csv saved: \n");
    }

    public void testPatternCSV() {
        logArea.append("test: writing a pattern to CSV file\n");
        TemporalPattern pat = TemporalPattern.getSamplePattern();
        List<TemporalPattern> patcoll = new ArrayList<>();
        patcoll.add(pat);
        CSVHandler.save(patcoll, outputDir + "temporalPatternSample.csv");
        logArea.append("csv saved: ");
    }

    public void testPatternConstraintCSV() {
        logArea.append("test: writing a pattern-constraint to CSV file\n");
        List<TemporalPatternConstraint> patconstraintcoll = TemporalPatternConstraint.getSampleConstraints();
        CSVHandler.save(patconstraintcoll, outputDir + "temporalPatternConstraintSample.csv");
        logArea.append("csv saved: ");
    }

    public void testSaveDefaultApSelectionManagerJSON() {
        logArea.append("test: writing a APSelectionManager.JSON\n");
        tcontrol.setDefaultAPSelectormanager();
        tcontrol.saveAPSelectorManager("APSelectorManager_Default.json");
        logArea.append("json saved: \n");
    }

//*******************Eventhandlers

}
