/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2016):                                                                          *
 * Universitat Politecnica de Valencia                                                        *
 * Camino de Vera, s/n                                                                        *
 * 46022 Valencia, Spain                                                                      *
 * www.upv.es                                                                                 *
 *                                                                                            * 
 * D I S C L A I M E R:                                                                       *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)          *
 * in the context of the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open           *
 * source project under the BSD3 license (http://opensource.org/licenses/BSD-3-Clause)        *                                                                                        * 
 *                                                                                            *
 **********************************************************************************************/

package es.upv.staq.testar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Label;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Displays a short-time flash message for feedback.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class FlashFeedback  extends JDialog implements Runnable {

	private static final long serialVersionUID = -3851564540655407657L;

	private static final int FLASH_DURATION = 1000; // ms	
	
	/**
	 * @param title Non-null and non empty text.
	 */
	private FlashFeedback(String title) {
		super((JFrame)null, title, false);
		this.setType(Type.POPUP);
		this.setUndecorated(true);
		Label msg = new Label(title);
		msg.setBackground(Color.BLACK);
		msg.setForeground(Color.WHITE);
		this.add(msg);
		int dimW = (title.length() + 1) * 12;
		this.setSize(new Dimension(dimW > 512 ? 512 : dimW, 32));
		this.setEnabled(false);
		this.setOpacity(0.75f);
	}
	
	public static void flash(String title){
		new FlashFeedback(title).run();
	}
	
	@Override
	public void run() {
	    this.setVisible(true);
		synchronized(this){
			try{
				this.wait(FLASH_DURATION);
			} catch (java.lang.InterruptedException e){}
		}
		this.setVisible(false);
		this.dispose();
	}
	
}