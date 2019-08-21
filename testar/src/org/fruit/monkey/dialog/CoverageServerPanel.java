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
*******************************************************************************************************/

package org.fruit.monkey.dialog;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class CoverageServerPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private File currentDirectory;

  private JLabel lblCoverageServerUsername;
  private JLabel lblCoverageServerHostname;
  private JLabel lblCoverageServerPort;
  private JLabel lblCoverageServerKeyFile;
  private JTextField txtCoverageServerUsername;
  private JTextField txtCoverageServerHostname;
  private JTextField txtCoverageServerPort;
  private JTextField txtCoverageServerKeyFile;
  private JButton btnSetCoverageServerKeyFile;

  private JLabel lblCoverageCommands;
  private JLabel lblCoverageCommandReset;
  private JLabel lblCoverageCommandDump;
  private JLabel lblCoverageCommandXmlReport;
  private JLabel lblCoverageCommandHtmlReport;
  private JTextField txtCoverageCommandReset;
  private JTextField txtCoverageCommandDump;
  private JTextField txtCoverageCommandXmlReport;
  private JTextField txtCoverageCommandHtmlReport;

  public CoverageServerPanel() {
    setLayout(null);

    JPanel coverageServerPanel = addCoverageServer();
    JPanel coverageCommandsPanel = addCoverageCommands();
    
    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(coverageServerPanel)
            .addComponent(coverageCommandsPanel))
            );
    layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(coverageServerPanel)
            .addComponent(coverageCommandsPanel));
  }

  private JPanel addCoverageServer() {
    JPanel  coverageServerPanel = new JPanel();

    lblCoverageServerUsername = new JLabel("User name:");
    txtCoverageServerUsername = new JTextField();
    lblCoverageServerHostname = new JLabel("Host name:");
    txtCoverageServerHostname = new JTextField();
    lblCoverageServerPort = new JLabel("Port:");
    txtCoverageServerPort = new JTextField();
    PlainDocument portDocument = (PlainDocument)txtCoverageServerPort.getDocument();
    portDocument.setDocumentFilter(new TexInputIntFilter());
    lblCoverageServerKeyFile = new JLabel("SSH key file:");
    txtCoverageServerKeyFile = new JTextField();
 
    btnSetCoverageServerKeyFile = new JButton("Select");
    btnSetCoverageServerKeyFile.addActionListener(this::btnSetCoverageServerKeyFileActionPerformed);
    btnSetCoverageServerKeyFile.setEnabled(true);
    
    GroupLayout layout = new GroupLayout(coverageServerPanel);
    coverageServerPanel.setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(lblCoverageServerUsername)
            .addComponent(lblCoverageServerHostname)
            .addComponent(lblCoverageServerPort)
            .addComponent(lblCoverageServerKeyFile))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(txtCoverageServerUsername)
            .addComponent(txtCoverageServerHostname)
            .addComponent(txtCoverageServerPort)
            .addComponent(txtCoverageServerKeyFile))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(btnSetCoverageServerKeyFile))
    );
    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblCoverageServerUsername)
            .addComponent(txtCoverageServerUsername))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblCoverageServerHostname)
            .addComponent(txtCoverageServerHostname))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblCoverageServerPort)
            .addComponent(txtCoverageServerPort))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblCoverageServerKeyFile)
            .addComponent(txtCoverageServerKeyFile)
            .addComponent(btnSetCoverageServerKeyFile)));
    return coverageServerPanel;
  }

  private JPanel addCoverageCommands() {
    JPanel  coverageCommandsPanel = new JPanel();

    lblCoverageCommands = new JLabel("Commands");
    Font f = lblCoverageCommands.getFont();
    lblCoverageCommands.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
    lblCoverageCommandReset = new JLabel("Reset:");
    txtCoverageCommandReset = new JTextField();
    lblCoverageCommandDump = new JLabel("Dump:");
    txtCoverageCommandDump = new JTextField();
    lblCoverageCommandXmlReport = new JLabel("XML report:");
    txtCoverageCommandXmlReport = new JTextField();
    lblCoverageCommandHtmlReport = new JLabel("HTML report:");
    txtCoverageCommandHtmlReport = new JTextField();

    GroupLayout layout = new GroupLayout(coverageCommandsPanel);
    coverageCommandsPanel.setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(lblCoverageCommands)
            .addComponent(lblCoverageCommandReset)
            .addComponent(lblCoverageCommandDump)
            .addComponent(lblCoverageCommandXmlReport)
            .addComponent(lblCoverageCommandHtmlReport))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(txtCoverageCommandReset)
            .addComponent(txtCoverageCommandDump)
            .addComponent(txtCoverageCommandXmlReport)
            .addComponent(txtCoverageCommandHtmlReport))
    );
    layout.setVerticalGroup(layout.createSequentialGroup()
        .addComponent(lblCoverageCommands)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblCoverageCommandReset)
            .addComponent(txtCoverageCommandReset))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblCoverageCommandDump)
            .addComponent(txtCoverageCommandDump))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblCoverageCommandXmlReport)
            .addComponent(txtCoverageCommandXmlReport))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblCoverageCommandHtmlReport)
            .addComponent(txtCoverageCommandHtmlReport)));
    return coverageCommandsPanel;
  }

  /**
   * Populate JPanelGeneral from Settings structure.
   *
   * @param settings The settings to load.
   */
  public void populateFrom(final Settings settings) {
    currentDirectory = new File(settings.get(ConfigTags.OutputDir)).getParentFile();

    // Coverage server
    txtCoverageServerUsername.setText(settings.get(ConfigTags.CoverageServerUsername));
    txtCoverageServerHostname.setText(settings.get(ConfigTags.CoverageServerHostname));
    txtCoverageServerPort.setText(settings.get(ConfigTags.CoverageServerPort));
    txtCoverageServerKeyFile.setText(settings.get(ConfigTags.CoverageServerKeyFile));
    // Coverage commands
    txtCoverageCommandReset.setText(settings.get(ConfigTags.CoverageCommandReset));
    txtCoverageCommandDump.setText(settings.get(ConfigTags.CoverageCommandDump));
    txtCoverageCommandXmlReport.setText(settings.get(ConfigTags.CoverageCommandXmlReport));
    txtCoverageCommandHtmlReport.setText(settings.get(ConfigTags.CoverageCommandHtmlReport));
  }

  /**
   * Retrieve information from the JPanelGeneral GUI.
   *
   * @param settings reference to the object where the settings will be stored.
   */
  public void extractInformation(final Settings settings) {
    // Coverage server
    settings.set(ConfigTags.CoverageServerUsername, txtCoverageServerUsername.getText());
    settings.set(ConfigTags.CoverageServerHostname, txtCoverageServerHostname.getText());
    settings.set(ConfigTags.CoverageServerPort, txtCoverageServerPort.getText());
    settings.set(ConfigTags.CoverageServerKeyFile, txtCoverageServerKeyFile.getText());
    // Coverage commands
    settings.set(ConfigTags.CoverageCommandReset, txtCoverageCommandReset.getText());
    settings.set(ConfigTags.CoverageCommandDump, txtCoverageCommandDump.getText());
    settings.set(ConfigTags.CoverageCommandXmlReport, txtCoverageCommandXmlReport.getText());
    settings.set(ConfigTags.CoverageCommandHtmlReport, txtCoverageCommandHtmlReport.getText());
  }

  private void btnSetCoverageServerKeyFileActionPerformed(ActionEvent evt) {
    JFileChooser fd = new JFileChooser();
    fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fd.setCurrentDirectory(currentDirectory);

    if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String file = fd.getSelectedFile().getAbsolutePath();
      txtCoverageServerKeyFile.setText(file);
    }
  }

  class TexInputIntFilter extends DocumentFilter {
	   @Override
	   public void insertString(FilterBypass fb, int offset, String string,
	         AttributeSet attr) throws BadLocationException {

	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.insert(offset, string);

	      if (test(sb.toString())) {
	         super.insertString(fb, offset, string, attr);
	      } else {
	         // warn the user and don't allow the insert
	      }
	   }

	   private boolean test(String text) {
	      try {
	         Integer.parseInt(text);
	         return true;
	      } catch (NumberFormatException e) {
	         return false;
	      }
	   }

	   @Override
	   public void replace(FilterBypass fb, int offset, int length, String text,
	         AttributeSet attrs) throws BadLocationException {

	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.replace(offset, offset + length, text);

	      if (test(sb.toString())) {
	         super.replace(fb, offset, length, text, attrs);
	      } else {
	         // warn the user and don't allow the insert
	      }
	   }

	   @Override
	   public void remove(FilterBypass fb, int offset, int length)
	         throws BadLocationException {
	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.delete(offset, offset + length);

	      if (test(sb.toString())) {
	         super.remove(fb, offset, length);
	      } else {
	         // warn the user and don't allow the insert
	      }
	   }
  }
}
