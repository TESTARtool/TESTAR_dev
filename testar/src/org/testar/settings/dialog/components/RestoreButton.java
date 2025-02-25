/***************************************************************************************************
 *
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
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

package org.testar.settings.dialog.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextArea;

public class RestoreButton extends JButton implements ActionListener {
	private static final long serialVersionUID = -1959638418354471229L;

	private JTextArea textAreaRegex;
	private final String SUSPICIOUS_PATTERN = ".*[eE]rror.*|.*[eE]xcepti[o?]n.*";

	public RestoreButton(JTextArea textAreaRegex) {
		this.textAreaRegex = textAreaRegex;
		this.setText("Restore");
		setSize(75, 27);
		addActionListener(this);
	}

	public void setPosition(int x, int y) {
		setLocation(x, y);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(textAreaRegex.getText() == null || textAreaRegex.getText().isEmpty()) {
			textAreaRegex.setText(SUSPICIOUS_PATTERN);
		}
	}
}
