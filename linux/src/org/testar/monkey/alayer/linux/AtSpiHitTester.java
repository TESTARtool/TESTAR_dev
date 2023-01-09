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


package org.testar.monkey.alayer.linux;


import org.testar.monkey.alayer.HitTester;


/**
 * An object that can execute an hit test action on an AtSpiElement - determining if the element can be clicked on
 * at a certain point on the screen.
 */
public class AtSpiHitTester implements HitTester {


    //region Global variables


    // Used to determine the class during serialization.
    private static final long serialVersionUID = 3216549871296454556L;


    private final AtSpiElement _element;


    //endregion


    //region Constructors


    /**
     * Default constructor wrapping an AtSpiElement to hit test.
     * @param element The AtSpiElement to hit test.
     */
    AtSpiHitTester(AtSpiElement element) {
        _element = element;
    }


    //endregion


    //region HitTester implementation


    /**
     * Runs the hit test action for a certain point on the element.
     * @param x The x-coordiante of the point.
     * @param y The y-coordinate of the point.
     * @return True if the element can be hit at the supplied point on the screen; False otherwise.
     */
    @Override
    public boolean apply(double x, double y) {
        return _element.root.visibleAt(_element, x, y);
    }


    /**
     * Runs the hit test action for a certain point on the element.
     * @param x The x-coordiante of the point.
     * @param y The y-coordinate of the point.
     * @param obscuredByChildFeature The element is obscured by a child??
     * @return True if the element can be hit at the supplied point on the screen; False otherwise.
     */
    @Override
    public boolean apply(double x, double y, boolean obscuredByChildFeature) {
        return _element.root.visibleAt(_element, x, y, obscuredByChildFeature);
    }


    //endregion


    //region Object overrides


    @Override
    public String toString() {

        if (_element == null) {
            return "AtSpiHitTester";
        } else {
            return "AtSpiHitTester for: " + _element.name + " bounding: " + _element.boundingBoxOnScreen.toString();
        }

    }


    //endregion


}
