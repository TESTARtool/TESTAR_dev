/***************************************************************************************************
 *
 * Copyright (c) 2023 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2023 - 2025 Universitat Politecnica de Valencia - www.upv.es
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


package org.testar.oracles.generic.visual;

import java.util.ArrayList;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;

public class MetricsHelper {

	/**
	 * Obtain all the Rectangles regions from the leaf widgets. 
	 * 
	 * @param state
	 * @return
	 */
	public static ArrayList<Shape> getRegions(State state) {
		ArrayList<Shape> regions = new ArrayList<>();

		for(Widget w : state) {
			if(w.childCount() < 1 && w.get(Tags.Shape, null) != null) {
				regions.add((Rect)w.get(Tags.Shape));
			}
		}

		return regions;
	}

	// Source: https://github.com/mathieuzen/questim/blob/workingapp/src/be/lilab/questim/server/Alignment.java
	public static double calculateAlignmentMetric(ArrayList<Shape> regions) {	
		int treshold = 1;
		int verticalAlignment1 = 0;
		int horizontalAlignment1 = 0;
		int verticalAlignment2 = 0;
		int horizontalAlignment2 = 0;
		int DAV = 0;
		int DAH = 0;
		int n = regions.size();
		double value = 0.0;

		for(int i=0; i<regions.size();i++){	
			Rect r1 = (Rect) regions.get(i);
			verticalAlignment1 = 0;
			horizontalAlignment1 = 0;
			verticalAlignment2 = 0;
			horizontalAlignment2 = 0;
			for(int j=0; j<regions.size();j++){
				if(j!=i){				
					Rect r2 = (Rect) regions.get(j);
					if((r1.x()<=r2.x()+treshold) && (r1.x()>=r2.x()-treshold))
						verticalAlignment1=1;
					if((r1.x()+r1.width()<=r2.x()+r2.width()+treshold) && (r1.x()+r1.width()>=r2.x()+r2.width()-treshold))
						verticalAlignment2=1;
					if((r1.y()<=r2.y()+treshold) && (r1.y()>=r2.y()-treshold))
						horizontalAlignment1=1;
					if((r1.y()+r1.height()<=r2.y()+r2.height()+treshold) && (r1.y()+r1.height()>=r2.y()+r2.height()-treshold))
						horizontalAlignment2=1;
				}
			}

			DAV += verticalAlignment1 + verticalAlignment2;
			DAH += horizontalAlignment1 + horizontalAlignment2;

		}

		value = (double)(DAV + DAH)/(n*4);
		return value * 100;
	}

	public static double calculateBalanceMetric(ArrayList<Shape> regions, double frameWidth, double frameHeight) {
		double [][] belonging = new double [regions.size()][4];
		double [] a = new double [regions.size()];
		double value = 0.0;

		double BMvert;
		double BMhori;

		double amax = 0;

		double wl = 0;
		double wr = 0;
		double wt = 0;
		double wb = 0;

		for(int i=0;i<regions.size();i++){

			Rect r = (Rect) regions.get(i);

			a[i] = (double)r.width()* (double)r.height();
			if(a[i]>amax){
				amax = a[i];
			}

			//Belonging tests
			//UL
			if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() < frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][0] = ((double)(frameWidth/2 - r.x())  * (frameHeight/2 - r.y()))/(r.width()*r.height());
			}

			else if((frameWidth/2 - r.x() < r.width()) && (frameHeight/2 - r.y() > r.height()))
			{
				belonging[i][0] = ((double)(frameWidth/2 - r.x())/(r.width()));
			}

			else if((frameWidth/2 - r.x() > r.width()) && (frameHeight/2 - r.y() < r.height()))
			{
				belonging[i][0] = ((double)(frameHeight/2 - r.y())/(r.height()));
			}

			else if((frameWidth/2 - r.x() > r.width()) && (frameHeight/2 - r.y() > r.height()))
			{
				belonging[i][0] = 1;
			}

			//UR
			if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][1] = ((double)(r.x()+r.width()  - frameWidth/2)  * (frameHeight/2 - r.y()))/(r.width()*r.height());
			}

			else if((r.x()+r.width() > frameWidth/2) && (r.y()+r.height() < frameHeight/2))
			{
				belonging[i][1] = ((double)(r.x()+r.width()  - frameWidth/2)/(r.width()));
			}

			else if((r.x() > frameWidth/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][1] = ((double)(frameHeight/2 - r.y())/(r.height()));
			}

			if((r.x() > frameWidth/2) && (r.y()+r.height() < frameHeight/2))
			{
				belonging[i][1] = 1;
			}

			//LL
			if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() < frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][2] = ((double)(frameWidth/2 - r.x())  * (r.y()+r.height() - frameHeight/2))/(r.width()*r.height());
			}

			else if((r.x() < frameWidth/2) && (r.x()+r.width() < frameWidth/2) && (r.y() < frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][2] = ((double)(r.y()+r.height()-frameHeight/2)/(r.height()));
			}

			else if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() > frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][2] = ((double)(frameWidth/2 - r.x())/(r.width()));
			}

			else if((r.x() < frameWidth/2) && (r.x()+r.width() < frameWidth/2) && (r.y() > frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][2] = 1;
			}

			//LR
			if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() < frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][3] = ((double)(r.x()+r.width()  - frameWidth/2)  * (r.y()+r.height() - frameHeight/2))/(r.width()*r.height());
			}

			else if((r.x() > frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() < frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][3] = ((double)(r.y()+r.height()-frameHeight/2)/(r.height()));
			}

			else if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() > frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][3] = ((double)(r.x()+r.width()-frameWidth/2)/(r.width()));
			}

			else if((r.x() > frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() > frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][3] = 1;
			}

			for(int j=0; j<4; j++){
				if(belonging[i][j]<0){
					belonging[i][j]=0;
				}
			}

			if(belonging[i][0] == 1 || belonging[i][1] == 1 || belonging[i][2] == 1 || belonging [i][3] == 1)
			{
				wl += belonging[i][0]*(1+(frameWidth/2 - (r.x()+r.width()/2))/frameWidth/2)*(a[i]/amax)+belonging[i][2]*(1+(frameWidth/2 - (r.x()+r.width()/2))/frameWidth/2)*(a[i]/amax);
				wr += belonging[i][1]*(1+((r.x()+r.width()/2) - frameWidth/2)/frameWidth/2)*(a[i]/amax)+belonging[i][3]*(1+((r.x()+r.width()/2) - frameWidth/2)/frameWidth/2)*(a[i]/amax);
				wt += belonging[i][0]*(1+(frameHeight/2 - (r.y()+r.height()/2))/frameHeight/2)*(a[i]/amax)+belonging[i][1]*(1+(frameHeight/2 - (r.y()+r.height()/2))/frameHeight/2)*(a[i]/amax);
				wb += belonging[i][2]*(1+((r.y()+r.height()/2) - frameHeight/2)/frameHeight/2)*(a[i]/amax)+belonging[i][3]*(1+((r.y()+r.height()/2) - frameHeight/2)/frameHeight/2)*(a[i]/amax);
			}
			else
			{
				wl += belonging[i][0]*(a[i]/amax)+belonging[i][2]*(a[i]/amax);
				wr += belonging[i][1]*(a[i]/amax)+belonging[i][3]*(a[i]/amax);
				wt += belonging[i][0]*(a[i]/amax)+belonging[i][1]*(a[i]/amax);
				wb += belonging[i][2]*(a[i]/amax)+belonging[i][3]*(a[i]/amax);
			}

		}

		BMvert = (wl-wr)/Math.max(wl,wr);
		BMhori = (wt-wb)/Math.max(wt,wb);

		value = 1-(Math.abs(BMvert)+Math.abs(BMhori))/2;			

		return value * 100;
	}

	public static double calculateCenterAlignment(ArrayList<Shape> regions) {
		int treshold = 5;
		int verticalCentralAlignment = 0;
		int horizontalCentralAlignment = 0;
		int CAV = 0;
		int CAH = 0;
		int n = regions.size();
		double value = 0;

		for(int i=0; i<regions.size();i++){	
			Rect r1 = (Rect) regions.get(i);
			verticalCentralAlignment = 0;
			horizontalCentralAlignment = 0;
			for(int j=0; j<regions.size();j++){
				if(j!=i){				
					Rect r2 = (Rect) regions.get(j);
					if((r1.x()+r1.width()/2<=r2.x()+r2.width()/2+treshold) && (r1.x()+r1.width()/2>=r2.x()+r2.width()/2-treshold))
						verticalCentralAlignment = 1;
					if((r1.y()+r1.height()/2<=r2.y()+r2.height()/2+treshold) && (r1.y()+r1.height()/2>=r2.y()+r2.height()/2-treshold))
						horizontalCentralAlignment = 1;
				}
			}

			CAV += verticalCentralAlignment;
			CAH += horizontalCentralAlignment;

		}

		value = (double)(CAV + CAH)/(n*2);
		return value * 100;

	}

	public static double calculateConcentricity(ArrayList<Shape> region, double frameWidth, double frameHeight) {

		double value = 0.0;
		double ddiag = Math.hypot((double)frameWidth/2,(double)frameHeight/2);
		double dbar = 0.0;
		double dic = 0.0;
		int xc = ((int)frameWidth)/2;
		int yc = ((int)frameHeight)/2;

		for(int i=0;i<region.size();i++){
			Rect r = (Rect) region.get(i);

			dbar += Math.hypot((double)(r.x()+r.width()/2)-xc, (double)(r.y()+r.height()/2)-yc);
		}

		dbar/=region.size();

		value = dbar/ddiag;

		return value * 100;

	}

	public static double calculateDensity(ArrayList<Shape> regions, double frameWidth, double frameHeight) { 
		double value = 0;
		int areaframe=0;
		int area=0;

		for(int i=0; i<regions.size(); i++){
			Rect r = (Rect) regions.get(i);
			if(r.x()!=0 || r.y()!=0)
			{
				area += r.width()*r.height();
			}

			areaframe = (int)frameWidth*(int)frameHeight;

			value = area/areaframe;

		}

		return value * 100;

	}

	public static double calculateSimplicity(ArrayList<Shape> regions, double frameWidth, double frameHeight) {
		int treshold = 1;
		double value = 0.0;

		int verticalAlignment1 = 0;
		int horizontalAlignment1 = 0;
		int verticalAlignment2 = 0;
		int horizontalAlignment2 = 0;
		int DAV = 0;
		int DAH = 0;
		int n = regions.size();

		for(int i=0; i<regions.size();i++){	
			Rect r1 = (Rect) regions.get(i);
			verticalAlignment1 = 0;
			horizontalAlignment1 = 0;
			verticalAlignment2 = 0;
			horizontalAlignment2 = 0;
			for(int j=0; j<regions.size();j++){
				if(j!=i){				
					Rect r2 = (Rect) regions.get(j);
					if(!(r1.x()<=r2.x()+treshold) && !(r1.x()>=r2.x()-treshold))
						verticalAlignment1=1;
					if(!(r1.x()+r1.width()<=r2.x()+r2.width()+treshold) && !(r1.x()+r1.width()>=r2.x()+r2.width()-treshold))
						verticalAlignment2=1;
					if(!(r1.y()<=r2.y()+treshold) && !(r1.y()>=r2.y()-treshold))
						horizontalAlignment1=1;
					if(!(r1.y()+r1.height()<=r2.y()+r2.height()+treshold) && !(r1.y()+r1.height()>=r2.y()+r2.height()-treshold))
						horizontalAlignment2=1;
				}
			}

			DAV += verticalAlignment1 + verticalAlignment2;
			DAH += horizontalAlignment1 + horizontalAlignment2;					
		}
		value = (double) 1/(DAV+DAH+n);

		return value * 100;
	}

}
