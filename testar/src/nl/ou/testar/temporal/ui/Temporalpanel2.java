package nl.ou.testar.temporal.ui;

import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.StateModel.Settings.StateModelPanel;
import nl.ou.testar.temporal.behavior.TemporalController;
import nl.ou.testar.temporal.structure.APSelectorManager;
import nl.ou.testar.temporal.structure.TemporalConstraintedPattern;
import nl.ou.testar.temporal.structure.TemporalOracle;
import nl.ou.testar.temporal.structure.WidgetFilter;
import nl.ou.testar.temporal.util.CSVHandler;
import nl.ou.testar.temporal.util.JSONHandler;
import nl.ou.testar.temporal.util.Spot_CheckerResultsParser;
import nl.ou.testar.temporal.util.TemporalType;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private JTextField textField5;
    private JTextField textField6;
    private JButton button1;
    private JButton button2;
    private JTextField textField7;
    private JButton button3;
    private JButton generateOraclesButton;
    private JTextField textField8;
    private JButton button4;
    private JButton startAnalyzerButton;
    private JButton stopAnalyzerButton;
    private JTextField textField9;
    private JButton button5;
    private JComboBox comboBox1;
    private JLabel potentialOraclesLabel;
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
    private JTextField AlivepropositionTextField;
    private JButton testDbButton;
    private JButton parseLTL;
    private JTextField textField4;
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

    public Temporalpanel2() {
        $$$setupUI$$$();
        System.out.println("debug creating temporal panel2 instance");
        //super(); // init as a JPanel css

        startAnalyzerButton.addActionListener(this::startTemporalWebAnalyzer);
        stopAnalyzerButton.addActionListener(this::stopTemporalWebAnalyzer);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea12.setText("cleared");
            }
        });
/*        reloadSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshsettings();
                textArea12.append("reloaded statemodel settings");
            }
        });*/
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
                                                      //testgraphml(e);
                                                      testtemporalmodel(e);
                                                  }
                                              }
        );
        defaultSelectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testSaveDefaultApSelectionManagerJSON();
            }
        });


        LTLModelCheckButton.addActionListener(this::LTLModelCheckWithSpot);
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
                performTemporalCheck(e);
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
                if (((String) comboBox1.getSelectedItem()).equals("LTL")) {
                    ModelCheck(e);
                } else
                    textArea12.append("TemporalType: " + (String) comboBox1.getSelectedItem() + " is not implemented");
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
        panel1.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:227px:grow,left:6dlu:noGrow,fill:10px:noGrow,left:32dlu:noGrow,right:max(p;42px):noGrow,left:4dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow,top:283px:noGrow,top:161px:noGrow"));
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
        panel1.add(tabbedPane1, cc.xyw(1, 2, 16));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,left:330px:noGrow,left:4dlu:noGrow,right:95px:noGrow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:noGrow,top:4dlu:noGrow,center:d:noGrow"));
        panel2.setVisible(false);
        tabbedPane1.addTab("Setup", panel2);
        final JLabel label1 = new JLabel();
        label1.setText("LTL Checker:");
        label1.setToolTipText("Used for LTL model check. the usual command to invoke is:  spot_checker");
        panel2.add(label1, cc.xy(1, 1));
        textField2 = new JTextField();
        panel2.add(textField2, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label2 = new JLabel();
        label2.setText("LTL  Parser:");
        label2.setToolTipText("used for testing syntax of  a LTL formula");
        panel2.add(label2, cc.xy(1, 3));
        textField3 = new JTextField();
        panel2.add(textField3, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label3 = new JLabel();
        label3.setText("Python Env. :");
        label3.setToolTipText("Path to Active Virtual environment");
        panel2.add(label3, cc.xy(1, 5));
        textFieldPythonEnvironment = new JTextField();
        panel2.add(textFieldPythonEnvironment, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        button9 = new JButton();
        button9.setText("...");
        panel2.add(button9, cc.xy(5, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JLabel label4 = new JLabel();
        label4.setText("Visualizer:");
        label4.setToolTipText("Usually this is the path to run.py");
        panel2.add(label4, cc.xy(1, 7));
        textFieldPythonVisualizer = new JTextField();
        panel2.add(textFieldPythonVisualizer, cc.xy(3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        button10 = new JButton();
        button10.setText("...");
        panel2.add(button10, cc.xy(5, 7, CellConstraints.LEFT, CellConstraints.DEFAULT));
        WSLCheckBox = new JCheckBox();
        WSLCheckBox.setText("WSL?");
        WSLCheckBox.setToolTipText("<html> Does this command need a WSL path?<br> \ne.g. starting with \"/mnt/C/...\"<br>\nWhen ticked then input files for the modelchecker are converted automatically.\n</html>");
        panel2.add(WSLCheckBox, cc.xy(5, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FormLayout("fill:143px:noGrow,left:4dlu:noGrow,fill:d:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:5dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:31px:noGrow,left:5dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:15px:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:max(m;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,center:max(d;4px):noGrow"));
        panel3.setVisible(true);
        tabbedPane1.addTab("Alpha Explore", panel3);
        testDbButton = new JButton();
        testDbButton.setText("Test DB Connection");
        panel3.add(testDbButton, cc.xy(1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        parseLTL = new JButton();
        parseLTL.setHideActionText(false);
        parseLTL.setText("Parse LTL Formula");
        parseLTL.setVisible(true);
        panel3.add(parseLTL, cc.xy(1, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JLabel label5 = new JLabel();
        label5.setText("Generate Files:");
        panel3.add(label5, cc.xywh(1, 5, 1, 3));
        defaultSelectorButton = new JButton();
        defaultSelectorButton.setText("Default Selector");
        defaultSelectorButton.setToolTipText("Generate default APSelectionManager");
        panel3.add(defaultSelectorButton, cc.xywh(3, 5, 1, 4, CellConstraints.LEFT, CellConstraints.DEFAULT));
        sampleOracleButton = new JButton();
        sampleOracleButton.setText("Sample Oracle");
        panel3.add(sampleOracleButton, cc.xywh(5, 5, 1, 4, CellConstraints.LEFT, CellConstraints.DEFAULT));
        graphMLButton = new JButton();
        graphMLButton.setText("GraphML");
        graphMLButton.setToolTipText("Make a model from the graphDB. Requires APSelectorManagerTEST.json file in directory temporal");
        panel3.add(graphMLButton, cc.xywh(9, 5, 2, 4, CellConstraints.LEFT, CellConstraints.DEFAULT));
        textField1 = new JTextField();
        textField1.setText("G(a->Fb)");
        textField1.setVisible(true);
        panel3.add(textField1, cc.xyw(3, 3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        temporalModelButton = new JButton();
        temporalModelButton.setText("Temporal Prepare");
        temporalModelButton.setToolTipText("<html>Exports/transforms the first model from the graphDB  into intermediate (JSON) format and then to HOA format. HOA can be loaded in LTL model checker SPOT. <br>Requires APSelectorManager file for filtering and<br>oracle file to instantiate LTL formulas");
        panel3.add(temporalModelButton, cc.xyw(16, 12, 2, CellConstraints.LEFT, CellConstraints.DEFAULT));
        loadTestSelectorButton = new JButton();
        loadTestSelectorButton.setText("...");
        panel3.add(loadTestSelectorButton, cc.xy(14, 10));
        APSelectorManagerTESTJSONTextField = new JTextField();
        APSelectorManagerTESTJSONTextField.setText("APSelectorManagerTEST.JSON");
        panel3.add(APSelectorManagerTESTJSONTextField, cc.xyw(3, 10, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label6 = new JLabel();
        label6.setText("APSelectorManager");
        panel3.add(label6, cc.xy(1, 10));
        loadOracleTESTCSVButton = new JButton();
        loadOracleTESTCSVButton.setText("...");
        panel3.add(loadOracleTESTCSVButton, cc.xy(14, 12));
        temporalOracleTESTCsvTextField = new JTextField();
        temporalOracleTESTCsvTextField.setText("temporalOracleTEST.csv");
        panel3.add(temporalOracleTESTCsvTextField, cc.xyw(3, 12, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label7 = new JLabel();
        label7.setText("Raw Oracles");
        panel3.add(label7, cc.xy(1, 12));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,left:330px:noGrow,left:4dlu:noGrow,fill:31px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel4.setVisible(false);
        tabbedPane1.addTab("Alpha LTL", panel4);
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, cc.xy(5, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label8 = new JLabel();
        label8.setText("HOAModel File");
        panel4.add(label8, cc.xy(1, 1));
        final JLabel label9 = new JLabel();
        label9.setText("LTLFormula File");
        panel4.add(label9, cc.xy(1, 3));
        final JLabel label10 = new JLabel();
        label10.setText("'Alive' proposition");
        panel4.add(label10, cc.xy(1, 5));
        Test_automaton4 = new JTextField();
        Test_automaton4.setText("~/testar/tests/test_automaton4.txt");
        panel4.add(Test_automaton4, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        Test_Formulas = new JTextField();
        Test_Formulas.setText("~/testar/tests/formulas-abc-100.txt");
        panel4.add(Test_Formulas, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        AlivepropositionTextField = new JTextField();
        AlivepropositionTextField.setMaximumSize(new Dimension(70, 2147483647));
        AlivepropositionTextField.setPreferredSize(new Dimension(60, 38));
        AlivepropositionTextField.setText("!dead");
        AlivepropositionTextField.setToolTipText("Only when Finite semantics are needed");
        panel4.add(AlivepropositionTextField, cc.xy(3, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));
        LTLModelCheckButton = new JButton();
        LTLModelCheckButton.setText("LTL Model Check");
        panel4.add(LTLModelCheckButton, cc.xy(1, 9));
        button11 = new JButton();
        button11.setMinimumSize(new Dimension(30, 30));
        button11.setText("...");
        panel4.add(button11, cc.xy(5, 1));
        final JLabel label11 = new JLabel();
        label11.setText("Output File");
        panel4.add(label11, cc.xy(1, 7));
        resultsTxtTextField = new JTextField();
        resultsTxtTextField.setText("checkerOutput.txt");
        panel4.add(resultsTxtTextField, cc.xy(3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        button8 = new JButton();
        button8.setMinimumSize(new Dimension(30, 30));
        button8.setText("...");
        panel4.add(button8, cc.xy(5, 7));
        button12 = new JButton();
        button12.setMinimumSize(new Dimension(30, 30));
        button12.setText("...");
        panel4.add(button12, cc.xy(5, 3));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FormLayout("left:117px:noGrow,left:4dlu:noGrow,fill:p:grow,left:4dlu:noGrow,fill:37px:noGrow,left:4dlu:noGrow,left:4dlu:noGrow,fill:41px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:5dlu:noGrow,fill:d:grow,fill:d:grow,right:38px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,right:p:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow,top:4dlu:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:4dlu:noGrow,center:d:noGrow,top:4dlu:noGrow,center:d:noGrow,top:4dlu:noGrow,center:d:noGrow"));
        tabbedPane1.addTab("Miner", panel5);
        final JLabel label12 = new JLabel();
        label12.setText("Oracle Patterns");
        panel5.add(label12, cc.xy(1, 1));
        textField5 = new JTextField();
        panel5.add(textField5, cc.xyw(3, 1, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        textField6 = new JTextField();
        panel5.add(textField6, cc.xyw(3, 7, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        textField4 = new JTextField();
        textField4.setEditable(false);
        textField4.setEnabled(true);
        panel5.add(textField4, cc.xyw(3, 9, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label13 = new JLabel();
        label13.setText("APSelectorManager");
        panel5.add(label13, cc.xy(1, 7));
        potentialOraclesLabel = new JLabel();
        potentialOraclesLabel.setText("Potential Oracles");
        panel5.add(potentialOraclesLabel, cc.xy(1, 9));
        final JSeparator separator2 = new JSeparator();
        panel5.add(separator2, cc.xyw(1, 6, 18, CellConstraints.FILL, CellConstraints.FILL));
        final JLabel label14 = new JLabel();
        label14.setText("Raw Oracles");
        panel5.add(label14, cc.xy(1, 4));
        textField7 = new JTextField();
        panel5.add(textField7, cc.xyw(3, 4, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label15 = new JLabel();
        label15.setText("Generate Files:");
        panel5.add(label15, cc.xy(1, 11));
        defaultSelectorButton1 = new JButton();
        defaultSelectorButton1.setText("Default Selector");
        defaultSelectorButton1.setToolTipText("Generate default APSelectionManager");
        panel5.add(defaultSelectorButton1, cc.xy(3, 11, CellConstraints.LEFT, CellConstraints.DEFAULT));
        sampleOracleButton1 = new JButton();
        sampleOracleButton1.setText("Sample Oracle");
        panel5.add(sampleOracleButton1, cc.xyw(4, 11, 2, CellConstraints.LEFT, CellConstraints.DEFAULT));
        button2 = new JButton();
        button2.setText("...");
        panel5.add(button2, cc.xy(8, 4));
        button1 = new JButton();
        button1.setText("...");
        panel5.add(button1, cc.xy(8, 1));
        button3 = new JButton();
        button3.setText("...");
        panel5.add(button3, cc.xy(8, 7));
        final JLabel label16 = new JLabel();
        label16.setText("Tactic");
        panel5.add(label16, cc.xy(10, 1));
        final JLabel label17 = new JLabel();
        label17.setText("Type");
        panel5.add(label17, cc.xy(10, 7));
        generateOraclesButton = new JButton();
        generateOraclesButton.setText("<html>Generate Oracles</html>");
        generateOraclesButton.setToolTipText("Combines the Patterns and the Model to generate Potential Oracles. Then checks the Candidates on the Model. ");
        panel5.add(generateOraclesButton, cc.xyw(10, 4, 8));
        modelCheckButton = new JButton();
        modelCheckButton.setText("Model Check");
        modelCheckButton.setToolTipText("Checks the Candidates on the Model. ");
        panel5.add(modelCheckButton, cc.xyw(10, 9, 8));
        comboBox1 = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("LTL");
        defaultComboBoxModel1.addElement("LTLF");
        defaultComboBoxModel1.addElement("LTLTRACE");
        defaultComboBoxModel1.addElement("CTL");
        defaultComboBoxModel1.addElement("MUCALC");
        comboBox1.setModel(defaultComboBoxModel1);
        comboBox1.setToolTipText("Type of Check to perform");
        panel5.add(comboBox1, cc.xyw(12, 7, 7, CellConstraints.LEFT, CellConstraints.DEFAULT));
        comboBox2 = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("RndPerPattern1");
        defaultComboBoxModel2.addElement("RndPerPattern5");
        defaultComboBoxModel2.addElement("RndPerPattern10");
        defaultComboBoxModel2.addElement("RndPerPattern100");
        defaultComboBoxModel2.addElement("RndPerPattern1000");
        defaultComboBoxModel2.addElement("RndPerPattern10000");
        defaultComboBoxModel2.addElement("Random10");
        defaultComboBoxModel2.addElement("Random100");
        defaultComboBoxModel2.addElement("Random1000");
        defaultComboBoxModel2.addElement("Random10000");
        defaultComboBoxModel2.addElement("Random100000");
        defaultComboBoxModel2.addElement("Random1000000");
        defaultComboBoxModel2.addElement("All(combinatorial)");
        comboBox2.setModel(defaultComboBoxModel2);
        comboBox2.setToolTipText("tactic to generate oracles from the supplied Pattern collection");
        comboBox2.setVerifyInputWhenFocusTarget(true);
        panel5.add(comboBox2, cc.xyw(12, 1, 2, CellConstraints.LEFT, CellConstraints.DEFAULT));
        graphMLButton1 = new JButton();
        graphMLButton1.setText("GraphML");
        graphMLButton1.setToolTipText("Make a model from the graphDB. Requires APSelectorManagerTEST.json file in directory temporal");
        panel5.add(graphMLButton1, cc.xyw(12, 11, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,left:330px:noGrow,left:4dlu:noGrow,left:4dlu:noGrow,right:40px:noGrow,left:4dlu:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        tabbedPane1.addTab("Visualizer", panel6);
        final JLabel label18 = new JLabel();
        label18.setText("Input Oracle File");
        panel6.add(label18, cc.xy(1, 1));
        textField8 = new JTextField();
        panel6.add(textField8, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        button4 = new JButton();
        button4.setText("...");
        panel6.add(button4, cc.xy(6, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        final JLabel label19 = new JLabel();
        label19.setText("GraphML File");
        panel6.add(label19, cc.xy(1, 3));
        textField9 = new JTextField();
        panel6.add(textField9, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        button5 = new JButton();
        button5.setText("...");
        panel6.add(button5, cc.xy(6, 3, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        stopAnalyzerButton = new JButton();
        stopAnalyzerButton.setText("Stop Analyzer");
        panel6.add(stopAnalyzerButton, cc.xy(1, 9));
        startAnalyzerButton = new JButton();
        startAnalyzerButton.setText("Start Analyzer");
        panel6.add(startAnalyzerButton, cc.xy(1, 7));
        final JLabel label20 = new JLabel();
        label20.setText("Output Oracle File");
        panel6.add(label20, cc.xy(1, 5));
        textField12 = new JTextField();
        panel6.add(textField12, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        button6 = new JButton();
        button6.setText("...");
        panel6.add(button6, cc.xy(6, 5));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setPreferredSize(new Dimension(1, 20));
        scrollPane1.setRequestFocusEnabled(false);
        panel1.add(scrollPane1, cc.xyw(1, 3, 12, CellConstraints.FILL, CellConstraints.FILL));
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
        panel1.add(clearButton, cc.xyw(13, 3, 4, CellConstraints.DEFAULT, CellConstraints.CENTER));
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

    private void ModelCheckWithSpot(String automatonFile, String formulaFile, String alivePropositionLTLF, String resultsFile) {
        // code goes here
        Process theProcess = null;
        BufferedReader inStream = null;
        BufferedReader errStream = null;

        //String cli = "ubuntu1804 run ~/testar/spot_checker --a automaton4.txt --ff formulas-abc-100.txt --ltlf !dead";
        String cli;
        cli = textField2.getText() + " --a " + toWSLPath(automatonFile) + " --ff " + toWSLPath(formulaFile);

        if (!alivePropositionLTLF.equals(""))
            cli = cli + " --ltlf " + alivePropositionLTLF;
        if (!resultsFile.equals("")) cli = cli + " --o " + toWSLPath(resultsFile);

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

        //String cli = "ubuntu1804 run ~/testar/spot_checker --a automaton4.txt --ff formulas-abc-100.txt --ltlf !dead";
        String cli;
        cli = textField2.getText() + " --a " + toWSLPath(Test_automaton4.getText()) + " --ff " + toWSLPath(Test_Formulas.getText());

        if (!AlivepropositionTextField.getText().equals(""))
            cli = cli + " --ltlf " + AlivepropositionTextField.getText();
        if (!resultsTxtTextField.getText().equals("")) cli = cli + " --o " + toWSLPath(resultsTxtTextField.getText());

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

    private void ModelCheck(ActionEvent e) {
        try {

            APSelectorManager APmgr = loadApSelectionManagerJSON(textField6.getText());

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

            tcontrol.computeTemporalModel(APmgr);
            tcontrol.saveModelAsJSON("APEncodedModel.json");
            tcontrol.saveModelAsHOA("Model.hoa");

            List<TemporalOracle> fromcoll;
            String file = textField7.getText();
            String[] fileparts = file.split("\\.");
            String strippedfile = file.substring(0, file.length() - fileparts[fileparts.length - 1].length());
            fromcoll = CSVHandler.load(file, TemporalOracle.class);
            if (fromcoll == null) {
                textArea12.append("verify the file at location '" + file + "' \n");
            } else {
                textArea12.append("csv loaded: \n");
                tcontrol.setOracleColl(fromcoll);
                textArea12.append("formulas saved: \n");
                tcontrol.saveFormulaFile(fromcoll, TemporalType.LTL, "Formulas.txt");


                CSVHandler.save(fromcoll, strippedfile + "_inputvalidation.csv");
                textArea12.append("input validation results csv saved: \n");

            }


            textArea12.append(" saving to file done\n");
            File automatonfile = new File(outputDir + "Model.hoa");
            File formulafile = new File(outputDir + "Formulas.txt");
            File resultsfile = new File(outputDir + "results.txt");
            String aliveprop = tcontrol.gettModel().getAliveProposition("!dead");
            ModelCheckWithSpot(automatonfile.getAbsolutePath(), formulafile.getAbsolutePath(), aliveprop, resultsfile.getAbsolutePath());
            //decode results
            Spot_CheckerResultsParser sParse = new Spot_CheckerResultsParser(tcontrol.gettModel(), fromcoll);
            String resultsAsString = "";
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(resultsfile))) {

                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    contentBuilder.append(sCurrentLine).append("\n");
                }
            } catch (IOException f) {
                f.printStackTrace();
            }

            List<TemporalOracle> modelCheckedOracles = sParse.parse(contentBuilder.toString());
            if (modelCheckedOracles == null) {
                System.err.println("Error detected in modelcheck results");
                textArea12.append("Error detected in modelcheck results");
                textArea12.append("\n");
            } else {
                // add to oraclecoll.
                CSVHandler.save(modelCheckedOracles, strippedfile + "_modelchecked.csv");
                textArea12.append("input validation results csv saved: \n");

            }


            tcontrol.shutdown();
        } catch (Exception f) {
            System.err.println("Error on testing db");
            textArea12.append("Error on testing db\n");
            textArea12.append("\n");
            f.printStackTrace();
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
            Desktop desktop = Desktop.getDesktop();
            URI uri = new URI("http://localhost:8050/shutdown");
            desktop.browse(uri);
            if (webAnalyzerProcess != null) webAnalyzerProcess.waitFor(1, TimeUnit.SECONDS);
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

            APSelectorManager APmgr = loadApSelectionManagerJSON(APSelectorManagerTESTJSONTextField.getText());

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

            tcontrol.computeTemporalModel(APmgr);
            tcontrol.saveModelAsJSON("APEncodedModel.json");
            tcontrol.saveModelAsHOA("Model.hoa");

            List<TemporalOracle> fromcoll;
            String file = temporalOracleTESTCsvTextField.getText();
            fromcoll = CSVHandler.load(file, TemporalOracle.class);
            if (fromcoll == null) {
                textArea12.append("verify the file at location '" + file + "' \n");
            } else {
                textArea12.append("csv loaded: \n");
                tcontrol.setOracleColl(fromcoll);
                tcontrol.saveFormulaFile(fromcoll, TemporalType.LTL, "Formulas.txt");

                String[] fileparts = file.split("\\.");
                String strippedfile = file.substring(0, file.length() - fileparts[fileparts.length - 1].length() - 1);

                CSVHandler.save(fromcoll, strippedfile + "_inputvalidation.csv");
                textArea12.append("inputvalidation results csv saved: \n");

            }

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
            textArea12.append("Formalism that was read from file: " + fromcoll.get(0).getPattern_TemporalFormalism() + "\n");
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
            textArea12.append("pattern that was read from file: " + fromcoll.get(0).getPattern_TemporalFormalism() + "\n");
            textArea12.append("widgetrole constraints that was read from file: " + fromcoll.get(0).getParameter_WidgetRoleConstraints().toString() + "\n");

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

    public String toWSLPath(String windowsFilePath) {
        Path winpath = Paths.get(windowsFilePath);
        StringBuilder wslpath = new StringBuilder();
        wslpath.append("/mnt/");
        wslpath.append(winpath.getRoot().toString().split(":")[0].toLowerCase());  // convert C:\\ --> c
        for (int i = 0; i < winpath.getNameCount(); i++) {
            wslpath.append("/" + winpath.getName(i));
        }
        return wslpath.toString();
    }
//*******************Eventhandlers


}
