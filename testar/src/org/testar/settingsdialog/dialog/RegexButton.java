/***************************************************************************************************
 *
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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

package org.testar.settingsdialog.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

public class RegexButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 8671774402267591519L;

	private JTextArea textAreaRegex;

	public RegexButton(JTextArea textAreaRegex) {
		this.textAreaRegex = textAreaRegex;
		this.setText("Check Regex");
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(textAreaRegex.getText() != null && !textAreaRegex.getText().isEmpty()) {
			String regexExpression = textAreaRegex.getText();
			try {
				Pattern.compile(regexExpression);
			} catch (PatternSyntaxException exception) {
				// If the regex syntax is wrong, paint the red color on the button
				this.setBackground(Color.RED);
				// Clear possible previous highlights and highlight the wrong regex characters
				if(exception.getMessage() != null) {
					clearTextArea();
					highlightTextArea(exception);
				}
				return;
			}
			// If the regex syntax is correct, paint the green color on the button
			this.setBackground(Color.GREEN);
			// Then clear possible previous highlights
			clearTextArea();
		} else {
			// If the field is empty, remove the background colors
			this.setBackground(null);
			// Then clear possible previous highlights
			clearTextArea();
		}
	}

	private void highlightTextArea(PatternSyntaxException exception) {
		Highlighter highlighter = textAreaRegex.getHighlighter();
		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);

		int indexNumber = exception.getMessage().indexOf("near index") + 10;
		int endNumber = exception.getMessage().indexOf("\n");

		int regexPointer = Integer.parseInt(exception.getMessage().substring(indexNumber, endNumber).trim());
		try {
			highlighter.addHighlight(regexPointer, regexPointer + 1, painter);
		} catch (BadLocationException e) { }
	}

	private void clearTextArea() {
		textAreaRegex.getHighlighter().removeAllHighlights();
	}

}
