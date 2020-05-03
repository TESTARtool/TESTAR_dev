package nl.ou.testar.ReinforcementLearning.QFunctions;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class QFunctionFactory {
    private final static double ALPHA_DISCOUNT = 1.0d;
    private static final double GAMMA_DISCOUNT = .99d;
    private static final double DEFAULT_QVALUE = 0.0d;

    public static QFunction getQFunction (final Settings settings){
        final double alphaDiscount = settings.get(ConfigTags.Alpha, ALPHA_DISCOUNT);
        final double gammaDiscount = settings.get(ConfigTags.Gamma, GAMMA_DISCOUNT);
        final double defaultQValue = settings.get(ConfigTags.DefaultValue, DEFAULT_QVALUE);

        System.out.println(String.format("QFunction loaded with alpha='%s' gammaDiscount='%s' and defaultQValue='%s'", alphaDiscount, gammaDiscount, defaultQValue));

        return new SarsaQFunction(alphaDiscount, gammaDiscount, defaultQValue);
    }
}
