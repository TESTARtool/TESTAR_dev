package org.fruit.monkey.dialog;

import org.fruit.monkey.Settings;
import org.fruit.monkey.SettingsDialog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MethodologyPanel extends JPanel {
  private JLabel methodologyLogo = getLogo("/icons/info/Methodology.jpg");

  public MethodologyPanel () throws IOException {
    setBackground(Color.WHITE);
    
    add(methodologyLogo);
    /*
    GroupLayout aboutPanelLayout = new GroupLayout(this);
    setLayout(aboutPanelLayout);
    aboutPanelLayout.setHorizontalGroup(
        aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(aboutPanelLayout.createSequentialGroup()
                 .addContainerGap()
            	 .addComponent(methodologyLogo, PREFERRED_SIZE, 500, PREFERRED_SIZE)
                 .addContainerGap()
            )
    );
    aboutPanelLayout.setVerticalGroup(
        aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
            .addGroup(aboutPanelLayout.createSequentialGroup()
            	.addContainerGap()	
                .addComponent(methodologyLogo, PREFERRED_SIZE,500, PREFERRED_SIZE)
                .addContainerGap()               
                )
    );
    */
  }

  private JLabel getLogo (String iconPath) throws IOException{
    return new JLabel(new ImageIcon(SettingsDialog.loadIcon(iconPath)));
  }

  public void populateFrom(Settings settings) {
  }
}
