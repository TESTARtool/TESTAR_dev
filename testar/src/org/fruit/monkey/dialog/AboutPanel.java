package org.fruit.monkey.dialog;

import org.fruit.monkey.SettingsDialog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;

public class AboutPanel extends JPanel {
  private JLabel lblUPVLogo = getLogo("/icons/logos/upv.png");
  private JLabel testarLogo = getLogo("/icons/logos/testar_logo.png");
  private JLabel prosLogo = getLogo("/icons/logos/pros.png");
  private JLabel ouLogo = getLogo("/icons/logos/ou.jpg");

  public AboutPanel () throws IOException {
    setBackground(Color.WHITE);

    GroupLayout aboutPanelLayout = new GroupLayout(this);
    setLayout(aboutPanelLayout);
    aboutPanelLayout.setHorizontalGroup(
        aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(aboutPanelLayout.createSequentialGroup()
                .addGroup(aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(aboutPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(prosLogo, PREFERRED_SIZE, 144, PREFERRED_SIZE)
                        .addGap(40)
                        .addComponent(ouLogo, PREFERRED_SIZE, 90, PREFERRED_SIZE)
                        .addPreferredGap(RELATED, 53, Short.MAX_VALUE)
                        .addComponent(lblUPVLogo, PREFERRED_SIZE, 153, PREFERRED_SIZE))
                    .addGroup(aboutPanelLayout.createSequentialGroup()
                        .addGap(144)
                        .addComponent(testarLogo, PREFERRED_SIZE, 186, PREFERRED_SIZE)))
                .addContainerGap())
    );
    aboutPanelLayout.setVerticalGroup(
        aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
            .addGroup(aboutPanelLayout.createSequentialGroup()
                .addGap(87)
                .addComponent(testarLogo, PREFERRED_SIZE, 75, PREFERRED_SIZE)
                .addPreferredGap(RELATED, 126, Short.MAX_VALUE)
                .addGroup(aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(lblUPVLogo, PREFERRED_SIZE, 53, PREFERRED_SIZE)
                    .addComponent(prosLogo, PREFERRED_SIZE, 56, PREFERRED_SIZE)
                    .addComponent(ouLogo, PREFERRED_SIZE, 70, PREFERRED_SIZE)))
    );

  }

  private JLabel getLogo (String iconPath) throws IOException{
    return new JLabel(new ImageIcon(SettingsDialog.loadIcon(iconPath)));
  }
}
