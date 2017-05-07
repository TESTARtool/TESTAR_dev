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
package org.fruit.alayer;

import org.fruit.Pair;

/**
 * A Canvas is a surface onto which you can draw e.g. lines, rectangles and circles.
 * It serves as a way to visualize e.g. <code>Action</code>'s and the <code>Shape</code> of
 * <code>Widget</code>'s.
 * 
 * @see Pen
 * @see Visualizer
 */
public interface Canvas {
    double width();
    double height();
    double x();
    double y();
	void begin();
	void end();
    void line(Pen pen, double x1, double y1, double x2, double y2);
    void text(Pen pen, double x, double y, double angle, String text);
    Pair<Double, Double> textMetrics(Pen pen, String text);
    void clear(double x, double y, double width, double height);
    void triangle(Pen pen, double x1, double y1, double x2, double y2, double x3, double y3);
    void image(Pen pen, double x, double y, double width, double height, int[] image, int imageWidth, int imageHeight);
    void ellipse(Pen pen, double x, double y, double width, double height);
    void rect(Pen pen, double x, double y, double width, double height);
    Pen defaultPen();
    void release();
}