/***************************************************************************************************
 *
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.settings.dialog;

import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;

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
	 * Retrieve information from the Advanced GUI.
	 * @param settings reference to the object where the settings will be stored.
	 */
	@Override
	public void extractInformation(final Settings settings) {
		settings.set(ConfigTags.KeyBoardListener, keyBoardListenCheck.isSelected());
	}

}