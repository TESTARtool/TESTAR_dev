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

public class SutServerPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private File currentDirectory;

  private JLabel lblSutServerRestart;
  private JTextField txtSutServerRestart;
  private JLabel lblSutServerUsername;
  private JTextField txtSutServerUsername;
  private JLabel lblSutServerHostname;
  private JTextField txtSutServerHostname;
  private JLabel lblSutServerPort;
  private JTextField txtSutServerPort;
  private JLabel lblSutServerKeyFile;
  private JTextField txtSutServerKeyFile;
  private JButton btnSetSutServerKeyFile;

  public SutServerPanel() {
    addSutServer();
    
    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(lblSutServerRestart)
            .addComponent(lblSutServerUsername)
            .addComponent(lblSutServerHostname)
            .addComponent(lblSutServerPort)
            .addComponent(lblSutServerKeyFile))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(txtSutServerRestart)
            .addComponent(txtSutServerUsername)
            .addComponent(txtSutServerHostname)
            .addComponent(txtSutServerPort)
            .addComponent(txtSutServerKeyFile))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(btnSetSutServerKeyFile))
    );
    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblSutServerRestart)
            .addComponent(txtSutServerRestart))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblSutServerUsername)
            .addComponent(txtSutServerUsername))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblSutServerHostname)
            .addComponent(txtSutServerHostname))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblSutServerPort)
            .addComponent(txtSutServerPort))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addComponent(lblSutServerKeyFile)
            .addComponent(txtSutServerKeyFile)
            .addComponent(btnSetSutServerKeyFile))
        );
  }

  private void addSutServer() {
    lblSutServerRestart = new JLabel("Restart command:");
    txtSutServerRestart = new JTextField();
    lblSutServerUsername = new JLabel("User name:");
    txtSutServerUsername = new JTextField();
    lblSutServerHostname = new JLabel("Host name:");
    txtSutServerHostname = new JTextField();
    lblSutServerPort = new JLabel("Port:");
    txtSutServerPort = new JTextField();
    PlainDocument portDocument = (PlainDocument)txtSutServerPort.getDocument();
    portDocument.setDocumentFilter(new TexInputIntFilter());
    lblSutServerKeyFile = new JLabel("Key file");
    txtSutServerKeyFile = new JTextField();
    btnSetSutServerKeyFile = new JButton("Select");
    btnSetSutServerKeyFile.addActionListener(this::btnSetSutServerKeyFileActionPerformed);
    btnSetSutServerKeyFile.setEnabled(true);
  }

  /**
   * Populate JPanelGeneral from Settings structure.
   *
   * @param settings The settings to load.
   */
  public void populateFrom(final Settings settings) {
    currentDirectory = new File(settings.get(ConfigTags.OutputDir)).getParentFile();

    // SUT server
    txtSutServerRestart.setText(settings.get(ConfigTags.SutServerRestart));
    txtSutServerUsername.setText(settings.get(ConfigTags.SutServerUsername));
    txtSutServerHostname.setText(settings.get(ConfigTags.SutServerHostname));
    txtSutServerPort.setText(settings.get(ConfigTags.SutServerPort));
    txtSutServerKeyFile.setText(settings.get(ConfigTags.SutServerKeyFile));
  }

  /**
   * Retrieve information from the JPanelGeneral GUI.
   *
   * @param settings reference to the object where the settings will be stored.
   */
  public void extractInformation(final Settings settings) {
    // SUT server
    settings.set(ConfigTags.SutServerRestart, txtSutServerRestart.getText());
    settings.set(ConfigTags.SutServerUsername, txtSutServerUsername.getText());
    settings.set(ConfigTags.SutServerHostname, txtSutServerHostname.getText());
    settings.set(ConfigTags.SutServerPort, txtSutServerPort.getText());
    settings.set(ConfigTags.SutServerKeyFile, txtSutServerKeyFile.getText());
  }

  private void btnSetSutServerKeyFileActionPerformed(ActionEvent evt) {
    JFileChooser fd = new JFileChooser();
    fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fd.setCurrentDirectory(currentDirectory);
    if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String file = fd.getSelectedFile().getAbsolutePath();
      txtSutServerKeyFile.setText(file);
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
