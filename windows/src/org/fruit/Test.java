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
package org.fruit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Point;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Taggable;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.devices.AWTMouse;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.windows.GDIScreenCanvas;
import org.fruit.alayer.windows.UIAStateBuilder;
import org.fruit.alayer.windows.UIATags;
import org.fruit.alayer.windows.WinProcess;

public final class Test{

	public static String queryAllTags(State s){
		StringBuilder sb = new StringBuilder();
		for(Widget w : s){
			for(Tag<?> t : w.tags())
				sb.append(w.get(t));
		}
		return sb.toString();
	}


	public static void testSerialization() throws IOException, ClassNotFoundException {
		//ISystem sys = new WinExecutable("C:\\Windows\\System32\\mspaint.exe");
		//ISystem sys = new WinExecutable("C:\\Program Files (x86)\\Microsoft Office\\Office14\\winword.exe");
		//ISystem sys = WinProcess.FromProcessName("firefox.exe");
		SUT sys = WinProcess.fromPID(5692);
		UIAStateBuilder sb = new UIAStateBuilder();


		Util.pause(2);

		State s = sb.apply(sys);

		queryAllTags(s);

		FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\Tanja Vos\\Desktop\\serialized_state"));
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(bos);


		String info;
		info = queryAllTags(s);
		System.out.println("Tree Size: " + Util.size(s) +  "   information size: " + info.length() + "  information: " );
		System.out.println(Util.treeDesc(s, 2, Tags.Desc, Tags.Shape, Tags.Role));

		//s.set(Tags.Screenshot, AWTCanvas.fromScreenshot(Rect.from(0, 0, 1920, 1080), AWTCanvas.StorageFormat.PNG, 1));

		double tuff1 = Util.time();
		for(int i = 0; i < 100; i++){
			oos.writeObject(s);
			oos.reset();
		}

		System.out.println(Util.time() - tuff1);
		info = queryAllTags(s);
		System.out.println("Tree Size: " + Util.size(s) +  "   information size: " + info.length() + "  information: " );

		oos.close();
		bos.close();

		sys.stop();
		sb.release();


		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Tanja Vos\\Desktop\\serialized_state"));
		BufferedInputStream bis = new BufferedInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(bis);


		State rs = (State)ois.readObject();

		ois.close();
		info = queryAllTags(rs);
		System.out.println("Tree Size: " + Util.size(s) +  "   information size: " + info.length() + "  information: " );


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

	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		UIAStateBuilder sb = new UIAStateBuilder();
		//SUT system = WinProcess.fromExecutable("C:\\Program Files\\CTE XL 3.1.3 Professional\\cte.exe");
		//SUT system = WinProcess.fromExecutable("C:\\Windows\\System32\\calc.exe");
		//SUT system = WinProcess.fromPID(2512);
		SUT system = WinProcess.fromPID(5692);
		
		//SUT system = WinProcess.fromProcName("firefox.exe");
		//SUT system = WinProcess.fromExecutable("C:\\Program Files (x86)\\Microsoft Office\\Office14\\winword.exe");
		Util.pause(5);

		GDIScreenCanvas cv = GDIScreenCanvas.fromPrimaryMonitor();
		Mouse mouse = AWTMouse.build();
		
		final int COUNT = 40;
		String before[] = new String[COUNT];
		
		double t1 = Util.time();
		double writeTime = 0;
		
		File file = new File("./test");
		file.delete();
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		
		State state = null;
		for(int i = 0; i < COUNT; i++){
			state = sb.apply(system);
			
			cv.begin();
			Util.clear(cv);
			Point cursor = mouse.cursor();
			Widget under = Util.widgetFromPoint(state, cursor.x(), cursor.y(), null);
            //Iterable<Widget> unders = Utils.widgetsFromPoint(state, cursor.x(), cursor.y());

			//state.set(Tags.Screenshot, AWTCanvas.fromScreenshot((Rect)state.get(Tags.Shape), AWTCanvas.StorageFormat.BMP, 1.0));
			
			if(under != null){
				Shape s = under.get(Tags.Shape, null);
				if(s != null){
					s.paint(cv, cv.defaultPen());
					//System.out.println("under cursor: " + under.get(Tags.Desc, null) + ",  " + under.get(Tags.ZIndex) + ",  " + under.get(Tags.Role, Roles.Widget));
				}
			}

//			for(Widget u : unders){
//				Shape s = u.get(Tags.Shape, null);
//				if(s != null){
//					s.paint(cv, cv.defaultPen());
//					cv.text(cv.defaultPen(), s.x(), s.y() - 20, 0, Utils.indexString(u));
//				}
//			}
			
			cv.end();
			
			Set<Tag<?>> tags = new HashSet<Tag<?>>();
			tags.addAll(Tags.tagSet());
			tags.addAll(UIATags.tagSet());
			tags.remove(UIATags.UIAProviderDescription);
			before[i] = Util.treeDesc(state, 2, tags.toArray(new Tag<?>[0]));
			
			double w1 = Util.time();
			TaggableBase t = new TaggableBase();
			t.set(Tags.SystemState, state);
			oos.writeObject(t);
			writeTime += Util.time() - w1;
		}

		oos.close();
		
		
		double t2 = Util.time();
		System.out.println("Writing took: " + writeTime + " seconds.");
		System.out.println("Size of generated file: " + file.length() / 1000.0 + " kilo bytes.");
		System.out.println("Overall time: " + (t2 - t1) + " seconds.");

		System.out.println("stopping system...");
		Util.stop(system);
		System.out.println("system stopped");
		
		//System.exit(0);

		sb.release();
		//System.out.println("sb released");
		
//		if(1==1)
//			return;
		
		t1 = Util.time();

		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(bis);

		for(int i = 0; i < COUNT; i++){
			Taggable t = (Taggable) ois.readObject();
			state = t.get(Tags.SystemState);
			cv.begin();
			//state.get(Tags.Screenshot).paint(cv, 0, 0, 300, 300);
			cv.end();
			Set<Tag<?>> tags = new HashSet<Tag<?>>();
			tags.addAll(Tags.tagSet());
			tags.addAll(UIATags.tagSet());
			tags.remove(UIATags.UIAProviderDescription);

			String after = Util.treeDesc(state, 2, tags.toArray(new Tag<?>[0]));
			assert(before[i].equals(after));
		}
		
		ois.close();
		
		t2 = Util.time();
		System.out.println("Reading took: " + (t2 - t1) + " seconds.");	
	}
}
