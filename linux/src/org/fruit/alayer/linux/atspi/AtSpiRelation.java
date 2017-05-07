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
    public AtSpiRelations type() { return AtSpiRelations.values()[LibAtSpi.atspi_relation_get_relation_type(_relationPtr)]; }


    private int _nrOfTargets;
    public int nrOfTargets() { return LibAtSpi.atspi_relation_get_n_targets(_relationPtr); }


    private List<AtSpiAccessible> _targets;
    public List<AtSpiAccessible> targets() { return getTargets(this); }


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
            for (AtSpiAccessible a : _targets) {
                a.retrieveAccessibleInfoNoRelations();
            }
        }


    }


    /**
     * Fills the instance with data for debug purposes.
     */
    public void retrieveInformationTree() {

        fillInstance(_relationPtr, this);

        for (AtSpiAccessible a : _targets) {
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