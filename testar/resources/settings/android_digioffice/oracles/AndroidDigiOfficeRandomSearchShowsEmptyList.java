package android_digioffice.oracles;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.android.enums.AndroidRoles;
import org.testar.monkey.alayer.android.enums.AndroidTags;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class AndroidDigiOfficeRandomSearchShowsEmptyList extends AbstractAndroidDigiOfficeOracle {

    private static final Pattern EMPTY_TEXT_RESOURCE_ID_PATTERN = Pattern.compile(".*empty-text.*");
    private static final Pattern LIST_RESOURCE_ID_PATTERN = Pattern.compile(".*list.*");

    private boolean awaitingRandomInputSearch = false;

    public AndroidDigiOfficeRandomSearchShowsEmptyList() {
        super("AndroidDigiOfficeRandomSearchShowsEmptyList");
    }

    public void activate() {
        this.awaitingRandomInputSearch = true;
    }

    public void deactivate() {
        this.awaitingRandomInputSearch = false;
    }

    @Override
    protected boolean isApplicable(State state) {
        return this.awaitingRandomInputSearch;
    }

    @Override
    protected List<Verdict> check(State state) {
        // Disable the empty-text check, since we are not sure if all states with empty lists have this pattern
        /*if (!stateContainsEmptyTextWidget(state)) {
            return Collections.singletonList(new Verdict(
                    Verdict.Severity.WARNING_RESOURCE_NOT_FOUND_FAULT,
                    "The random UUID Search typing action did not show any widget with resource id matching '.*empty-text.*'."));
        }*/

        for (Widget widget : state) {
            if (!isListScrollView(widget)) {
                continue;
            }

            if (containsAnyDescendantText(widget)) {
                return Collections.singletonList(new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        "The random UUID Search typing action showed a list scroll view with descendant widget text when the list should be empty."));
            }
        }

        return Collections.singletonList(Verdict.OK);
    }

    private boolean stateContainsEmptyTextWidget(State state) {
        for (Widget widget : state) {
            String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
            if (EMPTY_TEXT_RESOURCE_ID_PATTERN.matcher(resourceId).matches()) {
                return true;
            }
        }

        return false;
    }

    private boolean isListScrollView(Widget widget) {
        String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
        return AndroidRoles.AndroidScrollView.equals(widget.get(Tags.Role, null))
                && LIST_RESOURCE_ID_PATTERN.matcher(resourceId).matches();
    }

    private boolean containsAnyDescendantText(Widget widget) {
        for (int i = 0; i < widget.childCount(); i++) {
            Widget child = widget.child(i);

            String childText = child.get(AndroidTags.AndroidText, "");
            if (!childText.isEmpty()) {
                return true;
            }

            if (containsAnyDescendantText(child)) {
                return true;
            }
        }

        return false;
    }
}
