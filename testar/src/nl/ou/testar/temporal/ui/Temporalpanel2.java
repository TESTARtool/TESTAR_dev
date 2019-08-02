package nl.ou.testar.temporal.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.StateModel.Settings.StateModelPanel;
import nl.ou.testar.temporal.behavior.TemporalController;
import nl.ou.testar.temporal.structure.*;
import nl.ou.testar.temporal.util.CSVHandler;
import nl.ou.testar.temporal.util.JSONHandler;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private StateModelPanel stateModelPanel;
    String dataStoreText;
    String dataStoreServerDNS;
    String dataStoreDirectory;
    String dataStoreDBText;
    String dataStoreUser;
    String dataStorePassword;
    String dataStoreType;


    String spotChecker;
    String spotFormulaParser;
    String pythonEnvironment;
    String pythonVisualizer;

    //**** custom
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTextArea textArea12;
    private JButton clearButton;
    private JTextField textField1;
    private JButton parseLTLFormulaButton;
    private JTextField textField5;
    private JTextField textField6;
    private JButton button1;
    private JButton button2;
    private JTextField textField7;
    private JButton button3;
    private JButton mineOraclesButton;
    private JTextField textField8;
    private JButton button4;
    private JButton startAnalyzerButton;
    private JButton stopAnalyzerButton;
    private JTextField textField9;
    private JButton button5;
    private JComboBox comboBox1;
    private JRadioButton graphDBModelRadioButton;
    private JRadioButton fileModelRadioButton;
    private JTextField textField10;
    private JTextField textField12;
    private JButton button6;
    private JButton AllFilesButton;
    private JButton reloadSettingsButton;
    private JButton modelCheckButton;
    private JButton sampleOracleButton;
    private JButton temporalModelButton;
    private JButton samplePatternButton;
    private JButton graphMLButton;
    private JButton defaultSelectorButton;
    private JButton testDBConnectionButton;
    private JButton writeSelectorButton;
    private JButton loadSelectorButton;
    private JButton traceCheckButton;
    private JTextField textField2;
    private JButton button7;
    private JCheckBox WSLPathCheckBox;
    private JTextField textField3;
    private JButton button8;
    private JCheckBox WSLPathCheckBox1;
    private JTextField textFieldPythonEnvironment;
    private JButton button9;
    private JTextField textFieldPythonVisualizer;
    private JButton button10;
    private JButton LTLModelCheckButton;
    private JTextField aTestarTestsTest_automaton4TextField;
    private JTextField textFieldVisualizersStop;
    private JTextField textFieldStartPort;

    public Temporalpanel2() {
        System.out.println("debug creating temporal panel2 instance");
        //super(); // init as a JPanel css
        parseLTLFormulaButton.addActionListener(this::performTemporalCheck);
        startAnalyzerButton.addActionListener(this::startTemporalWebAnalyzer);
        stopAnalyzerButton.addActionListener(this::stopTemporalWebAnalyzer);
        AllFilesButton.addActionListener(this::testdb);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea12.setText("cleared");
            }
        });
        reloadSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshsettings();
                textArea12.append("reloaded statemodel settings");
            }
        });
        sampleOracleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testOracleCSV();
            }
        });
        samplePatternButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testPatternCSV();
            }
        });
        graphMLButton.addActionListener(this::testgraphml);
        temporalModelButton.addActionListener(this::testtemporalmodel);
        defaultSelectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testSaveDefaultApSelectionManagerJSON();
            }
        });
        testDBConnectionButton.addActionListener(this::testdbconnection);
        loadSelectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                APSelectorManager apmgr = testLoadApSelectionManagerJSON();
                textArea12.append("loaded selector manager: " + apmgr.getComments().toString());
            }
        });
        writeSelectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testSaveCheckedApSelectionManagerJSON();
            }
        });
        modelCheckButton.addActionListener(this::LTLModelCheckWithSpot);
    }

    public static Temporalpanel2 createTemporalPanel() {
        Temporalpanel2 panel = new Temporalpanel2();
        panel.initialize();
        return panel;
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        panel1.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:4dlu:noGrow,center:d:grow"));
        panel1.setPreferredSize(new Dimension(621, 340));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2), null));
        final JSeparator separator1 = new JSeparator();
        CellConstraints cc = new CellConstraints();
        panel1.add(separator1, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setMinimumSize(new Dimension(617, 275));
        tabbedPane1.setPreferredSize(new Dimension(617, 275));
        tabbedPane1.setRequestFocusEnabled(true);
        panel1.add(tabbedPane1, cc.xyw(1, 3, 16));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        tabbedPane1.addTab("Setup", panel2);
        final JLabel label1 = new JLabel();
        label1.setText("SPOT Checker:");
        label1.setToolTipText("Used for LTL model check. Usually command to invoke spot_checker");
        panel2.add(label1, cc.xy(1, 1));
        textField2 = new JTextField();
        panel2.add(textField2, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        button7 = new JButton();
        button7.setText("...");
        panel2.add(button7, cc.xy(5, 1));
        WSLPathCheckBox = new JCheckBox();
        WSLPathCheckBox.setText("WSL path");
        panel2.add(WSLPathCheckBox, cc.xy(7, 1));
        final JLabel label2 = new JLabel();
        label2.setText("SPOT  Parser:");
        label2.setToolTipText("used for testing syntax of  a LTL formula");
        panel2.add(label2, cc.xy(1, 3));
        textField3 = new JTextField();
        panel2.add(textField3, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        button8 = new JButton();
        button8.setText("...");
        panel2.add(button8, cc.xy(5, 3));
        WSLPathCheckBox1 = new JCheckBox();
        WSLPathCheckBox1.setText("WSL path");
        panel2.add(WSLPathCheckBox1, cc.xy(7, 3));
        final JLabel label3 = new JLabel();
        label3.setText("Python Env. :");
        label3.setToolTipText("Path to Active Virtual environment");
        panel2.add(label3, cc.xy(1, 7));
        textFieldPythonEnvironment = new JTextField();
        panel2.add(textFieldPythonEnvironment, cc.xy(3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        button9 = new JButton();
        button9.setText("...");
        panel2.add(button9, cc.xy(5, 7));
        final JLabel label4 = new JLabel();
        label4.setText("Visualizer:");
        label4.setToolTipText("Usually this is the path to run.py");
        panel2.add(label4, cc.xy(1, 9));
        textFieldPythonVisualizer = new JTextField();
        panel2.add(textFieldPythonVisualizer, cc.xy(3, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        button10 = new JButton();
        button10.setText("...");
        panel2.add(button10, cc.xy(5, 9));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, cc.xy(1, 5, CellConstraints.DEFAULT, CellConstraints.FILL));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,fill:max(d;4px):noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        tabbedPane1.addTab("Test", panel3);
        textField1 = new JTextField();
        textField1.setText("G(a->Fb)");
        panel3.add(textField1, cc.xyw(3, 1, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        sampleOracleButton = new JButton();
        sampleOracleButton.setText("Sample Oracle");
        panel3.add(sampleOracleButton, cc.xy(3, 9));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, cc.xy(7, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        parseLTLFormulaButton = new JButton();
        parseLTLFormulaButton.setText("Parse LTL Formula");
        panel3.add(parseLTLFormulaButton, cc.xy(1, 1));
        final JLabel label5 = new JLabel();
        label5.setText("Generate Files:");
        panel3.add(label5, cc.xywh(1, 9, 1, 3));
        graphMLButton = new JButton();
        graphMLButton.setText("GraphML");
        panel3.add(graphMLButton, cc.xy(7, 11));
        reloadSettingsButton = new JButton();
        reloadSettingsButton.setEnabled(false);
        reloadSettingsButton.setText("Reload Settings");
        panel3.add(reloadSettingsButton, cc.xy(1, 7));
        samplePatternButton = new JButton();
        samplePatternButton.setText("Sample Pattern");
        panel3.add(samplePatternButton, cc.xy(3, 11));
        temporalModelButton = new JButton();
        temporalModelButton.setText("TemporalModel");
        temporalModelButton.setToolTipText("requires APSelectorManagerTEST.json file in directory temporal");
        panel3.add(temporalModelButton, cc.xy(7, 9));
        defaultSelectorButton = new JButton();
        defaultSelectorButton.setText("Default Selector");
        panel3.add(defaultSelectorButton, cc.xy(5, 11));
        AllFilesButton = new JButton();
        AllFilesButton.setText("All Files");
        AllFilesButton.setToolTipText("requires APSelectorManagerTEST.json file in directory temporal");
        panel3.add(AllFilesButton, cc.xy(5, 9));
        testDBConnectionButton = new JButton();
        testDBConnectionButton.setText("Test DB Connection");
        panel3.add(testDBConnectionButton, cc.xy(1, 5));
        writeSelectorButton = new JButton();
        writeSelectorButton.setText("Write Selector");
        panel3.add(writeSelectorButton, cc.xy(5, 15));
        loadSelectorButton = new JButton();
        loadSelectorButton.setText("Load Selector");
        panel3.add(loadSelectorButton, cc.xy(3, 15));
        LTLModelCheckButton = new JButton();
        LTLModelCheckButton.setText("LTL Model Check");
        panel3.add(LTLModelCheckButton, cc.xy(1, 3));
        aTestarTestsTest_automaton4TextField = new JTextField();
        aTestarTestsTest_automaton4TextField.setText(" --a ~/testar/tests/test_automaton4.txt --ff ~/testar/tests/formulas-abc-100.txt");
        panel3.add(aTestarTestsTest_automaton4TextField, cc.xyw(3, 3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(9, 13, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Miner", panel4);
        final JLabel label6 = new JLabel();
        label6.setText("Pattern File");
        panel4.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel4.add(spacer3, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textField5 = new JTextField();
        panel4.add(textField5, new GridConstraints(1, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator2 = new JSeparator();
        panel4.add(separator2, new GridConstraints(0, 1, 1, 12, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textField6 = new JTextField();
        panel4.add(textField6, new GridConstraints(4, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator3 = new JSeparator();
        panel4.add(separator3, new GridConstraints(5, 1, 1, 12, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textField7 = new JTextField();
        panel4.add(textField7, new GridConstraints(6, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Candidate Oracle");
        panel4.add(label7, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("TemporalType");
        panel4.add(label8, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBox1 = new JComboBox();
        panel4.add(comboBox1, new GridConstraints(7, 1, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textField10 = new JTextField();
        textField10.setEditable(false);
        panel4.add(textField10, new GridConstraints(3, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        graphDBModelRadioButton = new JRadioButton();
        graphDBModelRadioButton.setText("GraphDB Model");
        panel4.add(graphDBModelRadioButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileModelRadioButton = new JRadioButton();
        fileModelRadioButton.setText("File Model");
        panel4.add(fileModelRadioButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel4.add(spacer4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        modelCheckButton = new JButton();
        modelCheckButton.setText("Model Check");
        modelCheckButton.setToolTipText("Checks the Candidates on the Model. ");
        panel4.add(modelCheckButton, new GridConstraints(7, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mineOraclesButton = new JButton();
        mineOraclesButton.setText("Mine Oracles");
        mineOraclesButton.setToolTipText("Combines the Patterns and the Model to generate Potential Oracles. Then checks the Candidates on the Model. ");
        panel4.add(mineOraclesButton, new GridConstraints(7, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        button1 = new JButton();
        button1.setText("...");
        panel4.add(button1, new GridConstraints(1, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        button2 = new JButton();
        button2.setText("...");
        panel4.add(button2, new GridConstraints(4, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        button3 = new JButton();
        button3.setText("...");
        panel4.add(button3, new GridConstraints(6, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        traceCheckButton = new JButton();
        traceCheckButton.setText("Trace Check");
        traceCheckButton.setToolTipText("Checks the Candidates on the Model. ");
        panel4.add(traceCheckButton, new GridConstraints(7, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        tabbedPane1.addTab("Visualizer", panel5);
        final JLabel label9 = new JLabel();
        label9.setText("Input Oracle File");
        panel5.add(label9, cc.xy(1, 5));
        textField8 = new JTextField();
        panel5.add(textField8, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        button4 = new JButton();
        button4.setText("...");
        panel5.add(button4, cc.xy(7, 5));
        final JLabel label10 = new JLabel();
        label10.setText("GraphML File");
        panel5.add(label10, cc.xy(1, 7));
        textField9 = new JTextField();
        panel5.add(textField9, cc.xy(3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        button5 = new JButton();
        button5.setText("...");
        panel5.add(button5, cc.xy(7, 7));
        final Spacer spacer5 = new Spacer();
        panel5.add(spacer5, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JSeparator separator4 = new JSeparator();
        panel5.add(separator4, cc.xyw(3, 9, 6, CellConstraints.FILL, CellConstraints.FILL));
        final JSeparator separator5 = new JSeparator();
        panel5.add(separator5, cc.xyw(3, 3, 6, CellConstraints.FILL, CellConstraints.FILL));
        textField12 = new JTextField();
        panel5.add(textField12, cc.xy(3, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        button6 = new JButton();
        button6.setText("...");
        panel5.add(button6, cc.xy(7, 11));
        final JLabel label11 = new JLabel();
        label11.setText("Output Oracle File");
        panel5.add(label11, cc.xy(1, 11));
        stopAnalyzerButton = new JButton();
        stopAnalyzerButton.setText("Stop Analyzer");
        panel5.add(stopAnalyzerButton, cc.xy(1, 15));
        startAnalyzerButton = new JButton();
        startAnalyzerButton.setText("Start Analyzer");
        panel5.add(startAnalyzerButton, cc.xy(1, 13));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, cc.xywh(1, 5, 9, 4, CellConstraints.FILL, CellConstraints.FILL));
        textArea12 = new JTextArea();
        scrollPane1.setViewportView(textArea12);
        clearButton = new JButton();
        clearButton.setText("Clear");
        panel1.add(clearButton, cc.xy(13, 8));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(graphDBModelRadioButton);
        buttonGroup.add(fileModelRadioButton);
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

    }

    //***********TESTAR generic panel code
    public void populateFrom(final Settings settings) {

    /*    spotChecker= settings.get(ConfigTags.SpotChecker);
        spotFormulaParser= settings.get(ConfigTags.SpotFormulaParser);
        pythonEnvironment= settings.get(ConfigTags.PythonEnvironment);
        pythonVisualizer= settings.get(ConfigTags.PythonVisualizer);*/

        textField2.setText(settings.get(ConfigTags.SpotChecker));
        textField3.setText(settings.get(ConfigTags.SpotFormulaParser));
        textFieldPythonEnvironment.setText(settings.get(ConfigTags.PythonEnvironment));
        textFieldPythonVisualizer.setText(settings.get(ConfigTags.PythonVisualizer));
        textFieldStartPort.setText(settings.get(ConfigTags.PythonVisualizerPort));
        textFieldVisualizersStop.setText(settings.get(ConfigTags.PythonVisualizerStop));


        // used here, but controlled on StateModelPanel
        dataStoreText = settings.get(ConfigTags.DataStore);
        dataStoreServerDNS = settings.get(ConfigTags.DataStoreServer);
        dataStoreDirectory = settings.get(ConfigTags.DataStoreDirectory);
        dataStoreDBText = settings.get(ConfigTags.DataStoreDB);
        dataStoreUser = settings.get(ConfigTags.DataStoreUser);
        dataStorePassword = settings.get(ConfigTags.DataStorePassword);
        dataStoreType = settings.get(ConfigTags.DataStoreType);


        outputDir = settings.get(ConfigTags.OutputDir);
        // check if the output directory has a trailing line separator
        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
            outputDir += File.separator;
        }
        outputDir = outputDir + "temporal";
        new File(outputDir).mkdirs();
        outputDir = outputDir + File.separator;
    }

    public void refreshsettings() {
        Settings dbsettings = new Settings();
        stateModelPanel.extractInformation(dbsettings);
        populateFrom(dbsettings);

    }

    public void extractInformation(final Settings settings) {
        settings.set(ConfigTags.SpotChecker, textField2.getText());
        settings.set(ConfigTags.SpotFormulaParser, textField3.getText());
        settings.set(ConfigTags.PythonEnvironment, textFieldPythonEnvironment.getText());
        settings.set(ConfigTags.PythonVisualizer, textFieldPythonVisualizer.getText());
        settings.set(ConfigTags.PythonVisualizerPort, textFieldStartPort.getText());
        settings.set(ConfigTags.PythonVisualizerStop, textFieldVisualizersStop.getText());


    }
    //***********TESTAR****************


    //******************Eventhandlers
    private void performTemporalCheck(ActionEvent evt) {
        // code goes here
        Process theProcess = null;
        BufferedReader inStream = null;
        BufferedReader errStream = null;
        String cli = textField3.getText() + " '" + textField1.getText() + "'";
        String response;
        String errorresponse = null;
        textArea12.setText("invoking : ");
        textArea12.append("\n");
        textArea12.setText(cli);
        textArea12.append("\n");
        // call the external program
        try {
            theProcess = Runtime.getRuntime().exec(cli);
        } catch (IOException e) {
            System.err.println("Error on exec() method");
            textArea12.append("Error on exec() method");
            textArea12.append("\n");
            e.printStackTrace();
        }

        // read from the called program's standard output stream
        try {
            inStream = new BufferedReader(new InputStreamReader
                    (theProcess.getInputStream()));
            errStream = new BufferedReader(new InputStreamReader
                    (theProcess.getErrorStream()));
            while ((response = inStream.readLine()) != null || (errorresponse = errStream.readLine()) != null) {
                if (response != null) {
                    System.out.println("response: " + response);
                    textArea12.append(response);
                }

                if (errorresponse != null) {
                    System.out.println("error response: " + errorresponse);
                    textArea12.append(errorresponse);
                }

                textArea12.append("\n");
            }

            textArea12.append("------------Action completed---------------" + "\n");
        } catch (IOException e) {
            System.err.println("Error on inStream.readLine()");
            textArea12.append("Error on inStream.readLine()");
            textArea12.append("\n");
            e.printStackTrace();
        }
    }

    private void LTLModelCheckWithSpot(ActionEvent evt) {
        // code goes here
        Process theProcess = null;
        BufferedReader inStream = null;
        BufferedReader errStream = null;

        //String cli = "ubuntu1804 run ~/testar/spot_checker'" + "--a " +"/mnt/c/Users/c/git/TESTAR_dev/testar/target/install/testar/bin/output/temporal/APmodel.HOA" + "--f ";
        String cli = textField2.getText() + " --a ~/testar/tests/test_automaton4.txt --ff ~/testar/tests/formulas-abc-100.txt";

        String response;
        String errorresponse = null;

        textArea12.setText("invoking : ");
        textArea12.append("\n");
        textArea12.setText(cli);
        textArea12.append("\n");
        // call the external program
        try {
            theProcess = Runtime.getRuntime().exec(cli);
        } catch (IOException e) {
            System.err.println("Error on exec() method");
            textArea12.append("Error on exec() method");
            textArea12.append("\n");
            e.printStackTrace();
        }

        // read from the called program's standard output stream
        try {
            inStream = new BufferedReader(new InputStreamReader
                    (theProcess.getInputStream()));
            errStream = new BufferedReader(new InputStreamReader
                    (theProcess.getErrorStream()));
            while ((response = inStream.readLine()) != null || (errorresponse = errStream.readLine()) != null) {
                if (response != null) {
                    System.out.println("response: " + response);
                    textArea12.append(response);
                }

                if (errorresponse != null) {
                    System.out.println("error response: " + errorresponse);
                    textArea12.append(errorresponse);
                }

                textArea12.append("\n");
            }

            textArea12.append("------------Action completed---------------" + "\n");
        } catch (IOException e) {
            System.err.println("Error on inStream.readLine()");
            textArea12.append("Error on inStream.readLine()");
            textArea12.append("\n");
            e.printStackTrace();
        }
    }

    private void startTemporalWebAnalyzer(ActionEvent evt) {
        // code goes here


        BufferedReader inStream = null;
        String cli_part1 = "python C:\\Users\\c\\git\\Testar_viz\\run.py";
        String cli = "C:\\Users\\c\\Anaconda3\\condabin\\conda.bat activate" + " && " + cli_part1;

        String response;
        textArea12.setText("invoking : ");
        textArea12.append("\n");
        textArea12.append(cli_part1);
        textArea12.append("\n");
        // call the external program
        try {
            if (webAnalyzerProcess == null) {
                webAnalyzerProcess = Runtime.getRuntime().exec(cli_part1);
                textArea12.append("Visualizer Started. goto http://localhost:8050");
                textArea12.append("\n");
                Desktop desktop = Desktop.getDesktop();
                URI uri = new URI("http://localhost:8050");
                desktop.browse(uri);
                // any error message?
                try {
                    StreamConsumer errorConsumer = new
                            StreamConsumer(webAnalyzerProcess.getErrorStream(), "ERROR");


                    // any output?
                    StreamConsumer outputConsumer = new
                            StreamConsumer(webAnalyzerProcess.getInputStream(), "OUTPUT");


                    // kick them off
                    errorConsumer.start();
                    outputConsumer.start();

                    // any error???
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } else {
                textArea12.append("Visualizer was already running. goto http://localhost:8050");
                textArea12.append("\n");
            }
        } catch (Exception e) {
            System.err.println("Error on exec() method");
            textArea12.append("Error on exec() method");
            textArea12.append("\n");
            e.printStackTrace();
        }
    }

    private void stopTemporalWebAnalyzer(ActionEvent evt) {
        try {
            if (webAnalyzerProcess != null) {
                webAnalyzerProcess.destroyForcibly();
                textArea12.append("Forcing Visualizer  to Stop.");
                boolean ret = webAnalyzerProcess.waitFor(5, TimeUnit.SECONDS);  //gently wait
                if (ret) {
                    webAnalyzerProcess = null;
                }
                textArea12.append("Visualizer Stopped. (exitcode was : " + ret + ")");
                textArea12.append("\n");
            }
        } catch (Exception e) {
            System.err.println("Error on stopping");
            textArea12.append("Error on stopping");
            textArea12.append("\n");
            e.printStackTrace();
        }

    }

    private void testdb(ActionEvent evt) {
        try {
            testOracleCSV();
            testPatternCSV();
            testSaveDefaultApSelectionManagerJSON();

            testtemporalmodel(null);
            testgraphml(null);

            textArea12.append("\n");

        } catch (Exception e) {
            System.err.println("Error on testing db");
            textArea12.append("Error on testing db\n");
            textArea12.append("\n");
            e.printStackTrace();
        }

    }

    private void testdbconnection(ActionEvent evt) {
        try {

            Config config = new Config();
            config.setConnectionType(dataStoreType);
            config.setServer(dataStoreServerDNS);
            config.setDatabase(dataStoreDBText);
            config.setUser(dataStoreUser);
            config.setPassword(dataStorePassword);

            String tmp = dataStoreDirectory;
            textArea12.append("connecting to: db\n");
            textArea12.repaint();
            config.setDatabaseDirectory(tmp);
            TemporalController tcontrol = new TemporalController(config, outputDir);
            textArea12.append(tcontrol.pingDB());
            textArea12.append("\n");
            tcontrol.shutdown();
        } catch (Exception e) {
            System.err.println("Error on testing db");
            textArea12.append("Error on testing db\n");
            textArea12.append("\n");
            e.printStackTrace();
        }

    }

    private void testtemporalmodel(ActionEvent evt) {
        try {

            APSelectorManager APmgr = testLoadApSelectionManagerJSON();

            Config config = new Config();
            config.setConnectionType(dataStoreType);
            config.setServer(dataStoreServerDNS);
            config.setDatabase(dataStoreDBText);
            config.setUser(dataStoreUser);
            config.setPassword(dataStorePassword);

            String tmp = dataStoreDirectory;
            textArea12.append("connecting to: db\n");
            textArea12.repaint();
            config.setDatabaseDirectory(tmp);
            TemporalController tcontrol = new TemporalController(config, outputDir);

            TemporalModel tmodel;
            tcontrol.computeTemporalModel(APmgr);
            tmodel = tcontrol.gettModel();
            JSONHandler.save(tmodel, outputDir + "APEncodedModel.json");
            textArea12.append(" saving to file done\n");
            tcontrol.shutdown();
        } catch (Exception e) {
            System.err.println("Error on testing db");
            textArea12.append("Error on testing db\n");
            textArea12.append("\n");
            e.printStackTrace();
        }

    }

    private void testgraphml(ActionEvent evt) {
        try {
            Config config = new Config();
            config.setConnectionType(dataStoreType);
            config.setServer(dataStoreServerDNS);
            config.setDatabase(dataStoreDBText);
            config.setUser(dataStoreUser);
            config.setPassword(dataStorePassword);

            String tmp = dataStoreDirectory;
            textArea12.append("connecting to: db\n");
            textArea12.repaint();
            config.setDatabaseDirectory(tmp);
            TemporalController tcontrol = new TemporalController(config, outputDir);
            boolean res = tcontrol.saveToGraphMLFile(outputDir + "GraphML.XML");
            textArea12.append(" saving to  graphml file done with result:" + res + "\n");
            textArea12.append("\n");
            tcontrol.shutdown();
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

        List<TemporalOracle> fromcoll;
        fromcoll = CSVHandler.load(outputDir + "temporalOracle3.csv", TemporalOracle.class);
        if (fromcoll == null) {
            textArea12.append("place a file called 'temporalOracle3.csv' in the directory: " + outputDir + "\n");
        } else {
            textArea12.append("csv loaded: \n");
            textArea12.append("Formalism that was read from file: " + fromcoll.get(0).getTemporalFormalism() + "\n");
            CSVHandler.save(fromcoll, outputDir + "temporalOracle2.csv");
            textArea12.append("csv saved: \n");

        }
    }

    public void testPatternCSV() {
        textArea12.append("performing a small test: writing a pattern to CSV file\n");


        TemporalConstraintedPattern pat = TemporalConstraintedPattern.getSamplePattern();
        List<TemporalConstraintedPattern> patcoll = new ArrayList<>();
        patcoll.add(pat);

        CSVHandler.save(patcoll, outputDir + "temporalPatternSample.csv");
        textArea12.append("csv saved: ");

        List<TemporalConstraintedPattern> fromcoll;
        fromcoll = CSVHandler.load(outputDir + "temporalPattern1.csv", TemporalConstraintedPattern.class);
        if (fromcoll == null) {
            textArea12.append("place a file called 'temporalPattern1.csv' in the directory: " + outputDir + "\n");
        } else {
            textArea12.append("csv loaded: \n");
            textArea12.append("pattern that was read from file: " + fromcoll.get(0).getTemporalFormalism() + "\n");
            textArea12.append("widgetrole constraints that was read from file: " + fromcoll.get(0).getWidgetRoleParameterConstraints().toString() + "\n");

            CSVHandler.save(fromcoll, outputDir + "temporalPattern2.csv");
            textArea12.append("csv saved: \n");

        }

    }

    public void testSaveDefaultApSelectionManagerJSON() {
        textArea12.append("performing a small test: writing an Selectionmanager.JSON\n");

        APSelectorManager APmgr = new APSelectorManager(true);
        JSONHandler.save(APmgr, outputDir + "APSelectorManager.json", true);
        textArea12.append("json saved: \n");
    }

    public void testSaveCheckedApSelectionManagerJSON() {
        textArea12.append(" writing  APSelectorManagerChecked.JSON\n");

        APSelectorManager APmgr = new APSelectorManager(true);
        JSONHandler.save(APmgr, outputDir + "APSelectorManagerChecked.json", true);
        textArea12.append("json saved: \n");
    }

    public APSelectorManager testLoadApSelectionManagerJSON() {


        APSelectorManager APmgr1;
        APmgr1 = (APSelectorManager) JSONHandler.load(outputDir + "APSelectorManagerTEST.json", APSelectorManager.class);

        if (APmgr1 == null) {
            textArea12.append("place a file called 'APSelectorManagerTEST.json' in the directory: " + outputDir + "\n");
            System.out.println("place a file called 'APSelectorManagerTEST.json' in the directory: " + outputDir + "\n");

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
