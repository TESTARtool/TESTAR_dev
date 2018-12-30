/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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


package nl.ou.testar;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;

/**
 * Panel with settings for the graph database.
 * Created by floren on 5-6-2017.
 */
public class GraphDBPanel extends JPanel {

  private static final long serialVersionUID = -2815422165938356237L;
  private JCheckBox graphDBEnabledChkBox = new JCheckBox();
    private JLabel label1 = new JLabel("Enabled");
    private JLabel label2 = new JLabel("url");
    private JLabel label3 = new JLabel("username");
    private JLabel label4 = new JLabel("password");

    private JTextField urlTextField = new JTextField();
    private JTextField userTextField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();

    private GraphDBPanel() {
        super();
    }

    /**
     * Create and Initialize GraphDBPanel.
     * @return GraphDBPanel.
     */
    public static GraphDBPanel createGraphDBPanel() {
        GraphDBPanel panel = new GraphDBPanel();
        panel.initialize();
        return panel;
    }

    /**
     * Initialize panel.
     */
    private void initialize() {
        setLayout(null);
        label1.setBounds(10,14,80,27);
        add(label1);
        graphDBEnabledChkBox.setBounds(90,14,50,27);
        add(graphDBEnabledChkBox);
        label2.setBounds(10,52,80,27);
        add(label2);
        urlTextField.setBounds(90,52,400,27);
        add(urlTextField);
        label3.setBounds(10,90,80,27);
        add(label3);
        userTextField.setBounds(90,90,400,27);
        add(userTextField);
        label4.setBounds(10,128,80,27);
        add(label4);
        passwordField.setBounds(90,128,400,27);
        add(passwordField);
    }

    /**
     * Populate GraphDBFields from Settings structure.
     * @param settings The settings to load.
     */
    public void populateFrom(final Settings settings) {
        graphDBEnabledChkBox.setSelected(settings.get(ConfigTags.GraphDBEnabled));
        urlTextField.setText(settings.get(ConfigTags.GraphDBUrl));
        userTextField.setText(settings.get(ConfigTags.GraphDBUser));
        passwordField.setText(settings.get(ConfigTags.GraphDBPassword));
    }

    /**
     * Retrieve information from the GraphDB GUI.
     * @param settings reference to the object where the settings will be stored.
     */
    public void extractInformation(final Settings settings) {
        settings.set(ConfigTags.GraphDBEnabled, graphDBEnabledChkBox.isSelected());
        settings.set(ConfigTags.GraphDBUrl,urlTextField.getText());
        settings.set(ConfigTags.GraphDBUser,userTextField.getText());
        settings.set(ConfigTags.GraphDBPassword,getPassword());
    }

    /**
     * Convert password field to string.
     * @return password as String.
     */
    private String getPassword() {
        StringBuilder result= new StringBuilder();
        for (char c: passwordField.getPassword()) {
            result.append(c);
        }
        return  result.toString();
    }

}
