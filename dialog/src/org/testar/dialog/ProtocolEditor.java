/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import jsyntaxpane.DefaultSyntaxKit;
import org.testar.core.util.Util;

public class ProtocolEditor extends javax.swing.JDialog {
	private static final long serialVersionUID = 5922037291232012481L;

	private String settingsDir;
	private String protocolClass;

	public ProtocolEditor(String settingsDir, String protocolClass) {
		this.settingsDir = settingsDir;
		this.protocolClass = protocolClass;
		enableDefaultSyntaxKit();
		initComponents();
		codeEditor.setContentType("text/java");
		File protocolFile = getProtocolFile();
		if(!protocolFile.exists()) {
			String msg = "No protocol java files found in '" + protocolClass + "'"
					+ System.getProperty("line.separator")
					+ "Please, edit the test.settings file and update the ProtocolClass setting adequately";
			System.err.println(msg);
		} else {
			codeEditor.setText(Util.readFile(protocolFile));
		}
	}

	/**
	 * Enable DefaultSyntaxKit when Nashorn Script Engine is detected in the JVM. 
	 * If not, inform the user in the console and start Edit Protocol without code highlights. 
	 */
	private void enableDefaultSyntaxKit() {
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("nashorn");
			if (engine != null) {
				DefaultSyntaxKit.initKit();
			} else {
				System.out.println("WARNING: Edit Protocol will not highlight the Java code");
				System.out.println("This is because Nashorn Script Engine is not detected in the JDK");
				System.out.println("To highlight the code in the Edit Protocol feature, we recommend using Java JDK 8 to 14");
			}
		} catch (Exception e) {
			System.err.println("ERROR: Trying to verify Nashorn Script Engine with ScriptEngineManager");
		}
	}

	private void initComponents() {
		jScrollPane1 = new javax.swing.JScrollPane();
		codeEditor = new javax.swing.JEditorPane();
		btnCompile = new javax.swing.JButton();
		jScrollPane2 = new javax.swing.JScrollPane();
		console = new javax.swing.JTextArea();

		setTitle("Protocol editor");      

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new java.awt.Dimension(800, 400));
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosed(java.awt.event.WindowEvent evt) {
				formWindowClosed(evt);
			}
		});

		codeEditor.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				codeEditorKeyPressed(evt);
			}
		});
		jScrollPane1.setViewportView(codeEditor);
		jScrollPane1.getVerticalScrollBar().setUnitIncrement(5);


		btnCompile.setText("Save and Compile");
		btnCompile.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCompileActionPerformed(evt);
			}
		});
		btnCompile.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				btnCompileKeyPressed(evt);
			}
		});

		console.setColumns(20);
		console.setLineWrap(true);
		console.setRows(5);
		console.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				consoleKeyPressed(evt);
			}
		});
		jScrollPane2.setViewportView(console);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jScrollPane1)
				.addComponent(jScrollPane2)
				.addComponent(btnCompile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(btnCompile)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
				);

		pack();
	}                       

	private void compile() {
		try {
			console.setText("Compiling...");
			console.update(console.getGraphics());
			File protocolFile = getProtocolFile();
			Util.saveToFile(codeEditor.getText(), protocolFile.getPath());
			File compileDir = protocolFile.getParentFile();
			List<File> fileList = new ArrayList<File>(1); fileList.add(compileDir);
			Util.compileJava(settingsDir, fileList, System.getProperty("java.class.path"));  
			console.setText(console.getText() + "OK");
		} catch (IOException | RuntimeException t) {
			if(t.getMessage() != null) {
				console.setText(console.getText() + "\n" + t.getMessage());
			}
		}
	}

	private File getProtocolFile() {
		if (Paths.get(protocolClass).isAbsolute()) {
			return new File(protocolClass + ".java");
		}
		else {
			return new File(settingsDir + protocolClass + ".java");
		}
	}

	private void btnCompileActionPerformed(java.awt.event.ActionEvent evt) {
		compile();
	}                                          

	private void codeEditorKeyPressed(java.awt.event.KeyEvent evt) {                                      
		if ((evt.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
			if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
				compile();
			}
		}else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
			this.dispose();
		}
	}                                     

	private void formWindowClosed(java.awt.event.WindowEvent evt) {                                  
		try {
			Util.saveToFile(codeEditor.getText(), getProtocolFile().getPath());
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}                                 

	private void consoleKeyPressed(java.awt.event.KeyEvent evt) {                                   
		if(evt.getKeyCode() == KeyEvent.VK_ESCAPE)
			this.dispose();
	}                                  

	private void btnCompileKeyPressed(java.awt.event.KeyEvent evt) {                                      
		if(evt.getKeyCode() == KeyEvent.VK_ESCAPE)
			this.dispose();
	}                                     

	private javax.swing.JButton btnCompile;
	private javax.swing.JEditorPane codeEditor;
	private javax.swing.JTextArea console;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
}
