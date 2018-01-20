package org.fruit.monkey.dialog;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static org.fruit.monkey.dialog.ToolTipTexts.label1TTT;
import static org.fruit.monkey.dialog.ToolTipTexts.label2TTT;

public class FilterPanel extends JPanel {
  private JTextArea txtClickFilter;
  private JTextArea txtProcessFilter;

  public FilterPanel() {
    JLabel jLabel11 = new JLabel(
        "Disabled actions by widgets' TITLE property (regular expression):");
    jLabel11.setToolTipText(label1TTT);
    JLabel jLabel12 = new JLabel(
        "Kill processes by name (regular expression):");
    jLabel12.setToolTipText(label2TTT);

    JScrollPane scrollPane_2 = new JScrollPane();
    JScrollPane scrollPane_3 = new JScrollPane();

    GroupLayout gl_jPanelFilters = new GroupLayout(this);
    this.setLayout(gl_jPanelFilters);
    gl_jPanelFilters.setHorizontalGroup(
        gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(gl_jPanelFilters.createSequentialGroup()
                .addGroup(gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(gl_jPanelFilters.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrollPane_3, PREFERRED_SIZE, 445, PREFERRED_SIZE))
                    .addGroup(gl_jPanelFilters.createSequentialGroup()
                        .addGap(24)
                        .addComponent(scrollPane_2, PREFERRED_SIZE, 447, PREFERRED_SIZE))
                    .addGroup(gl_jPanelFilters.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, PREFERRED_SIZE, 357, PREFERRED_SIZE)
                            .addComponent(jLabel11, PREFERRED_SIZE, 381, PREFERRED_SIZE))))
                .addContainerGap(29, Short.MAX_VALUE))
    );
    gl_jPanelFilters.setVerticalGroup(
        gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(gl_jPanelFilters.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(18)
                .addComponent(scrollPane_2, DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addGap(18)
                .addComponent(jLabel12)
                .addGap(18)
                .addComponent(scrollPane_3, PREFERRED_SIZE, 121, PREFERRED_SIZE)
                .addGap(22))
    );

    txtProcessFilter = new JTextArea();
    txtProcessFilter.setLineWrap(true);
    scrollPane_3.setViewportView(txtProcessFilter);

    txtClickFilter = new JTextArea();
    txtClickFilter.setLineWrap(true);
    scrollPane_2.setViewportView(txtClickFilter);
  }

  /**
   * Populate Filter Fields from Settings structure.
   * @param settings The settings to load.
   */
  public void populateFrom(final Settings settings) {
    txtClickFilter.setText(settings.get(ConfigTags.ClickFilter));
    txtProcessFilter.setText(settings.get(ConfigTags.ProcessesToKillDuringTest));
  }

  /**
   * Retrieve information from the Filter  GUI.
   * @param settings reference to the object where the settings will be stored.
   */
  public void extractInformation(final Settings settings) {
    settings.set(ConfigTags.ClickFilter, txtClickFilter.getText());
    settings.set(ConfigTags.ProcessesToKillDuringTest, txtProcessFilter.getText());
  }
}
