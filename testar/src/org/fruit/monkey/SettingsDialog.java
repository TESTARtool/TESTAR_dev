/*
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 * *
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 * *
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *
 */

/**
 * @author Sebastian Bauersfeld
 */
package org.fruit.monkey;

import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.GraphDBPanel;
import org.fruit.Util;
import org.fruit.monkey.dialog.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.logging.Logger.getLogger;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static javax.swing.UIManager.*;
import static org.fruit.Util.compileProtocol;
import static org.fruit.monkey.dialog.ToolTipTexts.*;


public class SettingsDialog extends JFrame implements Observer {
  private static final long serialVersionUID = 5156320008281200950L;

  static final String TESTAR_VERSION = "v1.3";

  private String settingsFile;
  private Settings settings;
  private Settings ret;

  private JButton btnGenerate;
  private JButton btnSpy;
  private JButton btnReplay;
  private JButton btnView;

  private GeneralPanel generalPanel;
  private WalkerPanel walkerPanel;
  private FilterPanel filterPanel;
  private OraclePanel oraclePanel;
  private TimingPanel timingPanel;
  private MiscPanel miscPanel;
  private GraphDBPanel graphDBPanel;
  private MethodologyPanel methodologyPanel;

  /**
   * Starts the settings Dialog.
   *
   * @throws IOException when Icons cannot be found.
   */
  SettingsDialog() throws IOException {
    getContentPane().setBackground(Color.WHITE);
    try {
      for (LookAndFeelInfo info : getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      getLogger(SettingsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    this.setIconImage(loadIcon("/icons/logos/TESTAR.jpg"));

    ToolTipManager.sharedInstance().setDismissDelay(25000);
    ToolTipManager.sharedInstance().setInitialDelay(100);
    initComponents();

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        saveCurrentSettings();
      }
    });
  }

  public Settings run(Settings settings, String settingsFile) {
    this.settings = settings;
    this.settingsFile = settingsFile;
    this.ret = null;
    this.setVisible(true);
    populateInformation(settings);

    while (this.isShowing())
      Util.pause(0.1);

    return ret;
  }

  public static Image loadIcon(String path) throws IOException {
	  System.out.println("SettingsDialog: path = "+path);
	  File file = new File(path);
	  
	  System.exit(0);
    return ImageIO.read(SettingsDialog.class.getResourceAsStream(path));
  }

  private void start(AbstractProtocol.Modes mode) {
    try {
      extractInformation(settings);
      checkSettings(settings);
      saveCurrentSettings();
      settings.set(ConfigTags.Mode, mode);
      ret = settings;
      if (settings.get(ConfigTags.AlwaysCompile)) {
        compileProtocol(settings.get(ConfigTags.ProtocolClass));
      }
      this.dispose();
    } catch (IllegalStateException ise) {
      JOptionPane.showMessageDialog(this, ise.getMessage(), "Invalid Settings!", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void checkSettings(Settings settings) throws IllegalStateException {
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

    if (!new File(settings.get(ConfigTags.OutputDir)).exists()) {
      throw new IllegalStateException("Output Directory does not exist!");
    }
    if (!new File(settings.get(ConfigTags.TempDir)).exists()) {
      throw new IllegalStateException("Temp Directory does not exist!");
    }

    miscPanel.checkSettings();
  }

  private void saveCurrentSettings() {
    extractInformation(settings);
    try {
      Util.saveToFile(settings.toFileString(), settingsFile);
      System.out.println("Saved current settings to <" + settingsFile + ">");
    } catch (IOException e1) {
      LogSerialiser.log("Unable to save current settings to <" + settingsFile + ">: " + e1.toString() + "\n");
    }
  }

  private void switchSettings(String sutSettings) {
    String previousSSE = Main.getSSE()[0];
    String sse = sutSettings + Main.SUT_SETTINGS_EXT;
    if (previousSSE.equals(sse)) {
      return;
    }
    saveCurrentSettings();
    new File("./resources/settings/" + previousSSE).renameTo(new File("./resources/settings/" + sse));
    try {
      settingsFile = "./resources/settings/" + sutSettings + "/" + Main.SETTINGS_FILE;
      settings = Main.loadSettings(new String[0], settingsFile);
      populateInformation(settings);
      System.out.println("Switched to <" + settingsFile + ">");
    } catch (ConfigException cfe) {
      LogSerialiser.log("Unable to switch to <" + sutSettings + "> settings!\n");
    }
  }

  private void populateInformation(Settings settings) {
    generalPanel.populateFrom(settings);
    walkerPanel.populateFrom(settings);
    filterPanel.populateFrom(settings);
    oraclePanel.populateFrom(settings);
    timingPanel.populateFrom(settings);
    miscPanel.populateFrom(settings);
    graphDBPanel.populateFrom(settings);
  }

  private void extractInformation(Settings settings) {
    generalPanel.extractInformation(settings);
    walkerPanel.extractInformation(settings);
    filterPanel.extractInformation(settings);
    oraclePanel.extractInformation(settings);
    timingPanel.extractInformation(settings);
    miscPanel.extractInformation(settings);
    graphDBPanel.extractInformation(settings);
  }

  private void initComponents() throws IOException {
    // Init start buttons
    btnGenerate = getBtnGenerate();
    btnSpy = getBtnSpy();
    btnReplay = getBtnReplay();
    btnView = getBtnView();

    JTabbedPane jTabsPane = new JTabbedPane();
    jTabsPane.addTab("About", new AboutPanel());
    generalPanel = new GeneralPanel(this);
    jTabsPane.addTab("General Settings", generalPanel);
    walkerPanel = new WalkerPanel();
    jTabsPane.addTab("UI-walker", walkerPanel);
    filterPanel = new FilterPanel();
    jTabsPane.addTab("Filters", filterPanel);
    oraclePanel = new OraclePanel();
    jTabsPane.addTab("Oracles", oraclePanel);
    timingPanel = new TimingPanel();
    jTabsPane.addTab("Time Settings", timingPanel);
    miscPanel = new MiscPanel();
    jTabsPane.addTab("Misc", miscPanel);
    graphDBPanel = GraphDBPanel.createGraphDBPanel();
    jTabsPane.addTab("GraphDB", graphDBPanel);
    methodologyPanel = new MethodologyPanel();
    jTabsPane.addTab("Methodology", methodologyPanel);
    setLayout(jTabsPane);
    pack();
    setCentreScreen();
  }

  /**
   * Make the window appear centre screen
   */
  private void setCentreScreen() {
    Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();

    Rectangle bounds = new Rectangle();
    bounds.x = (int) scrDim.getWidth() / 2 - getWidth() / 2;
    bounds.y = (int) scrDim.getHeight() / 2 - getHeight() / 2;
    bounds.width = getWidth();
    bounds.height = getHeight();

    setBounds(bounds);
  }

  private void setLayout(JTabbedPane jTabsPane) {
    jTabsPane.setSelectedComponent(generalPanel);

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("TESTAR " + TESTAR_VERSION);
    setLocationByPlatform(true);
    setName("TESTAR Settings"); // NOI18N
    setResizable(false);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);

    layout.setHorizontalGroup(
        layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(jTabsPane, PREFERRED_SIZE, 505, PREFERRED_SIZE)
                    .addGroup(getStartGroup(layout)))
                .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(btnGenerate, PREFERRED_SIZE, 129, PREFERRED_SIZE)
                    .addComponent(btnSpy, PREFERRED_SIZE, 129, PREFERRED_SIZE)
                    .addComponent(btnReplay, PREFERRED_SIZE, 129, PREFERRED_SIZE)
                    .addComponent(btnView, PREFERRED_SIZE, 129, PREFERRED_SIZE))
                .addPreferredGap(RELATED)
                .addComponent(jTabsPane, PREFERRED_SIZE, 400, PREFERRED_SIZE)
                .addContainerGap())
    );
  }

  private GroupLayout.SequentialGroup getStartGroup(GroupLayout layout) {
    GroupLayout.SequentialGroup group = layout.createSequentialGroup();

    group.addComponent(btnSpy, PREFERRED_SIZE, 123, PREFERRED_SIZE);
    group.addGap(2, 2, 2);
    group.addComponent(btnGenerate, PREFERRED_SIZE, 123, PREFERRED_SIZE);
    group.addGap(2, 2, 2);
    group.addComponent(btnReplay, PREFERRED_SIZE, 123, PREFERRED_SIZE);
    group.addPreferredGap(RELATED);
    group.addComponent(btnView, PREFERRED_SIZE, 123, PREFERRED_SIZE);
    group.addGap(0, 0, Short.MAX_VALUE);

    return group;
  }

  private JButton getBtnGenerate() throws IOException {
    JButton btn = new JButton();
    btn.setBackground(new Color(255, 255, 255));
    btn.setIcon(new ImageIcon(loadIcon("/icons/engine.jpg")));
    btn.setToolTipText(btnGenerateTTT);
    btn.setFocusPainted(false);
    btn.addActionListener(this::btnGenerateActionPerformed);
    return btn;
  }

  private void btnGenerateActionPerformed(ActionEvent evt) {
    start(AbstractProtocol.Modes.Generate);
  }

  private JButton getBtnSpy() throws IOException {
    JButton btn = new JButton();
    btn.setBackground(new Color(255, 255, 255));
    btn.setIcon(new ImageIcon(loadIcon("/icons/magnifier.png")));
    btn.setToolTipText(btnSpyTTT);
    btn.setFocusPainted(false);
    btn.addActionListener(this::btnSpyActionPerformed);
    return btn;
  }

  private void btnSpyActionPerformed(ActionEvent evt) {
    start(AbstractProtocol.Modes.Spy);
  }

  private JButton getBtnReplay() throws IOException {
    JButton btn = new JButton();
    btn.setBackground(new Color(255, 255, 255));
    btn.setIcon(new ImageIcon(loadIcon("/icons/rewind.jpg")));
    btn.setToolTipText(btnReplayTTT);
    btn.setFocusPainted(false);
    btn.addActionListener(this::btnReplayActionPerformed);
    return btn;
  }

  private void btnReplayActionPerformed(ActionEvent evt) {
    JFileChooser fd = new JFileChooser();
    fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fd.setCurrentDirectory(new File(settings.get(ConfigTags.PathToReplaySequence)).getParentFile());

    if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String file = fd.getSelectedFile().getAbsolutePath();
      settings.set(ConfigTags.PathToReplaySequence, file);
      start(AbstractProtocol.Modes.Replay);
    }
  }

  private JButton getBtnView() throws IOException {
    JButton btn = new JButton();
    btn.setBackground(new Color(255, 255, 255));
    btn.setIcon(new ImageIcon(loadIcon("/icons/view.jpg")));
    btn.setToolTipText(btnViewTTT);
    btn.setFocusPainted(false);
    btn.addActionListener(this::btnViewActionPerformed);
    return btn;
  }

  private void btnViewActionPerformed(ActionEvent evt) {
    JFileChooser fd = new JFileChooser();
    fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fd.setCurrentDirectory(new File(settings.get(ConfigTags.PathToReplaySequence)).getParentFile());

    if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String file = fd.getSelectedFile().getAbsolutePath();
      settings.set(ConfigTags.PathToReplaySequence, file);
      start(AbstractProtocol.Modes.View);
    }
  }

  @Override
  public void update(Observable o, Object arg) {
    switchSettings((String) arg);
  }
}
