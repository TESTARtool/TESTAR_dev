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
//cleanUpDirTTT
package org.fruit.monkey.dialog;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.awt.event.ActionListener;

public class CleanUpPanel extends JPanel {
	private static final int GAP_X = 20;
	private static final int HOOGTE = 21;
	private static final int BREEDTE = 192;
	private static final int START_Y = 70;
	private static final int START_X = 70;
	private static final int DELTA = 30;
	private static final long serialVersionUID = 1L;
	private int nDirs;
	private Settings settings;
	private String rootFolderName;
	private String[] outputDirs;
	private JCheckBox[] cbox;
	private JButton delete;

	public CleanUpPanel() {

	}

	private void setupCleanUpDirs() {
		cbox = new JCheckBox[nDirs + 2];

		// root folder
		cbox[0] = new JCheckBox("root");
		cbox[0].setBounds(START_X, START_Y, BREEDTE, HOOGTE);
		if (isEmptyRoot()) {
			cbox[0].setEnabled(false);
		} else {
			cbox[0].setEnabled(true);

		}
		// sub folders
		for (int i = 1; i < nDirs + 1; i++) {
			cbox[i] = new JCheckBox(outputDirs[i - 1]);
			cbox[i].setBounds(START_X, START_Y + i * DELTA, BREEDTE, HOOGTE);
			
			if (isEmptyDir(i)) {
				cbox[i].setEnabled(false);
			} else {
				cbox[i].setEnabled(true);
			}
		}
		// all folders
		cbox[nDirs + 1] = new JCheckBox("Select all");
		cbox[nDirs + 1].setBounds(START_X - GAP_X, START_Y - DELTA, BREEDTE, HOOGTE);
		cbox[nDirs + 1].setSelected(true);

		// actionlisteners
		for (int i = 0; i <= nDirs + 1; i++) {
			int m = i;
			cbox[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					actionCheckBox(m);
				}
			});
			add(cbox[i]);
		}
	}

	/**
	 * Populate CleanUp Fields from Settings structure.
	 *
	 * @param settings
	 *            The settings to load.
	 */
	public void populateFrom(final Settings settings) {
		setLayout(null);
		
		// instructie regel
		JLabel titel = new JLabel("Check the folder(s) you want to make empty");
		titel.setBounds(START_X - GAP_X, START_Y - 2 * DELTA, (int) (1.5 * BREEDTE), HOOGTE);
		add(titel);
		
		// root folder, sub folder en all folders
		this.settings = settings;
		this.rootFolderName = settings.get(ConfigTags.OutputDir) + "/";
		outputDirs = new File(rootFolderName).list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return !name.endsWith(".log");
			}
		});
		this.nDirs = outputDirs.length;
		setupCleanUpDirs();
		
		// delete button
		delete = new JButton("Confirm deletion");
		delete.setBounds(START_X + GAP_X + BREEDTE, START_Y + (nDirs-1) * DELTA, BREEDTE, 2 * HOOGTE);
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				actionButton();
			}
		});
		add(delete);
	}

	private void actionCheckBox(int i) {
		boolean allChecked = cbox[nDirs + 1].isSelected();
		if (i == nDirs + 1 && allChecked) {
			for (int j = 0; j < nDirs + 1; j++) {
				cbox[j].setSelected(false);
			}
		}
		if (i != (nDirs + 1)) {
			cbox[nDirs + 1].setSelected(false);
		}
	}

	private void actionButton() {
		// all folders is geselecteerd
		Boolean allChecked = cbox[nDirs + 1].isSelected();
		
		for (int i = 0; i < nDirs + 1; i++) {
			// all folders is geselecteerd of de desbetreffende folder
			if (cbox[i].isSelected() || allChecked) {
				cleanDir(i);
				cbox[i].setEnabled(false);
				cbox[i].setSelected(false);
			}
		}
		int allEmpty = 0;
		for (int i = 0; i < nDirs + 1; i++) {
			if (!cbox[i].isEnabled()) {
				allEmpty++;
			}	
		}
		if (allEmpty == nDirs + 1) {
			cbox[nDirs + 1].setEnabled(false);
			delete.setEnabled(false);
		}
	}
	
	private boolean isEmptyDir(int i) {
		String folderName;
		if (i == 0) {
			folderName = rootFolderName;
		} else {
			folderName = rootFolderName + outputDirs[i-1];
		}
		File[] files = 
				new File(folderName).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return !name.contains("dummy");
				}
			});
		if (files.length == 0) { 
			return true;
		}
		return false;
	}
	
	private boolean isEmptyRoot() {
		File[] files = 
				new File(rootFolderName).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return name.toLowerCase().endsWith(".log");
				}
			});
		
		if (files.length == 0) { 
			return true;
		}
		return false;
	}

	private void cleanDir(int i) {
		String rootFolderName = settings.get(ConfigTags.OutputDir) + "/"; 
		String folderName;
		if (i == 0) {
			folderName = rootFolderName;
		} else {
			folderName = rootFolderName + outputDirs[i-1];
		}
		
		File[] files = new File(folderName).listFiles();
		
		if (files.length > 0) {
			System.out.println();
			System.out.println(folderName);
			for (int j = 0; j < folderName.length(); j++) {
				System.out.print("-");
			}
			System.out.println();
		}
		
		for (File f : files) {
			if (!f.isDirectory() && !f.getName().toLowerCase().contains("dummy")) {
				if (f.delete()) {
					System.out.println(f.getName() + " deleted");
				}
			}
		}
	}
}
