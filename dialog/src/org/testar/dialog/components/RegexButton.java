/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog.components;

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
		setSize(105, 27);
		addActionListener(this);
	}

	public void setPosition(int x, int y) {
		setLocation(x, y);
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
