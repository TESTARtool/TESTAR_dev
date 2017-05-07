package org.fruit.alayer.linux.atspi;


import org.fruit.alayer.linux.util.BridJHelper;
import org.fruit.alayer.linux.atspi.enums.AtSpiRoles;
import org.fruit.alayer.linux.glib.GArray;
import org.fruit.alayer.linux.glib.GHashTable;

import java.util.ArrayList;
import java.util.List;


/**
 * Java implementation of the AtSpiAccessible object.
 */
public class AtSpiAccessible {


    //region Properties


    private long _accessiblePtr;
    public long accessiblePtr() {
        return _accessiblePtr;
    }


    private String _name;
    public String name() {
        return BridJHelper.convertToString(LibAtSpi.atspi_accessible_get_name(_accessiblePtr, 0));
    }


    private String _description;
    public String description() {
        return BridJHelper.convertToString(LibAtSpi.atspi_accessible_get_description(_accessiblePtr, 0));
    }


    private long _parentPtr;
    public long parentPtr() {
        return LibAtSpi.atspi_accessible_get_parent(_accessiblePtr, 0);
    }


    private AtSpiAccessible _parent;
    public AtSpiAccessible parent() {
        return _parent;
    }


    private int _childCount;
    public int childCount() {
        return LibAtSpi.atspi_accessible_get_child_count(_accessiblePtr, 0);
    }


    private List<AtSpiAccessible> _children;
    public List<AtSpiAccessible> children() { return getAccessibleChildren(this); }


    private int _indexInParent;
    public int indexInParent() {
        return LibAtSpi.atspi_accessible_get_index_in_parent(_accessiblePtr, 0);
    }


    // TODO: relation_set.


    private AtSpiRoles _role;
    public AtSpiRoles role() {
        return AtSpiRoles.values()[LibAtSpi.atspi_accessible_get_role(_accessiblePtr, 0)];
    }


    private String _roleName;
    public String roleName() {
        return BridJHelper.convertToString(LibAtSpi.atspi_accessible_get_role_name(_accessiblePtr, 0));
    }


    private AtSpiStateSet _states;
    public AtSpiStateSet states() {
        return AtSpiStateSet.CreateInstance(LibAtSpi.atspi_accessible_get_state_set(_accessiblePtr));
    }


    private List<AtSpiRelation> _relations;
    public List<AtSpiRelation> relations() {
        return createRelationList(LibAtSpi.atspi_accessible_get_relation_set(_accessiblePtr, 0));
    }


    private GHashTable _attributes;
    public GHashTable attributes() {
        return GHashTable.CreateInstance(LibAtSpi.atspi_accessible_get_attributes(_accessiblePtr, 0));
    }


    private GArray<String> _attributesAsArray;
    public GArray<String> attributesAsArray() { return GArray.CreateInstance(LibAtSpi.atspi_accessible_get_attributes_as_array(_accessiblePtr, 0), String.class); }


    private String _toolkitName;
    public String toolkitName() {
        return BridJHelper.convertToString(LibAtSpi.atspi_accessible_get_toolkit_name(_accessiblePtr, 0));
    }


    private String _toolkitVersion;
    public String toolkitVersion() {
        return BridJHelper.convertToString(LibAtSpi.atspi_accessible_get_toolkit_version(_accessiblePtr, 0));
    }


    // TODO: application.


    private AtSpiAction _action;
    public AtSpiAction action() { return AtSpiAction.CreateInstance(LibAtSpi.atspi_accessible_get_action_iface(_accessiblePtr)); }


    private AtSpiComponent _component;
    public AtSpiComponent component() { return AtSpiComponent.CreateInstance(LibAtSpi.atspi_accessible_get_component_iface(_accessiblePtr)); }


    private AtSpiValue _value;
    public AtSpiValue value() { return AtSpiValue.CreateInstance(LibAtSpi.atspi_accessible_get_value_iface(_accessiblePtr)); }


    private GArray<String> _interfaces;
    public GArray<String> interfaces() {
        return GArray.CreateInstance(LibAtSpi.atspi_accessible_get_interfaces(_accessiblePtr), String.class);
    }


    //endregion


    //region Constructors


    /**
     * Default empty constructor.
     */
    private AtSpiAccessible() {

    }


    /**
     * Creates a new instance of an AtSpiAccessible object from a pointer.
     * @param accessiblePtr Pointer to the AtSpiAccessible object.
     * @return A Java instance of an AtSpiAccessible object.
     */
    public static AtSpiAccessible CreateInstance(long accessiblePtr) {
        return CreateInstance(accessiblePtr, null);
    }


    /**
     * Creates a new instance of an AtSpiAccessible object from a pointer.
     * @param accessiblePtr Pointer to the AtSpiAccessible object.
     * @param parent The parent of the AtSpiAccessible object to create.
     * @return A Java instance of an AtSpiAccessible object.
     */
    public static AtSpiAccessible CreateInstance(long accessiblePtr, AtSpiAccessible parent) {


        if (accessiblePtr == 0) {
            return null;
        }


        // Create a new instance.
        AtSpiAccessible aObj = new AtSpiAccessible();


        // Fill the instance's properties.
        //fillInstance(windowPtr, aObj, parent);


        aObj._accessiblePtr = accessiblePtr;


        if (parent == null && aObj.parentPtr() != 0) {
            aObj._parent = AtSpiAccessible.CreateInstance(aObj._parentPtr);
        } else if (parent != null) {
            aObj._parent = parent;
        }


        return aObj;

    }


    /**
     * Fills an AtSpiAccessible object's information.
     * @param accessiblePtr Pointer to the AtSpiAccessible object.
     * @param aObj The Java instance of an AtSpiAccessible object.
     * @param parent The parent of the AtSpiAccessible object to fill the information for.
     */
    private static void fillInstance(long accessiblePtr, AtSpiAccessible aObj, AtSpiAccessible parent) {


        // Fill the properties with the information.
        aObj._accessiblePtr = accessiblePtr;
        aObj._name = BridJHelper.convertToString(LibAtSpi.atspi_accessible_get_name(accessiblePtr, 0));
        aObj._description = BridJHelper.convertToString(LibAtSpi.atspi_accessible_get_description(accessiblePtr, 0));
        aObj._parentPtr = LibAtSpi.atspi_accessible_get_parent(accessiblePtr, 0);

        if (parent == null && aObj._parentPtr != 0) {
            aObj._parent = AtSpiAccessible.CreateInstance(aObj._parentPtr);
        } else if (parent != null) {
            aObj._parent = parent;
        }

        aObj._childCount = LibAtSpi.atspi_accessible_get_child_count(accessiblePtr, 0);
        aObj._children = getAccessibleChildren(aObj);
        aObj._indexInParent = LibAtSpi.atspi_accessible_get_index_in_parent(accessiblePtr, 0);
        aObj._role = AtSpiRoles.values()[LibAtSpi.atspi_accessible_get_role(accessiblePtr, 0)];
        aObj._roleName = BridJHelper.convertToString(LibAtSpi.atspi_accessible_get_role_name(accessiblePtr, 0));
        aObj._states = AtSpiStateSet.CreateInstance(LibAtSpi.atspi_accessible_get_state_set(accessiblePtr));

        if (aObj._states != null) {
            aObj._states.retrieveStates();
        }

        aObj._attributes = GHashTable.CreateInstance(LibAtSpi.atspi_accessible_get_attributes(accessiblePtr, 0));
        aObj._attributesAsArray = GArray.CreateInstance(LibAtSpi.atspi_accessible_get_attributes_as_array(accessiblePtr, 0), String.class);
        aObj._toolkitName = BridJHelper.convertToString(LibAtSpi.atspi_accessible_get_toolkit_name(accessiblePtr, 0));
        aObj._toolkitVersion = BridJHelper.convertToString(LibAtSpi.atspi_accessible_get_toolkit_version(accessiblePtr, 0));
        aObj._action = AtSpiAction.CreateInstance(LibAtSpi.atspi_accessible_get_action_iface(accessiblePtr));
        aObj._component = AtSpiComponent.CreateInstance(LibAtSpi.atspi_accessible_get_component_iface(accessiblePtr));

        if (aObj._component != null) {
            aObj._component.retrieveInformation(true);
        }

        aObj._interfaces = GArray.CreateInstance(LibAtSpi.atspi_accessible_get_interfaces(accessiblePtr), String.class);

        aObj._value = AtSpiValue.CreateInstance(LibAtSpi.atspi_accessible_get_value_iface(accessiblePtr));

        if (aObj._value != null) {
            aObj._value.retrieveInformation();
        }

        aObj._relations = createRelationList(LibAtSpi.atspi_accessible_get_relation_set(accessiblePtr, 0));

        if (aObj._relations != null) {
            for (AtSpiRelation r : aObj._relations) {
                // Don't get the tree since it will be way too heavy and might cause endless loops.
                r.retrieveInformation(false);
            }
        }

    }


    //endregion


    //region AtSpiAccessible Functionality


    /**
     * Sets all of the descendants for this accessible object.
     * @param fillInfo Whether to create a tree with complete info or just parent - child relations.
     */
    public void createTree(boolean fillInfo) {


        // Create a complete tree with full info.
        if (fillInfo) {
            retrieveAccessibleInfoTree();
            return;
        }


        // Only create parent - child relations.
        _children = getAccessibleChildren(this);

        for (AtSpiAccessible child : _children) {
            child.createTree(fillInfo);
        }

    }


    /**
     * Fills all the data of the AtSpiAccessible object - mostly for testing purposes: readily available data.
     */
    public void retrieveAccessibleInfo() {
        fillInstance(_accessiblePtr, this, null);
    }


    /**
     * Fills all the data of the AtSpiAccessible object but not the relations - mostly for testing purposes: readily available data.
     */
    public void retrieveAccessibleInfoNoRelations() {
        fillInstance(_accessiblePtr, this, null);
    }


    /**
     * Gets all information for all of the descendants for this accessible object.
     */
    public void retrieveAccessibleInfoTree() {

        fillInstance(_accessiblePtr, this, null);

        for (AtSpiAccessible child : _children) {
            child.retrieveAccessibleInfoTree();
        }

    }


    //endregion


    //region Helper functions


    /**
     * Gets all children pointers of an AtSpiAccessible object.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @return A list of pointers to the children AtspiAccessible objects.
     */
    public static List<Long> getAccessibleChildrenPtrs(long accessiblePtr) {


        ArrayList<Long> children = new ArrayList<>();


        // First get the number of children.
        int childCount = LibAtSpi.atspi_accessible_get_child_count(accessiblePtr, 0);


        // Call the function numerous times to get all children.
        for (int i = 0; i < childCount; i++) {

            long childPtr = LibAtSpi.atspi_accessible_get_child_at_index(accessiblePtr, i, 0);

            if (childPtr != 0) {
                children.add(childPtr);
            }

        }


        return children;


    }


    /**
     * Gets all children of an AtSpiAccessible object.
     * @param accessiblePtr A pointer to the AtspiAccessible object on which to operate.
     * @return A list of children AtspiAccessible objects.
     */
    public static List<AtSpiAccessible> getAccessibleChildren(long accessiblePtr) {
        return getAccessibleChildren(AtSpiAccessible.CreateInstance(accessiblePtr));
    }


    /**
     * Gets all children of an AtSpiAccessible object.
     * @param parent The parent of the children to get.
     * @return A list of children AtspiAccessible objects.
     */
    public static List<AtSpiAccessible> getAccessibleChildren(AtSpiAccessible parent) {


        ArrayList<AtSpiAccessible> children = new ArrayList<>();


        // Get the pointers to the children.
        List<Long> childPtrs = getAccessibleChildrenPtrs(parent.accessiblePtr());


        // For each pointer create a new instance of AtSpiAccessible.
        for (long childPtr : childPtrs) {

            AtSpiAccessible childInstance = AtSpiAccessible.CreateInstance(childPtr, parent);

            if (childInstance != null) {
                children.add(childInstance);
            }

        }


        return children;


    }


    /**
     * Turns a pointer to a GArray containing pointer to AtSpiRelation objects into a list of AtSpiRelations.
     * @param relationsArrayPtr Pointer to a GArray containing pointers to AtSpiRelation objects.
     * @return A list of AtSpiRelations.
     */
    private static List<AtSpiRelation> createRelationList(long relationsArrayPtr) {


        GArray<Long> relationPtrs = GArray.CreateInstance(relationsArrayPtr, Long.class);


        if (relationPtrs == null) {
            return null;
        }

        ArrayList<AtSpiRelation> rs = new ArrayList<>();

        for (Long ptr : relationPtrs.elements()) {

            AtSpiRelation r = AtSpiRelation.CreateInstance(ptr);

            if (r != null) {
                rs.add(r);
            }

        }


        return rs;

    }


    //endregion


    //region Object overrides


    /**
     * Returns a string representation of an AtSpiAccessible object.
     * @return Returns a string representation of an AtSpiAccessible object.
     */
    @Override
    public String toString() {
        return "Name: " + _name + " - Role: " + _roleName + " - Children: " + _childCount;
    }


    //endregion


}