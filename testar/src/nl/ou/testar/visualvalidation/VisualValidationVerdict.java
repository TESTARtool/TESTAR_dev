package nl.ou.testar.visualvalidation;

import nl.ou.testar.visualvalidation.matcher.ContentMatchResult;
import org.fruit.alayer.Verdict;

public class VisualValidationVerdict {
    static final double warningSeverity = 0.1;
    static final double errorSeverity = 0.15;
    static final double failedToMatchSeverity = 0.16;

    private static String composeVerdictMessage(ContentMatchResult match) {
        return String.format("\"%s\" matched %d%%.", match.expectedText, match.matchedPercentage);
    }

    public static Verdict createSuccessVerdict(ContentMatchResult match) {
        return new Verdict(Verdict.SEVERITY_OK, composeVerdictMessage(match));
    }

    public static Verdict createAlmostMatchedVerdict(ContentMatchResult match) {
        return new Verdict(warningSeverity, composeVerdictMessage(match));
    }

    public static Verdict createHardlyMatchedVerdict(ContentMatchResult match) {
        return new Verdict(errorSeverity, composeVerdictMessage(match));
    }

    public static Verdict createFailedToMatchVerdict(TextElement match) {
        return new Verdict(failedToMatchSeverity, String.format("Failed to match \"%s\".", match._text));
    }

}
