package android_digioffice.oracles;

import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.plugin.NativeLinker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidDigiOfficeDuplicatedText extends AbstractAndroidDigiOfficeOracle {

    private static final String DUPLICATED_TEXT_PATTERN_REGEX = "^(?=\\b(.*\\D.*)(\\s*\\W*\\s*)\\1(\\b|\\W))(?!\\W)";
    private static final String REPEATED_PUNCTUATION_PATTERN_REGEX = "([\\p{Punct}])\\1{2,}";

    private final Pattern duplicatedTextPattern;
    private final Pattern repeatedPunctuationPattern;
    private final Pattern ignorePattern;
    private final String ignorePatternRegEx;

    public AndroidDigiOfficeDuplicatedText() {
        this("");
    }

    public AndroidDigiOfficeDuplicatedText(String ignorePatternRegEx) {
        super("AndroidDigiOfficeDuplicatedText");
        this.duplicatedTextPattern = Pattern.compile(DUPLICATED_TEXT_PATTERN_REGEX);
        this.repeatedPunctuationPattern = Pattern.compile(REPEATED_PUNCTUATION_PATTERN_REGEX);
        this.ignorePatternRegEx = ignorePatternRegEx == null ? "" : ignorePatternRegEx;
        this.ignorePattern = this.ignorePatternRegEx.isEmpty() ? null : Pattern.compile(this.ignorePatternRegEx);
    }

    @Override
    protected boolean isApplicable(State state) {
        for (Widget widget : state) {
            String textValue = widget.get(AndroidTags.AndroidText, "");
            if (!textValue.isEmpty() && !isEditTextWidget(widget)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<Verdict> check(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            String textValue = widget.get(AndroidTags.AndroidText, "");
            if (textValue.isEmpty() || isEditTextWidget(widget)) {
                continue;
            }

            Matcher duplicatedTextMatcher = this.duplicatedTextPattern.matcher(textValue);
            Matcher repeatedPunctuationMatcher = this.repeatedPunctuationPattern.matcher(textValue);
            if (!duplicatedTextMatcher.find() && !repeatedPunctuationMatcher.find()) {
                continue;
            }

            if (this.ignorePattern != null) {
                Matcher ignoreMatcher = this.ignorePattern.matcher(textValue);
                if (ignoreMatcher.find()) {
                    continue;
                }
            }

            String verdictMsg = String.format(
                    "Detected duplicated or repeated Android text! AndroidResourceId: %s , AndroidText: %s",
                    widget.get(AndroidTags.AndroidResourceId, ""),
                    textValue);

            verdicts.add(new Verdict(
                    Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                    verdictMsg,
                    Arrays.asList((Rect) widget.get(Tags.Shape))));
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }

    private boolean isEditTextWidget(Widget widget) {
        Role role = widget.get(Tags.Role, Roles.Widget);
        return Role.isOneOf(role, NativeLinker.getNativeTypeableRoles());
    }
}
