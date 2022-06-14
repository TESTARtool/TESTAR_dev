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


/**
 *  @author Sebastian Bauersfeld
 */
package org.testar.settingsdialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jsyntaxpane.DefaultSyntaxKit;
import org.testar.monkey.Util;

public class ProtocolEditor extends javax.swing.JDialog {
    private static final long serialVersionUID = 5922037291232012481L;

    private String settingsDir;
    private String protocolClass; // by urueda
    
    //public ProtocolEditor() {
    public ProtocolEditor(String settingsDir, String protocolClass) { // by urueda
        this.settingsDir = settingsDir;
    	this.protocolClass = protocolClass;
        DefaultSyntaxKit.initKit();
        initComponents();
        codeEditor.setContentType("text/java");
        //codeEditor.setText(Util.readFile(new File("./CustomProtocol.java")));
        codeEditor.setText(Util.readFile(getProtocolFile()));
    }

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        codeEditor = new javax.swing.JEditorPane();
        btnCompile = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        console = new javax.swing.JTextArea();

        // by mimarmu1
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
            //Util.saveToFile(codeEditor.getText(), "./CustomProtocol.java");
            // begin by urueda
            File protocolFile = getProtocolFile();
            Util.saveToFile(codeEditor.getText(), protocolFile.getPath());
            File compileDir = protocolFile.getParentFile();
			List<File> fileList = new ArrayList<File>(1); fileList.add(compileDir); // by urueda
            Util.compileJava(settingsDir, fileList,
            				 System.getProperty("java.class.path")); //";./monkey.jar");
            // end bu urueda     
            console.setText(console.getText() + "OK");
        } catch (IOException t) {
            console.setText(console.getText() + "\n" + t.getMessage());
        }
    }

    private File getProtocolFile() {
        File protocolFile ;
        if (Paths.get(protocolClass).isAbsolute()) {
            protocolFile = new File(protocolClass + ".java");
        }
        else {
            protocolFile = new File(settingsDir + protocolClass + ".java");
        }
        return protocolFile;
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
            //Util.saveToFile(codeEditor.getText(), "./CustomProtocol.java");
            Util.saveToFile(codeEditor.getText(), getProtocolFile().getPath()); // by urueda
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
