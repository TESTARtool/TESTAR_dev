/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
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
	
	private List<Taggable> cachedSequence;
	private int sequenceViewIndex;
	private static final int DIRECTION_NEXT = 1, DIRECTION_PREVIOUS = -1;

	public SequenceViewer(Settings settings) throws ClassNotFoundException, IOException{
		this.settings = settings;
		initComponents();
		this.setBounds(0, 0, 1024, 768);

		this.setTitle("Sequence viewer");
		cachedSequence = new ArrayList<Taggable>();
		sequenceViewIndex = -1; stateCount = -1;
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		//Start with the first picture instead a black screen
		nextPic();
		postActionPerformed();

		this.run();
	}

	private void initComponents(){
		panel1 = new java.awt.Panel();
		btnBegin = new java.awt.Button();
		btnPrev = new java.awt.Button();
		btnNext = new java.awt.Button();
		btnEnd = new java.awt.Button();
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

		btnBegin.setLabel("Begin");
		btnBegin.setName("");
		btnBegin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnBeginActionPerformed(evt);
				} catch (ClassNotFoundException | IOException e) {
					System.out.println(e+": Error reading selected sequence");
					setVisible(false);
				}
			}
		});
		btnPrev.setLabel("Previous");
		btnPrev.setName("");
		btnPrev.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				try {
					btnPrevActionPerformed(evt);
				} catch (ClassNotFoundException | IOException e) {
					System.out.println(e+": Error reading selected sequence");
					setVisible(false);
				}
			}
		});

		btnNext.setLabel("Next");
		btnNext.setName("");
		btnNext.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnNextActionPerformed(evt);
				} catch (ClassNotFoundException | IOException e) {
					System.out.println(e+": Error reading selected sequence");
					setVisible(false);
				}
			}
		});

		btnEnd.setLabel("End");
		btnEnd.setName("");
		btnEnd.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnEndActionPerformed(evt);
				} catch (ClassNotFoundException | IOException e) {
					System.out.println(e+": Error reading selected sequence");
					setVisible(false);
				}
			}
		});
		
		lblInfo.setText("");

		javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
		panel1.setLayout(panel1Layout);
		panel1Layout.setHorizontalGroup(
				panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panel1Layout.createSequentialGroup()
						.addComponent(btnBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(btnPrev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(btnEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addContainerGap())
				);
		panel1Layout.setVerticalGroup(
				panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panel1Layout.createSequentialGroup()
						.addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnPrev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

	private void postActionPerformed(){
		display.setBounds(0, 0, buffer.getWidth(), buffer.getHeight());
		display.repaint();
		display.paint(display.getGraphics());
		pack();		
	}

	private void btnBeginActionPerformed(java.awt.event.ActionEvent evt) throws ClassNotFoundException, IOException {                                        
			beginPic();
			postActionPerformed();
	}                	

	private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) throws ClassNotFoundException, IOException {                                        
			prevPic();
			postActionPerformed();
	}                	
	
	private void btnNextActionPerformed(java.awt.event.ActionEvent evt) throws ClassNotFoundException, IOException {                                        
			nextPic();
			postActionPerformed();
	}                

	private void btnEndActionPerformed(java.awt.event.ActionEvent evt) throws ClassNotFoundException, IOException {                                        
			endPic();
			postActionPerformed();
	}                	
	
	public void movePic(Taggable fragment, int direction) throws IOException, ClassNotFoundException{
		State state = fragment.get(Tags.SystemState, new StdState());

		String scrshotPath = state.get(Tags.ScreenshotPath, null);
		Image img = AWTCanvas.fromFile(scrshotPath);

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

		cv.end();
		
		stateCount += direction;
		
		updateInfo(a.get(Tags.Desc, "<no description available>"));		
		
	}

	public void beginPic() throws IOException, ClassNotFoundException{
		synchronized(cachedSequence){			
			if (sequenceViewIndex <= 0)
				return; // next must be invoked first! 
	
			int steps = sequenceViewIndex;
			sequenceViewIndex = 0;		
			movePic(cachedSequence.get(sequenceViewIndex),-steps);
		}
	}

	public void prevPic() throws IOException, ClassNotFoundException{
		synchronized(cachedSequence){			
			if (sequenceViewIndex <= 0)
				return; // next must be invoked first! 
	
			sequenceViewIndex--;		
			movePic(cachedSequence.get(sequenceViewIndex),DIRECTION_PREVIOUS);
		}
	}

	public void nextPic() throws IOException, ClassNotFoundException{
		synchronized(cachedSequence){			
			if(stream == null){
				FileInputStream fis = new FileInputStream(new File(settings.get(PathToReplaySequence)));
				//BufferedInputStream bis = new BufferedInputStream(fis);
				BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(fis));
				stream = new ObjectInputStream(bis);
			}

			Taggable fragment = null;

			if (sequenceViewIndex < cachedSequence.size() - 1)
				fragment = cachedSequence.get(sequenceViewIndex + 1);
			else {
				//This try catch is used for the case of reaching the end of the fragment
				try{
					fragment = (Taggable) stream.readObject();
					cachedSequence.add(fragment);
				} catch (IOException ioe){ return; }
			}

			sequenceViewIndex++;
			movePic(fragment,DIRECTION_NEXT);
		}
	}

	public void endPic() throws IOException, ClassNotFoundException{
		synchronized(cachedSequence){		
			if(stream == null){
				FileInputStream fis = new FileInputStream(new File(settings.get(PathToReplaySequence)));
				//BufferedInputStream bis = new BufferedInputStream(fis);
				BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(fis));
				stream = new ObjectInputStream(bis);
			}

			int steps = cachedSequence.size() - sequenceViewIndex - 1;
			sequenceViewIndex = cachedSequence.size() - 1;
			
			Taggable fragment = null;
			
			//This try catch is used for the case of reaching the end of the fragment
			try{
				do{
					fragment = (Taggable) stream.readObject();
					cachedSequence.add(fragment);
					steps++;
					sequenceViewIndex++;
				}while(true); // til end of file
			} catch (IOException ioe) { // end of file reached?
				movePic(fragment == null ? cachedSequence.get(sequenceViewIndex) : fragment, steps);
			}		
		}
	}	

	public void updateInfo(String actionText){
		lblInfo.setText("State: " + Integer.toString(stateCount+1) + "  Action: " + actionText);
	}

	Settings settings;

	// prevent thread finish while dialog is visible
	private void run() {
		while(isShowing()){
			Util.pause(1);
		}
	}

	private java.awt.Button btnBegin;
	private java.awt.Button btnPrev;
	private java.awt.Button btnNext;
	private java.awt.Button btnEnd;
	private java.awt.Canvas display;
	private java.awt.Label lblInfo;
	private java.awt.Panel panel1;
	private java.awt.Panel panel2;
	private java.awt.ScrollPane scrollPane1;
}
