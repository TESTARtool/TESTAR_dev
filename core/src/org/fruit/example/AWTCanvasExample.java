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
		AWTCanvas scrshot = AWTCanvas.fromScreenshot(Rect.from(0, 0, xres, yres));
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
