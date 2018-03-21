/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.monkey;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jsyntaxpane.DefaultSyntaxKit;

import org.fruit.Util;

public class ProtocolEditor extends javax.swing.JDialog {
    private static final long serialVersionUID = 5922037291232012481L;

    private String protocolClass; // by urueda
    
    //public ProtocolEditor() {
    public ProtocolEditor(String protocolClass) { // by urueda
    	this.protocolClass = protocolClass;
        DefaultSyntaxKit.initKit();
        initComponents();
        codeEditor.setContentType("text/java");
        //codeEditor.setText(Util.readFile(new File("./CustomProtocol.java")));
        codeEditor.setText(Util.readFile(new File("./settings/" + protocolClass + ".java"))); // by urueda
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
            Util.saveToFile(codeEditor.getText(), "./settings/" + this.protocolClass + ".java");
            File compileDir = new File("./settings/" + new StringTokenizer(this.protocolClass,"/").nextToken());
			List<File> fileList = new ArrayList<File>(1); fileList.add(compileDir); // by urueda
            Util.compileJava(fileList,
            				 System.getProperty("java.class.path")); //";./monkey.jar");
            // end bu urueda     
            console.setText(console.getText() + "OK");
        } catch (Throwable t) {
            console.setText(console.getText() + "\n" + t.getMessage());
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
            //Util.saveToFile(codeEditor.getText(), "./CustomProtocol.java");
            Util.saveToFile(codeEditor.getText(), "./settings/" + this.protocolClass + ".java"); // by urueda
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
