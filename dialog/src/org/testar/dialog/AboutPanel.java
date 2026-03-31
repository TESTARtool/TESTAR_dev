/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static javax.swing.GroupLayout.PREFERRED_SIZE;

public class AboutPanel extends JPanel {

	private static final long serialVersionUID = 5542811933551996647L;

	private JLabel lblUPVLogo = getLogo("/icons/logos/upv_pros.png");
	private JLabel testarLogo = getLogo("/icons/logos/testar_logo.png");
	private JLabel ouLogo = getLogo("/icons/logos/ou.jpg");
	private JLabel testomatLogo  = getLogo("/icons/logos/TESTOMAT_Logo.png");
	private JLabel fittestLogo = getLogo("/icons/logos/fittest_logo.png");
	private JLabel decoderLogo = getLogo("/icons/logos/decoder_logo.jpg");
	private JLabel ivvesLogo = getLogo("/icons/logos/ivves_logo.jpg");
	private JLabel iv4xrLogo = getLogo("/icons/logos/iv4xr_logo.png");
	private JLabel enactestLogo = getLogo("/icons/logos/enactest.png");
	private JLabel autolinkLogo = getLogo("/icons/logos/autolink.png");

	public AboutPanel () throws IOException {
		setBackground(Color.WHITE);

		GroupLayout aboutPanelLayout = new GroupLayout(this);
		setLayout(aboutPanelLayout);
		aboutPanelLayout.setHorizontalGroup(
				aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(aboutPanelLayout.createSequentialGroup()
						.addGroup(aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(aboutPanelLayout.createSequentialGroup()
										.addGap(40)
										.addComponent(ouLogo, PREFERRED_SIZE, 90, PREFERRED_SIZE)
										.addGap(80)
										.addComponent(testarLogo, PREFERRED_SIZE, 190, PREFERRED_SIZE)
										.addGap(40)
										.addComponent(lblUPVLogo, PREFERRED_SIZE, 190, PREFERRED_SIZE))
								.addGroup(aboutPanelLayout.createSequentialGroup()
										.addGap(20)
										.addComponent(testomatLogo, PREFERRED_SIZE, 150, PREFERRED_SIZE)
										.addGap(20)
										.addComponent(fittestLogo, PREFERRED_SIZE, 150, PREFERRED_SIZE)
										.addGap(20)
										.addComponent(enactestLogo, PREFERRED_SIZE, 120, PREFERRED_SIZE)
										.addGap(20)
										.addComponent(autolinkLogo, PREFERRED_SIZE, 120, PREFERRED_SIZE))
								.addGroup(aboutPanelLayout.createSequentialGroup()
										.addGap(40)
										.addComponent(decoderLogo, PREFERRED_SIZE, 150, PREFERRED_SIZE)
										.addGap(40)
										.addComponent(iv4xrLogo, PREFERRED_SIZE, 150, PREFERRED_SIZE)
										.addGap(40)
										.addComponent(ivvesLogo, PREFERRED_SIZE, 150, PREFERRED_SIZE)))
						.addContainerGap())
				);
		aboutPanelLayout.setVerticalGroup(
				aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(aboutPanelLayout.createSequentialGroup()
						.addGap(10)
						.addGroup(aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(lblUPVLogo, PREFERRED_SIZE, 110, PREFERRED_SIZE)
								.addComponent(testarLogo, PREFERRED_SIZE, 75, PREFERRED_SIZE)
								.addComponent(ouLogo, PREFERRED_SIZE, 70, PREFERRED_SIZE))
						.addGap(30)
						.addGroup(aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(fittestLogo, PREFERRED_SIZE, 75, PREFERRED_SIZE)
								.addComponent(testomatLogo, PREFERRED_SIZE, 75, PREFERRED_SIZE)
								.addComponent(enactestLogo, PREFERRED_SIZE, 75, PREFERRED_SIZE)
								.addComponent(autolinkLogo, PREFERRED_SIZE, 75, PREFERRED_SIZE))
						.addGap(25)
						.addGroup(aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(ivvesLogo, PREFERRED_SIZE, 100, PREFERRED_SIZE)
								.addComponent(iv4xrLogo, PREFERRED_SIZE, 80, PREFERRED_SIZE)
								.addComponent(decoderLogo, PREFERRED_SIZE, 100, PREFERRED_SIZE)))
				);

	}

	private JLabel getLogo (String iconPath) throws IOException{
		return new JLabel(new ImageIcon(SettingsDialog.loadIcon(iconPath)));
	}
}
