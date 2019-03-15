/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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

package org.fruit.alayer.linux;

import org.fruit.alayer.Rect;

import java.util.Comparator;

/**
 * Compares two AtSpiElements - used when sorting a list of AtSpiElements.
 */
public class AtSpiElementComparer implements Comparator<AtSpiElement> {

    private static final int WORSE = 1, BETTER = -1, EVEN = 0;

    /**
     * Compares two AtSpiElements and returns a result to be able to sort the elements in a list.
     * @param o1 The first AtSpiElement.
     * @param o2 The second AtSpiElement.
     * @return 1 if worse, -1 if better, 0 if even.
     */
    @Override
    public int compare(AtSpiElement o1, AtSpiElement o2) {
        if (o1.zIndex < o2.zIndex) {
            return WORSE;
        } else if (o1.zIndex > o2.zIndex) {
            return BETTER;
        } else {
            if (o1.boundingBoxOnScreen != null) {
                if (o2.boundingBoxOnScreen != null) {
                    double area1 = Rect.area(o1.boundingBoxOnScreen);
                    double area2 = Rect.area(o2.boundingBoxOnScreen);
                    return area1 < area2 ? BETTER: (area1 > area2 ? WORSE: EVEN);
                } else {
                    return BETTER;
                }
            } else {
                return WORSE;
            }
        }
    }
}
