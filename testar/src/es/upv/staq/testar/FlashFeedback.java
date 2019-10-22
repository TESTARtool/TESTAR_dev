/***************************************************************************************************
*
* Copyright (c) 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
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

	private static int FLASH_DURATION = 1000; // ms	
	
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
		this.setSize(new Dimension(Math.min(dimW, 512), 32));
		this.setOpacity(0.75f);
	}
	
	public static void flash(String title, int duration){
		if(duration>1000) FLASH_DURATION = duration;
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
