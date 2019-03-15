/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 Open Universiteit - www.ou.nl
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
***************************************************************************************************/

/**
 * @author Sebastian Bauersfeld
 */
package org.fruit.monkey;

import static java.util.logging.Logger.getLogger;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static javax.swing.UIManager.*;
import static org.fruit.Util.compileProtocol;

import es.upv.staq.testar.serialisation.LogSerialiser;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import nl.ou.testar.GraphDBPanel;
import nl.ou.testar.subroutine.CleanUpPanel;
import nl.ou.testar.subroutine.CompoundActionPanel;
import nl.ou.testar.subroutine.TgherkinFilePanel;
import org.fruit.Util;
import org.fruit.monkey.dialog.*;

/**
 * This class takes care of the SettingsDialogue of TESTAR (the TESTAR GUI).
 */

public class SettingsDialog extends JFrame implements Observer {
  private static final long serialVersionUID = 5156320008281200950L;

  static final String TESTAR_VERSION = "2.0.1";

  private String settingsFile;
  private Settings settings;
  //TODO: what is this ret variable. Can't you just return settings in the run method?
  private Settings ret;

  private JButton btnGenerate;
  private JButton btnSpy;
  private JButton btnReplay;
  private JButton btnView;
  private JButton btnRecord;

  private JTabbedPane jtabsPane;
  private GeneralPanel generalPanel;
  private FilterPanel filterPanel;
  private OraclePanel oraclePanel;
  private TimingPanel timingPanel;
  private MiscPanel miscPanel;
  private CleanUpPanel cleanUpPanel;
  private GraphDBPanel graphDBPanel;
  private TgherkinPanel tgherkinPanel;
  private TgherkinFilePanel tg_panel;
  private CompoundActionPanel ca_panel;

  /**
   * Starts the settings Dialog.
   * @throws IOException when Icons cannot be found.
   */
  SettingsDialog() throws IOException {
    getContentPane().setBackground(Color.WHITE);
    try {
      for (LookAndFeelInfo info: getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException | InstantiationException
        | IllegalAccessException | UnsupportedLookAndFeelException ex) {
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

  /**
   * run method.
   * @param settings The settings to load
   * @param settingsFile   filename of file containing settings
   * @return null
   */
  public Settings run(Settings settings, String settingsFile) {
    this.settings = settings;
    this.settingsFile = settingsFile;
    this.ret = null;
    this.setVisible(true);
    populateInformation(settings);

    while (this.isShowing()) {
      Util.pause(0.1);
    }

    return ret;
  }

  public static Image loadIcon(String path) throws IOException {
    return ImageIO.read(SettingsDialog.class.getResourceAsStream(path));
  }

  /**
   * This is the method that is called when you click on one of the big mode buttons
   * in TESTAR dialog.
   * @param mode indicates the MODE button that was clicked.
   */
  private void start(RuntimeControlsProtocol.Modes mode) {
    try {
      extractInformation(settings);
      checkSettings(settings);
      settings.set(ConfigTags.Mode, mode);
      saveCurrentSettings();
      ret = settings;
      if (settings.get(ConfigTags.AlwaysCompile)) {
        compileProtocol(Main.getSettingsDir(), settings.get(ConfigTags.ProtocolClass));
      }
      this.dispose();
    } catch (IllegalStateException ise) {
      JOptionPane.showMessageDialog(this, ise.getMessage(),
          "Invalid Settings!", JOptionPane.ERROR_MESSAGE);
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
      Settings.setSettingsPath(settingsFile.substring(0,settingsFile.indexOf("test.settings")-1));
      System.out.println("Saved current settings to <" + settingsFile + ">");
    } catch (IOException e1) {
      LogSerialiser.log("Unable to save current settings to <" + settingsFile + ">: " + e1.toString() + "\n");
    }
  }

  /**
   * This replaces the original test.settings file with a complete settings file
   * @param sutSettings settings of the SUT
   */
  private void switchSettings(String sutSettings) {
    String previousSSE = Main.getSSE()[0];
    String sse = sutSettings + Main.SUT_SETTINGS_EXT;
    if (previousSSE.equals(sse)) {
      return;
    }
    saveCurrentSettings();
    String settingsDir = Main.getSettingsDir();
    new File(settingsDir + previousSSE).renameTo(new File(settingsDir + sse));
    try {
      settingsFile = settingsDir + sutSettings + File.separator + Main.SETTINGS_FILE;
      settings = Main.loadSettings(new String[0], settingsFile);
      populateInformation(settings);
      System.out.println("Switched to <" + settingsFile + ">");
      Main.setSseActivated(sutSettings);
    } catch (ConfigException cfe) {
      LogSerialiser.log("Unable to switch to <" + sutSettings + "> settings!\n");
    }
  }

  private void populateInformation(Settings settings) {
    generalPanel.populateFrom(settings);
    filterPanel.populateFrom(settings);
    oraclePanel.populateFrom(settings);
    timingPanel.populateFrom(settings);
    miscPanel.populateFrom(settings);
    cleanUpPanel.populateFrom(settings);
    graphDBPanel.populateFrom(settings);
    tgherkinPanel.populateFrom(settings);

    // only show Tgherkin tab if the protocol is a DocumentProtocol
    if (tgherkinPanel.isDocumentProtocol()) {

      if (!tgherkinPanel.isActive()) {
        jtabsPane.addTab("Tgherkin", tgherkinPanel);
        tgherkinPanel.setActive(true);
      }
    } else {
      if (tgherkinPanel.isActive()) {
        jtabsPane.remove(tgherkinPanel);
        tgherkinPanel.setActive(false);
      }
    }
    
    tg_panel.populateFrom(settings);

    // only show Tgherkin File tab if the protocol is a TgherkinFileProtocol
    if (tg_panel.isClassBoolean()) {
      if (!tg_panel.isActive()) {
        jtabsPane.addTab("Tgherkin File", tg_panel);
        tg_panel.setActive(true);
      }
    } else {
      if (tg_panel.isActive()) {
        jtabsPane.remove(tg_panel);
        tg_panel.setActive(false);
      }
    }

    ca_panel.populateFrom(settings);

    // only show Compound Action tab if the protocol is a CompoundActionProtocol
    if (ca_panel.isClassBoolean()) {
      if (!ca_panel.isActive()) {
        jtabsPane.addTab("Compound Action", ca_panel);
        ca_panel.setActive(true);
      }
    } else {
      if (ca_panel.isActive()) {
        jtabsPane.remove(ca_panel);
        ca_panel.setActive(false);
      }
    }
  }

  private void extractInformation(Settings settings) {
    generalPanel.extractInformation(settings);
    filterPanel.extractInformation(settings);
    oraclePanel.extractInformation(settings);
    timingPanel.extractInformation(settings);
    miscPanel.extractInformation(settings);
    graphDBPanel.extractInformation(settings);
    tgherkinPanel.extractInformation(settings);
    tg_panel.extractInformation(settings);
    ca_panel.extractInformation(settings);
  }

  private void initComponents() throws IOException {
    // Init start buttons
    btnGenerate = getBtnGenerate();
    btnSpy = getBtnSpy();
    btnReplay = getBtnReplay();
    btnView = getBtnView();
    btnRecord = getBtnRecord();

    jtabsPane = new JTabbedPane();
    jtabsPane.addTab("About", new AboutPanel());
    generalPanel = new GeneralPanel(this);
    jtabsPane.addTab("General Settings", generalPanel);
    filterPanel = new FilterPanel();
    jtabsPane.addTab("Filters", filterPanel);
    oraclePanel = new OraclePanel();
    jtabsPane.addTab("Oracles", oraclePanel);
    timingPanel = new TimingPanel();
    jtabsPane.addTab("Time Settings", timingPanel);
    miscPanel = new MiscPanel();
    jtabsPane.addTab("Misc", miscPanel);
    graphDBPanel = GraphDBPanel.createGraphDBPanel();
    jtabsPane.addTab("GraphDB", graphDBPanel);
    cleanUpPanel = new CleanUpPanel();
    jtabsPane.addTab("Clean Up", cleanUpPanel);
    tgherkinPanel = new TgherkinPanel();
    tg_panel = new TgherkinFilePanel();
    ca_panel = new CompoundActionPanel();

    setLayout(jtabsPane);
    pack();
    setCentreScreen();
  }

  /**
   * Make the window appear center screen.
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

  private void setLayout(JTabbedPane jtabsPane) {
    jtabsPane.setSelectedComponent(generalPanel);

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
                    .addComponent(jtabsPane, PREFERRED_SIZE, 620, PREFERRED_SIZE)
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
                         .addComponent(btnView, PREFERRED_SIZE, 129, PREFERRED_SIZE)
                         .addComponent(btnRecord, PREFERRED_SIZE, 129, PREFERRED_SIZE)
                    )
                .addPreferredGap(RELATED)
                .addComponent(jtabsPane, PREFERRED_SIZE, 400, PREFERRED_SIZE)
                .addContainerGap())
    );
  }

  private GroupLayout.SequentialGroup getStartGroup(GroupLayout layout) {
    GroupLayout.SequentialGroup group = layout.createSequentialGroup();

    group.addComponent(btnSpy, 120, 120, 120);
    group.addGap(2, 2, 2);
    group.addComponent(btnGenerate, 120, 120, 120);
    group.addGap(2, 2, 2);
    group.addComponent(btnRecord, 120, 120, 120);
    group.addGap(2, 2, 2);
    group.addComponent(btnReplay, 120, 120, 120);
    group.addGap(2, 2, 2);
    group.addComponent(btnView, 120, 120, 120);

    return group;
  }

  private JButton getBtnGenerate() throws IOException {
    JButton btn = new JButton();
    btn.setBackground(new Color(255, 255, 255));
    btn.setIcon(new ImageIcon(loadIcon("/icons/button_generate.png")));
    btn.setToolTipText(ToolTipTexts.getBtnGenerateTTT());
    btn.setFocusPainted(false);
    btn.addActionListener(this::btnGenerateActionPerformed);
    return btn;
  }

  private void btnGenerateActionPerformed(ActionEvent evt) {
    start(RuntimeControlsProtocol.Modes.Generate);
  }

  private JButton getBtnSpy() throws IOException {
    JButton btn = new JButton();
    btn.setBackground(new Color(255, 255, 255));
    btn.setIcon(new ImageIcon(loadIcon("/icons/button_spy.png")));
    btn.setToolTipText(ToolTipTexts.getBtnSpyTTT());
    btn.setFocusPainted(false);
    btn.addActionListener(this::btnSpyActionPerformed);
    return btn;
  }

  private void btnSpyActionPerformed(ActionEvent evt) {
    start(RuntimeControlsProtocol.Modes.Spy);
  }

  private JButton getBtnReplay() throws IOException {
    JButton btn = new JButton();
    btn.setBackground(new Color(255, 255, 255));
    btn.setIcon(new ImageIcon(loadIcon("/icons/button_replay.png")));
    btn.setToolTipText(ToolTipTexts.getBtnReplayTTT());
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
      start(RuntimeControlsProtocol.Modes.Replay);
    }
  }

  private JButton getBtnView() throws IOException {
    JButton btn = new JButton();
    btn.setBackground(new Color(255, 255, 255));
    btn.setIcon(new ImageIcon(loadIcon("/icons/button_view.png")));
    btn.setToolTipText(ToolTipTexts.getBtnViewTTT());
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
      start(RuntimeControlsProtocol.Modes.View);
    }
  }

  private JButton getBtnRecord() throws IOException {
    JButton btn = new JButton();
    btn.setBackground(new Color(255, 255, 255));
    btn.setIcon(new ImageIcon(loadIcon("/icons/button_record.png")));
    btn.setToolTipText(ToolTipTexts.getBtnRecordTTT());
    btn.setFocusPainted(false);
    btn.addActionListener(this::btnRecordActionPerformed);
    return btn;
  }

  private void btnRecordActionPerformed(ActionEvent evt) {
      start(RuntimeControlsProtocol.Modes.Record);
    }

  @Override
  public void update(Observable o, Object arg) {
    switchSettings((String) arg);
  }
}
