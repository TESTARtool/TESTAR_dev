/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog.components;

import javax.swing.*;
import javax.swing.undo.*;
import java.awt.event.*;

public class UndoTextArea extends JTextArea {
	private static final long serialVersionUID = 290672486316436268L;

	private UndoManager undoManager;

	public UndoTextArea() {
		super();
		initUndoRedo();
	}

	private void initUndoRedo() {
		undoManager = new UndoManager();
		this.getDocument().addUndoableEditListener(undoManager);

		InputMap inputMap = this.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap actionMap = this.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke("control Z"), "undo");
		actionMap.put("undo", new AbstractAction() {
			private static final long serialVersionUID = -8861247600878908377L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (undoManager.canUndo()) {
					undoManager.undo();
				}
			}
		});

		inputMap.put(KeyStroke.getKeyStroke("control Y"), "redo");
		actionMap.put("redo", new AbstractAction() {
			private static final long serialVersionUID = -5258736629980800355L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (undoManager.canRedo()) {
					undoManager.redo();
				}
			}
		});
	}

	public void setInitialText(String text) {
		this.setText(text);
		undoManager.discardAllEdits(); // Mark this text as the baseline
	}

	public void undo() {
		if (undoManager.canUndo()) {
			undoManager.undo();
		}
	}

	public void redo() {
		if (undoManager.canRedo()) {
			undoManager.redo();
		}
	}
}
