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
package nl.ou.testar.tgherkin;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import jsyntaxpane.DefaultSyntaxKit;
import nl.ou.testar.tgherkin.gen.TgherkinLexer;
import nl.ou.testar.tgherkin.gen.TgherkinParser;
import nl.ou.testar.tgherkin.model.Document;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.fruit.Util;

/**
 * Editor for the Tgherkin language.
 * 
 */
public class TgherkinEditor extends javax.swing.JDialog{	
	
	private static final long serialVersionUID = 3388045245131983479L;
	private String fileName;	
    
    /**
     * Constructor.
     * @param fileName name of the file with text in Tgherkin grammar.
     */
    public TgherkinEditor(String fileName) { 
        this.fileName = fileName;
    	DefaultSyntaxKit.initKit();
        initComponents();
        codeEditor.setContentType("text/plain");
        codeEditor.setText(Util.readFile(new File(fileName))); 
    }

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        codeEditor = new javax.swing.JEditorPane();
        btnCheck = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        console = new javax.swing.JTextArea();
        setTitle("Tgherkin editor");      
        
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


        btnCheck.setText("Save and Check");
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckActionPerformed(evt);
            }
        });
        btnCheck.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCheckKeyPressed(evt);
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
            .addComponent(btnCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }                       


    private void check() {
        try {
            console.setText("Lexing and parsing..." + System.getProperty("line.separator"));
            console.update(console.getGraphics());
            Util.saveToFile(codeEditor.getText(), fileName);
			ANTLRInputStream inputStream;
			inputStream = new ANTLRInputStream(new FileInputStream(fileName));
			TgherkinLexer lexer = new TgherkinLexer(inputStream);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
		    TgherkinParser parser = new TgherkinParser(tokens);
		    TgherkinErrorListener errorListener = new TgherkinErrorListener();
			parser.removeErrorListeners();
			parser.addErrorListener(errorListener);
			Document document = new DocumentBuilder().visitDocument(parser.document());
			List<String> errorList = errorListener.getErrorList();
			if (errorList.size() == 0) {
				// post-processing check
				console.setText(console.getText() + "Post-processing..." + System.getProperty("line.separator"));
				errorList = document.check();
			}
			if (errorList.size() == 0) {
				console.setText(console.getText() + "OK");
			}else {
				StringBuilder stringBuilder = new StringBuilder();
				for(String errorText : errorList) {
					stringBuilder.append(errorText);
				}
				console.setText(stringBuilder.toString());
			}
        } catch (Throwable t) {
            console.setText(console.getText() + System.getProperty("line.separator") + t.getMessage());
        }
    }


    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {                                           
        check();
    }                                          

    private void codeEditorKeyPressed(java.awt.event.KeyEvent evt) {                                      
        if ((evt.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                check();
            }
        }else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
            this.dispose();
        }
    }                                     

    private void formWindowClosed(java.awt.event.WindowEvent evt) {                                  
        try {
            Util.saveToFile(codeEditor.getText(), fileName); 
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }                                 

    private void consoleKeyPressed(java.awt.event.KeyEvent evt) {                                   
    	if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
    		this.dispose();
    	}    
    }                                  

    private void btnCheckKeyPressed(java.awt.event.KeyEvent evt) {                                      
    	if(evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
    		this.dispose();
    	}    
    }                                     

    private javax.swing.JButton btnCheck;
    private javax.swing.JEditorPane codeEditor;
    private javax.swing.JTextArea console;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;

}
