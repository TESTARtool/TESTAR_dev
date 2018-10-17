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
	private static final String OUTPUT_TYPE_LOG = ".log";
	private static final String OUTPUT_TYPE_CSV = ".csv";
	private static final int GAP_X = 20;
	private static final int HOOGTE = 20;
	private static final int BREEDTE = 192;
	private static final int START_Y = 105;
	private static final int START_SETTINGS_X = 70;
	private static final int START_OUTPUT_X = 280;
	private static final int DELTA = 26;
	private static final long serialVersionUID = 1L;
	private String rootSettingsFolderName;
	private String[] settingsDirs;
	private int nSettingsDirs;
	private JCheckBox[] settingsBox;
	private String rootOutputFolderName;
	private String[] outputDirs;
	private int nOutputDirs;
	private JCheckBox[] outputBox;
	
	private JButton delete;
	
	public CleanUpPanel() {
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
		titel.setBounds(START_SETTINGS_X - GAP_X, START_Y - (int) (3.5  * DELTA), (int) (1.5 * BREEDTE), HOOGTE);
		add(titel);

		// root folder, sub folder en all folders
		this.rootSettingsFolderName = settings.get(ConfigTags.OutputDir) + "/";
		this.rootOutputFolderName = "./output/";

		// instructie regel
		JLabel settingsLabel = new JLabel(rootSettingsFolderName);
		settingsLabel.setBounds(START_SETTINGS_X - GAP_X, START_Y - 2 * DELTA, BREEDTE, HOOGTE);
		add(settingsLabel);
		
		// instructie regel
		JLabel outputLabel = new JLabel(rootOutputFolderName);
		outputLabel.setBounds(START_OUTPUT_X - 2 * GAP_X, START_Y - 2 * DELTA, BREEDTE, HOOGTE);
		add(outputLabel);
		

		settingsDirs = new File(rootSettingsFolderName).list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return !name.endsWith(OUTPUT_TYPE_LOG);
			}
		});
		this.nSettingsDirs = settingsDirs.length;
		setupSettingsDirs();

		outputDirs = new File(rootOutputFolderName).list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return !name.endsWith(OUTPUT_TYPE_CSV);
			}
		});
		this.nOutputDirs = outputDirs.length;
		setupOutputDirs();

		// delete button
		delete = new JButton("Confirm deletion");
		delete.setBounds(START_SETTINGS_X + GAP_X + BREEDTE, START_Y + (nSettingsDirs-1) * DELTA, BREEDTE, 2 * HOOGTE);
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				actionButton();
			}
		});
		add(delete);
	}

	private void setupSettingsDirs() {
		settingsBox = new JCheckBox[nSettingsDirs + 2];

		// root folder
		settingsBox[0] = new JCheckBox("root");
		settingsBox[0].setBounds(START_SETTINGS_X, START_Y, BREEDTE, HOOGTE);
		if (isEmptySettingsRoot()) {
			settingsBox[0].setEnabled(false);
		} else {
			settingsBox[0].setEnabled(true);

		}
		// sub folders
		for (int i = 1; i < nSettingsDirs + 1; i++) {
			settingsBox[i] = new JCheckBox(settingsDirs[i - 1]);
			settingsBox[i].setBounds(START_SETTINGS_X, START_Y + i * DELTA, BREEDTE, HOOGTE);
			
			if (isEmptySettingsDir(i)) {
				settingsBox[i].setEnabled(false);
			} else {
				settingsBox[i].setEnabled(true);
			}
		}
		// all folders
		settingsBox[nSettingsDirs + 1] = new JCheckBox("Select all");
		settingsBox[nSettingsDirs + 1].setBounds(START_SETTINGS_X - GAP_X, START_Y - DELTA, BREEDTE, HOOGTE);
		settingsBox[nSettingsDirs + 1].setSelected(true);

		// actionlisteners
		for (int i = 0; i <= nSettingsDirs + 1; i++) {
			int m = i;
			settingsBox[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					actionSettingsCheckBox(m);
				}
			});
			add(settingsBox[i]);
		}
	}
	
	private void setupOutputDirs() {
		outputBox = new JCheckBox[nOutputDirs + 2];

		// root folder
		outputBox[0] = new JCheckBox("root");
		outputBox[0].setBounds(START_OUTPUT_X, START_Y, BREEDTE, HOOGTE);
		if (isEmptyOutputRoot()) {
			outputBox[0].setEnabled(false);
		} else {
			outputBox[0].setEnabled(true);

		}
		// sub folders
		for (int i = 1; i < nOutputDirs + 1; i++) {
			outputBox[i] = new JCheckBox(outputDirs[i - 1]);
			outputBox[i].setBounds(START_OUTPUT_X, START_Y + i * DELTA, BREEDTE, HOOGTE);
			
			if (isEmptyOutputDir(i)) {
				outputBox[i].setEnabled(false);
			} else {
				outputBox[i].setEnabled(true);
			}
		}
		// all folders
		outputBox[nOutputDirs + 1] = new JCheckBox("Select all");
		outputBox[nOutputDirs + 1].setBounds(START_OUTPUT_X - GAP_X, START_Y - DELTA, BREEDTE, HOOGTE);
		outputBox[nOutputDirs + 1].setSelected(true);
		
		// actionlisteners
		for (int i = 0; i <= nOutputDirs + 1; i++) {
			int m = i;
			outputBox[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					actionOutputCheckBox(m);
				}
			});
			add(outputBox[i]);
		}
	}

	private void actionSettingsCheckBox(int i) {
		boolean allChecked = settingsBox[nSettingsDirs + 1].isSelected();
		if (i == nSettingsDirs + 1 && allChecked) {
			for (int j = 0; j < nSettingsDirs + 1; j++) {
				settingsBox[j].setSelected(false);
			}
		}
		if (i != (nSettingsDirs + 1)) {
			settingsBox[nSettingsDirs + 1].setSelected(false);
		}
	}

	private void actionOutputCheckBox(int i) {
		boolean allChecked = outputBox[nOutputDirs + 1].isSelected();
		if (i == nOutputDirs + 1 && allChecked) {
			for (int j = 0; j < nOutputDirs + 1; j++) {
				outputBox[j].setSelected(false);
			}
		}
		if (i != (nOutputDirs + 1)) {
			outputBox[nOutputDirs + 1].setSelected(false);
		}
	}
	
	private void actionButton() {
		// all folders is geselecteerd
		Boolean allSettingsChecked = settingsBox[nSettingsDirs + 1].isSelected();
		for (int i = 0; i < nSettingsDirs + 1; i++) {
			// all folders is geselecteerd of de desbetreffende folder
			if (settingsBox[i].isSelected() || allSettingsChecked) {
				cleanSettingsDir(i);
				settingsBox[i].setEnabled(false);
				settingsBox[i].setSelected(false);
			}
		}
		
		Boolean allOutputChecked = outputBox[nOutputDirs + 1].isSelected();
		for (int i = 0; i < nOutputDirs + 1; i++) {
			// all folders is geselecteerd of de desbetreffende folder
			if (outputBox[i].isSelected() || allOutputChecked) {
				cleanOutputDir(i);
				outputBox[i].setEnabled(false);
				outputBox[i].setSelected(false);
			}
		}
		
		int allSettingsEmpty = 0;
		for (int i = 0; i < nSettingsDirs + 1; i++) {
			if (!settingsBox[i].isEnabled()) {
				allSettingsEmpty++;
			}	
		}
		
		int allOutputEmpty = 0;
		for (int i = 0; i < nOutputDirs + 1; i++) {
			if (!outputBox[i].isEnabled()) {
				allOutputEmpty++;
			}	
		}
		
		
		if (allSettingsEmpty == nSettingsDirs + 1) {
			settingsBox[nSettingsDirs + 1].setEnabled(false);
		}
		if (allOutputEmpty == nOutputDirs + 1) {
			outputBox[nOutputDirs + 1].setEnabled(false);
		}
		if (allSettingsEmpty == nSettingsDirs + 1 
			&& allOutputEmpty == nOutputDirs + 1) {
			delete.setEnabled(false);
		}
	}
	
	private boolean isEmptySettingsDir(int i) {
		String folderName;
		if (i == 0) {
			folderName = rootSettingsFolderName;
		} else {
			folderName = rootSettingsFolderName + settingsDirs[i-1];
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
	
	private boolean isEmptyOutputDir(int i) {
		String folderName;
		if (i == 0) {
			folderName = rootOutputFolderName;
		} else {
			folderName = rootOutputFolderName + outputDirs[i-1];
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

	
	private boolean isEmptySettingsRoot() {
		File[] files = 
				new File(rootSettingsFolderName).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return name.toLowerCase().endsWith(OUTPUT_TYPE_LOG);
				}
			});
		
		if (files.length == 0) { 
			return true;
		}
		return false;
	}
	
	private boolean isEmptyOutputRoot() {
		File[] files = 
				new File(rootOutputFolderName).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return name.toLowerCase().endsWith(OUTPUT_TYPE_CSV);
				}
			});
		
		if (files.length == 0) { 
			return true;
		}
		return false;
	}
	
	private void cleanSettingsDir(int i) {
		String folderName;
		if (i == 0) {
			folderName = rootSettingsFolderName;
		} else {
			folderName = rootSettingsFolderName + settingsDirs[i-1];
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
	
	private void cleanOutputDir(int i) {
		String folderName;
		if (i == 0) {
			folderName = rootOutputFolderName;
		} else {
			folderName = rootOutputFolderName + outputDirs[i-1];
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

			if (f.isDirectory()) {
				String fname = f.getName();
				System.out.println(folderName + "/" + fname);
				File subfiles = new File(folderName + "/" + fname);
				for (File g : subfiles.listFiles()) {
					if (g.delete()) {
						System.out.println(fname + "." + g.getName() + " deleted");
					}

				}
			}
			if (f.delete()) {
				System.out.println(f.getName() + " deleted");
			}
		}
	}
}
