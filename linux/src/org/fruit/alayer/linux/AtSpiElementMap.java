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

import org.fruit.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data structure representing a mapping of AtSpiElements to be something - only used for AtSpiElements
 * that are TopLevelContainers.
 *
 * This map could (perhaps should) be made generic...
 */
public class AtSpiElementMap implements Serializable {

    // Used to determine the class during serialization.
    private static final long serialVersionUID = 444555666888222999L;

    //region Properties

    private final List<AtSpiElement> elements = new ArrayList<>();

    //endregion

    //region Constructors

    /**
     * Default empty constructor.
     */
    AtSpiElementMap() {

    }

    //endregion

    //region Map functionality - This used to be in the ElementMapBuilder

    /**
     * Tries to add an element to the map.
     * @param element The element to add to the map.
     */
    void addElement(AtSpiElement element) {

        Assert.notNull(element);

        if (element.boundingBoxOnScreen != null) {
            elements.add(element);
        }

    }

    /**
     * Sorts the elements in the map.
     */
    void sort() {
        elements.sort(new AtSpiElementComparer());
    }

    //endregion

    //region Functionality used by AtSpiRootElement -> AtSpiHitTester

    /**
     * Gets the first top level container element that encompasses a certain point on the screen.
     * @param x The x-coordinate of the point to encompass.
     * @param y The y-coordinate of the point to encompass.
     * @return The first top level container element that encompasses a certain point on the screen.
     */
    public AtSpiElement at(double x, double y) {
        for (AtSpiElement element: elements) {
            if (element.boundingBoxOnScreen.contains(x, y)) {
                return element;
            }
        }
        return null;
    }

    //endregion

}
