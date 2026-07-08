package android_digioffice.oracles;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.oracles.Oracle;

import java.util.Collections;
import java.util.List;

public abstract class AbstractAndroidDigiOfficeOracle implements Oracle {

    private final String oracleName;
    private int evaluatedCount = 0;
    private int applicableCount = 0;
    private int violatedCount = 0;

    protected AbstractAndroidDigiOfficeOracle(String oracleName) {
        this.oracleName = oracleName;
    }

    @Override
    public void initialize() {
    }

    @Override
    public final List<Verdict> getVerdicts(State state) {
        evaluatedCount++;

        if (!isApplicable(state)) {
            return Collections.singletonList(Verdict.OK);
        }

        applicableCount++;

        List<Verdict> verdicts = check(state);
        if (containsFailure(verdicts)) {
            violatedCount++;
        }

        if (verdicts == null || verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }

    protected abstract boolean isApplicable(State state);

    protected abstract List<Verdict> check(State state);

    public String getOracleName() {
        return oracleName;
    }

    public int getEvaluatedCount() {
        return evaluatedCount;
    }

    public int getApplicableCount() {
        return applicableCount;
    }

    public int getViolatedCount() {
        return violatedCount;
    }

    public boolean isVacuousPass() {
        return evaluatedCount > 0 && applicableCount == 0;
    }

    public String getSummaryLine() {
        StringBuilder summary = new StringBuilder();
        summary.append(oracleName)
                .append(": evaluated=").append(evaluatedCount)
                .append(", applicable=").append(applicableCount)
                .append(", violated=").append(violatedCount);

        if (isVacuousPass()) {
            summary.append(" [VACUOUS PASS]");
        }

        return summary.toString();
    }

    private boolean containsFailure(List<Verdict> verdicts) {
        if (verdicts == null) {
            return false;
        }

        for (Verdict verdict : verdicts) {
            if (verdict != null && verdict.severity() > Verdict.OK.severity()) {
                return true;
            }
        }

        return false;
    }
}
