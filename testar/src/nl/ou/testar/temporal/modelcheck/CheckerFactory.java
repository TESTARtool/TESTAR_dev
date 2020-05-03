package nl.ou.testar.temporal.modelcheck;

import nl.ou.testar.temporal.oracle.TemporalFormalism;


public class CheckerFactory {
    public static ModelChecker getModelChecker(TemporalFormalism temporalFormalism) {
        ModelChecker modelChecker;
        switch (temporalFormalism) {
            case CTL:
                modelChecker = new ITS_CTL_ModelChecker();
                break;
            case CTL_ITS:
                modelChecker = new ITS_CTL_ModelChecker();
                break;
            case CTL_GAL:
                modelChecker = new GAL_CTL_ModelChecker();
                break;
            case LTL_LTSMIN:
                modelChecker = new LTSMIN_LTL_ModelChecker();
                break;
            case CTL_LTSMIN:
                modelChecker = new LTSMIN_CTL_ModelChecker();
                break;
            case LTL_ITS:
                modelChecker = new ITS_LTL_ModelChecker();
                break;
            case LTL_SPOT:
                modelChecker = new SPOT_LTL_ModelChecker();
                break;
            case LTL:
                modelChecker = new SPOT_LTL_ModelChecker();
                break;
            default:
                modelChecker = null;
                break;

        }
        modelChecker.setTemporalFormalism(temporalFormalism);
        return modelChecker;
    }

}
