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
package org.fruit.example;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Color;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Rect;

public class AWTCanvasExample {
	public static void main(String[] args) throws IOException, ClassNotFoundException{

		final int xres = 1024, yres = 768;
		// NOTE: Provide a fake windowHandle for now, when this example code is used it must be replaced by a real handle.
		AWTCanvas scrshot = AWTCanvas.fromScreenshot(Rect.from(0, 0, xres, yres), 0);
		AWTCanvas pic = AWTCanvas.fromFile("/Users/guitest/Desktop/fruit_logo.png");
		//pic = AWTImage.FromScreenshot(new Rect(0, 0, xres, yres));
		
		scrshot.begin();
		int crop = 10;
		System.out.println(pic.width());
		pic.paint(scrshot, Rect.from(crop, crop, pic.width() - 2 * crop, pic.height() - 2 * crop), 
				Rect.from(0, 0, 200, 200));
		
		
		Pen p = Pen.newPen().setStrokeWidth(20).setColor(Color.from(0, 250, 0, 255)).build();
		//scrshot.rect(p, 0, 0, 100, 100, true);
		scrshot.line(p, 0, 0, 100, 100);

		scrshot.text(p, 400, 400, 0, "bla blubb 34 91 $ % ^ & *()");
		
		scrshot.end();
		
		scrshot.saveAsJpeg("/Users/guitest/Desktop/wuffinger.jpg", 0.5f);
		scrshot.saveAsPng("/Users/guitest/Desktop/wuffinger.png");

		for(int i = 0; i < 1; i++){
			saveImage(scrshot, "/Users/guitest/Desktop/brabutzinger");
			scrshot = loadImage("/Users/guitest/Desktop/brabutzinger");
			scrshot.saveAsJpeg("/Users/guitest/Desktop/wuffinger" + i + ".jpg", 0.5f);
		}
		

	}


	public static void saveImage(AWTCanvas image, String file) throws IOException{
		FileOutputStream fos = new FileOutputStream(new File(file));
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(bos);

		for(int i = 0; i < 1; i++){
			oos.writeObject(image);
			oos.reset();
		}

		oos.close();
		bos.close();
	}

	public static AWTCanvas loadImage(String file) throws IOException, ClassNotFoundException{
		FileInputStream fis = new FileInputStream(new File(file));
		BufferedInputStream bis = new BufferedInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(bis);

		AWTCanvas ret = (AWTCanvas)ois.readObject();
		ois.close();
		return ret;
	}

}
