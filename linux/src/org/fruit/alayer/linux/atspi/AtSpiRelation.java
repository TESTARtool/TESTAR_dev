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

package org.fruit.alayer.linux.atspi;

import org.fruit.alayer.linux.atspi.enums.AtSpiRelations;

import java.util.ArrayList;
import java.util.List;

/**
 * Java implementation of an AtSpiRelation object - An interface via which non-hierarchical relationships are
 * indicated. An instance of this interface represents a "one-to-many" correspondance.
 */
public class AtSpiRelation {

    //region Properties

    private long _relationPtr;
    public long relationPtr() {
        return _relationPtr;
    }

    private AtSpiRelations _type;
    public AtSpiRelations type() {
      return AtSpiRelations.values()[LibAtSpi.atspi_relation_get_relation_type(_relationPtr)];
    }

    private int _nrOfTargets;
    public int nrOfTargets() {
      return LibAtSpi.atspi_relation_get_n_targets(_relationPtr);
    }

    private List<AtSpiAccessible> _targets;
    public List<AtSpiAccessible> targets() {
      return getTargets(this);
    }

    //endregion

    //region Constructors + Initialization

    /**
     * Default empty constructor.
     */
    private AtSpiRelation() {

    }

    /**
     * Creates a new instance of an AtSpiRelation object from a pointer.
     * @param relationPtr Pointer to the AtSpiRelation object.
     * @return A Java instance of an AtSpiRelation object.
     */
    public static AtSpiRelation CreateInstance(long relationPtr) {

        if (relationPtr == 0) {
            return null;
        }

        // Create a new instance.
        AtSpiRelation rObj = new AtSpiRelation();

        // Fill the instance's properties.
        rObj._relationPtr = relationPtr;

        return rObj;

    }

    /**
     * Fills an AtSpiRelation object's information.
     * @param relationPtr Pointer to the AtSpiRelation object.
     * @param rObj The Java instance of an AtSpiRelation object.
     */
    private static void fillInstance(long relationPtr, AtSpiRelation rObj) {

        // Fill the properties with the information.
        rObj._type = AtSpiRelations.values()[LibAtSpi.atspi_relation_get_relation_type(relationPtr)];
        rObj._nrOfTargets = LibAtSpi.atspi_relation_get_n_targets(relationPtr);

        rObj._targets = getTargets(rObj);

    }

    //endregion

    //region Relation functionality

    /**
     * Fills the instance with data for debug purposes.
     */
    public void retrieveInformation(boolean fillTargetInfo) {

        fillInstance(_relationPtr, this);

        if (fillTargetInfo) {
            for (AtSpiAccessible a: _targets) {
                a.retrieveAccessibleInfoNoRelations();
            }
        }

    }

    /**
     * Fills the instance with data for debug purposes.
     */
    public void retrieveInformationTree() {

        fillInstance(_relationPtr, this);

        for (AtSpiAccessible a: _targets) {
            a.retrieveAccessibleInfoNoRelations();
        }

    }

    /**
     * Creates a list of targets this relation targets.
     * @param relation The AtSpiRelation to get the targets for.
     * @return A List of AtSpiAccessible targets.
     */
    private static List<AtSpiAccessible> getTargets(AtSpiRelation relation) {

        ArrayList<AtSpiAccessible> t = new ArrayList<>();

        for (int i = 0; i < relation.nrOfTargets(); i++) {

            AtSpiAccessible a = AtSpiAccessible.CreateInstance(LibAtSpi.atspi_relation_get_target(relation._relationPtr, i));

            if (a != null) {
                t.add(a);
            }

        }

        return t;

    }

    //endregion

    //region Object overrides

    /**
     * Returns a string representation of an AtSpiRelation object.
     * @return Returns a string representation of an AtSpiRelation object.
     */
    @Override
    public String toString() {
        return "Relation: " + type().toString() +  " - Targets: " + nrOfTargets();
    }

    //endregion

}
