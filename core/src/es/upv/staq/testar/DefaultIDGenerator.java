package es.upv.staq.testar;

import org.fruit.alayer.*;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.exceptions.NoSuchTagException;
import java.util.*;
import java.util.zip.CRC32;

public class DefaultIDGenerator implements IDGenerator {

    public static final String ID_PREFIX_CONCRETE = "C";
    public static final String ID_PREFIX_ABSTRACT_R = "R";
    public static final String ID_PREFIX_ABSTRACT_R_T = "T";
    public static final String ID_PREFIX_ABSTRACT_R_T_P = "P";
    public static final String ID_PREFIX_ABSTRACT = "A";
    public static final String ID_PREFIX_CONCRETE_CUSTOM = "CC";
    public static final String ID_PREFIX_ABSTRACT_CUSTOM = "AC";

    public static final String ID_PREFIX_STATE = "S";
    public static final String ID_PREFIX_WIDGET = "W";
    public static final String ID_PREFIX_ACTION = "A";

    public static final Role[] ROLES_ABSTRACT_ACTION = new Role[]{ // discard parameters
            /// ActionRoles.MouseMove,
            ActionRoles.Type,
            ActionRoles.KeyDown,
            ActionRoles.KeyUp
    };

    /**
     * Builds IDs for a widget or state.
     * @param widget A widget or a State (widget-tree, or widget with children)
     *
     * An identifier (alphanumeric) for a state is built as: f(w1 + ... + wn),
     * where wi (i=1..n) is the identifier for a widget in the widget-tree
     * and the + operator is the concatenation of identifiers (alphanumeric).
     * The order of the widgets in f is determined by the UI structure.
     * f is a formula that converts, with low collision, a text of varying length
     * to a shorter representation: hashcode(text) + length(text) + crc32(text).
     *
     * An identifier (alphanumeric) for a widget is calculated based on
     * the concatenation of a set of accessibility properties (e.g. ROLE, TITLE, ENABLED and PATH).
     * An example for an enabled "ok" button could be: Buttonoktrue0,0,1 ("0,0,1" being the path in the widget-tree).
     *
     */
    public void buildIDs(Widget widget) {
        if (widget.parent() != null){
            widget.set(Tags.ConcreteID, ID_PREFIX_WIDGET + ID_PREFIX_CONCRETE + codify(widget, Tags.ConcreteID));
            widget.set(Tags.AbstractID, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT_R + codify(widget, Tags.AbstractID));
            widget.set(Tags.Abstract_R_ID, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT_R + codify(widget, Tags.Abstract_R_ID));
            widget.set(Tags.Abstract_R_T_ID, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT_R_T + codify(widget, Tags.Abstract_R_T_P_ID));
            widget.set(Tags.Abstract_R_T_P_ID, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT_R_T_P + codify(widget, Tags.Abstract_R_T_P_ID));
            widget.set(Tags.ConcreteIDCustom, ID_PREFIX_WIDGET + ID_PREFIX_CONCRETE_CUSTOM + codify(widget, Tags.ConcreteIDCustom));
            widget.set(Tags.AbstractIDCustom, ID_PREFIX_WIDGET + ID_PREFIX_ABSTRACT_CUSTOM + codify(widget, Tags.AbstractIDCustom));
        } else if (widget instanceof State) { // UI root
            StringBuilder concreteId, abstractId, abstractRoleId, abstractRoleTitleId, abstractRoleTitlePathId, concreteIdCustom, abstractIdCustom;
            concreteId = new StringBuilder(abstractId = new StringBuilder(abstractRoleId = new StringBuilder(abstractRoleTitleId = new StringBuilder(abstractRoleTitlePathId = new StringBuilder(concreteIdCustom = new StringBuilder(abstractIdCustom = new StringBuilder()))))));
            for (Widget childWidget : (State) widget){
                if (childWidget != widget){
                    buildIDs(childWidget);
                    concreteId.append(childWidget.get(Tags.ConcreteID));
                    abstractId.append(childWidget.get(Tags.AbstractID));
                    abstractRoleId.append(childWidget.get(Tags.Abstract_R_ID));
                    abstractRoleTitleId.append(childWidget.get(Tags.Abstract_R_T_ID));
                    abstractRoleTitlePathId.append(childWidget.get(Tags.Abstract_R_T_P_ID));
                    concreteIdCustom.append(childWidget.get(Tags.ConcreteIDCustom));
                    abstractIdCustom.append(childWidget.get(Tags.AbstractIDCustom));
                }
            }
            widget.set(Tags.ConcreteID, ID_PREFIX_STATE + ID_PREFIX_CONCRETE + lowCollisionID(concreteId.toString()));
            widget.set(Tags.AbstractID, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT + lowCollisionID(abstractId.toString()));
            widget.set(Tags.Abstract_R_ID, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT_R + lowCollisionID(abstractRoleId.toString()));
            widget.set(Tags.Abstract_R_T_ID, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT_R_T + lowCollisionID(abstractRoleTitleId.toString()));
            widget.set(Tags.Abstract_R_T_P_ID, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT_R_T_P + lowCollisionID(abstractRoleTitlePathId.toString()));
            widget.set(Tags.ConcreteIDCustom, ID_PREFIX_STATE + ID_PREFIX_CONCRETE_CUSTOM + lowCollisionID(concreteIdCustom.toString()));
            widget.set(Tags.AbstractIDCustom, ID_PREFIX_STATE + ID_PREFIX_ABSTRACT_CUSTOM + lowCollisionID(abstractIdCustom.toString()));
        }
    }
    public void buildIDs(State state, Set<Action> actions) {
        for (Action a : actions)
            buildIDs(state,a);

        // for the custom abstract action identifier, we first sort the actions by their path in the widget tree
        // and then set their ids using incremental counters
        Map<Role, Integer> roleCounter = new HashMap<>();
        actions.stream().
                filter(action -> {
                    try {
                        action.get(Tags.OriginWidget).get(Tags.Path);
                        return true;
                    }
                    catch (NoSuchTagException ex) {
                        System.out.println("No origin widget found for action role: ");
                        System.out.println(action.get(Tags.Role));
                        System.out.println(action.get(Tags.Desc));
                        return false;
                    }
                }).
                sorted(Comparator.comparing(action -> action.get(Tags.OriginWidget).get(Tags.Path))).
                forEach(
                        action -> {
                            updateRoleCounter(action, roleCounter);
                            String abstractActionId = ID_PREFIX_ACTION + ID_PREFIX_ABSTRACT_CUSTOM + state.get(Tags.AbstractIDCustom) + "_" + getAbstractActionIdentifier(action, roleCounter);
                            action.set(Tags.AbstractIDCustom, abstractActionId);
                        }
                );
    }

    public void buildIDs(State state, Action action) {
        action.set(Tags.ConcreteID, ID_PREFIX_ACTION + ID_PREFIX_CONCRETE +
                codify(state.get(Tags.ConcreteID), action));
        action.set(Tags.ConcreteIDCustom, ID_PREFIX_ACTION + ID_PREFIX_CONCRETE_CUSTOM +
                codify(state.get(Tags.ConcreteIDCustom), action));
        action.set(Tags.AbstractID, ID_PREFIX_ACTION + ID_PREFIX_ABSTRACT +
                codify(state.get(Tags.ConcreteID), action, ROLES_ABSTRACT_ACTION));
    }

    public void buildEnvironmentActionIDs(State state, Action action) {
        action.set(Tags.ConcreteID, ID_PREFIX_ACTION + ID_PREFIX_CONCRETE +
                codify(state.get(Tags.ConcreteID), action));
        action.set(Tags.ConcreteIDCustom, ID_PREFIX_ACTION + ID_PREFIX_CONCRETE_CUSTOM +
                codify(state.get(Tags.ConcreteIDCustom), action));
        action.set(Tags.AbstractID, ID_PREFIX_ACTION + ID_PREFIX_ABSTRACT +
                codify(state.get(Tags.ConcreteID), action, ROLES_ABSTRACT_ACTION));
        action.set(Tags.AbstractIDCustom, ID_PREFIX_ACTION + ID_PREFIX_ABSTRACT_CUSTOM +
                codify(state.get(Tags.AbstractIDCustom), action, ROLES_ABSTRACT_ACTION));
    }

    private static String codify(Widget state, Tag<?>... tags){
        return lowCollisionID(getTaggedString(state, tags));
    }

    private static String codify(String stateID, Action action, Role... discardParameters){
        return lowCollisionID(stateID + action.toString(discardParameters));
    }

    private static String getTaggedString(Widget leaf, Tag<?>... tags){
        StringBuilder sb = new StringBuilder();
        for(Tag<?> t : tags) {
            sb.append(leaf.get(t, null));
            // check if we are dealing with a state management tag and, if so, if it has child tags
            // that we need to incorporate
            if (StateManagementTags.isStateManagementTag(t) && StateManagementTags.getTagGroup(t).equals(StateManagementTags.Group.ControlPattern)) {
                StateManagementTags.getChildTags(t).stream().sorted(Comparator.comparing(Tag::name)).forEach(tag -> sb.append(leaf.get(tag, null)));
            }
        }
        return sb.toString();
    }

    private static void updateRoleCounter(Action action, Map<Role, Integer> roleCounter) {
        Role role;
        try {
            role = action.get(Tags.OriginWidget).get(Tags.Role);
        }
        catch (NoSuchTagException e) {
            role = action.get(Tags.Role, Roles.Invalid);
        }
        // if the role as key is not present, this will initialize with 1, otherwise it will increment with 1
        roleCounter.merge(role, 1, Integer::sum);
    }

    public String getAbstractStateModelHash(String applicationName, String applicationVersion) {
        // we calculate the hash using the tags that are used in constructing the custom abstract state id
        // for now, an easy way is to order them alphabetically by name
        Tag<?>[] abstractTags = CodingManager.getCustomTagsForAbstractId().clone();
        Arrays.sort(abstractTags, Comparator.comparing(Tag::name));
        StringBuilder hashInput = new StringBuilder();
        for (Tag<?> tag : abstractTags) {
            hashInput.append(tag.name());
        }
        // we add the application name and version to the hash input
        hashInput.append(applicationName);
        hashInput.append(applicationVersion);
        return lowCollisionID(hashInput.toString());
    }

    private static String getAbstractActionIdentifier(Action action, Map<Role, Integer> roleCounter) {
        Role role;
        String pathId = "";

        // Capture some Action context
        try {
            Widget origin = action.get(Tags.OriginWidget);
            pathId = getPathId(origin);
            role = origin.get(Tags.Role);
        }
        catch (NoSuchTagException e) {
            role = action.get(Tags.Role, Roles.Invalid);
        }
        String abstractActionId = pathId + "." + roleCounter.getOrDefault(role, 999);
        return abstractActionId;
    }

    private static String getPathId(Widget w) {
        String name = w.get(Tags.Title);
        String result = name;

        Widget parent = w.parent();
        if (parent == null) {
            return result;
        }
        else {
            return getPathId(parent) + "." + result;
        }
    }
    
    public static String lowCollisionID(String text){ // reduce ID collision probability
        CRC32 crc32 = new CRC32();
        crc32.update(text.getBytes());
        return Integer.toUnsignedString(text.hashCode(), Character.MAX_RADIX) +
                Integer.toHexString(text.length()) +
                crc32.getValue();
    }
}
