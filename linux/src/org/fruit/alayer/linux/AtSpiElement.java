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
import org.fruit.alayer.linux.atspi.enums.AtSpiRoles;
import org.fruit.alayer.linux.enums.AtSpiElementOrientations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents an AT-SPI node that can be used by Testar.
 */
public class AtSpiElement implements Serializable {


    //region Properties


    public AtSpiElement parent;
    List<AtSpiElement> children = new ArrayList<>();


    public AtSpiRootElement root;


    /**
     * Reference to the AtSpiWidget this AtSpiElement is linked to in the State (widget tree) object created by AtSpiStateFetcher.
     */
    AtSpiWidget backRef;


    //region General AT-SPI information


    long accessiblePtr;
    public String name; 
    public String description; 
    public String toolkitName;
    public AtSpiRoles role;
    Rect boundingBoxOnScreen;


    //endregion


    //region AT-SPI State information


    boolean isEnabled, hasFocus, isFocusable, isModal, isBlocked;
    AtSpiElementOrientations orientation;


    //endregion


    //region Inferred information


    boolean canScrollHorizontally, canScrollVertically, isTopLevelContainer, canScroll;
    double zIndex, hScrollViewSizePercentage, vScrollViewSizePercentage, hScrollPercentage, vScrollPercentage;


    //endregion


    //region Miscellaneous information


    /**
     * Specifies that this element should be ignored since it contains invalid information or
     * does not hold enough information for Testar to operate successfully.
     */
    boolean ignore;


    //endregion


    //endregion


    //region Constructors


    /**
     * Default constructor with a parent specified.
     * @param parent The parent of this AT-SPI element.
     */
    AtSpiElement(AtSpiElement parent) {

        this.parent = parent;

        if (parent != null) {
            root = parent.root;
        }

    }


    //endregion


    //region Serializable functionality


    // Used to determine the class during serialization.
    private static final long serialVersionUID = 159753654852L;


    // Most likely used to serialize and deserialize an instance of this class - don't know if this is used by Testar though.



    /**
     * Serialize an instance of this object.
     * @param oos The outputstream to write to.
     * @throws IOException An IO error occurred.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }


    /**
     * Deserialize an instance of this object.
     * @param ois The inputstream to write to.
     * @throws IOException An IO error occurred.
     * @throws ClassNotFoundException Class could not be found.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
    }


    //endregion


    //region Object overrides


    @Override
    public String toString() {
        if (role != null) {
            return "Name: " + name + " - Role: " + role.toString() + " - Children: " + children.size();
        } else {
            return "Name: " + name + " - Children: " + children.size();
        }

    }


    //endregion


}
