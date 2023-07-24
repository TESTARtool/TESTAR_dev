package org.testar.settingsdialog.dialog;

import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.settingsdialog.SettingsPanel;

import javax.swing.*;


public class AdvancedPanel extends SettingsPanel {
	private JCheckBox keyBoardListenCheck;
	public AdvancedPanel() {
		setLayout(null);
		
	    keyBoardListenCheck = new JCheckBox("Listen to Keyboard shortcuts");
	    keyBoardListenCheck.setBounds(10, 12, 192, 21);
	    add(keyBoardListenCheck);
		
	}
    public void populateFrom(final Settings settings) {
    	keyBoardListenCheck.setSelected(settings.get(ConfigTags.KeyBoardListener));
    }

    /**
     * Retrieve information from the Filter  GUI.
     * @param settings reference to the object where the settings will be stored.
     */
    @Override
    public void extractInformation(final Settings settings) {
    	settings.set(ConfigTags.KeyBoardListener, keyBoardListenCheck.isSelected());
    }
	
}