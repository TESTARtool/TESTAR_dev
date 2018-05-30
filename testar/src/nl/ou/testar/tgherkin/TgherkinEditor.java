/**
 *  Tgherkin editor.
 */
package nl.ou.testar.tgherkin;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

import nl.ou.testar.tgherkin.gen.TgherkinParser;
import nl.ou.testar.tgherkin.model.Document;

import org.fruit.Util;

/**
 * TgherkinEditor is responsible for editing Tgherkin documents.
 * 
 */
public class TgherkinEditor extends javax.swing.JDialog{	

	/**
	 * Margin width in pixels.
	 */
	public static final int MARGIN_WIDTH_PX = 28;
	private static final long serialVersionUID = 3388045245131983479L;
	private String fileName;	

	/**
	 * Constructor.
	 * @param fileName name of the file with text in Tgherkin grammar.
	 */
	public TgherkinEditor(String fileName) { 
		this.fileName = fileName;
		initComponents();
		codeEditor.setText(Util.readFile(new File(fileName))); 
	}

	private void initComponents() {
		jScrollPane1 = new javax.swing.JScrollPane();
		codeEditor = new JTextPane(new TgherkinStyledDocument());
		codeEditor.setContentType("text/plain");
		codeEditor.setFont(new Font("Courier New", Font.PLAIN, 12));
		btnCheck = new javax.swing.JButton();
		jScrollPane2 = new javax.swing.JScrollPane();
		console = new javax.swing.JTextArea();
		setTitle("Tgherkin editor");      

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new java.awt.Dimension(800, 800));
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
		LineNumbersView lineNumbers = new LineNumbersView(codeEditor);
		jScrollPane1.setViewportView(codeEditor);
		jScrollPane1.setRowHeaderView(lineNumbers);
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
			console.setText("Saving file..." + System.getProperty("line.separator"));
			console.update(console.getGraphics());
			Util.saveToFile(codeEditor.getText(), fileName);
			console.setText("Lexing and parsing..." + System.getProperty("line.separator"));
			console.update(console.getGraphics());
			TgherkinParser parser = Utils.getTgherkinParser(codeEditor.getText());
			TgherkinErrorListener errorListener = new TgherkinErrorListener();
			parser.addErrorListener(errorListener);
			Document document = new DocumentBuilder().visitDocument(parser.document());
			List<String> errorList = errorListener.getErrorList();
			if (errorList.size() == 0) {
				// post-processing check
				console.setText(console.getText() + "Post-processing..." + System.getProperty("line.separator"));
				console.update(console.getGraphics());
				errorList = document.check();
			}
			if (errorList.size() == 0) {
				console.setText(console.getText() + "OK");
			}else {
				StringBuilder stringBuilder = new StringBuilder();
				for(String errorText : errorList) {
					stringBuilder.append(errorText);
				}
				console.setText(console.getText() + stringBuilder.toString());
			}
			console.update(console.getGraphics());
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
			ioe.printStackTrace();
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
	private javax.swing.JTextPane codeEditor;
	private javax.swing.JTextArea console;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;

	// Copyright rememberjava.com. Licensed under GPL 3. See http://rememberjava.com/license */
	/**
	 * Left hand side RowHeaderView for a JEditorPane in a JScrollPane. 
	 * Highlights the currently selected line. Handles line wrapping, frame resizing.
	 */
	class LineNumbersView extends JComponent implements DocumentListener, CaretListener, ComponentListener {

		private static final long serialVersionUID = 1L;
		private JTextComponent editor;
		private Font font;

		/**
		 * Constructor.
		 * @param editor given editor
		 */
		LineNumbersView(JTextComponent editor) {
			this.editor = editor;
			editor.getDocument().addDocumentListener(this);
			editor.addComponentListener(this);
			editor.addCaretListener(this);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Rectangle clip = g.getClipBounds();
			int startOffset = editor.viewToModel(new Point(0, clip.y));
			int endOffset = editor.viewToModel(new Point(0, clip.y + clip.height));
			while (startOffset <= endOffset) {
				try {
					String lineNumber = getLineNumber(startOffset);
					if (lineNumber != null) {
						int x = getInsets().left + 2;
						int y = getOffsetY(startOffset);
						if (font == null) {
							font = new Font(Font.MONOSPACED, Font.BOLD, editor.getFont().getSize()); 
						}
						g.setFont(font);
						if (isCurrentLine(startOffset)) {
							g.setColor(Color.RED);
						}else {
							g.setColor(Color.BLACK);
						}
						g.drawString(lineNumber, x, y);
					}
					startOffset = Utilities.getRowEnd(editor, startOffset) + 1;
				} catch (BadLocationException e) {
					e.printStackTrace();
					// ignore and continue
				}
			}
		}

		/**
		 * Returns the line number of the element based on the given (start) offset
		 * in the editor model. Returns null if no line number should or could be
		 * provided (e.g. for wrapped lines).
		 */
		private String getLineNumber(int offset) {
			Element root = editor.getDocument().getDefaultRootElement();
			int index = root.getElementIndex(offset);
			Element line = root.getElement(index);
			if (line.getStartOffset() == offset) {
				return String.format("%3d", index + 1);
			}
			return null;
		}

		/**
		 * Returns the y axis position for the line number belonging to the element
		 * at the given (start) offset in the model.
		 */
		private int getOffsetY(int offset) throws BadLocationException {
			FontMetrics fontMetrics = editor.getFontMetrics(editor.getFont());
			int descent = fontMetrics.getDescent();
			Rectangle r = editor.modelToView(offset);
			int y = r.y + r.height - descent;
			return y;
		}

		/**
		 * Returns true if the given start offset in the model is the selected (by
		 * cursor position) element.
		 */
		private boolean isCurrentLine(int offset) {
			int caretPosition = editor.getCaretPosition();
			Element root = editor.getDocument().getDefaultRootElement();
			return root.getElementIndex(offset) == root.getElementIndex(caretPosition);
		}

		/**
		 * Schedules a refresh of the line number margin on a separate thread.
		 */
		private void documentChanged() {
			SwingUtilities.invokeLater(() -> {
				repaint();
			});
		}

		/**
		 * Updates the size of the line number margin based on the editor height.
		 */
		private void updateSize() {
			Dimension size = new Dimension(MARGIN_WIDTH_PX, editor.getHeight());
			setPreferredSize(size);
			setSize(size);
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			documentChanged();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			documentChanged();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			documentChanged();
		}

		@Override
		public void caretUpdate(CaretEvent e) {
			documentChanged();
		}

		@Override
		public void componentResized(ComponentEvent e) {
			updateSize();
			documentChanged();
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentShown(ComponentEvent e) {
			updateSize();
			documentChanged();
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}
	}

}