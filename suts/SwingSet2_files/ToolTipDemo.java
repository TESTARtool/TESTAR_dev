/*
 * %W% %E%
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Oracle or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * %W% %E%
 */


import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.filechooser.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import java.applet.*;
import java.net.*;

/**
 * ToolTip Demo
 *
 * @version %I% %G%
 * @author Jeff Dinkins
 */
public class ToolTipDemo extends DemoModule {

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
	ToolTipDemo demo = new ToolTipDemo(null);
	demo.mainImpl();
    }

    /**
     * ToolTipDemo Constructor
     */
    public ToolTipDemo(SwingSet2 swingset) {
	// Set the title for this demo, and an icon used to represent this
	// demo inside the SwingSet2 app.
	super(swingset, "ToolTipDemo", "toolbar/ToolTip.gif");

	// Set the layout manager.
	JPanel p = getDemoPanel();
	p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
	p.setBackground(Color.white);

	// Create a Cow to put in the center of the panel.
	Cow cow = new Cow();
	cow.getAccessibleContext().setAccessibleName(getString("ToolTipDemo.accessible_cow"));

	// Set the tooltip text. Note, for fun, we also set more tooltip text
	// descriptions for the cow down below in the Cow.contains() method.
	cow.setToolTipText(getString("ToolTipDemo.cow"));

	// Add the cow midway down the panel
	p.add(Box.createRigidArea(new Dimension(1, 150)));
	p.add(cow);
    }


    class Cow extends JLabel {
	Polygon cowgon = new Polygon();
	
	public Cow() {
	    super(createImageIcon("tooltip/cow.gif", getString("ToolTipDemo.bessie")));
	    setAlignmentX(CENTER_ALIGNMENT);

	    // Set polygon points that define the outline of the cow.
	    cowgon.addPoint(3,20);    cowgon.addPoint(44,4);
	    cowgon.addPoint(79,15);   cowgon.addPoint(130,11);
	    cowgon.addPoint(252,5);   cowgon.addPoint(181,17);
	    cowgon.addPoint(301,45);  cowgon.addPoint(292,214);
	    cowgon.addPoint(269,209); cowgon.addPoint(266,142);
	    cowgon.addPoint(250,161); cowgon.addPoint(235,218);
	    cowgon.addPoint(203,206); cowgon.addPoint(215,137);
	    cowgon.addPoint(195,142); cowgon.addPoint(143,132);
	    cowgon.addPoint(133,189); cowgon.addPoint(160,200);
	    cowgon.addPoint(97,196);  cowgon.addPoint(107,182);
	    cowgon.addPoint(118,185); cowgon.addPoint(110,144);
	    cowgon.addPoint(59,77);   cowgon.addPoint(30,82);
	    cowgon.addPoint(30,35);   cowgon.addPoint(15,36);
	}
	
	boolean moo = false;
	boolean milk = false;
	boolean tail = false;

	// Use the contains method to set the tooltip text depending
	// on where the mouse is over the cow.
	public boolean contains(int x, int y) {
	    if(!cowgon.contains(new Point(x, y))) {
		return false;
	    }
	    
	    if((x > 30) && (x < 60) && (y > 60) && (y < 85)) {
		if(!moo) {
		    setToolTipText("<html><center><font color=blue size=+2>" +
				   getString("ToolTipDemo.moo") + "</font></center></html>");
		    moo = true;
		    milk = false;
		    tail = false;
		}
	    } else if((x > 150) && (x < 260) && (y > 90) && (y < 145)) {
		if(!milk) {
		    setToolTipText("<html><center><font face=AvantGarde size=+1 color=white>" +
				   getString("ToolTipDemo.got_milk") + "</font></center></html>");
		    milk = true;
		    moo = false;
		    tail = false;
		}
	    } else if((x > 280) && (x < 300) && (y > 20) && (y < 175)) {
		if(!tail) {
		    setToolTipText("<html><em><b>" + getString("ToolTipDemo.tail") + "</b></em></html>");
		    tail = true;
		    moo = false;
		    milk = false;
		}
	    } else if(moo || milk || tail) {
		setToolTipText(getString("ToolTipDemo.tooltip_features"));
		moo = false;
		tail = false;
		milk = false;
	    }

	    return true;
	}
    }
    
}
