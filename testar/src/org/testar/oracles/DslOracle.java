package org.testar.oracles;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;

public abstract class DslOracle implements Oracle, OracleWidgetsMapping {

    /* Boolean variable to determine if the DSL oracle has been applied */

    private boolean vacuousPass = true;

    public boolean isVacuousPass() {
        return vacuousPass;
    }

    public void markAsNonVacuous() {
        vacuousPass = false;
    }

    /* List<String> constraints to determine if the DSL oracle should be applied */

    private List<String> sectionConstraints;
    private Widget constraintWidget;

    protected void addSectionConstraint(List<String> sectionConstraints) {
        this.sectionConstraints = sectionConstraints;
    }

    /**
     * Get the constraint widget to descend and apply the DSL test oracle. 
     * If no constraintWidget matches, get and empty state. 
     */
    protected Widget getConstraintWidgetOrState(State state) {
        // If no constraints, return by default the whole state
        if (sectionConstraints == null || sectionConstraints.isEmpty()) {
            return state;
        }

        // Else, reset the constraint check and apply the heuristic
        constraintWidget = null;

        // Start matching from the root state. This will:
        //  - try matching constraint[0] at State (URL/title or header)
        //  - if not, search deeper for the first match
        sectionConstraintHeuristic(state, new ArrayList<>(sectionConstraints), 0);

        // Return the constraintWidget obtained with the heuristic. 
        // If no constraintWidget matches, return an empty state. 
        return constraintWidget != null ? constraintWidget : new StateStub();
    }

    /**
     * Tries to match constraints[index..] along a downward path starting somewhere in the subtree rooted at 'widget'. 
     *
     * If constraints[index] matches at 'widget', we then try to match the next constraint in its subtree. 
     * If that fails, we backtrack and try to match constraints[index] deeper in the subtree. 
     */
    private boolean sectionConstraintHeuristic(Widget widget, List<String> constraints, int index) {
        if (widget == null) {
            return false;
        }

        if (index >= constraints.size()) {
            // Should not normally happen with current call pattern, but keep safe
            return true;
        }

        String currentConstraint = constraints.get(index);
        boolean matchedHere = matchesConstraintAtNode(widget, currentConstraint, index);

        // 1) Try using THIS node as the match for constraints[index]
        if (matchedHere) {
            // If this was the last constraint, we are done and we set constraintWidget
            if (index == constraints.size() - 1) {
                if (constraintWidget == null) {
                    constraintWidget = widget.parent() != null ? widget.parent() : widget;
                }
                return true;
            }

            // Next constraint must be satisfied somewhere in THIS node's subtree
            for (int i = 0; i < widget.childCount(); i++) {
                Widget child = widget.child(i);
                if (sectionConstraintHeuristic(child, constraints, index + 1)) {
                    return true;
                }
            }
            // matching here didn't lead to a full sequence, then fall through and try deeper
        }

        // 2) Try to match the SAME constraint deeper in the subtree (do not advance index yet)
        for (int i = 0; i < widget.childCount(); i++) {
            Widget child = widget.child(i);
            if (sectionConstraintHeuristic(child, constraints, index)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if 'widget' satisfies a given constraint.
     *
     * Semantics:
     *  - For the FIRST constraint (index == 0) on the State:
     *       check URL/title first (string or regex).
     *  - For headers:
     *       check header text among this widget's siblings (parent's children),
     *       including the widget itself.
     */
    private boolean matchesConstraintAtNode(Widget widget, String constraint, int index) {
        // 1) If this is the root state and it's the first constraint, we first try URL/title
        if (index == 0 && widget instanceof State) {
            State state = (State) widget;
            String url   = state.get(WdTags.WebHref, "");
            String title = state.get(WdTags.WebTitle, "");

            if (matchesTextWithConstraint(url, constraint) || matchesTextWithConstraint(title, constraint)) {
                return true;
            }
            // If it doesn't match URL/title, we fall through and try headers around it
        }

        // 2) Header text among siblings (including this widget)
        return matchesHeaderAroundNode(widget, constraint);
    }

    /**
     * Checks if any header among this widget's siblings (including itself) matches the constraint.
     */
    private boolean matchesHeaderAroundNode(Widget widget, String constraint) {
        Widget parent = widget.parent();

        if (parent != null) {
            // Check all siblings + this widget via parent.children
            for (int i = 0; i < parent.childCount(); i++) {
                Widget candidate = parent.child(i);
                String text = candidate.get(WdTags.WebTextContent, "");
                Role role   = candidate.get(Tags.Role, Roles.Widget);

                if (!text.isEmpty() && isHeaderRole(role) && matchesTextWithConstraint(text, constraint)) {
                    return true;
                }
            }
        } else {
            // No parent: only check the widget itself as a header (edge case)
            String text = widget.get(WdTags.WebTextContent, "");
            Role role   = widget.get(Tags.Role, Roles.Widget);

            if (!text.isEmpty() && isHeaderRole(role) && matchesTextWithConstraint(text, constraint)) {
                return true;
            }
        }

        return false;
    }

    private boolean isHeaderRole(Role role) {
        return role == WdRoles.WdHEADER
                || role == WdRoles.WdH1
                || role == WdRoles.WdH2
                || role == WdRoles.WdH3
                || role == WdRoles.WdH4
                || role == WdRoles.WdH5
                || role == WdRoles.WdH6;
    }

    /**
     * Matches text with a constraint that can be either:
     *  - "plain" text: case-insensitive contains
     *  - "regex-like": if it seems to contain regex meta characters, we compile and apply as a regex
     */
    private boolean matchesTextWithConstraint(String text, String constraint) {
        if (text == null || constraint == null) {
            return false;
        }

        if (looksLikeRegex(constraint)) {
            try {
                Pattern pattern = Pattern.compile(constraint, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                return pattern.matcher(text).find();
            } catch (PatternSyntaxException e) {
                // If the regex is invalid, gracefully fall back to plain contains
            }
        }

        return text.toLowerCase(Locale.ROOT).contains(constraint.toLowerCase(Locale.ROOT));
    }

    /**
     * Heuristic: consider a constraint "regex-like" if it contains common regex metacharacters.
     */
    private boolean looksLikeRegex(String constraint) {
        String metaChars = ".*+?[](){}|\\";
        for (int i = 0; i < constraint.length(); i++) {
            char c = constraint.charAt(i);
            if (metaChars.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }

}
