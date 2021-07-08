package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunctionFactory;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QFunctionFactory {

    private static final Logger logger = LoggerFactory.getLogger(QFunctionFactory.class);

    private final static float ALPHA_DISCOUNT = 1.0f;
    private static final float GAMMA_DISCOUNT = .99f;
    private static final float DEFAULT_QVALUE = 0.0f;

    public static QFunction getQFunction (final Settings settings){
        final float alphaDiscount = settings.get(ConfigTags.Alpha, ALPHA_DISCOUNT);
        final float gammaDiscount = settings.get(ConfigTags.Gamma, GAMMA_DISCOUNT);
        final float defaultQValue = settings.get(ConfigTags.DefaultValue, DEFAULT_QVALUE);

        logger.info("QFunction loaded with alpha='{}' gammaDiscount='{}' and defaultQValue='{}'", alphaDiscount, gammaDiscount, defaultQValue);

        final String qfunction = settings.get(ConfigTags.QFunction, "");
        final QFunction selectedQFunction;

        switch(qfunction) {
            case "QlearningFunction":
                System.out.println("//*/*/*/*/*/*/*/*/* qlearningfunction");
                selectedQFunction = new QlearningFunction(alphaDiscount, gammaDiscount, defaultQValue);
                break;
            default:
                selectedQFunction = new SarsaQFunction(alphaDiscount, gammaDiscount, defaultQValue);
        }


        return selectedQFunction;
    }
}
