/***************************************************************************************************
*
* Copyright (c) 2013 - 2025 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
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
package org.testar.monkey.alayer;

import org.testar.monkey.Pair;

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
    void paintBatch();
}
