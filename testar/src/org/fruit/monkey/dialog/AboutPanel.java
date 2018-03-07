/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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
