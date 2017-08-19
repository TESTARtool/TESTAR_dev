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

import static org.fruit.monkey.ConfigTags.PathToReplaySequence;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Action;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Finder;
import org.fruit.alayer.Image;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.StdState;
import org.fruit.alayer.Taggable;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Visualizer;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.NOP;
import org.fruit.alayer.visualizers.ShapeVisualizer;


public class SequenceViewer extends javax.swing.JFrame{

	private static final long serialVersionUID = -7545369239319448135L;
	ObjectInputStream stream;
	BufferedImage buffer = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_ARGB);
	int stateCount;
	
	// begin by urueda
	private List<Taggable> cachedSequence;
	private int sequenceViewIndex;
	private static final int DIRECTION_NEXT = 1, DIRECTION_PREVIOUS = -1;
	// end by urueda

	public SequenceViewer(Settings settings) {
		this.settings = settings;
		initComponents();
		this.setBounds(0, 0, 1024, 768);
		// begin by urueda
		this.setTitle("Sequence viewer"); // by urueda
		cachedSequence = new ArrayList<Taggable>();
		sequenceViewIndex = -1; stateCount = -1;
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// end by urueda
	}

	private void initComponents() {
		panel1 = new java.awt.Panel();
		btnBegin = new java.awt.Button(); // by urueda
		btnPrev = new java.awt.Button(); // by urueda
		btnNext = new java.awt.Button();
		btnEnd = new java.awt.Button(); // by urueda
		lblInfo = new java.awt.Label();
		panel2 = new java.awt.Panel();
		scrollPane1 = new java.awt.ScrollPane();

		display = new Canvas(){
			private static final long serialVersionUID = 5259423015295162447L;
			public void paint(Graphics g){
				g.setColor(java.awt.Color.BLACK);
				g.fillRect(0, 0, display.getWidth(), display.getHeight());
				double wfactor = (double)(display.getWidth()) / (double)buffer.getWidth();
				double hfactor = (double)(display.getHeight()) / (double)buffer.getHeight();
				double factor = Math.min(1.0, Math.min(wfactor, hfactor));					
				g.drawImage(buffer, 0, 0, (int)(buffer.getWidth() * factor), (int)(buffer.getHeight() * factor), null);
			}
		};


		addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				display.setBounds(0, 0, buffer.getWidth(), buffer.getHeight());
			}
		});

		// begin by urueda
		btnBegin.setLabel("Begin");
		btnBegin.setName("");
		btnBegin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnBeginActionPerformed(evt);
			}
		});
		btnPrev.setLabel("Previous");
		btnPrev.setName("");
		btnPrev.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPrevActionPerformed(evt);
			}
		});
		// end by urueda

		btnNext.setLabel("Next");
		btnNext.setName("");
		btnNext.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNextActionPerformed(evt);
			}
		});

		// begin by urueda
		btnEnd.setLabel("End");
		btnEnd.setName("");
		btnEnd.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnEndActionPerformed(evt);
			}
		});
		// end by urueda
		
		lblInfo.setText("");

		javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
		panel1.setLayout(panel1Layout);
		panel1Layout.setHorizontalGroup(
				panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panel1Layout.createSequentialGroup()
						.addComponent(btnBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // by urueda
						.addComponent(btnPrev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // by urueda
						.addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(btnEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // by urueda
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addContainerGap())
				);
		panel1Layout.setVerticalGroup(
				panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panel1Layout.createSequentialGroup()
						.addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // by urueda
								.addComponent(btnPrev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // by urueda
								.addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // by urueda
								.addGap(0, 10, Short.MAX_VALUE)))
				);

		add(panel1, java.awt.BorderLayout.NORTH);

		panel2.setLayout(new java.awt.BorderLayout());

		scrollPane1.add(display);

		panel2.add(scrollPane1, java.awt.BorderLayout.CENTER);

		add(panel2, java.awt.BorderLayout.CENTER);

		pack();
		setVisible(true);
		//updateInfo("");
	}                      

	// refactor by urueda
	private void postActionPerformed(){
		display.setBounds(0, 0, buffer.getWidth(), buffer.getHeight());
		display.repaint();
		display.paint(display.getGraphics());
		pack();		
	}

	// by urueda
	private void btnBeginActionPerformed(java.awt.event.ActionEvent evt) {                                        
		try {
			beginPic();
			postActionPerformed();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException(e1);
		}
	}                	

	// by urueda
	private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {                                        
		try {
			prevPic();
			postActionPerformed();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException(e1);
		}
	}                	
	
	private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {                                        
		try {
			nextPic();
			postActionPerformed();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException(e1);
		}
	}                

	// by urueda
	private void btnEndActionPerformed(java.awt.event.ActionEvent evt) {                                        
		try {
			endPic();
			postActionPerformed();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException(e1);
		}
	}                	
	
	// refactor by urueda
	public void movePic(Taggable fragment, int direction) throws IOException, ClassNotFoundException{
		State state = fragment.get(Tags.SystemState, new StdState());

		//Image img = state.get(Tags.Screenshot, null);
		// begin by urueda
		String scrshotPath = state.get(Tags.ScreenshotPath, null);
		Image img = AWTCanvas.fromFile(scrshotPath);
		// end by urueda
		if(img == null){
			AWTCanvas awtc = new AWTCanvas(0.0, 0.0, new BufferedImage(1024, 768, BufferedImage.TYPE_INT_ARGB), AWTCanvas.StorageFormat.PNG, 1.0);
			awtc.begin();
			Pen blackFill = Pen.newPen().setColor(Color.Black).setFillPattern(FillPattern.Solid).build();
			awtc.rect(blackFill, 0, 0, awtc.width(), awtc.height());
			Pen whiteText = Pen.newPen().setColor(Color.White).setFillPattern(FillPattern.Solid).setFontSize(100).build();
			awtc.text(whiteText, 0, awtc.height() / 2.0, 0, "No screenshot available!");
			awtc.end();
			img = awtc;
		}

		if(img.width() != buffer.getWidth() || img.height() != buffer.getHeight())
			buffer = new BufferedImage((int)img.width(), (int)img.height(), BufferedImage.TYPE_INT_ARGB);

		AWTCanvas cv = new AWTCanvas(0.0, 0.0, buffer, AWTCanvas.StorageFormat.PNG, 1);
		cv.begin();
		img.paint(cv, Rect.from(0, 0, img.width(), img.height()), Rect.from(0, 0, cv.width(), cv.height()));

		Action a = fragment.get(Tags.ExecutedAction, new NOP());
		//Visualizer v = a.get(Tags.Visualizer, Util.NullVisualizer);
		//v.run(state, cv, Pen.startFrom(Pen.DefaultPen).setColor(Color.Red).setFillPattern(FillPattern.Solid).build());
		// begin by urued
		if (state.childCount() > 0){
			Shape sutShape = state.child(0).get(Tags.Shape);
			List<Finder> targets = a.get(Tags.Targets, null);
			if (targets != null){
				Assert.notNull(sutShape);
				Visualizer v;
				Widget w;
				Shape s, vShape;
				Pen pen = Pen.startFrom(Pen.PEN_DEFAULT)
						.setColor(Color.Red)
						.setFillPattern(FillPattern.Stroke)
						.setStrokeWidth(2.0)
						.build();
				for (Finder f : targets){
					w = f.apply(state);
					s = w.get(Tags.Shape);
					Assert.notNull(s);
					vShape = Rect.from(s.x() - sutShape.x(), s.y() - sutShape.y(), // absolute to SUT' relative
									   s.width(), s.height());
					v = new ShapeVisualizer(pen, vShape, null, 0.0, 0.0);
					v.run(state, cv, pen);
				}
			}
		}
		Verdict verdict = fragment.get(Tags.OracleVerdict, null);
		if (verdict != null){
			Pen penVerdict = Pen.startFrom(Pen.PEN_DEFAULT)
					.setColor(Color.Blue)
					.setFillPattern(FillPattern.Stroke)
					.setStrokeWidth(3.0)
					.build();
			verdict.visualizer().run(state, cv, penVerdict);
		}
		// end by urueda
		cv.end();
		
		stateCount += direction; // by urueda
		
		updateInfo(a.get(Tags.Desc, "<no description available>"));		
		
	}

	// by urueda
	public void beginPic() throws IOException, ClassNotFoundException{
		synchronized(cachedSequence){			
			if (sequenceViewIndex <= 0)
				return; // next must be invoked first! 
	
			int steps = sequenceViewIndex;
			sequenceViewIndex = 0;		
			movePic(cachedSequence.get(sequenceViewIndex),-steps); // refactor
		}
	}

	// by urueda
	public void prevPic() throws IOException, ClassNotFoundException{
		synchronized(cachedSequence){			
			if (sequenceViewIndex <= 0)
				return; // next must be invoked first! 
	
			sequenceViewIndex--;		
			movePic(cachedSequence.get(sequenceViewIndex),DIRECTION_PREVIOUS); // refactor
		}
	}

	public void nextPic() throws IOException, ClassNotFoundException{
		synchronized(cachedSequence){ // by urueda			
			if(stream == null){
				FileInputStream fis = new FileInputStream(new File(settings.get(PathToReplaySequence)));
				//BufferedInputStream bis = new BufferedInputStream(fis);
				BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(fis)); // by urueda
				stream = new ObjectInputStream(bis);
			}

			Taggable fragment = null;

			// begin by urueda
			if (sequenceViewIndex < cachedSequence.size() - 1)
				fragment = cachedSequence.get(sequenceViewIndex + 1);
			else {
				// end by urueda
				try{
					fragment = (Taggable) stream.readObject();
					cachedSequence.add(fragment); // by urueda
				} catch (IOException ioe){ return; }
			}

			// begin by urueda
			sequenceViewIndex++;
			movePic(fragment,DIRECTION_NEXT); // refactor
			// end by urueda
		}
	}

	// by urueda
	public void endPic() throws IOException, ClassNotFoundException{
		synchronized(cachedSequence){		
			if(stream == null){
				FileInputStream fis = new FileInputStream(new File(settings.get(PathToReplaySequence)));
				//BufferedInputStream bis = new BufferedInputStream(fis);
				BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(fis)); // by urueda
				stream = new ObjectInputStream(bis);
			}

			int steps = cachedSequence.size() - sequenceViewIndex - 1;
			sequenceViewIndex = cachedSequence.size() - 1;
			
			Taggable fragment = null;
			try{
				do{
					fragment = (Taggable) stream.readObject();
					cachedSequence.add(fragment); // by urueda
					steps++;
					sequenceViewIndex++;
				}while(true); // til end of file
			} catch (IOException ioe) { // end of file reached?
				movePic(fragment == null ? cachedSequence.get(sequenceViewIndex) : fragment, steps); // refactor
			}			
		}
	}	

	public void updateInfo(String actionText){
		lblInfo.setText("State: " + Integer.toString(stateCount) + "  Action: " + actionText);
	}

	Settings settings;

	// prevent thread finish while dialog is visible
	public void run() {
		while(isShowing()){
			Util.pause(1);
		}
	}

	private java.awt.Button btnBegin; // by urueda
	private java.awt.Button btnPrev; // by urueda
	private java.awt.Button btnNext;
	private java.awt.Button btnEnd; // by urueda
	private java.awt.Canvas display;
	private java.awt.Label lblInfo;
	private java.awt.Panel panel1;
	private java.awt.Panel panel2;
	private java.awt.ScrollPane scrollPane1;
}