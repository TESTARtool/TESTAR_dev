package android_digioffice.oracles;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.android.enums.AndroidTags;

import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeRandomSearchShowsEmptyListOrFallback extends AbstractAndroidDigiOfficeOracle {

    private static final String RESOURCE_ID_LIST_EMPTY = "list-empty-text";
    private final String DEFAULT_USER_FALLBACK;

    private boolean awaitingRandomInputSearch = false;

    public AndroidDigiOfficeRandomSearchShowsEmptyListOrFallback(String fallbackUser) {
        super("AndroidDigiOfficeRandomSearchShowsEmptyListOrFallback");
        DEFAULT_USER_FALLBACK = fallbackUser;
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
        if (!stateContainsExpectedSearchResult(state)) {
            return Collections.singletonList(new Verdict(
                Verdict.Severity.WARNING_RESOURCE_NOT_FOUND_FAULT,
                "The random UUID Search typing action did not show either the expected '" + RESOURCE_ID_LIST_EMPTY
                        + "' widget or a widget text containing '" + DEFAULT_USER_FALLBACK + "'."));
        }

        return Collections.singletonList(Verdict.OK);
    }

    private boolean stateContainsExpectedSearchResult(State state) {
        for (Widget widget : state) {
            String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
            if (resourceId.contains(RESOURCE_ID_LIST_EMPTY)) {
                return true;
            }

            String widgetText = widget.get(AndroidTags.AndroidText, "");
            if (widgetText.contains(DEFAULT_USER_FALLBACK)) {
                return true;
            }
        }

        return false;
    }
}
